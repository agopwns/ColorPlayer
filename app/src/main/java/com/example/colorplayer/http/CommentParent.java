package com.example.colorplayer.http;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommentParent {

    @SerializedName("Item")
    @Expose
    private Item item;

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

}