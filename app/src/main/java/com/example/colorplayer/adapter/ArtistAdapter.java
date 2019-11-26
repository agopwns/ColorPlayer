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
import com.example.colorplayer.model.Artist;
import com.example.colorplayer.model.Song;
import com.example.colorplayer.utils.PreferencesUtility;

import java.util.ArrayList;
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

            List<Song> artistSongList = SongLoader.getSongsForArtistId(mContext, "" + artist.id,100);
            long tempAlbumId = 0;
            if(artistSongList != null){
                for(int i = 0; i < artistSongList.size(); i++){
                    tempAlbumId = (artistSongList.get(i).albumId);
                    if(tempAlbumId != 0)
                        break;
                }

            }

            // 앨범 이미지 로드
            Uri uri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), tempAlbumId);
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

    private Artist getSong(int position){
        return arraylist.get(position);
    }

    public class ArtistHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView artistName, artistSongs;
        ImageView albumArt;

        public ArtistHolder(View itemView) {
            super(itemView);
            artistName = itemView.findViewById(R.id.artist_name);
            artistSongs = itemView.findViewById(R.id.artist_songs);
            albumArt = itemView.findViewById(R.id.album_art);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent moveIntent = new Intent(mContext.getApplicationContext(), PlayingListActivity.class);

            try {
                // 0. 앨범 ID에 해당하는 곡 리스트 가져오기
                int artistId = (int) arraylist.get(getAdapterPosition()).id; // 현재 앨범의 albumId
                List<Song> artistSongList = SongLoader.getSongsForArtistId(mContext, "" + artistId,100);

                // 1. 재생 목록 세팅
                AudioApplication.getInstance().getServiceInterface().setPlayList(getSongIdsList(artistSongList));

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
