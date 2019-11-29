package com.example.colorplayer.adapter;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.colorplayer.AudioApplication;
import com.example.colorplayer.R;
import com.example.colorplayer.activities.PlayerActivity;
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
    private SongEventListener listener;


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
        }
    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    private Song getSong(int position){
        return arraylist.get(position);
    }

    public class SongHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView musicTitle, musicArtist;
        ImageView albumArt;

        public SongHolder(View itemView) {
            super(itemView);
            musicTitle = itemView.findViewById(R.id.music_title);
            musicArtist = itemView.findViewById(R.id.music_artist);
            albumArt = itemView.findViewById(R.id.album_art);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent moveIntent = new Intent(mContext.getApplicationContext(), PlayerActivity.class);

            // 재생 목록 세팅
            // 랜덤 셔플이 다시 덮어씌워지지 않도록 방지하기 위한 Service 내부에 처리 되어 있음
            AudioApplication.getInstance().getServiceInterface().setPlayList(getSongIdsList());

            // 랜덤 재생 중일 시, 원래 리스트에서 플레이 한다.
            if(AudioApplication.getInstance().getServiceInterface().getShuffleState())
                AudioApplication.getInstance().getServiceInterface().playShuffledClick(getAdapterPosition());
            else
                // 선택한 오디오 재생
                AudioApplication.getInstance().getServiceInterface().play(getAdapterPosition());

            moveIntent.putExtra("songTitle", getSong(getAdapterPosition()).title);
            mContext.startActivity(moveIntent);
        }
    }

    private long[] getSongIds() {
        long[] ret = new long[getItemCount()];
        for (int i = 0; i < getItemCount(); i++) {
            ret[i] = arraylist.get(i).id;
        }
        return ret;
    }

    private ArrayList<Long> getSongIdsList() {
        ArrayList<Long> ret = new ArrayList<Long>();
        for (int i = 0; i < getItemCount(); i++) {
            ret.add(arraylist.get(i).id);
        }
        return ret;
    }

//    @Override
//    public void updateDataSet(List<Song> arraylist) {
//        this.arraylist = arraylist;
//        this.songIDs = getSongIds();
//    }



}
