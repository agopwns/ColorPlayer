package com.example.colorplayer.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.colorplayer.R;
import com.example.colorplayer.adapter.FolderAdapter;
import com.example.colorplayer.dataloader.FolderLoader;
import com.example.colorplayer.utils.PreferencesUtility;

import java.io.File;
import java.util.List;

/**
 * Created by nv95 on 10.11.16.
 */

public class FolderListFragment extends Fragment/* implements StorageSelectDialog.OnDirSelectListener*/ {

    private FolderAdapter mAdapter;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_folder_list, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView_folder_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (getActivity() != null)
            new loadFolders().execute("");
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

//    @Override
//    public void onDirSelected(File dir) {
//        mAdapter.updateDataSetAsync(dir);
//    }

    private class loadFolders extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Activity activity = getActivity();
            if (activity != null) {
                // 1. 여기서 외장 리스트를 만든다
                // /storage/0000-0000/Music
                // /storage/0/emulated
                String tempRoot = "/storage/emulated/0";
                List<File> tempList = FolderLoader.getMediaFiles(new File(tempRoot), true);

                // 2. 각각 필터를 적용해서 mp3파일이 있는 경우에만 상위 폴더의 File 로 리스트에 저장
                tempRoot = "/storage/0000-0000/Music";
                tempList = FolderLoader.getMediaFiles(new File(tempRoot), true);
                tempList = FolderLoader.getFinalList();

                mAdapter = new FolderAdapter(activity, tempList);
                //updateTheme();
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPreExecute() {
        }
    }
}
