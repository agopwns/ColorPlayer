package com.example.colorplayer.http;

import com.example.colorplayer.model.Event;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface EventApiService {
    @GET("/test/event")
    Call<Object> getEvents();

    @POST("/test/event")
    Call<Event> postEvent(@Body Event body);
}