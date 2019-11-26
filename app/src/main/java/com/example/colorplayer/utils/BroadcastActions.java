package com.example.colorplayer.utils;

import android.content.IntentFilter;

public class BroadcastActions {
    // 오디오 서비스
    public final static String PREPARED = "PREPARED";
    public final static String PLAY_STATE_CHANGED = "PLAY_STATE_CHANGED";
    public final static String PLAY_NEXT_SONG = "PLAY_NEXT_SONG";
    public final static String STOPPED = "STOPPED";

    // 재생 목록
    public final static String PLAY_LIST_ADD = "PLAY_LIST_ADD";
}
