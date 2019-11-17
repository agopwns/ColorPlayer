package com.example.colorplayer.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.colorplayer.R;
import com.example.colorplayer.adapter.SongAdapter;
import com.example.colorplayer.model.Song;

import java.util.ArrayList;


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
        list.add(new Song("노래 제목", "가수 이름", "앨범명"));
        list.add(new Song("노래 제목", "가수 이름", "앨범명"));
        list.add(new Song("노래 제목", "가수 이름", "앨범명"));
        list.add(new Song("노래 제목", "가수 이름", "앨범명"));
        list.add(new Song("노래 제목", "가수 이름", "앨범명"));
        list.add(new Song("노래 제목", "가수 이름", "앨범명"));
        list.add(new Song("노래 제목", "가수 이름", "앨범명"));
        list.add(new Song("노래 제목", "가수 이름", "앨범명"));
        list.add(new Song("노래 제목", "가수 이름", "앨범명"));
        list.add(new Song("노래 제목", "가수 이름", "앨범명"));
        list.add(new Song("노래 제목", "가수 이름", "앨범명"));
        list.add(new Song("노래 제목", "가수 이름", "앨범명"));
        list.add(new Song("노래 제목", "가수 이름", "앨범명"));

        recyclerView.setHasFixedSize(true);
        adapter = new SongAdapter(getActivity(), list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        Log.d("Frag", "MainFragment");

        return view;
    }
}