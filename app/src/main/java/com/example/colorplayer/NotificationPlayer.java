package com.example.colorplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.example.colorplayer.activities.MainActivity;
import com.example.colorplayer.utils.CommandActions;
import com.example.colorplayer.utils.RemoteViewSize;

public class NotificationPlayer {

    private final static int NOTIFICATION_PLAYER_ID = 0x342;
    private AudioService mService;
    private NotificationManager mNotificationManager;
    private NotificationManagerBuilder mNotificationManagerBuilder;
    private boolean isForeground;

    public NotificationPlayer(AudioService service) {
        mService = service;
        mNotificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    // 음악 상태의 변경을 확인하고 NotificationPlayer 를 업데이트 하기 위함
    public void updateNotificationPlayer() {
        cancel();
        mNotificationManagerBuilder = new NotificationManagerBuilder();
        mNotificationManagerBuilder.execute();
    }

    public void removeNotificationPlayer() {
        cancel();
        mService.stopForeground(true);
        isForeground = false;
    }

    private void cancel() {
        if (mNotificationManagerBuilder != null) {
            mNotificationManagerBuilder.cancel(true);
            mNotificationManagerBuilder = null;
        }
    }

    private class NotificationManagerBuilder extends AsyncTask<Void, Void, Notification> {
        private RemoteViews mRemoteViews;
        private NotificationCompat.Builder mNotificationBuilder;
        private PendingIntent mMainPendingIntent;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Intent mainActivity = new Intent(mService, MainActivity.class);
            mMainPendingIntent = PendingIntent.getActivity(mService, 0, mainActivity, 0);
            if(mService.getRemoteViewSizeState().equals(RemoteViewSize.LARGE))
                mRemoteViews = createRemoteView(R.layout.notification_large_player);
            else
                mRemoteViews = createRemoteView(R.layout.notification_player);

            mNotificationBuilder = new NotificationCompat.Builder(mService, "colorPlayerChannel");
            mNotificationBuilder
                    .setSmallIcon(R.drawable.baseline_whatshot_black_48)
                    .setContentTitle("제목")
                    .setContentText("텍스트")
                    .setOngoing(true)
                    .setContentIntent(mMainPendingIntent)
                    .setContent(mRemoteViews);

            Notification notification = mNotificationBuilder.build();
            notification.priority = Notification.PRIORITY_MAX;
            notification.contentIntent = mMainPendingIntent;
            if (!isForeground) {
                isForeground = true;
                // 서비스를 Foreground 상태로 만든다
                mService.startForeground(NOTIFICATION_PLAYER_ID, notification);
            }
        }

        @Override
        protected Notification doInBackground(Void... params) {
            mNotificationBuilder.setContent(mRemoteViews);
            mNotificationBuilder.setContentIntent(mMainPendingIntent);
            mNotificationBuilder.setPriority(Notification.PRIORITY_MAX);
            Notification notification = mNotificationBuilder.build();
            updateRemoteView(mRemoteViews, notification);
            return notification;
        }

        @Override
        protected void onPostExecute(Notification notification) {
            super.onPostExecute(notification);
            try {
                mNotificationManager.notify(NOTIFICATION_PLAYER_ID, notification);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private RemoteViews createRemoteView(int layoutId) {
            RemoteViews remoteView = new RemoteViews(mService.getPackageName(), layoutId);
            Intent actionTogglePlay = new Intent(CommandActions.TOGGLE_PLAY);
            Intent actionForward = new Intent(CommandActions.FORWARD);
            Intent actionRewind = new Intent(CommandActions.REWIND);
            Intent actionClose = new Intent(CommandActions.CLOSE);
            Intent actionExpand = new Intent(CommandActions.EXPAND);
            Intent actionCollapse = new Intent(CommandActions.COLLAPSE);
            PendingIntent togglePlay = PendingIntent.getService(mService, 0, actionTogglePlay, 0);
            PendingIntent forward = PendingIntent.getService(mService, 0, actionForward, 0);
            PendingIntent rewind = PendingIntent.getService(mService, 0, actionRewind, 0);
            PendingIntent close = PendingIntent.getService(mService, 0, actionClose, 0);
            PendingIntent expand = PendingIntent.getService(mService, 0, actionExpand, 0);
            PendingIntent collapse = PendingIntent.getService(mService, 0, actionCollapse, 0);

            remoteView.setOnClickPendingIntent(R.id.noti_btn_play_pause, togglePlay);
            remoteView.setOnClickPendingIntent(R.id.noti_btn_forward, forward);
            remoteView.setOnClickPendingIntent(R.id.noti_btn_rewind, rewind);
            remoteView.setOnClickPendingIntent(R.id.noti_btn_close, close);

            //if(mService.getRemoteViewSizeState().equals(RemoteViewSize.LARGE))
                remoteView.setOnClickPendingIntent(R.id.noti_btn_collapse, collapse);
            //else
                remoteView.setOnClickPendingIntent(R.id.noti_btn_expand, expand);

            return remoteView;
        }

        private void updateRemoteView(RemoteViews remoteViews, Notification notification) {
            if (mService.isPlaying()) {
                remoteViews.setImageViewResource(R.id.noti_btn_play_pause, R.drawable.baseline_pause_black_24);
            } else {
                remoteViews.setImageViewResource(R.id.noti_btn_play_pause, R.drawable.baseline_play_arrow_black_24);
            }

            try {
                String title = mService.getAudioItem().title;
                remoteViews.setTextViewText(R.id.noti_title, title);
                //Uri albumArtUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), mService.getAudioItem().albumId);
                //Picasso.get().load(albumArtUri).error(R.drawable.test1024).into(remoteViews, R.id.noti_alt_album, NOTIFICATION_PLAYER_ID, notification);
            } catch (Exception e){
                Log.d("Notification", "Notification 생성 에러 : " + e);
            }
        }

        private void updateRemoteViewSize(){

        }

    }



}
