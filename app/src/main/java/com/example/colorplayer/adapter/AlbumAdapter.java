package com.example.colorplayer.adapter;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.colorplayer.AudioApplication;
import com.example.colorplayer.R;
import com.example.colorplayer.activities.PlayingListActivity;
import com.example.colorplayer.dataloader.SongLoader;
import com.example.colorplayer.model.Album;
import com.example.colorplayer.model.Song;

import java.util.ArrayList;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumHolder> {

    private List<Album> arraylist;
    private Activity mContext;

    public AlbumAdapter(Activity context, List<Album> arraylist) {
        this.arraylist = arraylist;
        this.mContext = context;
    }

    @Override
    public AlbumHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_album_list, parent, false);
        return new AlbumHolder(v);
    }

    @Override
    public void onBindViewHolder(AlbumHolder holder, int position) {
        final Album album = getAlbum(position);
        if(album!=null){
            holder.albumTitle.setText(album.title);
            holder.albumArtist.setText(album.artistName);
            String tempYear = Integer.toString(album.year);
            if(tempYear.length() > 4)
                tempYear = tempYear.substring(0, 4);

            if(!tempYear.equals("0"))
                holder.albumYear.setText(tempYear);

            // 앨범 이미지 로드
            Uri uri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), album.id);
            Glide
                    .with(holder.itemView.getContext())
                    .load(uri)
                    .placeholder(R.drawable.test)
                    .error(R.drawable.test)
                    .into(holder.albumArt);
        }
    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    private Album getAlbum(int position){
        return arraylist.get(position);
    }

    public class AlbumHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView albumTitle, albumArtist, albumYear;
        ImageView albumArt;

        public AlbumHolder(View itemView) {
            super(itemView);
            albumTitle = itemView.findViewById(R.id.album_title);
            albumArtist = itemView.findViewById(R.id.album_artist);
            albumYear = itemView.findViewById(R.id.album_year);
            albumArt = itemView.findViewById(R.id.album_art);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent moveIntent = new Intent(mContext.getApplicationContext(), PlayingListActivity.class);

            try {
                // 0. 앨범 ID에 해당하는 곡 리스트 가져오기
                int albumId = (int) arraylist.get(getAdapterPosition()).id; // 현재 앨범의 albumId
                List <Song> albumSongList = new ArrayList<>();
                albumSongList = SongLoader.getSongsForAlbumId(mContext, "" + albumId,100);

                // 1. 재생 목록 세팅
                AudioApplication.getInstance().getServiceInterface().setPlayList(getSongIdsList(albumSongList));

                // 2. 플레이
                AudioApplication.getInstance().getServiceInterface().setSongPosition(0);
                AudioApplication.getInstance().getServiceInterface().play(0);

            } catch(Exception e){
                Log.d("AlbumAdapter", "onClick 에러 발생 : " + e);
            }
            mContext.startActivity(moveIntent);
        }

        private ArrayList<Long> getSongIdsList(List <Song> albumSongList) {
            ArrayList<Long> ret = new ArrayList<Long>();
            for (int i = 0; i < albumSongList.size(); i++) {
                ret.add(albumSongList.get(i).id);
            }
            return ret;
        }
    }



}
