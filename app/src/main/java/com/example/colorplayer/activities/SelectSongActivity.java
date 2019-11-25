package com.example.colorplayer.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.colorplayer.R;
import com.example.colorplayer.adapter.SongAdapter;

public class SelectSongActivity extends AppCompatActivity {

    ImageButton backButton, confirmButton;
    TextView textViewCurrentCount, textViewTotalCount;
    RecyclerView recyclerView;
    private SongAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_song);
    }



}
