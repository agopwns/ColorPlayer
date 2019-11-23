package com.example.colorplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.example.colorplayer.model.Song;
import com.example.colorplayer.utils.RepeatActions;

import java.util.ArrayList;

public class AudioServiceInterface {

    private ServiceConnection mServiceConnection;
    private AudioService mService;

    public AudioServiceInterface(Context context) {
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = ((AudioService.AudioServiceBinder) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mServiceConnection = null;
                mService = null;
            }
        };
        context.bindService(new Intent(context, AudioService.class)
                .setPackage(context.getPackageName()), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void setPlayList(ArrayList<Long> audioIds) {
        if (mService != null) {
            mService.setPlayList(audioIds);
        }
    }

    public boolean getPreparedState(){
        if(mService != null)
            return mService.getPreparedState();
        else
            return false;
    }

    public void toggleShuffleList(){
        if(mService != null)
            mService.toggleShuffleList();
    }

    public boolean getShuffleState(){
        if(mService != null)
            return mService.getShuffleState();
        else
            return false;
    }

    public ArrayList<Long> getPlayList() {
        ArrayList<Long> returnList = new ArrayList<>();
        if (mService != null)
            return mService.getPlayList();
        else
            return returnList;
    }

    public int getSongPosition(){
        if(mService != null)
            return mService.getSongPosition();
        else
            return 0;
    }

    public void play(int position) {
        if (mService != null) {
            mService.play(position);
        }
    }

    public void playShuffledClick(int position) {
        if (mService != null) {
            mService.playShuffledClick(position);
        }
    }

    public void play() {
        if (mService != null) {
            mService.play();
        }
    }

    public void pause() {
        if (mService != null) {
            mService.pause();
        }
    }

    public void forward() {
        if (mService != null) {
            mService.forward();
        }
    }

    public void rewind() {
        if (mService != null) {
            mService.rewind();
        }
    }

    public void togglePlay() {
        if (isPlaying()) {
            mService.pause();
        } else {
            mService.play();
        }
    }

    public boolean isPlaying() {
        if (mService != null) {
            return mService.isPlaying();
        }
        return false;
    }

    public Song getAudioItem() {
        if (mService != null) {
            return mService.getAudioItem();
        }
        return null;
    }

    public long getPosition() {
        if (mService != null) {
            return mService.getPosition();
        }
        return 0;
    }

    public long getPositionWhenStopped() {
        if (mService != null) {
            return mService.getPositionWhenStopped();
        }
        return 0;
    }

    public long getDuration() {
        if (mService != null) {
            return mService.getDuration();
        }
        return 0;
    }

    public long getDurationWhenStopped() {
        if (mService != null) {
            return mService.getDurationWhenStopped();
        }
        return 0;
    }

    public void setSeekTo(int progress){
        if (mService != null) {
            mService.setSeekTo(progress);
        }
    }

    public void toggleRepeatState(){
        if (mService != null)
            mService.toggleRepeatState();
    }

    public String getRepeatState(){
        if (mService != null)
            return mService.getRepeatState();
        else
            return RepeatActions.REPEAT_ALL;
    }

}
