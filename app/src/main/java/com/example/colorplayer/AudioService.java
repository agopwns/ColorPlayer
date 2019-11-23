package com.example.colorplayer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;

import com.example.colorplayer.dataloader.SongLoader;
import com.example.colorplayer.model.Song;
import com.example.colorplayer.utils.CommandActions;
import com.example.colorplayer.utils.RemoteViewSize;
import com.example.colorplayer.utils.RepeatActions;

import java.util.ArrayList;
import java.util.Collections;

public class AudioService extends Service {

    private final IBinder mBinder = new AudioServiceBinder();
    private MediaPlayer mMediaPlayer;
    private boolean isPrepared;
    private boolean isShuffled = false;
    private String repeatState = RepeatActions.REPEAT_ALL;
    private String remoteViewSize = RemoteViewSize.LARGE;

    public class AudioServiceBinder extends Binder {
        AudioService getService() {
            return AudioService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

        mNotificationPlayer = new NotificationPlayer(this);
        createNotificationChannel();

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                isPrepared = true;
                mp.start();
                // 준비 상태를 NowPlayingActivity 에 알리기 위함
                sendBroadcast(new Intent(BroadcastActions.PREPARED));
                updateNotificationPlayer();
            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isPrepared = false;
                // 재생 상태 변경을 알려서 NowPlayingActivity 에서 UI 업데이트를 하기 위함
                forward();
                updateNotificationPlayer();
            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                isPrepared = false;
                // Warning : 곡이 준비되고 재생되는 것보다 빠르게 getPosition, getDuration 을 사용하려고
                // 오류가 남.
                // 에러시 다음 곡을 넘어가는 것을 방지하기 위해 다시 재생
                mCurrentPosition--;
                Log.d("AudioService", "setOnErrorListener 에러 발생");
                updateNotificationPlayer();
                return false;
            }
        });
        mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {

            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // RemoteView 를 클릭시 해당 함수를 통해 action 이 들어오고 그에 맞는 서비스 동작을 실행한다
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (CommandActions.TOGGLE_PLAY.equals(action)) {
                if (isPlaying()) {
                    pause();
                } else {
                    play();
                }
            } else if (CommandActions.REWIND.equals(action)) {
                rewind();
            } else if (CommandActions.FORWARD.equals(action)) {
                forward();
            } else if (CommandActions.CLOSE.equals(action)) {
                pause();
                removeNotificationPlayer();
            } else if (CommandActions.COLLAPSE.equals(action)){
                remoteViewSize = RemoteViewSize.SMALL;
                updateNotificationPlayer();
            } else if (CommandActions.EXPAND.equals(action)){
                remoteViewSize = RemoteViewSize.LARGE;
                updateNotificationPlayer();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    // 재생 목록 리스트
    private ArrayList<Long> mAudioIds = new ArrayList<>();
    private ArrayList<Long> mShuffleAudioIds = new ArrayList<>();
    private ArrayList<Long> mTempAudioIds = new ArrayList<>();
    public void setPlayList(ArrayList<Long> audioIds) {
        // 랜덤 셔플이 다시 덮어씌워지지 않도록 방지하기 위함
        if (!isShuffled) {
            if (!mAudioIds.equals(audioIds)) {
                mAudioIds.clear();
                mAudioIds.addAll(audioIds);
            }
            // 랜덤 셔플 리스트 준비
            mShuffleAudioIds = audioIds;
            Collections.shuffle(mShuffleAudioIds);
        }
    }

    public ArrayList<Long> getPlayList(){
        return mAudioIds;
    }
    public boolean getPreparedState(){
        return isPrepared;
    }

    public void toggleShuffleList(){
        if(!isShuffled){
            // 랜덤으로 전환
            mTempAudioIds = mAudioIds;
            mAudioIds = mShuffleAudioIds;
            isShuffled = true;
        } else {
            // 일반 재생으로 전환
            mAudioIds = mTempAudioIds;
            isShuffled = false;
        }
    }

    public boolean getShuffleState(){
        return isShuffled;
    }

    public void toggleRepeatState(){

        if(repeatState.equals(RepeatActions.REPEAT_ALL))
            repeatState = RepeatActions.REPEAT_ONE;
        else if(repeatState.equals(RepeatActions.REPEAT_ONE))
            repeatState = RepeatActions.REPEAT_NONE;
        else if(repeatState.equals(RepeatActions.REPEAT_NONE))
            repeatState = RepeatActions.REPEAT_ALL;
    }

    public String getRepeatState(){
        return repeatState;
    }

    public int getSongPosition(){
        return mCurrentPosition;
    }

    // 현재 재생할 오디오 정보 가져오기
    private int mCurrentPosition;
    private Song song;
    private void queryAudioItem(int position) {
        try {
            mCurrentPosition = position;

            long audioId = mAudioIds.get(position);
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String[] projection = new String[]{
                    "_id", "title", "artist", "album", "duration", "track", "artist_id", "album_id"
            };
            String selection = MediaStore.Audio.Media._ID + " = ?";
            String[] selectionArgs = {String.valueOf(audioId)};
            Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    song = SongLoader.getSongForCursor(cursor);
                }
                cursor.close();
            }
        } catch (Exception e){
            Log.d("AudioService", "queryAudioItem 에러 발생 : " + e);
        }
    }

    private void queryAudioItemShuffledClick(int position) {
        try {
            mCurrentPosition = position;

            long audioId = mTempAudioIds.get(position);
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String[] projection = new String[]{
                    "_id", "title", "artist", "album", "duration", "track", "artist_id", "album_id"
            };
            String selection = MediaStore.Audio.Media._ID + " = ?";
            String[] selectionArgs = {String.valueOf(audioId)};
            Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    song = SongLoader.getSongForCursor(cursor);
                }
                cursor.close();
            }
        } catch (Exception e){
            Log.d("AudioService", "queryAudioItem 에러 발생 : " + e);
        }
    }

    // mMediaPlayer 준비
    private void prepare() {
        try {
            // TODO : path 수정
            Uri uri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + song.id);
            mMediaPlayer.setDataSource(getApplicationContext(), uri);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("AudioService", "prepare 에러 발생 : " + e);
        }
    }

    // play 기능
    public void play(int position) {
        queryAudioItem(position);
        stop();
        prepare();
    }

    public void playShuffledClick(int position) {
        queryAudioItemShuffledClick(position);
        stop();
        prepare();
    }

    public void play() {
        if (isPrepared) {
            mMediaPlayer.start();
            sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED));
            updateNotificationPlayer();
        }
    }

    public void pause() {
        if (isPrepared) {
            mMediaPlayer.pause();
            sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED));
            updateNotificationPlayer();
        }
    }

    private void stop() {
        mMediaPlayer.stop();
        mMediaPlayer.reset();
    }

    public void forward() {

        if(repeatState.equals(RepeatActions.REPEAT_ALL)){
            // 기존 로직
            if (mAudioIds.size() - 1 > mCurrentPosition) {
                mCurrentPosition++; // 다음 포지션으로 이동.
            } else {
                mCurrentPosition = 0; // 처음 포지션으로 이동.
            }
        }
        else if(repeatState.equals(RepeatActions.REPEAT_ONE)){
            // 아무것도 하지 않음. 포지션의 위치가 바뀌지 않기 때문에 한곡 반복
        }
        else if(repeatState.equals(RepeatActions.REPEAT_NONE)){
            if (mAudioIds.size() - 1 > mCurrentPosition) {
                mCurrentPosition++; // 다음 포지션으로 이동.
            } else {
                mCurrentPosition = 0; // 처음 포지션으로 이동.
                pause();
            }
        }
        play(mCurrentPosition);
        sendBroadcast(new Intent(BroadcastActions.PLAY_NEXT_SONG));
    }

    public void rewind() {
        if (mCurrentPosition > 0) {
            mCurrentPosition--; // 이전 포지션으로 이동.
        } else {
            mCurrentPosition = mAudioIds.size() - 1; // 마지막 포지션으로 이동.
        }
        play(mCurrentPosition);
        sendBroadcast(new Intent(BroadcastActions.PLAY_NEXT_SONG));
    }

    public Song getAudioItem() {
        return song;
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public long getPosition() {
        if(isPlaying())
            return mMediaPlayer.getCurrentPosition();
        else
            return 0;
    }

    public long getPositionWhenStopped() {
        if(isPrepared)
            return mMediaPlayer.getCurrentPosition();
        else
            return 0;
    }

    public long getDuration() {
        if(isPlaying())
            return mMediaPlayer.getDuration();
        else
            return 0;
    }

    public String getRemoteViewSizeState() {
        return remoteViewSize;
    }

    public long getDurationWhenStopped() {
        if(isPrepared)
            return mMediaPlayer.getDuration();
        else
            return 0;
    }

    public void setSeekTo(int progress){
        mMediaPlayer.seekTo(progress);
    }

    private NotificationPlayer mNotificationPlayer;

    private void updateNotificationPlayer() {
        if (mNotificationPlayer != null) {
            mNotificationPlayer.updateNotificationPlayer();
        }
    }

    private void removeNotificationPlayer() {
        if (mNotificationPlayer != null) {
            mNotificationPlayer.removeNotificationPlayer();
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ColorPlayer";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel("colorPlayerChannel", name, importance);
            channel.setVibrationPattern(new long[]{0});
            channel.enableVibration(true);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
