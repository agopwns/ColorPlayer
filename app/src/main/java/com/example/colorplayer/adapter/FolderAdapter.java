package com.example.colorplayer.adapter;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.colorplayer.AudioApplication;
import com.example.colorplayer.R;
import com.example.colorplayer.activities.PlayingListActivity;
import com.example.colorplayer.dataloader.FolderLoader;
import com.example.colorplayer.dataloader.SongLoader;
import com.example.colorplayer.model.Song;
import com.example.colorplayer.utils.PreferencesUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nv95 on 10.11.16.
 */

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ItemHolder>{

    @NonNull
    private List<File> mFileSet;
    private List<File> mOriginSet;
    private List<Song> mSongs;
    private File mRoot;
    private Activity mContext;
    private final Drawable[] mIcons;
    private boolean mBusy = false;


    public FolderAdapter(Activity context, List<File> fileList) {
        mContext = context;
        mIcons = new Drawable[]{
                ContextCompat.getDrawable(context, R.drawable.folder_outline),
                ContextCompat.getDrawable(context, R.drawable.folder_settings_outline),
                ContextCompat.getDrawable(context, R.drawable.baseline_music_note_white_24),
        };
        mSongs = new ArrayList<>();
        mFileSet = fileList;
        mOriginSet = fileList;
        getSongsForFiles(mFileSet);
        //updateDataSet(root);
    }

    @Override
    public FolderAdapter.ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_folder_list, viewGroup, false);
        return new ItemHolder(v);
    }

    @Override
    public void onBindViewHolder(final FolderAdapter.ItemHolder holder, int i) {
        File localItem = mFileSet.get(i);
        Song song = mSongs.get(i);
        holder.title.setText(localItem.getName());
        if (localItem.isDirectory()) {
            holder.albumArt.setImageDrawable("..".equals(localItem.getName()) ? mIcons[1] : mIcons[0]);
        } else {
            // 앨범 이미지 로드
            Uri uri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), song.albumId);
            Glide
                    .with(holder.itemView.getContext())
                    .load(uri)
                    .error(mIcons[2])
                    .into(holder.albumArt);
        }
    }

    @Override
    public int getItemCount() {
        return mFileSet.size();
    }

    public boolean updateDataSetAsync(File newRoot) {
        if (mBusy) return false;
        mRoot = newRoot;
        return true;
    }

    private void getSongsForFiles(List<File> files) {
        mSongs.clear();
        for (File file : files) {
            mSongs.add(SongLoader.getSongFromPath(file.getAbsolutePath(), mContext));
        }
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView title;
        protected ImageView albumArt;

        public ItemHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.folder_title);
            this.albumArt = (ImageView) view.findViewById(R.id.album_art);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mBusy) return;

            final File f = mFileSet.get(getAdapterPosition());

            if (f.isDirectory() && updateDataSetAsync(f)) {
                List<File> files = FolderLoader.getMediaFiles(mRoot,true);
                getSongsForFiles(files);

                Intent moveIntent = new Intent(mContext.getApplicationContext(), PlayingListActivity.class);
                try {
                    // 1. 재생 목록 세팅
                    AudioApplication.getInstance().getServiceInterface().setPlayList(getSongIdsList());

                    // 2. 플레이
                    AudioApplication.getInstance().getServiceInterface().setSongPosition(0);
                    AudioApplication.getInstance().getServiceInterface().play(0);
                } catch(Exception e){
                    Log.d("AlbumAdapter", "onClick 에러 발생 : " + e);
                }
                mContext.startActivity(moveIntent);
            }
        }

        private ArrayList<Long> getSongIdsList() {
            ArrayList<Long> ret = new ArrayList<Long>();
            for (int i = 1; i < mSongs.size(); i++) {
                ret.add(mSongs.get(i).id);
            }
            return ret;
        }

    }


}