package com.example.colorplayer.callback;

import com.example.colorplayer.model.PlayList;

public interface PlayListEventListener {

    void onPlayListClick(PlayList playList);
    void onPlayListLongClick(PlayList playList);
}
