package com.example.colorplayer.adapter;

import android.content.ContentUris;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.colorplayer.R;
import com.example.colorplayer.callback.SongEventListener;
import com.example.colorplayer.model.MiniSong;
import com.example.colorplayer.model.Song;

import java.util.ArrayList;
import java.util.List;

public class SelectListAdapter extends RecyclerView.Adapter<SelectListAdapter.SongHolder> {

    public int currentlyPlayingPosition;
    private List<Song> arraylist;
    public List<MiniSong> returnList = new ArrayList<>();
    private AppCompatActivity mContext;
    private boolean isSelectedAll;
    private long[] songIDs;
    private boolean isPlaylist;
    private boolean animate;
    private int lastPosition = -1;
    private String ateKey;
    private long playlistId;
    private SongEventListener listener;

    // 생성자에서 데이터 ArrayList 와 Context 전달 받음
    public SelectListAdapter(AppCompatActivity context, List<Song> arraylist, boolean isPlaylistSong, boolean animate) {
        this.arraylist = arraylist;
        this.mContext = context;
        this.isPlaylist = isPlaylistSong;
        this.songIDs = getSongIds();
        this.animate = animate;
    }

    @Override
    public SongHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_list, parent, false);
        return new SongHolder(v);
    }

    @Override
    public void onBindViewHolder(SongHolder holder, final int position) {
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

            if (!isSelectedAll)
                holder.checkBox.setChecked(false);
            else
                holder.checkBox.setChecked(true);

            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    CheckBox checkBox = (CheckBox) buttonView;
                    if(checkBox.isChecked()){
                        MiniSong miniSong = new MiniSong(song.id, song.title, position);
                        returnList.add(miniSong);
                    } else {
                        for(int i = 0; i < returnList.size(); i++){
                            if(returnList.get(i).position == position)
                                returnList.remove(i);
                        }
                    }
                }
            });
        }
    }

    public void selectAll(){
        isSelectedAll=true;
        notifyDataSetChanged();
    }
    public void unselectall(){
        isSelectedAll=false;
        notifyDataSetChanged();
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
        CheckBox checkBox;

        public SongHolder(View itemView) {
            super(itemView);
            musicTitle = itemView.findViewById(R.id.music_title);
            musicArtist = itemView.findViewById(R.id.music_artist);
            albumArt = itemView.findViewById(R.id.album_art);
            checkBox = itemView.findViewById(R.id.checkbox_select_one);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if(!checkBox.isChecked()){
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
        }
    }

    private long[] getSongIds() {
        long[] ret = new long[getItemCount()];
        for (int i = 0; i < getItemCount(); i++) {
            ret[i] = arraylist.get(i).id;
        }
        return ret;
    }

    public ArrayList<Long> getSongIdsList() {
        ArrayList<Long> ret = new ArrayList<Long>();
        for (int i = 0; i < returnList.size(); i++) {
            ret.add(returnList.get(i).id);
        }
        return ret;
    }

}
