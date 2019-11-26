package com.example.colorplayer.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.colorplayer.AudioApplication;
import com.example.colorplayer.R;
import com.example.colorplayer.adapter.SelectListAdapter;
import com.example.colorplayer.dataloader.SongLoader;
import com.example.colorplayer.db.PlayListDB;
import com.example.colorplayer.db.PlayListDao;
import com.example.colorplayer.model.PlayList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.example.colorplayer.utils.IntentActions.PLAY_LIST_ID;

public class SelectSongActivity extends AppCompatActivity {

    ImageButton backButton, confirmButton;
    TextView textViewCurrentCount, textViewTotalCount;
    RecyclerView recyclerView;
    CheckBox allSelectCheckbox;
    private SelectListAdapter adapter;
    Long mId;
    private PlayListDao playListDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_song);

        if(getIntent().getExtras() != null){
            mId = getIntent().getExtras().getLong(PLAY_LIST_ID);
        }
        playListDao = PlayListDB.getInstance(this).playListDao();

        textViewCurrentCount = findViewById(R.id.tv_current_count);
        textViewTotalCount = findViewById(R.id.tv_total_count);

        recyclerView = findViewById(R.id.recyclerView_playing_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        confirmButton = findViewById(R.id.button_confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 기존 곡 불러오기
                Gson gson = new Gson();
                PlayList originList = playListDao.getPlayListById(mId);
                String json = originList.getSongIdList();
                Type listType = new TypeToken<ArrayList<String>>() {}.getType();
                List<String> temp = gson.fromJson(json, listType);

                // List<String> -> List<long>
                ArrayList<Long> songIdList = new ArrayList<>();
                // 기존 곡 추가
                if(temp != null){
                    for(int i = 0; i < temp.size(); i++){
                        songIdList.add(Long.parseLong(temp.get(i)));
                    }
                }
                // 선택한 곡 추가
                ArrayList<Long> result = adapter.getSongIdsList();
                for(int i = 0; i < result.size(); i++){
                    songIdList.add(result.get(i));
                }
                // 저장
                json = gson.toJson(songIdList);
                originList.setSongIdList(json);
                playListDao.updatePlayList(originList);
                // 추가한 목록 플레이
                AudioApplication.getInstance().getServiceInterface().setPlayList(songIdList);
                AudioApplication.getInstance().getServiceInterface().setSongPosition(0);
                //AudioApplication.getInstance().getServiceInterface().play(0);
                // 액티비티 종료
                finish();
            }
        });

        allSelectCheckbox = findViewById(R.id.checkbox_select_all);
        allSelectCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(allSelectCheckbox.isChecked()){
                    adapter.selectAll();
                } else {
                    adapter.unselectall();
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        new loadSongs().execute("");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO : onResume 에서 해서 이제껏 느렸던 걸까? 속도 테스트 해보자
//        new loadSongs().execute("");
    }

    private class loadSongs extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                if (this != null)
                    adapter = new SelectListAdapter(SelectSongActivity.this
                            , SongLoader.getAllSongs(getApplicationContext())
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
