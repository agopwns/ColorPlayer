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
            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isPrepared = false;
            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                isPrepared = false;
                return false;
            }
        });
        mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                forward();
                play();
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
        }
    }

    // play 기능
    public void play(int position) {
        queryAudioItem(position);
        //song = SongLoader.getSongForID(getApplicationContext(), position);
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




}
