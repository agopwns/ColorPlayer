package com.example.colorplayer.fragment;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.colorplayer.R;
import com.example.colorplayer.adapter.SongAdapter;
import com.example.colorplayer.dataloader.SongLoader;
import com.example.colorplayer.model.Song;

import java.util.ArrayList;
import java.util.List;


public class SongListFragment extends Fragment {
    ViewPager viewPager;
    RecyclerView recyclerView;
    private SongAdapter adapter;
    private ArrayList<Song> list = new ArrayList<>();

    public SongListFragment() {


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_music_list);

        recyclerView.setHasFixedSize(true);
        //adapter = new SongAdapter(getActivity(), list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        new loadSongs().execute("");
        recyclerView.setAdapter(adapter);
        Log.d("Frag", "MainFragment");

        return view;
    }

//    private void reloadAdapter() {
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(final Void... unused) {
//                List<Song> songList = SongLoader.getAllSongs(getActivity());
//                adapter.updateDataSet(songList);
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                adapter.notifyDataSetChanged();
//            }
//        }.execute();
//    }

    private class loadSongs extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (getActivity() != null)
                adapter = new SongAdapter((AppCompatActivity) getActivity(), SongLoader.getAllSongs(getActivity()), false, false);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            recyclerView.setAdapter(adapter);
//            if (getActivity() != null)
//                recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        }

        @Override
        protected void onPreExecute() {
        }
    }
}