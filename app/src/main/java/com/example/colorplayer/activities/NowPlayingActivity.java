package com.example.colorplayer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.colorplayer.AudioApplication;
import com.example.colorplayer.R;
import com.example.colorplayer.model.Song;

public class NowPlayingActivity extends AppCompatActivity {

    ImageButton backButton, optionButton,
            repeatButton, prevButton, playButton, nextButton, randomButton;
    ImageView albumArt;
    TextView title, artist, duration;
    Song song;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);

        // 음악 재생은 AudioApplication 의 서비스로 작동
        // TODO : foreground 도 작성해야 앱이 꺼져도 살아있음
        albumArt = findViewById(R.id.album_art);
        title = findViewById(R.id.music_title);
        artist = findViewById(R.id.artist_name);
        duration = findViewById(R.id.song_end_time);

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
                AudioApplication.getInstance().getServiceInterface().play();
            }
        });

        // 다음 곡 재생
        nextButton = findViewById(R.id.button_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioApplication.getInstance().getServiceInterface().forward();
                updateUI();
                AudioApplication.getInstance().getServiceInterface().play();
            }
        });
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
            duration.setText("" + song.duration);
        }
    }



}
