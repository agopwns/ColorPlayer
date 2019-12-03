package com.example.colorplayer.youtube;

public class YoutubeVideoItem {

    public String videoId;
    public String title;
    public String thumbnailUrl;
    public String viewCount;
    public String duration;

    public YoutubeVideoItem(String title, String videoId, String thumbnailUrl, String viewCount
            , String duration) {

        this.title = title;
        this.videoId = videoId;
        this.thumbnailUrl = thumbnailUrl;
        this.viewCount = viewCount;
        this.duration = duration;
    }

}