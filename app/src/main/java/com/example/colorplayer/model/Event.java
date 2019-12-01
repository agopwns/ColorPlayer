package com.example.colorplayer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Event {

    @SerializedName("event_num")
    @Expose
    private Integer eventNum;
    @SerializedName("event_id")
    @Expose
    public final String EventId;
    @SerializedName("event_url")
    @Expose
    public final String EventUrl;

    public Event(String eventId, String eventUrl) {
        EventId = eventId;
        EventUrl = eventUrl;
    }

    public String getEventId() {
        return EventId;
    }

    public String getEventUrl() {
        return EventUrl;
    }
}
