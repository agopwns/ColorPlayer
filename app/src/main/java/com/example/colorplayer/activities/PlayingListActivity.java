package com.example.colorplayer.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.example.colorplayer.AudioApplication;
import com.example.colorplayer.AudioServiceInterface;
import com.example.colorplayer.R;
import com.example.colorplayer.adapter.SongAdapter;
import com.example.colorplayer.dataloader.SongLoader;
import com.example.colorplayer.model.Song;

import java.util.ArrayList;

public class PlayingListActivity extends AppCompatActivity {

    ImageButton backButton;
    TextView textViewCurrentCount, textViewTotalCount;
    RecyclerView recyclerView;
    private SongAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing_list);

        textViewCurrentCount = findViewById(R.id.tv_current_count);
        textViewTotalCount = findViewById(R.id.tv_total_count);

        recyclerView = findViewById(R.id.recyclerView_playing_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 변경
        adapter = new SongAdapter(PlayingListActivity.this
                , AudioApplication.getInstance().getServiceInterface().getPlayingListByCurrentIdList()
                , false, false);
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 기존
//        new loadSongs().execute("");
        textViewCurrentCount.setText("" + (AudioApplication.getInstance().getServiceInterface().getTempCurrentPosition() + 1));
        textViewTotalCount.setText("" + AudioApplication.getInstance().getServiceInterface().getPlayingListCount());
    }

    private class loadSongs extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                if (this != null)
                    adapter = new SongAdapter(PlayingListActivity.this
                            , AudioApplication.getInstance().getServiceInterface().getPlayingListByCurrentIdList()
                            , false, false);
            } catch (Exception e){
                Log.d("PlayingListActivity", "loadSongs 오류 발생 : " + e);
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                recyclerView.setAdapter(adapter);
            } catch (Exception e){
                Log.d("PlayingListActivity", "onPostExecute 오류 발생 : " + e);
            }

        }

        @Override
        protected void onPreExecute() {
        }
    }
}
