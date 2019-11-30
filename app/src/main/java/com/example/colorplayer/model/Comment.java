package com.example.colorplayer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Comment {

    @SerializedName("com_num")
    @Expose
    private Integer comNum;
    @SerializedName("mem_id")
    @Expose
    private String memId;
    @SerializedName("com_song_name")
    @Expose
    private String comSongName;
    @SerializedName("com_artist")
    @Expose
    private String comArtist;
    @SerializedName("com_content")
    @Expose
    private String comContent;
    @SerializedName("com_time")
    @Expose
    private String comTime;

    public Integer getComNum() {
        return comNum;
    }

    public void setComNum(Integer comNum) {
        this.comNum = comNum;
    }

    public String getMemId() {
        return memId;
    }

    public void setMemId(String memId) {
        this.memId = memId;
    }

    public String getComSongName() {
        return comSongName;
    }

    public void setComSongName(String comSongName) {
        this.comSongName = comSongName;
    }

    public String getComArtist() {
        return comArtist;
    }

    public void setComArtist(String comArtist) {
        this.comArtist = comArtist;
    }

    public String getComContent() {
        return comContent;
    }

    public void setComContent(String comContent) {
        this.comContent = comContent;
    }

    public String getComTime() {
        return comTime;
    }

    public void setComTime(String comTime) {
        this.comTime = comTime;
    }
}
