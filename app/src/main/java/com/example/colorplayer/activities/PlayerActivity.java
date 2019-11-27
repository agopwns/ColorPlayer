package com.example.colorplayer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.colorplayer.AudioApplication;
import com.example.colorplayer.db.SongInfoDB;
import com.example.colorplayer.db.SongInfoDao;
import com.example.colorplayer.model.SongInfo;
import com.example.colorplayer.utils.BroadcastActions;
import com.example.colorplayer.R;
import com.example.colorplayer.model.Song;
import com.example.colorplayer.utils.RepeatActions;
import com.example.colorplayer.utils.Time;

public class PlayerActivity extends AppCompatActivity {

    ImageButton backButton, optionButton,
            repeatButton, prevButton, playButton, nextButton, shuffleButton,
            favoriteButton, playingListButton;
    ImageView albumArt;
    TextView title, artist, duration, totalTime;
    Song song;
    SeekBar seekBar;
    private SongInfoDao dao;
    private int overflowcounter = 0;
    private boolean isActivityPaused = false;
    private int playCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);

        // 음악 재생은 AudioApplication 의 서비스로 작동
        albumArt = findViewById(R.id.album_art);
        title = findViewById(R.id.music_title);
        artist = findViewById(R.id.artist_name);
        duration = findViewById(R.id.song_start_time);
        totalTime = findViewById(R.id.song_end_time);
        seekBar = findViewById(R.id.song_progress_normal);

        registerBroadcast();

        // dao 연결
        dao = SongInfoDB.getInstance(this).songInfoDao();

        // width 보다 길 경우 텍스트 움직임 설정을 위해
        title.setSelected(true);

        // 재생 and 일시정지
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

        // 이전 곡 재생
        prevButton = findViewById(R.id.button_prev);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioApplication.getInstance().getServiceInterface().rewind();
                //updateUI();
            }
        });

        // 다음 곡 재생
        nextButton = findViewById(R.id.button_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioApplication.getInstance().getServiceInterface().forward();
                //updateUI();
            }
        });

        // 진행 시크바 움직일 때 서비스에도 설정해줘야 함
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(AudioApplication.getInstance() != null && fromUser){
                    AudioApplication.getInstance().getServiceInterface().setSeekTo(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        // 셔플 버튼 클릭시 셔플된 곡 리스트를 서비스에 넘겨줘야 함
        shuffleButton = findViewById(R.id.button_shuffle);
        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AudioApplication.getInstance().getServiceInterface().getShuffleState())
                    shuffleButton.setImageResource(R.drawable.shuffle_unuse);
                else
                    shuffleButton.setImageResource(R.drawable.baseline_shuffle_white_24);

                // 셔플 상태 변경
                AudioApplication.getInstance().getServiceInterface().toggleShuffleList();
            }
        });

        // 반복 버튼
        repeatButton = findViewById(R.id.button_repeat);
        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(AudioApplication.getInstance().getServiceInterface().getRepeatState().equals(RepeatActions.REPEAT_ALL))
                    repeatButton.setImageResource(R.drawable.baseline_repeat_one_white_24);
                else if(AudioApplication.getInstance().getServiceInterface().getRepeatState().equals(RepeatActions.REPEAT_ONE))
                    repeatButton.setImageResource(R.drawable.shuffle_disabled);
                else if(AudioApplication.getInstance().getServiceInterface().getRepeatState().equals(RepeatActions.REPEAT_NONE))
                    repeatButton.setImageResource(R.drawable.baseline_repeat_white_24);

                // 반복 상태 변경
                AudioApplication.getInstance().getServiceInterface().toggleRepeatState();
            }
        });

        // 재생중인 목록 버튼
        playingListButton = findViewById(R.id.button_playing_list);
        playingListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveIntent = new Intent(getApplicationContext(), PlayingListActivity.class);
                moveIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if(AudioApplication.getInstance().getServiceInterface().getPreparedState())
                    startActivity(moveIntent);
            }
        });

        // 좋아요 버튼
        favoriteButton = findViewById(R.id.button_favorite);
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 현재 곡 id, isFavorite 저장
                SongInfo songInfo = new SongInfo();
                songInfo.setId(song.id);
                SongInfo beforeSongInfo = dao.getSongInfosById(song.id);
                if(beforeSongInfo == null){
                    songInfo.setFavorite(true);
                    favoriteButton.setImageResource(R.drawable.baseline_favorite_white_24);
                    dao.insertSongInfo(songInfo);
                } else {
                    if(beforeSongInfo.isFavorite()){
                        songInfo.setFavorite(false);
                        favoriteButton.setImageResource(R.drawable.baseline_favorite_border_white_24);
                    } else {
                        songInfo.setFavorite(true);
                        favoriteButton.setImageResource(R.drawable.baseline_favorite_white_24);
                    }
                    dao.updateSongInfo(songInfo);
                }
            }
        });

        // 처음 액티비티 진입시 현재 재생 곡 데이터 바인딩
        updateUINextSong();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (seekBar != null){
            isActivityPaused = false;
            seekBar.postDelayed(mUpdateProgress, 10);
        }

        if(AudioApplication.getInstance().getServiceInterface().getPreparedState()){
            if(playCount != 0)
                updateUINextSong();
            playCount++;
        }
        if(AudioApplication.getInstance().getServiceInterface().getShuffleState())
            shuffleButton.setImageResource(R.drawable.baseline_shuffle_white_24);
        else
            shuffleButton.setImageResource(R.drawable.shuffle_unuse);

        if(AudioApplication.getInstance().getServiceInterface().getRepeatState().equals(RepeatActions.REPEAT_ALL))
            repeatButton.setImageResource(R.drawable.baseline_repeat_white_24);
        else if(AudioApplication.getInstance().getServiceInterface().getRepeatState().equals(RepeatActions.REPEAT_ONE))
            repeatButton.setImageResource(R.drawable.baseline_repeat_one_white_24);
        else if(AudioApplication.getInstance().getServiceInterface().getRepeatState().equals(RepeatActions.REPEAT_NONE))
            repeatButton.setImageResource(R.drawable.shuffle_disabled);
    }

    @Override
    public void onPause() {
        super.onPause();
        isActivityPaused = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
    }

    private void updateUI() {
        if(AudioApplication.getInstance() != null){
            song = AudioApplication.getInstance().getServiceInterface().getAudioItem();
            if(playButton != null){
                if (AudioApplication.getInstance().getServiceInterface().isPlaying()) {
                    playButton.setImageResource(R.drawable.baseline_pause_white_36);
                } else {
                    playButton.setImageResource(R.drawable.baseline_play_arrow_white_36);
                }
                Log.d("PlayerActivity", "updateUI()");
            }
        }
    }

    private void testPlayCount(){
        if(song != null && dao != null){
            SongInfo temp = dao.getSongInfosById(song.id);
            if(temp != null){
                Log.d("testPlayCount", "현재 곡 id : " + temp.getId()
                        + "\r\n현재 곡 제목 : " + temp.getTitle()
                        + "\r\n현재 곡 재생 카운트 : " + temp.getPlayCount());
            }
        }
    }

    private void updateUINextSong() {
        if(AudioApplication.getInstance() != null){
            song = AudioApplication.getInstance().getServiceInterface().getAudioItem();

            if(playButton != null){
                Log.d("PlayerActivity", "노래재생 updateUINextSong()");
//                if(AudioApplication.getInstance().getServiceInterface().isPlaying()){
                    playButton.setImageResource(R.drawable.baseline_pause_white_36);
//                }else{
//                    playButton.setImageResource(R.drawable.baseline_play_arrow_white_36);
//                }
                Log.d("PlayerActivity", "updateUINextSong()");
            }
            // 좋아요 버튼
            if(favoriteButton != null && song != null){
                SongInfo songInfo = dao.getSongInfosById(song.id);
                if(songInfo != null){
                    if(songInfo.isFavorite())
                        favoriteButton.setImageResource(R.drawable.baseline_favorite_white_24);
                    else
                        favoriteButton.setImageResource(R.drawable.baseline_favorite_border_white_24);
                } else {
                    favoriteButton.setImageResource(R.drawable.baseline_favorite_border_white_24);
                }
            }
            testPlayCount();

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

    private void updateUINextSongWhenStop() {
        if(AudioApplication.getInstance() != null){
            song = AudioApplication.getInstance().getServiceInterface().getAudioItem();
            if(playButton != null){
                if(AudioApplication.getInstance().getServiceInterface().isPlaying()){
                    playButton.setImageResource(R.drawable.baseline_play_arrow_white_36);
                }else{
                    playButton.setImageResource(R.drawable.baseline_pause_white_36);
                }
                Log.d("PlayerActivity", "updateUINextSongWhenStop()");
            }
            // 좋아요 버튼
            if(favoriteButton != null && song != null){
                SongInfo songInfo = dao.getSongInfosById(song.id);
                if(songInfo != null){
                    if(songInfo.isFavorite())
                        favoriteButton.setImageResource(R.drawable.baseline_favorite_white_24);
                    else
                        favoriteButton.setImageResource(R.drawable.baseline_favorite_border_white_24);
                } else {
                    favoriteButton.setImageResource(R.drawable.baseline_favorite_border_white_24);
                }
            }
            testPlayCount();
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

    private void updateUIEndToFirst() {
        if(AudioApplication.getInstance() != null){

            if(playButton != null)
                playButton.setImageResource(R.drawable.baseline_play_arrow_white_36);
            Log.d("PlayerActivity", "updateUIEndToFirst()");

            song = AudioApplication.getInstance().getServiceInterface().getAudioItem();

            // 좋아요 버튼
            if(favoriteButton != null && song != null){
                SongInfo songInfo = dao.getSongInfosById(song.id);
                if(songInfo != null){
                    if(songInfo.isFavorite())
                        favoriteButton.setImageResource(R.drawable.baseline_favorite_white_24);
                    else
                        favoriteButton.setImageResource(R.drawable.baseline_favorite_border_white_24);
                } else {
                    favoriteButton.setImageResource(R.drawable.baseline_favorite_border_white_24);
                }
            }
            testPlayCount();
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

            long maxDuration = song.duration;

            if (seekBar != null && maxDuration != 0) {

                seekBar.setMax((int)maxDuration);
                seekBar.setProgress(0);

                totalTime.setText(Time.makeShortTimeString(getApplication(), maxDuration / 1000));

                if (duration != null && this != null)
                    duration.setText(Time.makeShortTimeString(getApplication(), 0));
            }
        }
    }

    public Runnable mUpdateProgress = new Runnable() {

        @Override
        public void run() {
            setPlayInfo();
        }
    };

    private void setPlayInfo() {
        long position = AudioApplication.getInstance().getServiceInterface().getPosition();
        long maxDuration = AudioApplication.getInstance().getServiceInterface().getDuration();

        if (seekBar != null && maxDuration != 0) {

            seekBar.setMax((int)maxDuration);
            seekBar.setProgress((int) position);

            totalTime.setText(Time.makeShortTimeString(getApplication(), maxDuration / 1000));

            if (duration != null && this != null && position / 1000 <= maxDuration / 1000)
                duration.setText(Time.makeShortTimeString(getApplication(), position / 1000));
        }
        overflowcounter--;
        // 서비스가 준비됬는지를 꼼꼼히 확인하면
        // 딜레이를 높게 잡지 않아도 된다.
        int delay = 10;
        if (overflowcounter < 0 && !isActivityPaused) {
            overflowcounter++;
            seekBar.postDelayed(mUpdateProgress, delay);
        }
    }

    // 재생 상태 변경을 받는 브로드캐스트 리시버
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(BroadcastActions.PLAY_STATE_CHANGED)) {
                updateUI();
            } else if(intent.getAction().equals(BroadcastActions.PLAY_NEXT_SONG)) {

                if(AudioApplication.getInstance().getServiceInterface().isPlaying())
                    updateUINextSong();
                else
                    updateUINextSongWhenStop();
            } else if(intent.getAction().equals(BroadcastActions.STOPPED)) {
                // 시작 초, duration, 시크바 초기화
                updateUIEndToFirst();
                AudioApplication.getInstance().getServiceInterface().tempPause();
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
