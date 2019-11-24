package com.example.colorplayer.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.colorplayer.R;
import com.example.colorplayer.adapter.ArtistAdapter;
import com.example.colorplayer.adapter.SongAdapter;
import com.example.colorplayer.dataloader.ArtistLoader;
import com.example.colorplayer.model.Artist;
import com.example.colorplayer.model.Song;
import com.example.colorplayer.utils.PreferencesUtility;

import java.util.ArrayList;


public class ArtistListFragment extends Fragment {

    private ArtistAdapter mAdapter;
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private RecyclerView.ItemDecoration itemDecoration;
    private PreferencesUtility mPreferences;
    private boolean isGrid;

    public ArtistListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_artist_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mAdapter);

        Log.d("Frag", "ArtistFragment");
        if (getActivity() != null)
            new loadArtists().execute("");
        return view;

    }

    private class loadArtists extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (getActivity() != null)
                mAdapter = new ArtistAdapter(getActivity(), ArtistLoader.getAllArtists(getActivity()));
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            if (mAdapter != null) {
                //mAdapter.setHasStableIds(true);
                recyclerView.setAdapter(mAdapter);
            }
            if (getActivity() != null) {
                //setItemDecoration();
            }
        }

        @Override
        protected void onPreExecute() {
        }
    }
}