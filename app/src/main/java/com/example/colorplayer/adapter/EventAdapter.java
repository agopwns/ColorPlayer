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
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.colorplayer.AudioApplication;
import com.example.colorplayer.R;
import com.example.colorplayer.activities.PlayingListActivity;
import com.example.colorplayer.dataloader.SongLoader;
import com.example.colorplayer.model.Album;
import com.example.colorplayer.model.Event;
import com.example.colorplayer.model.Song;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.AlbumHolder> {

    private List<Event> arraylist;
    private Activity mContext;

    public EventAdapter(Activity context, List<Event> arraylist) {
        this.arraylist = arraylist;
        this.mContext = context;
    }

    @Override
    public AlbumHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend_list_event_cardview, parent, false);
        return new AlbumHolder(v);
    }

    @Override
    public void onBindViewHolder(AlbumHolder holder, int position) {
        final Event event = getAlbum(position);
        if(event!=null){

            // 앨범 이미지 로드
            String awsUrl =
                    "https://colorplayer-deployments-mobilehub-937790274.s3.ap-northeast-2.amazonaws.com/";
            Uri uri = Uri.parse(awsUrl + event.getEventId() + ".jpg") ;
            Glide
                    .with(holder.itemView.getContext())
                    .load(uri)
                    .centerInside()
                    .placeholder(R.drawable.test)
                    .error(R.drawable.test)
                    .into(holder.imageViewEvent);
        }
    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    private Event getAlbum(int position){
        return arraylist.get(position);
    }

    public class AlbumHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView imageViewEvent;

        public AlbumHolder(View itemView) {
            super(itemView);
            imageViewEvent = itemView.findViewById(R.id.imageview_recommend);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                if(arraylist.size() < 1) return;
                String uri = arraylist.get(getAdapterPosition()).getEventUrl();
                Intent liknIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(uri));
                mContext.startActivity(liknIntent);
            } catch(Exception e){
                Log.d("EventAdapter", "onClick 에러 발생 : " + e);
                Toast.makeText(mContext, "EventAdapter 에러 : " + e, Toast.LENGTH_SHORT).show();
            }
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
