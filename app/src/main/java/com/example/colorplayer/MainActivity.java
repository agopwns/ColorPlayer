package com.example.colorplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

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

        tb = (Toolbar) findViewById(R.id.main_app_bar);
        setSupportActionBar(tb);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
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
}
