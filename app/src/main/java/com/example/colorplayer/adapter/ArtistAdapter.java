package com.example.colorplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.colorplayer.R;
import com.example.colorplayer.callback.ArtistEventListener;
import com.example.colorplayer.callback.SongEventListener;
import com.example.colorplayer.model.Artist;
import com.example.colorplayer.model.Song;

import java.util.ArrayList;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistHolder> {

    private Context context;
    private ArrayList<Artist> artist;
    private ArtistEventListener listener;

    // 생성자에서 데이터 ArrayList 와 Context 전달 받음
    public ArtistAdapter(Context context, ArrayList<Artist> artist) {
        this.context = context;
        this.artist = artist;
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
            holder.artistSongs.setText("" + artist.getSongCount());

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
                    listener.onMovieClick(artist);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return artist.size();
    }

    private Artist getSong(int position){
        return artist.get(position);
    }

    class ArtistHolder extends RecyclerView.ViewHolder {

        TextView artistName, artistSongs;
//        ImageView movieImageView;

        public ArtistHolder(View itemView) {
            super(itemView);
            artistName = itemView.findViewById(R.id.artist_name);
            artistSongs = itemView.findViewById(R.id.artist_songs);
//            movieImageView = itemView.findViewById(R.id.movie_image);
        }
    }

    public void setListener(ArtistEventListener listener){
        this.listener = listener;
    }
}
