package com.example.colorplayer.http;

import com.google.gson.annotations.SerializedName;

public class Member {
    @SerializedName("id")
    public String id;
    @SerializedName("password")
    public String password;

    public Member(String id, String password) {
        this.id = id;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
