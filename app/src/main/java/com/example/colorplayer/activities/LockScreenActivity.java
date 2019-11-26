package com.example.colorplayer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.colorplayer.AudioApplication;
import com.example.colorplayer.R;
import com.example.colorplayer.model.Song;
import com.example.colorplayer.model.SongInfo;
import com.example.colorplayer.utils.BroadcastActions;
import com.example.colorplayer.utils.Time;

public class LockScreenActivity extends AppCompatActivity {
    ImageButton prevButton, playButton, nextButton;
    TextView title, artist;
    Song song;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);



        // 풀 스크린
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 액티비티를 최상위로
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        setShowWhenLocked(true);
        //setTurnScreenOn(true);

        // 재생 상태 브로드 캐스트
        registerBroadcast();

        // 액티비티 생성시 현재 플레이 중인 곡 데이터 바인딩
        title = findViewById(R.id.lock_title);
        artist = findViewById(R.id.lock_artist);

        // 현재 재생 중인 곡 정보 있을 시 업데이트
        song = AudioApplication.getInstance().getServiceInterface().getAudioItem();
        if(song != null){
            // width 보다 길 경우 텍스트 움직임 설정을 위해
            title.setSelected(true);
            title.setText(song.title);
            artist.setText(song.artistName);
        }

        // 재생 and 일시정지
        playButton = findViewById(R.id.lock_btn_play_pause);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AudioApplication.getInstance().getServiceInterface().isPlaying())
                    playButton.setImageResource(R.drawable.baseline_play_arrow_white_36);
                else
                    playButton.setImageResource(R.drawable.baseline_pause_white_36);

                AudioApplication.getInstance().getServiceInterface().togglePlay();
            }
        });

        // 이전 곡 재생
        prevButton = findViewById(R.id.lock_btn_rewind);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioApplication.getInstance().getServiceInterface().rewind();
                //updateUI();
            }
        });

        // 다음 곡 재생
        nextButton = findViewById(R.id.lock_btn_forward);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioApplication.getInstance().getServiceInterface().forward();
                //updateUI();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUIPlayPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
    }

    private void updateUIPlayPause() {
        if(AudioApplication.getInstance() == null || playButton == null) return;
        if (AudioApplication.getInstance().getServiceInterface().isPlaying())
            playButton.setImageResource(R.drawable.baseline_pause_white_36);
        else
            playButton.setImageResource(R.drawable.baseline_play_arrow_white_36);
    }

    private void updateUINextSong() {
        if(AudioApplication.getInstance() == null || playButton == null) return;

        song = AudioApplication.getInstance().getServiceInterface().getAudioItem();
        if(AudioApplication.getInstance().getServiceInterface().isPlaying())
            playButton.setImageResource(R.drawable.baseline_pause_white_36);
        else
            playButton.setImageResource(R.drawable.baseline_play_arrow_white_36);

        // 타이틀, 아티스트
        if(title == null || artist == null || song == null) return;
        title.setText(song.title);
        artist.setText(song.artistName);
    }

    private void updateUINextSongWhenStop() {
        if(AudioApplication.getInstance() == null || playButton == null) return;
        song = AudioApplication.getInstance().getServiceInterface().getAudioItem();
        if(AudioApplication.getInstance().getServiceInterface().isPlaying())
            playButton.setImageResource(R.drawable.baseline_play_arrow_white_36);
        else
            playButton.setImageResource(R.drawable.baseline_pause_white_36);

        // 타이틀, 아티스트
        if(title == null || artist == null || song == null) return;
        title.setText(song.title);
        artist.setText(song.artistName);
    }

    private void updateUIEndToFirst() {
        if(AudioApplication.getInstance() == null || playButton == null) return;
        playButton.setImageResource(R.drawable.baseline_play_arrow_white_36);
        song = AudioApplication.getInstance().getServiceInterface().getAudioItem();

        // 타이틀, 아티스트
        if(title == null || artist == null || song == null) return;
        title.setText(song.title);
        artist.setText(song.artistName);
    }

    // 재생 상태 변경을 받는 브로드캐스트 리시버
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(BroadcastActions.PLAY_STATE_CHANGED)) {
                updateUIPlayPause();
            } else if(intent.getAction().equals(BroadcastActions.PLAY_NEXT_SONG)) {
                if(AudioApplication.getInstance().getServiceInterface().isPlaying())
                    updateUINextSong();
                else
                    updateUINextSongWhenStop();
            } else if(intent.getAction().equals(BroadcastActions.STOPPED)) {
                // 시작 초, duration, 시크바 초기화
                updateUIEndToFirst();
                AudioApplication.getInstance().getServiceInterface().tempPause();
            }
        }
    };

    public void registerBroadcast(){
        IntentFilter filterPlayState = new IntentFilter();
        filterPlayState.addAction(BroadcastActions.PLAY_STATE_CHANGED);

        IntentFilter filterNext = new IntentFilter();
        filterNext.addAction(BroadcastActions.PLAY_NEXT_SONG);

        IntentFilter filterStop = new IntentFilter();
        filterStop.addAction(BroadcastActions.STOPPED);

        registerReceiver(mBroadcastReceiver, filterPlayState);
        registerReceiver(mBroadcastReceiver, filterNext);
        registerReceiver(mBroadcastReceiver, filterStop);
    }

    public void unregisterBroadcast(){
        unregisterReceiver(mBroadcastReceiver);
    }
}
