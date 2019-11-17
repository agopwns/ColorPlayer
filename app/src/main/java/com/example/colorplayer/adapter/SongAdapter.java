package com.example.colorplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.colorplayer.R;
import com.example.colorplayer.callback.SongEventListener;
import com.example.colorplayer.model.Song;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongHolder> {

    private Context context;
    private ArrayList<Song> songs;
    private SongEventListener listener;

    // 생성자에서 데이터 ArrayList 와 Context 전달 받음
    public SongAdapter(Context context, ArrayList<Song> songs) {
        this.context = context;
        this.songs = songs;
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
            holder.musicTitle.setText(song.getTitle());
            holder.musicArtist.setText(song.getArtist());

            // 이미지뷰 모서리 둥글게
//            GradientDrawable drawable =
//                    (GradientDrawable) context.getDrawable(R.drawable.background_rounding);
//            holder.movieImageView.setBackground(drawable);
//            holder.movieImageView.setClipToOutline(true);

//            if(movie.getMovieImagePath() != null){
//                Uri tempUri = Uri.parse(movie.getMovieImagePath());
//                Glide
//                        .with(holder.itemView.getContext())
//                        .load(tempUri)
//                        .into(holder.movieImageView);
//            } else {
//                Glide
//                        .with(holder.itemView.getContext())
//                        .load(R.drawable.baseline_add_photo_alternate_black_48dp)
//                        .into(holder.movieImageView);
//            }

            // 노트 클릭 이벤트 초기화
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onMovieClick(song);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    private Song getSong(int position){
        return songs.get(position);
    }

    class SongHolder extends RecyclerView.ViewHolder {

        TextView musicTitle, musicArtist;
//        ImageView movieImageView;

        public SongHolder(View itemView) {
            super(itemView);
            musicTitle = itemView.findViewById(R.id.music_title);
            musicArtist = itemView.findViewById(R.id.music_artist);
//            movieImageView = itemView.findViewById(R.id.movie_image);
        }
    }

    public void setListener(SongEventListener listener){
        this.listener = listener;
    }
}
