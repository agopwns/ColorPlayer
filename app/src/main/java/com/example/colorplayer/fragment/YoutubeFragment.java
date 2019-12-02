package com.example.colorplayer.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.colorplayer.R;
import com.example.colorplayer.activities.YoutubeSearchResultActivity;
import com.example.colorplayer.adapter.SongAdapter;
import com.example.colorplayer.dataloader.SongLoader;
import com.example.colorplayer.model.Song;

import java.util.ArrayList;

import static com.example.colorplayer.utils.IntentActions.YOUTUBE_SEARCH_KEYWORD;

public class YoutubeFragment extends Fragment{
    ViewPager viewPager;
    RecyclerView recyclerView;
    private SongAdapter adapter;
    private ArrayList<Song> list = new ArrayList<>();
    ImageButton searchButton;
    TextView searchKeyword;


    public YoutubeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_youtube, container, false);

        // TODO : 검색 버튼 클릭시 인텐트로 검색 결과 화면 넘기기
        searchButton = view.findViewById(R.id.button_search);
        searchKeyword = (TextView) view.findViewById(R.id.et_search_keyword);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = searchKeyword.getText().toString();
                if(keyword.isEmpty()) return;
                Intent moveIntent = new Intent(getActivity(), YoutubeSearchResultActivity.class);
                moveIntent.putExtra(YOUTUBE_SEARCH_KEYWORD, keyword);
                startActivity(moveIntent);
            }
        });
        return view;
    }



}