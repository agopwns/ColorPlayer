package com.example.colorplayer.http;

import com.example.colorplayer.model.Comment;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CommentApiService {
    @GET("/test/comment/{title}")
    Call<Object> getCommentByTitleId(@Path("title") String title);

    @POST("/test/comment")
    Call<Comment> postUser(@Body Comment body);
}