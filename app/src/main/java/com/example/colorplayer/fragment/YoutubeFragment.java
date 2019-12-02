package com.example.colorplayer.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.colorplayer.R;
import com.example.colorplayer.adapter.SongAdapter;
import com.example.colorplayer.dataloader.SongLoader;
import com.example.colorplayer.model.Song;

import java.util.ArrayList;

public class YoutubeFragment extends Fragment{
    ViewPager viewPager;
    RecyclerView recyclerView;
    private SongAdapter adapter;
    private ArrayList<Song> list = new ArrayList<>();

    public YoutubeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_youtube, container, false);

        // TODO : 검색 버튼 클릭시 인텐트로 검색 결과 화면 넘기기


        return view;
    }



}