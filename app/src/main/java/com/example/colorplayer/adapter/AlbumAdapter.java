package com.example.colorplayer.adapter;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.colorplayer.R;
import com.example.colorplayer.callback.SongEventListener;
import com.example.colorplayer.model.Album;
import com.example.colorplayer.model.Song;
import com.example.colorplayer.utils.PreferencesUtility;

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

    class AlbumHolder extends RecyclerView.ViewHolder {

        TextView albumTitle, albumArtist, albumYear;
        ImageView albumArt;

        public AlbumHolder(View itemView) {
            super(itemView);
            albumTitle = itemView.findViewById(R.id.album_title);
            albumArtist = itemView.findViewById(R.id.album_artist);
            albumYear = itemView.findViewById(R.id.album_year);
            albumArt = itemView.findViewById(R.id.album_art);
        }
    }

}
