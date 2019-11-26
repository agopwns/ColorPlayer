package com.example.colorplayer.model;

public class MiniSong {

    public final long id;
    public final String title;
    public final int position;

    public MiniSong() {
        this.id = -1;
        this.title = "";
        this.position = -1;
    }

    public MiniSong(long _id, String _title, int position) {
        this.id = _id;
        this.title = _title;
        this.position = position;
    }


}