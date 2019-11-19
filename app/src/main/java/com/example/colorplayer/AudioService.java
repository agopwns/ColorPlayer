package com.example.colorplayer;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;

import com.example.colorplayer.adapter.SongAdapter;
import com.example.colorplayer.dataloader.SongLoader;
import com.example.colorplayer.model.Song;

import java.util.ArrayList;

public class AudioService extends Service {

    private final IBinder mBinder = new AudioServiceBinder();
    private MediaPlayer mMediaPlayer;
    private boolean isPrepared;

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
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                isPrepared = true;
                mp.start();
                // 준비 상태를 NowPlayingActivity 에 알리기 위함
                sendBroadcast(new Intent(BroadcastActions.PREPARED));
            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isPrepared = false;
                // 재생 상태 변경을 알려서 NowPlayingActivity 에서 UI 업데이트를 하기 위함
                forward();
                sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED));
            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                isPrepared = false;

                // Warning : ViewPager 에서 미디어 플레이어 사용시 오류가 난다는 상황을 많이 발견
                // 에러시 다음 곡을 넘어가는 것을 방지하기 위해 다시 재생
                mCurrentPosition--;
                Log.d("AudioService", "setOnErrorListener 에러 발생");
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
    public void setPlayList(ArrayList<Long> audioIds) {
        if (mAudioIds.size() != audioIds.size()) {
            if (!mAudioIds.equals(audioIds)) {
                mAudioIds.clear();
                mAudioIds.addAll(audioIds);
            }
        }
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

    public void play() {
        if (isPrepared) {
            mMediaPlayer.start();
        }
    }

    public void pause() {
        if (isPrepared) {
            mMediaPlayer.pause();
        }
    }

    private void stop() {
        mMediaPlayer.stop();
        mMediaPlayer.reset();
    }

    public void forward() {
        if (mAudioIds.size() - 1 > mCurrentPosition) {
            mCurrentPosition++; // 다음 포지션으로 이동.
        } else {
            mCurrentPosition = 0; // 처음 포지션으로 이동.
        }
        play(mCurrentPosition);
    }

    public void rewind() {
        if (mCurrentPosition > 0) {
            mCurrentPosition--; // 이전 포지션으로 이동.
        } else {
            mCurrentPosition = mAudioIds.size() - 1; // 마지막 포지션으로 이동.
        }
        play(mCurrentPosition);
    }

    public Song getAudioItem() {
        return song;
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public long getPosition() {
        if(mMediaPlayer.isPlaying())
            return mMediaPlayer.getCurrentPosition();
        else
            return 0;
    }

    public long getDuration() {
        if(mMediaPlayer.isPlaying())
            return mMediaPlayer.getDuration();
        else
            return 0;
    }

    public void setSeekTo(int progress){
        mMediaPlayer.seekTo(progress);
    }




}
