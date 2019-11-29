package com.example.colorplayer.http;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommentChild {

    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("artist")
    @Expose
    private String artist;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}