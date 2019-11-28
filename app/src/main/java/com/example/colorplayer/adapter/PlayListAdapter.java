package com.example.colorplayer.adapter;

import android.app.Activity;
import android.content.Intent;
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
import com.example.colorplayer.activities.CustomListActivity;
import com.example.colorplayer.callback.PlayListEventListener;
import com.example.colorplayer.db.PlayListDB;
import com.example.colorplayer.db.PlayListDao;
import com.example.colorplayer.model.PlayList;
import com.example.colorplayer.model.Song;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.example.colorplayer.utils.IntentActions.PLAY_LIST_ID;
import static com.example.colorplayer.utils.IntentActions.PLAY_LIST_TITLE;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.PlayListHolder> {

    private List<PlayList> arraylist;
    private Activity mContext;
    private PlayListDao playListDao;
    private PlayListEventListener listener;

    public PlayListAdapter(Activity context, List<PlayList> arraylist) {
        this.arraylist = arraylist;
        this.mContext = context;
    }

    @Override
    public PlayListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_play_list_cardview, parent, false);
        return new PlayListHolder(v);
    }

    @Override
    public void onBindViewHolder(PlayListHolder holder, int position) {
        final PlayList item = getPlayList(position);
        if(item!=null){
            holder.title.setText(item.getTitle());
            Gson gson = new Gson();
            String json = item.getSongIdList();
            Type listType = new TypeToken<ArrayList<String>>() {
            }.getType();
            List<String> temp = gson.fromJson(json, listType);
            // List<String> -> List<long>
            ArrayList<Long> songIdList = new ArrayList<>();
            // 기존 곡 추가
            if (temp != null) {
                for (int i = 0; i < temp.size(); i++) {
                    songIdList.add(Long.parseLong(temp.get(i)));
                }
            }
            holder.listCount.setText("" + songIdList.size());
            // 이미지 로드
            int test = 0;
            switch (position % 10){
                case 0 : test = R.drawable.play_list_background1; break;
                case 1 : test = R.drawable.play_list_background2; break;
                case 2 : test = R.drawable.play_list_background3; break;
                case 3 : test = R.drawable.play_list_background4; break;
                case 4 : test = R.drawable.play_list_background5; break;
                case 5 : test = R.drawable.play_list_background6; break;
                case 6 : test = R.drawable.play_list_background7; break;
                case 7 : test = R.drawable.play_list_background8; break;
                case 8 : test = R.drawable.play_list_background9; break;
                case 9 : test = R.drawable.play_list_background10; break;
            }
            Glide
                .with(holder.itemView.getContext())
                .load(test)
                .error(R.drawable.test)
                .into(holder.backgroudImage);

            // 노트 클릭 이벤트 초기화
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onPlayListClick(item);
                }
            });

            // 노트 롱클릭 이벤트 초기화
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onPlayListLongClick(item);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    private PlayList getPlayList(int position){
        return arraylist.get(position);
    }

    public class PlayListHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/{

        TextView title, listCount;
        ImageView backgroudImage;

        public PlayListHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_play_list_title);
            listCount = itemView.findViewById(R.id.tv_play_list_count);
            backgroudImage = itemView.findViewById(R.id.imageview_play_list);
            //itemView.setOnClickListener(this);
        }

        private ArrayList<Long> getSongIdsList(List <Song> albumSongList) {
            ArrayList<Long> ret = new ArrayList<Long>();
            for (int i = 0; i < albumSongList.size(); i++) {
                ret.add(albumSongList.get(i).id);
            }
            return ret;
        }
    }


    public void setListener(PlayListEventListener listener){
        this.listener = listener;
    }
}
