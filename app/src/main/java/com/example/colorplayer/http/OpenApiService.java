package com.example.colorplayer.http;

import com.example.colorplayer.model.Member;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface OpenApiService {
    @GET("/test/member/{id}")
    Call<Object> getUserInfo(@Path("id") String id);

    @POST("/test/member")
    Call<Member> postUser(@Body Member body);
}