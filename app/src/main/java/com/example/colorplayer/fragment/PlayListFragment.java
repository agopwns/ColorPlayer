package com.example.colorplayer.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.colorplayer.R;
import com.example.colorplayer.adapter.AlbumAdapter;
import com.example.colorplayer.adapter.PlayListAdapter;
import com.example.colorplayer.dataloader.AlbumLoader;
import com.example.colorplayer.db.PlayListDB;
import com.example.colorplayer.db.PlayListDao;
import com.example.colorplayer.utils.PreferencesUtility;


public class PlayListFragment extends Fragment {

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
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_play_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // 기존
        playListDao = PlayListDB.getInstance(getActivity()).playListDao();
//        if (getActivity() != null)
//            new loadAlbums().execute("");
        mAdapter = new PlayListAdapter(getActivity(), playListDao.getPlayList());
        recyclerView.setAdapter(mAdapter);
        return view;
    }

    private class loadAlbums extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            if (getActivity() != null)
                mAdapter = new PlayListAdapter(getActivity(), playListDao.getPlayList());
            return "Executed";
        }
        @Override
        protected void onPostExecute(String result) {
            recyclerView.setAdapter(mAdapter);
            //to add spacing between cards
        }
        @Override
        protected void onPreExecute() {
        }
    }
}