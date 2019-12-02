package com.example.colorplayer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.colorplayer.R;

import static com.example.colorplayer.utils.AES256Chiper.AES_Encode;
import static com.example.colorplayer.utils.IntentActions.YOUTUBE_SEARCH_KEYWORD;
import static com.example.colorplayer.utils.urlUtils.AWS_MEMBER_URL;

import com.example.colorplayer.adapter.VideoListAdapter;
import com.example.colorplayer.http.NullOnEmptyConverterFactory;
import com.example.colorplayer.http.OpenApiService;
import com.example.colorplayer.http.YoutubeSearchApiService;
import com.example.colorplayer.model.Member;
import com.example.colorplayer.model.YoutubeVideoItem;
import com.example.colorplayer.youtube.YoutubeSearchHelper;
import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer.*;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class YoutubeSearchResultActivity extends AppCompatActivity {

    String mKeyword;
    YouTubePlayerView youtubeView;
    YoutubeSearchApiService apiService;
    private static String TAG = "YoutubeSearchResultActivity";
    ArrayList<YoutubeVideoItem> mVideos;
    VideoListAdapter mAdapter;

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

        // 기존 restful 예시
//        String url = "https://www.googleapis.com";
//
//        Retrofit retrofit =
//                new Retrofit.Builder()
//                        .baseUrl(url)
//                        .addConverterFactory(new NullOnEmptyConverterFactory())
//                        .addConverterFactory(GsonConverterFactory.create()).build();
//
//        apiService = retrofit.create(YoutubeSearchApiService.class);
//        Call<Object> res = apiService.getVideoIdsByKeyword("id", mKeyword, "video",
//                "AIzaSyBc0My6KMhjGR4YKiB3zjqIQgdev-BFDi4");
//
//        try {
//            res.enqueue(new Callback<Object>() {
//                @Override
//                public void onResponse(Call<Object> call, Response<Object> response) {
//                    Log.d(TAG, response.body().toString());
//
//                    mVideos = new ArrayList<>();
//
//                    // JSON 파싱
//                    try {
//                        JSONObject o1 = new JSONObject(response. body(). toString());
//                        JSONArray a1 = o1.getJSONArray("items");
//                        mVideos.clear();
//
//                        for (int i = 0; i < a1.length(); i++) {
//                            JSONObject o2 = a1.getJSONObject(i);
//                            String videoId = o2.getJSONObject("id").getString("videoId");
//                            String title = o2.getJSONObject("snippet").getString("title");
//                            String thumbnailUrl = o2.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("medium").getString("url");
//                            YoutubeVideoItem video = new YoutubeVideoItem(title, videoId, thumbnailUrl, 0);
//                            mVideos.add(video);
//                        }
//                        Log.d(TAG, "파싱 성공 발생");
//                    } catch (JSONException e) {
//                        Log.d(TAG, "파싱 실패 발생 : " + e.toString());
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<Object> call, Throwable t) {
//                    Toast.makeText(getApplicationContext(), "통신 실패 발생", Toast.LENGTH_SHORT ).show();
//                    Log.d(TAG, "통신 실패 발생 : " + t.toString());
//                }
//            });
//        } catch (Exception e) {
//            Log.d(TAG, "통신 에러 발생 : " + e);
//        }
        RecyclerView rv = findViewById(R.id.recyclerView_search_result_list);

        LinearLayoutManager manager =
                new LinearLayoutManager(getApplicationContext(),
                        LinearLayoutManager.VERTICAL, false);

        rv.setLayoutManager(manager);

        mAdapter = new VideoListAdapter(getApplicationContext(), getLifecycle(), null);

        rv.setAdapter(mAdapter);

        startLoadingVideos(mKeyword);


    }

    private void startLoadingVideos (String keyword) {

        YoutubeSearchHelper h = new YoutubeSearchHelper();

        h.searchYoutube(keyword, new YoutubeSearchHelper.OnSearchCompleteListener() {
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

//                if (mSwipe.isRefreshing()) {
//                    mSwipe.setRefreshing(false);
//                }
            }
        });

    }
}
