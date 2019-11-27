package com.example.colorplayer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.colorplayer.AudioApplication;
import com.example.colorplayer.OnLockService;
import com.example.colorplayer.db.PlayListDB;
import com.example.colorplayer.db.PlayListDao;
import com.example.colorplayer.fragment.FolderListFragment;
import com.example.colorplayer.fragment.PlayListFragment;
import com.example.colorplayer.model.PlayList;
import com.example.colorplayer.utils.BroadcastActions;
import com.example.colorplayer.R;
import com.example.colorplayer.adapter.SectionPageAdapter;
import com.example.colorplayer.animation.ZoomOutPageTransformer;
import com.example.colorplayer.fragment.SongListFragment;
import com.example.colorplayer.fragment.AlbumListFragment;
import com.example.colorplayer.fragment.ArtistListFragment;
import com.example.colorplayer.model.Song;
import com.example.colorplayer.utils.PreferencesUtility;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private Toolbar tb;
    private ViewPager mViewPager;
    ImageView albumArt;
    TextView title, artist;
    LinearLayout nowPlayingCard;
    ImageButton playButton, searchButton, registerButton, loginButton;
    Song song;
    SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());
    private PreferencesUtility mPreferences;
    private int pagePosition = 0;
    private PlayListDao playListDao;
    String result; // 재생 목록 이름

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 권한 거부시 앱 종료
        checkPermission();

        registerBroadcast();

        // 화면 체크 서비스 시작
        Intent intent = new Intent(
                getApplicationContext(),//현재제어권자
                OnLockService.class); // 이동할 컴포넌트
        startService(intent);

        mPreferences = PreferencesUtility.getInstance(getApplicationContext());
        playListDao = PlayListDB.getInstance(this).playListDao();

        albumArt = findViewById(R.id.album_art);
        title = findViewById(R.id.music_title);
        artist = findViewById(R.id.artist_name);
        nowPlayingCard = findViewById(R.id.content);

        searchButton = findViewById(R.id.icon_search);
        registerButton = findViewById(R.id.menu_register);
        loginButton = findViewById(R.id.menu_login);

        albumArt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveIntent = new Intent(getApplicationContext(), PlayerActivity.class);
                moveIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if(AudioApplication.getInstance().getServiceInterface().getPreparedState())
                    startActivity(moveIntent);
            }
        });

        nowPlayingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveIntent = new Intent(getApplicationContext(), PlayerActivity.class);
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
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                // 뷰페이저 선택된 포지션 쉐어드 저장
                mPreferences.setString(mPreferences.VIEW_PAGER_POSITION, "" + position);
                if(pagePosition == 4){
                    tb.getMenu().clear();
                    tb.inflateMenu(R.menu.menu_play_list);
                }
                else{
                    tb.getMenu().clear();
                    tb.inflateMenu(R.menu.menu);
                }
//                if(pagePosition == 4)
//                    tb.getMenu().getItem(1).setEnabled(true);
//                else
//                    tb.getMenu().getItem(1).setEnabled(false);
                pagePosition = position;
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        // 쉐어드에 저장된 페이지
        String initPosition = mPreferences.getString(mPreferences.VIEW_PAGER_POSITION);
        if(!initPosition.equals(""))
            setupViewPager(mViewPager, Integer.parseInt(initPosition));
        else
            setupViewPager(mViewPager, 0);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();

        // 현재 로그인 페이지에서 넘어올 때 id 값이 있으면 로그인 성공 메시지를 띄워줌
        if(getIntent().getExtras() != null
        && !getIntent().getExtras().getString("id").equals("")){
            Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
            // 메뉴에서
        }
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

    public void setupViewPager(ViewPager viewPager, int position) {
        adapter.addFragment(new SongListFragment(), "노래");
        adapter.addFragment(new AlbumListFragment(), "앨범");
        adapter.addFragment(new ArtistListFragment(), "아티스트");
        adapter.addFragment(new FolderListFragment(), "폴더");
        adapter.addFragment(new PlayListFragment(), "재생목록");

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
    }

    // Toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if(pagePosition == 4)
            //menu.getItem(1).setEnabled(true);
            inflater.inflate(R.menu.menu_play_list, menu);
        else
            //menu.getItem(1).setEnabled(false);
            inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d("test", "onPrepareOptionsMenu - 옵션메뉴가 " +
                "화면에 보여질때 마다 호출됨");
        if(mPreferences.isLoginStatus()){ // 로그인 한 상태: 로그인은 안보이게, 로그아웃은 보이게
            menu.getItem(4).setEnabled(true);
            menu.getItem(3).setEnabled(false);
        }else{ // 로그 아웃 한 상태 : 로그인 보이게, 로그아웃은 안보이게
            menu.getItem(3).setEnabled(true);
            menu.getItem(4).setEnabled(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.icon_search:
                break;
            case R.id.icon_add :
                createPlayList(getWindowManager().getDefaultDisplay());
                break;
            case R.id.menu_register :
                Intent registerIntent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(registerIntent);
                break;
            case R.id.menu_login :
                Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginIntent);
                break;
            case R.id.menu_logout :
                mPreferences.setLoginStatus(false);
                Toast.makeText(getApplicationContext(), "로그아웃 하였습니다."
                                        , Toast.LENGTH_SHORT ).show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void createPlayList(Display display) {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(this);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.custom_dialog);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        // 다이얼로그 크기 재설정
        Point size =new Point();
        display.getSize(size);
        Window window = dlg.getWindow();
        int x = (int)(size.x * 0.9f);
        int y = (int)(size.y * 0.27f);
        window.setLayout(x, y);

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        final EditText message = (EditText) dlg.findViewById(R.id.mesgase);
        final Button okButton = (Button) dlg.findViewById(R.id.okButton);
        final Button cancelButton = (Button) dlg.findViewById(R.id.cancelButton);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // '확인' 버튼 클릭시 저장
                String result = message.getText().toString();
                // 재생 목록 생성 후 DB 저장
                PlayList playList = new PlayList();
                playList.setTitle(result);
                playListDao.insertPlayList(playList);
                sendBroadcast(new Intent(BroadcastActions.PLAY_LIST_ADD));
                Toast.makeText(getApplicationContext(), "\"" +  message.getText().toString() + "\" 으로 재생 목록을 만들었습니다.", Toast.LENGTH_SHORT).show();
                // 커스텀 다이얼로그를 종료한다.
                dlg.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "취소 했습니다.", Toast.LENGTH_SHORT).show();
                // 커스텀 다이얼로그를 종료한다.
                dlg.dismiss();
            }
        });
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
