package com.example.colorplayer.http;

import com.google.gson.JsonArray;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface OpenApiService {
    @GET("/test/member/{id}")
    Call<MemberData> getUserInfo(@Path("id") String id);
}