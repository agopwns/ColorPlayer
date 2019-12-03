package com.example.colorplayer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.example.colorplayer.R;

import static com.example.colorplayer.utils.IntentActions.YOUTUBE_SEARCH_KEYWORD;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer.*;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class YoutubeSearchResultActivity extends AppCompatActivity {

    String mKeyword;
    YouTubePlayerView youtubeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_search_result);

        if(getIntent().getExtras() != null){
            mKeyword = getIntent().getExtras().getString(YOUTUBE_SEARCH_KEYWORD);
        }

        // youtube player 재생
//        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view);
//        getLifecycle().addObserver(youTubePlayerView);
//
//        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
//            @Override
//            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
//                String videoId = "S0Q4gqBUs7c";
//                youTubePlayer.loadVideo(videoId, 0);
//            }
//        });



    }
}
