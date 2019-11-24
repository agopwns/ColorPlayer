package com.example.colorplayer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.colorplayer.AudioApplication;
import com.example.colorplayer.adapter.FolderAdapter;
import com.example.colorplayer.dataloader.FolderLoader;
import com.example.colorplayer.fragment.FolderListFragment;
import com.example.colorplayer.utils.BroadcastActions;
import com.example.colorplayer.R;
import com.example.colorplayer.adapter.SectionPageAdapter;
import com.example.colorplayer.animation.ZoomOutPageTransformer;
import com.example.colorplayer.fragment.SongListFragment;
import com.example.colorplayer.fragment.AlbumListFragment;
import com.example.colorplayer.fragment.ArtistListFragment;
import com.example.colorplayer.model.Song;
import com.example.colorplayer.utils.PreferencesUtility;
import com.example.colorplayer.utils.Time;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar tb;
    private ViewPager mViewPager;
    ImageView albumArt;
    TextView title, artist;
    LinearLayout nowPlayingCard;
    ImageButton playButton, searchButton, optionButton;
    Song song;
    SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());
    private PreferencesUtility mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 권한 거부시 앱 종료
        checkPermission();

        registerBroadcast();

        mPreferences = PreferencesUtility.getInstance(getApplicationContext());

        albumArt = findViewById(R.id.album_art);
        title = findViewById(R.id.music_title);
        artist = findViewById(R.id.artist_name);
        nowPlayingCard = findViewById(R.id.content);

        searchButton = findViewById(R.id.icon_search);
        optionButton = findViewById(R.id.icon_option);

        albumArt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveIntent = new Intent(getApplicationContext(), NowPlayingActivity.class);
                moveIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if(AudioApplication.getInstance().getServiceInterface().getPreparedState())
                    startActivity(moveIntent);
            }
        });

        nowPlayingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveIntent = new Intent(getApplicationContext(), NowPlayingActivity.class);
                moveIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if(AudioApplication.getInstance().getServiceInterface().getPreparedState())
                    startActivity(moveIntent);
            }
        });

        // nowPlayingCard 재생 버튼
        playButton = findViewById(R.id.button_play);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AudioApplication.getInstance().getServiceInterface().isPlaying())
                    playButton.setImageResource(R.drawable.baseline_play_arrow_white_36);
                else
                    playButton.setImageResource(R.drawable.baseline_pause_white_36);

                AudioApplication.getInstance().getServiceInterface().togglePlay();
            }
        });

        tb = (Toolbar) findViewById(R.id.main_app_bar);
        setSupportActionBar(tb);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        // TODO : 탭 커스텀 레이아웃 적용
//        tabLayout.addTab(tabLayout.newTab().setCustomView(createTabView("노래")));
//        tabLayout.addTab(tabLayout.newTab().setCustomView(createTabView("아티스트")));
//        tabLayout.addTab(tabLayout.newTab().setCustomView(createTabView("앨범")));
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
    }

    private View createTabView(String tabName) {
        View tabView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tab, null);
        TextView txt_name = (TextView) tabView.findViewById(R.id.txt_name);
        txt_name.setText(tabName);
        return tabView;

    }
    public void setupViewPager(ViewPager viewPager) {
        adapter.addFragment(new SongListFragment(), "노래");
        adapter.addFragment(new AlbumListFragment(), "앨범");
        adapter.addFragment(new ArtistListFragment(), "아티스트");
        adapter.addFragment(new FolderListFragment(), "폴더");
        adapter.addFragment(new ArtistListFragment(), "재생목록");

        viewPager.setAdapter(adapter);
    }

    // Toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public void checkPermission(){
        //현재 안드로이드 버전이 6.0미만이면 메서드를 종료한다.
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;
        String [] permission_list = {"android.permission.WRITE_EXTERNAL_STORAGE"
                                    , "android.permission.READ_EXTERNAL_STORAGE"};

        for(String permission : permission_list){
            //권한 허용 여부를 확인한다.
            int chk = checkCallingOrSelfPermission(permission);

            if(chk == PackageManager.PERMISSION_DENIED){
                //권한 허용을여부를 확인하는 창을 띄운다
                requestPermissions(permission_list,0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==0)
        {
            for(int i=0; i<grantResults.length; i++)
            {
                //허용됬다면
                if(grantResults[i]==PackageManager.PERMISSION_GRANTED){
                }
                else {
                    Toast.makeText(getApplicationContext(),"앱 권한을 설정하세요",Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

    private void updateUI() {
        if(AudioApplication.getInstance() != null){
            song = AudioApplication.getInstance().getServiceInterface().getAudioItem();
            if(song == null) return;

            if(playButton != null){
                if (AudioApplication.getInstance().getServiceInterface().isPlaying()) {
                    playButton.setImageResource(R.drawable.baseline_pause_white_36);
                } else {
                    playButton.setImageResource(R.drawable.baseline_play_arrow_white_36);
                }
            }

            // 앨범 이미지 로드
            Uri uri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), song.albumId);
            Glide
                    .with(this)
                    .load(uri)
                    .error(R.drawable.test)
                    .into(albumArt);

            // 타이틀, 아티스트
            title.setText(song.title);
            artist.setText(song.artistName);
        }
    }

    private void updateUINextSong() {
        if(AudioApplication.getInstance() != null){
            song = AudioApplication.getInstance().getServiceInterface().getAudioItem();
            if(playButton != null){
                playButton.setImageResource(R.drawable.baseline_pause_white_36);
            }
            if(albumArt != null){
                // 앨범 이미지 로드
                Uri uri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), song.albumId);
                Glide
                        .with(this)
                        .load(uri)
                        .error(R.drawable.test)
                        .into(albumArt);
            }
            // 타이틀, 아티스트
            if(title != null && artist != null){
                title.setText(song.title);
                artist.setText(song.artistName);
            }
        }
    }

    private void updateUIEndToFirst() {
        if(AudioApplication.getInstance() != null){

            if(playButton != null)
                playButton.setImageResource(R.drawable.baseline_play_arrow_white_36);

            song = AudioApplication.getInstance().getServiceInterface().getAudioItem();

            if(albumArt != null){
                // 앨범 이미지 로드
                Uri uri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), song.albumId);
                Glide
                        .with(this)
                        .load(uri)
                        .error(R.drawable.ic_whatshot_24px_white)
                        .into(albumArt);
            }
            // 타이틀, 아티스트
            if(title != null && artist != null){
                title.setText(song.title);
                artist.setText(song.artistName);
            }

        }
    }

    // 재생 상태 변경을 받는 브로드캐스트 리시버
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(BroadcastActions.PLAY_STATE_CHANGED)) {
                updateUI();
            } else if(intent.getAction().equals(BroadcastActions.PLAY_NEXT_SONG)) {
                updateUINextSong();
            } else if(intent.getAction().equals(BroadcastActions.STOPPED)) {
                // 시작 초, duration, 시크바 초기화
                AudioApplication.getInstance().getServiceInterface().tempPause();
                updateUIEndToFirst();
            }
        }
    };

    public void registerBroadcast(){
        IntentFilter filterPlayState = new IntentFilter();
        filterPlayState.addAction(BroadcastActions.PLAY_STATE_CHANGED);

        IntentFilter filterNext = new IntentFilter();
        filterNext.addAction(BroadcastActions.PLAY_NEXT_SONG);

        IntentFilter filterStop = new IntentFilter();
        filterStop.addAction(BroadcastActions.STOPPED);

        registerReceiver(mBroadcastReceiver, filterPlayState);
        registerReceiver(mBroadcastReceiver, filterNext);
        registerReceiver(mBroadcastReceiver, filterStop);
    }

    public void unregisterBroadcast(){
        unregisterReceiver(mBroadcastReceiver);
    }



}
