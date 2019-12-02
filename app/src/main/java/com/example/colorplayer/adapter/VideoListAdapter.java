package com.example.colorplayer.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;

import com.example.colorplayer.R;
import com.example.colorplayer.model.YoutubeVideoItem;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<YoutubeVideoItem> mItems = new ArrayList<>();
    private Lifecycle mLifeCycle;


    public VideoListAdapter(Context c, Lifecycle lifecycle,  @Nullable ArrayList<YoutubeVideoItem> items) {

        mContext = c;
        mLifeCycle = lifecycle;
        setItems(items);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_youtube_videolist, parent, false);

        ViewHolder v = new ViewHolder(view);

        return v;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final YoutubeVideoItem item = mItems.get(position);

        holder.mTitleTV.setText(item.title);
        holder.youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {

                youTubePlayer.loadVideo(item.videoId, 0);
                youTubePlayer.pause();
                //youTubePlayer.cueVideo(item.videoId, 0);
            }
        });
    }

    @Override
    public int getItemCount() {

        return (mItems == null) ? 0 : mItems.size();

    }


    public void setItems(ArrayList<YoutubeVideoItem> items) {

        mItems.clear();

        if (items != null) {
            mItems.addAll(items);
        }
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitleTV, mLikesCountTV;
        private EditText mCommentET;
        private ImageView mImageView;
        private Button mLikeButton, mCommentButton;
        private YouTubePlayerView youTubePlayerView;


        public ViewHolder(View itemView) {
            super(itemView);

            mTitleTV = itemView.findViewById(R.id.tv_song_title);
            youTubePlayerView = itemView.findViewById(R.id.youtube_player_view);

            // TODO : 클릭시 재생으로 바꾸기
            youTubePlayerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                }
            });
        }
    }
}