package com.example.colorplayer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.colorplayer.AudioApplication;
import com.example.colorplayer.BroadcastActions;
import com.example.colorplayer.R;
import com.example.colorplayer.model.Song;
import com.example.colorplayer.utils.Time;

public class NowPlayingActivity extends AppCompatActivity {

    ImageButton backButton, optionButton,
            repeatButton, prevButton, playButton, nextButton, randomButton;
    ImageView albumArt;
    TextView title, artist, duration, totalTime;
    Song song;
    SeekBar seekBar;
    private int overflowcounter = 0;
    private boolean isActivityPaused = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);

        // 음악 재생은 AudioApplication 의 서비스로 작동
        // TODO : foreground 도 작성해야 앱이 꺼져도 살아있음
        albumArt = findViewById(R.id.album_art);
        title = findViewById(R.id.music_title);
        artist = findViewById(R.id.artist_name);
        duration = findViewById(R.id.song_start_time);
        totalTime = findViewById(R.id.song_end_time);
        seekBar = findViewById(R.id.song_progress_normal);

        registerBroadcast();
        // 처음 액티비티 진입시 현재 재생 곡 데이터 바인딩
        updateUI();

        // 재생 and 일시정지
        playButton = findViewById(R.id.button_play);
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
        prevButton = findViewById(R.id.button_prev);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AudioApplication.getInstance().getServiceInterface().rewind();
                updateUI();

//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        AudioApplication.getInstance().getServiceInterface().rewind();
//                        updateUI();
//                        if (seekBar != null){
//                            overflowcounter = 0;
//                            seekBar.postDelayed(mUpdateProgress, 10);
//                        }
//                    }
//                }, 200);


            }
        });

        // 다음 곡 재생
        nextButton = findViewById(R.id.button_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioApplication.getInstance().getServiceInterface().forward();
                updateUI();

//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        AudioApplication.getInstance().getServiceInterface().forward();
//                        updateUI();
//                        if (seekBar != null){
//                            overflowcounter = 0;
//                            seekBar.postDelayed(mUpdateProgress, 10);
//                        }
//                    }
//                }, 200);
            }
        });

        // 진행 시크바 움직일 때 서비스에도 설정해줘야 함
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(AudioApplication.getInstance() != null && fromUser){
                    AudioApplication.getInstance().getServiceInterface().setSeekTo(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (seekBar != null){
            isActivityPaused = false;
            seekBar.postDelayed(mUpdateProgress, 10);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isActivityPaused = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
    }

    private void updateUI() {
        if(AudioApplication.getInstance() != null){
            song = AudioApplication.getInstance().getServiceInterface().getAudioItem();

            // 앨범 이미지 로드
            Uri uri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), song.albumId);
            Glide
                    .with(this)
                    .load(uri)
                    .error(R.drawable.test)
                    .into(albumArt);

            // 타이틀, 아티스트
            title.setText(song.title);
            artist.setText(song.artistName);
        }
    }

    public Runnable mUpdateProgress = new Runnable() {

        @Override
        public void run() {

            long position = AudioApplication.getInstance().getServiceInterface().getPosition();
            long maxDuration = AudioApplication.getInstance().getServiceInterface().getDuration();

            if (seekBar != null && maxDuration != 0) {

                seekBar.setMax((int)maxDuration);
                seekBar.setProgress((int) position);

                // TODO : 초 단위 변경
                totalTime.setText(Time.makeShortTimeString(getApplication(), maxDuration / 1000));

                if (duration != null && this != null)
                    duration.setText(Time.makeShortTimeString(getApplication(), position / 1000));
            }
            overflowcounter--;
            // 서비스가 준비됬는지를 꼼꼼히 확인하면
            // 딜레이를 높게 잡지 않아도 된다.
            int delay = 10;
            if (overflowcounter < 0 && !isActivityPaused) {
                overflowcounter++;
                seekBar.postDelayed(mUpdateProgress, delay);
            }
        }
    };

    // 재생 상태 변경을 받는 브로드캐스트 리시버
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI();
        }
    };

    public void registerBroadcast(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastActions.PLAY_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver, filter);
    }

    public void unregisterBroadcast(){
        unregisterReceiver(mBroadcastReceiver);
    }

}
