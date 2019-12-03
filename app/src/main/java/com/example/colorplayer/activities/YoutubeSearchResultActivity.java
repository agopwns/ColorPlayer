package com.example.colorplayer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.colorplayer.R;

import static com.example.colorplayer.utils.IntentActions.YOUTUBE_SEARCH_KEYWORD;

import com.example.colorplayer.adapter.VideoListAdapter;
import com.example.colorplayer.youtube.YoutubeSearchHelper;
import com.example.colorplayer.youtube.YoutubeVideoItem;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer.*;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;

public class YoutubeSearchResultActivity extends AppCompatActivity {

    String mKeyword;
    YouTubePlayerView youtubeView;
    VideoListAdapter mAdapter;
    TextView keywordTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_search_result);

        if(getIntent().getExtras() != null){
            mKeyword = getIntent().getExtras().getString(YOUTUBE_SEARCH_KEYWORD);
        }

        keywordTitle = findViewById(R.id.search_keyword);
        keywordTitle.setText(mKeyword);

        RecyclerView rv = findViewById(R.id.recyclerView_search_result_list);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext()
                , LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(manager);
        mAdapter = new VideoListAdapter(getApplicationContext(), getLifecycle(),null);
        rv.setAdapter(mAdapter);
        startLoadingVideos();
    }

    private void startLoadingVideos () {

        YoutubeSearchHelper h = new YoutubeSearchHelper();

        h.searchYoutube(mKeyword, new YoutubeSearchHelper.OnSearchCompleteListener() {
            @Override
            public void onSearchComplete(ArrayList<YoutubeVideoItem> videos) {

                mAdapter.setItems(videos);

//                if (mSwipe.isRefreshing()) {
//                    mSwipe.setRefreshing(false);
//                }
            }

            @Override
            public void onGetLikesTaskFinished(ArrayList<YoutubeVideoItem> videos) {

                mAdapter.setItems(videos);
//
//                if (mSwipe.isRefreshing()) {
//                    mSwipe.setRefreshing(false);
//                }
            }
        });

    }
}
