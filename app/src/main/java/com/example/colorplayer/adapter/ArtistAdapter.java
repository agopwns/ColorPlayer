package com.example.colorplayer.adapter;

import android.app.Activity;
import android.content.ContentUris;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.colorplayer.R;
import com.example.colorplayer.model.Artist;
import com.example.colorplayer.utils.PreferencesUtility;

import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistHolder> {

    private List<Artist> arraylist;
    private Activity mContext;
    private boolean isGrid;

    public ArtistAdapter(Activity context, List<Artist> arraylist) {
        this.arraylist = arraylist;
        this.mContext = context;
        this.isGrid = PreferencesUtility.getInstance(mContext).isArtistsInGrid();
    }

    @Override
    public ArtistHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_artist_list, parent, false);
        return new ArtistHolder(v);
    }

    @Override
    public void onBindViewHolder(ArtistHolder holder, int position) {
        final Artist artist = getSong(position);
        if(artist!=null){
            holder.artistName.setText(artist.getName());
            holder.artistSongs.setText(artist.getSongCount() + "곡");

            // 앨범 이미지 로드
            Uri uri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), artist.id);
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

    private Artist getSong(int position){
        return arraylist.get(position);
    }

    class ArtistHolder extends RecyclerView.ViewHolder {

        TextView artistName, artistSongs;
        ImageView albumArt;

        public ArtistHolder(View itemView) {
            super(itemView);
            artistName = itemView.findViewById(R.id.artist_name);
            artistSongs = itemView.findViewById(R.id.artist_songs);
            albumArt = itemView.findViewById(R.id.album_art);
        }
    }

}
