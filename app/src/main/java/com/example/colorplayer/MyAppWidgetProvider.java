package com.example.colorplayer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.example.colorplayer.activities.MainActivity;
import com.example.colorplayer.utils.BroadcastActions;
import com.example.colorplayer.utils.CommandActions;
import com.squareup.picasso.Picasso;

public class MyAppWidgetProvider extends AppWidgetProvider {
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);


        String action = intent.getAction();
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_large_player);
        if (BroadcastActions.PREPARED.equals(action)) {
            updateAlbumArt(context, remoteViews); // 재생음악 파일이 변경된 경우.
        }
        //updateAlbumArt(context, remoteViews); // 재생음악 파일이 변경된 경우.
        updatePlayState(context, remoteViews); // 재생상태 업데이트.
        updateWidget(context, remoteViews); // 앱위젯 업데이트.
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private void updateAlbumArt(Context context, RemoteViews remoteViews) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));
        long albumId = AudioApplication.getInstance().getServiceInterface().getAudioItem().albumId;
        Uri albumArtUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);
        Picasso.get()
                .load(albumArtUri)
                .error(R.drawable.baseline_whatshot_black_18)
                .into(remoteViews, R.id.widget_alt_album, appWidgetIds);
    }

    private void updatePlayState(Context context, RemoteViews remoteViews) {
        if (AudioApplication.getInstance().getServiceInterface().isPlaying()) {
            remoteViews.setImageViewResource(R.id.widget_btn_play_pause, R.drawable.baseline_pause_black_18);
        } else {
            remoteViews.setImageViewResource(R.id.widget_btn_play_pause, R.drawable.baseline_play_arrow_black_18);
        }
        String title = "재생중인 음악이 없습니다.";
        if (AudioApplication.getInstance().getServiceInterface().getAudioItem() != null) {
            title = AudioApplication.getInstance().getServiceInterface().getAudioItem().title;
        }
        remoteViews.setTextViewText(R.id.widget_title, title);

        Intent actionLaunch = new Intent(context, MainActivity.class);
        Intent actionTogglePlay = new Intent(CommandActions.TOGGLE_PLAY);
        Intent actionForward = new Intent(CommandActions.FORWARD);
        Intent actionRewind = new Intent(CommandActions.REWIND);

        PendingIntent launch = PendingIntent.getActivity(context, 0, actionLaunch, 0);
        PendingIntent togglePlay = PendingIntent.getService(context, 0, actionTogglePlay, 0);
        PendingIntent forward = PendingIntent.getService(context, 0, actionForward, 0);
        PendingIntent rewind = PendingIntent.getService(context, 0, actionRewind, 0);

        remoteViews.setOnClickPendingIntent(R.id.widget_alt_album, launch);
        remoteViews.setOnClickPendingIntent(R.id.widget_btn_play_pause, togglePlay);
        remoteViews.setOnClickPendingIntent(R.id.widget_btn_forward, forward);
        remoteViews.setOnClickPendingIntent(R.id.widget_btn_rewind, rewind);
    }

    private void updateWidget(Context context, RemoteViews remoteViews) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));
        if (appWidgetIds != null && appWidgetIds.length > 0) {
            appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
        }
    }

}
