package com.example.colorplayer.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.colorplayer.R;
import com.example.colorplayer.adapter.ArtistAdapter;
import com.example.colorplayer.adapter.SongAdapter;
import com.example.colorplayer.model.Artist;
import com.example.colorplayer.model.Song;

import java.util.ArrayList;


public class ArtistListFragment extends Fragment {
    ViewPager viewPager;
    RecyclerView recyclerView;
    private ArtistAdapter adapter;
    private ArrayList<Artist> list = new ArrayList<>();

    public ArtistListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_artist_list);
        list.add(new Artist(1,"가수 이름", 100, 100));
        list.add(new Artist(1,"가수 이름", 100, 100));
        list.add(new Artist(1,"가수 이름", 100, 100));
        list.add(new Artist(1,"가수 이름", 100, 100));
        list.add(new Artist(1,"가수 이름", 100, 100));
        list.add(new Artist(1,"가수 이름", 100, 100));
        list.add(new Artist(1,"가수 이름", 100, 100));
        list.add(new Artist(1,"가수 이름", 100, 100));
        list.add(new Artist(1,"가수 이름", 100, 100));
        list.add(new Artist(1,"가수 이름", 100, 100));

        recyclerView.setHasFixedSize(true);
        adapter = new ArtistAdapter(getActivity(), list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        Log.d("Frag", "ArtistFragment");
        return view;

    }
}