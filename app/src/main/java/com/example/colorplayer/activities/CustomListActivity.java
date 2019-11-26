package com.example.colorplayer.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.colorplayer.AudioApplication;
import com.example.colorplayer.R;
import com.example.colorplayer.adapter.SongAdapter;
import com.example.colorplayer.db.PlayListDB;
import com.example.colorplayer.db.PlayListDao;
import com.example.colorplayer.model.PlayList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.example.colorplayer.utils.IntentActions.PLAY_LIST_ID;

public class CustomListActivity extends AppCompatActivity {

    ImageButton backButton, addButton;
    TextView textViewCurrentCount, textViewTotalCount;
    RecyclerView recyclerView;
    private SongAdapter adapter;
    Long mId;
    PlayListDao playListDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_playing_list);

        if(getIntent().getExtras() != null){
            mId = getIntent().getExtras().getLong(PLAY_LIST_ID);
        }
        playListDao = PlayListDB.getInstance(this).playListDao();

        textViewCurrentCount = findViewById(R.id.tv_current_count);

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

        addButton = findViewById(R.id.button_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveIntent = new Intent(getApplicationContext(), SelectSongActivity.class);
                moveIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if(mId != null)
                    moveIntent.putExtra(PLAY_LIST_ID, mId);
                startActivity(moveIntent);
            }
        });

        loadSongs(this != null);
    }

    private void loadSongs(boolean b) {
        Gson gson = new Gson();
        PlayList originList = playListDao.getPlayListById(mId);
        String json = originList.getSongIdList();
        Type listType = new TypeToken<ArrayList<String>>() {
        }.getType();
        List<String> temp = gson.fromJson(json, listType);

        // List<String> -> List<long>
        ArrayList<Long> songIdList = new ArrayList<>();
        // 기존 곡 추가
        if (temp != null) {
            for (int i = 0; i < temp.size(); i++) {
                songIdList.add(Long.parseLong(temp.get(i)));
            }
        }
        AudioApplication.getInstance().getServiceInterface().setPlayList(songIdList);
        AudioApplication.getInstance().getServiceInterface().setSongPosition(0);

        textViewCurrentCount.setText("" + songIdList.size());

        if (b && songIdList != null)
            adapter = new SongAdapter(CustomListActivity.this
                    , AudioApplication.getInstance().getServiceInterface().getPlayingListByCurrentIdList()
                    , false, false);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 기존
        //new loadSongs().execute("");
        // TODO : 곡 개수에 맞게 변경 필요
        //textViewCurrentCount.setText("" + (AudioApplication.getInstance().getServiceInterface().getTempCurrentPosition() + 1));
        //textViewTotalCount.setText("" + AudioApplication.getInstance().getServiceInterface().getPlayingListCount());
    }

    private class loadSongs extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                loadSongs(this != null);
            } catch (Exception e){
                Log.d("PlayingListActivity", "loadSongs 오류 발생 : " + e);
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if(adapter != null)
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