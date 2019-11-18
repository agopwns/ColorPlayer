package com.example.colorplayer.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.colorplayer.R;
import com.example.colorplayer.callback.SongEventListener;
import com.example.colorplayer.model.Song;

import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongHolder> {

    public int currentlyPlayingPosition;
    private List<Song> arraylist;
    private AppCompatActivity mContext;
    private long[] songIDs;
    private boolean isPlaylist;
    private boolean animate;
    private int lastPosition = -1;
    private String ateKey;
    private long playlistId;

    // 생성자에서 데이터 ArrayList 와 Context 전달 받음
    public SongAdapter(AppCompatActivity context, List<Song> arraylist, boolean isPlaylistSong, boolean animate) {
        this.arraylist = arraylist;
        this.mContext = context;
        this.isPlaylist = isPlaylistSong;
        this.songIDs = getSongIds();
        this.animate = animate;
    }

    @Override
    public SongHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_music_list, parent, false);
        return new SongHolder(v);
    }

    @Override
    public void onBindViewHolder(SongHolder holder, int position) {
        final Song song = getSong(position);
        if(song!=null){
            holder.musicTitle.setText(song.title);
            holder.musicArtist.setText(song.artistName);

            // 앨범 이미지 로드
            Uri uri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), song.albumId);
            Glide
                .with(holder.itemView.getContext())
                .load(uri)
                .error(R.drawable.test)
                .into(holder.albumArt);

//            ImageLoader.getInstance().displayImage(TimberUtils.getAlbumArtUri(song.albumId).toString(),
//                    holder.albumArt, new DisplayImageOptions.Builder().cacheInMemory(true)
//                            .showImageOnLoading(R.drawable.ic_empty_music2)
//                            .resetViewBeforeLoading(true).build());

//            if (MusicPlayer.getCurrentAudioId() == song.id) {
//                holder.musicTitle.setTextColor(Config.accentColor(mContext, ateKey));
//                if (MusicPlayer.isPlaying()) {
//                    holder.visualizer.setColor(Config.accentColor(mContext, ateKey));
//                    holder.visualizer.setVisibility(View.VISIBLE);
//                } else {
//                    holder.visualizer.setVisibility(View.GONE);
//                }
//            } else {
//                holder.visualizer.setVisibility(View.GONE);
//                if (isPlaylist) {
//                    holder.musicTitle.setTextColor(Color.WHITE);
//                } else {
//                    holder.musicTitle.setTextColor(Config.textColorPrimary(mContext, ateKey));
//                }
//            }

        }
    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    private Song getSong(int position){
        return arraylist.get(position);
    }

    class SongHolder extends RecyclerView.ViewHolder {

        TextView musicTitle, musicArtist;
        ImageView albumArt;

        public SongHolder(View itemView) {
            super(itemView);
            musicTitle = itemView.findViewById(R.id.music_title);
            musicArtist = itemView.findViewById(R.id.music_artist);
            albumArt = itemView.findViewById(R.id.album_art);
        }
    }

    public long[] getSongIds() {
        long[] ret = new long[getItemCount()];
        for (int i = 0; i < getItemCount(); i++) {
            ret[i] = arraylist.get(i).id;
        }

        return ret;
    }

//    @Override
//    public void updateDataSet(List<Song> arraylist) {
//        this.arraylist = arraylist;
//        this.songIDs = getSongIds();
//    }


}
