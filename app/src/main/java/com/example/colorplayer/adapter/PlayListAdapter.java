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
import com.example.colorplayer.activities.MyPlayingListActivity;
import com.example.colorplayer.activities.PlayingListActivity;
import com.example.colorplayer.dataloader.SongLoader;
import com.example.colorplayer.db.PlayListDB;
import com.example.colorplayer.db.PlayListDao;
import com.example.colorplayer.model.PlayList;
import com.example.colorplayer.model.Song;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.PlayListHolder> {

    private List<PlayList> arraylist;
    private Activity mContext;
    private PlayListDao playListDao;

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
            // TODO : 리스트 개수 파싱 필요
            holder.listCount.setText(item.getSongIdList());

            // 배경 api 혹은 url 로드 추가
            // 앨범 이미지 로드
//            Uri uri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), item.id);
//            Glide
//                    .with(holder.itemView.getContext())
//                    .load(uri)
//                    .error(R.drawable.test)
//                    .into(holder.backgroudImage);
        }
    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    private PlayList getPlayList(int position){
        return arraylist.get(position);
    }

    public class PlayListHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView title, listCount;
        ImageView backgroudImage;

        public PlayListHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_play_list_title);
            listCount = itemView.findViewById(R.id.tv_play_list_count);
            backgroudImage = itemView.findViewById(R.id.imageview_play_list);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent moveIntent = new Intent(mContext.getApplicationContext(), MyPlayingListActivity.class);
            try {
                // 1. 현재 재생 목록 id 에 해당하는 곡 리스트 DB 에서 가져오기
                int id = (int) arraylist.get(getAdapterPosition()).getId(); // 현재 앨범의 albumId
                PlayList list = new PlayList();
                playListDao = PlayListDB.getInstance(mContext).playListDao();
                list = playListDao.getPlayListById(id);

                // 2. JSON 파싱
                String json = list.getSongIdList();
                Gson gson = new Gson();
                Type listType = new TypeToken<ArrayList<String>>() {}.getType();
                List<String> temp = gson.fromJson(json, listType);

                // 3. List<String> -> List<long>
                ArrayList<Long> songIdList = new ArrayList<>();
                if(temp != null){
                    for(int i = 0; i < temp.size(); i++){
                        songIdList.add(Long.parseLong(temp.get(i)));
                    }
                }
                // 4. 재생 목록 세팅
                if(songIdList != null && songIdList.size() > 0)
                AudioApplication.getInstance().getServiceInterface().setPlayList(songIdList);

                // 5. 만약 개수가 0보다 크면 플레이
//                AudioApplication.getInstance().getServiceInterface().setSongPosition(0);
//                AudioApplication.getInstance().getServiceInterface().play(0);

            } catch(Exception e){
                Log.d("PlayListHolder", "onClick 에러 발생 : " + e);
            }
            moveIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            if(AudioApplication.getInstance().getServiceInterface().getPreparedState())
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
