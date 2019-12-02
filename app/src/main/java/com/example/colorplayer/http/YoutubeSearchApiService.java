package com.example.colorplayer.http;

import com.example.colorplayer.model.Member;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface YoutubeSearchApiService {
    @GET("/youtube/v3/search/")
    Call<Object> getVideoIdsByKeyword(@Query("part") String part,
                                      @Query("q") String q,
                                      @Query("type") String type,
                                      @Query("key") String key);

}