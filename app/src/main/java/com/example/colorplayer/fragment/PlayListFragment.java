package com.example.colorplayer.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.colorplayer.AudioApplication;
import com.example.colorplayer.R;
import com.example.colorplayer.activities.CustomListActivity;
import com.example.colorplayer.adapter.PlayListAdapter;
import com.example.colorplayer.callback.PlayListEventListener;
import com.example.colorplayer.db.PlayListDB;
import com.example.colorplayer.db.PlayListDao;
import com.example.colorplayer.model.PlayList;
import com.example.colorplayer.utils.BroadcastActions;
import com.example.colorplayer.utils.PreferencesUtility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.example.colorplayer.utils.IntentActions.PLAY_LIST_ID;
import static com.example.colorplayer.utils.IntentActions.PLAY_LIST_TITLE;


public class PlayListFragment extends Fragment implements PlayListEventListener{

    private PlayListAdapter mAdapter;
    private RecyclerView recyclerView;
    private PreferencesUtility mPreferences;
    private PlayListDao playListDao;

    public PlayListFragment() {

    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = PreferencesUtility.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_list, container, false);
        registerBroadcast();
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_play_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // 기존
        playListDao = PlayListDB.getInstance(getActivity()).playListDao();
//        if (getActivity() != null)
//            new loadAlbums().execute("");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPlayList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
    }

    private void loadPlayList() {
        mAdapter = new PlayListAdapter(getActivity(), playListDao.getPlayList());
        mAdapter.setListener(this);
        recyclerView.setAdapter(mAdapter);
    }


    @Override
    public void onPlayListClick(PlayList playList) {
        Intent moveIntent = new Intent(getActivity(), CustomListActivity.class);
        try {
            // 1. 현재 재생 목록 id 에 해당하는 곡 리스트 DB 에서 가져오기
            int id = (int) playList.getId();
            PlayList list = new PlayList();
            playListDao = PlayListDB.getInstance(getActivity()).playListDao();
            list = playListDao.getPlayListById(id);

            // 2. JSON 파싱
            String json = list.getSongIdList();
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<String>>() {}.getType();
            List<String> temp = gson.fromJson(json, listType);

            // 3. List<String> -> List<long>
            ArrayList<Long> songIdList = new ArrayList<>();
            if(temp != null){
                for(int i = 0; i < temp.size(); i++){
                    songIdList.add(Long.parseLong(temp.get(i)));
                }
            }
            // 4. 재생 목록 세팅
//                if(songIdList != null && songIdList.size() > 0)
//                AudioApplication.getInstance().getServiceInterface().setPlayList(songIdList);

            // 5. 만약 개수가 0보다 크면 플레이
//                AudioApplication.getInstance().getServiceInterface().setSongPosition(0);
//                AudioApplication.getInstance().getServiceInterface().play(0);

        } catch(Exception e){
            Log.d("PlayListHolder", "onClick 에러 발생 : " + e);
        }
        moveIntent.putExtra(PLAY_LIST_ID, playList.getId());
        moveIntent.putExtra(PLAY_LIST_TITLE, playList.getTitle());
        moveIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if(AudioApplication.getInstance().getServiceInterface().getPreparedState())
            getActivity().startActivity(moveIntent);
    }

    @Override
    public void onPlayListLongClick(final PlayList playList) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.app_name)
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                playListDao.deletePlayList(playList);
                loadPlayList();
            }
        })
                .create()
                .show();
    }

    // 재생 목록 상태 변경을 받는 브로드캐스트 리시버
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(BroadcastActions.PLAY_LIST_ADD)) {
                loadPlayList();
            }
        }
    };

    public void registerBroadcast(){
        IntentFilter filterPlayState = new IntentFilter();
        filterPlayState.addAction(BroadcastActions.PLAY_LIST_ADD);

        getActivity().registerReceiver(mBroadcastReceiver, filterPlayState);
    }

    public void unregisterBroadcast(){
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

//    private class loadAlbums extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... params) {
//            if (getActivity() != null)
//                mAdapter = new PlayListAdapter(getActivity(), playListDao.getPlayList());
//            return "Executed";
//        }
//        @Override
//        protected void onPostExecute(String result) {
//            recyclerView.setAdapter(mAdapter);
//            //to add spacing between cards
//        }
//        @Override
//        protected void onPreExecute() {
//        }
//    }
}