package com.example.colorplayer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.colorplayer.R;
import com.example.colorplayer.adapter.SectionPageAdapter;
import com.example.colorplayer.animation.ZoomOutPageTransformer;
import com.example.colorplayer.fragment.SongListFragment;
import com.example.colorplayer.fragment.AlbumListFragment;
import com.example.colorplayer.fragment.ArtistListFragment;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private Toolbar tb;
    private ViewPager mViewPager;
    SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 권한 거부시 앱 종료
        checkPermission();

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
        adapter.addFragment(new ArtistListFragment(), "폴더");
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
}
