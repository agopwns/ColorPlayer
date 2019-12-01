package com.example.colorplayer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.colorplayer.AudioApplication;
import com.example.colorplayer.db.SongInfoDB;
import com.example.colorplayer.db.SongInfoDao;
import com.example.colorplayer.http.CommentApiService;
import com.example.colorplayer.http.Example;
import com.example.colorplayer.http.NullOnEmptyConverterFactory;
import com.example.colorplayer.model.Comment;
import com.example.colorplayer.model.SongInfo;
import com.example.colorplayer.utils.AES256Chiper;
import com.example.colorplayer.utils.BroadcastActions;
import com.example.colorplayer.R;
import com.example.colorplayer.model.Song;
import com.example.colorplayer.utils.PreferencesUtility;
import com.example.colorplayer.utils.RepeatActions;
import com.example.colorplayer.utils.Time;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.colorplayer.utils.urlUtils.AWS_COMMENT_URL;
import static com.example.colorplayer.utils.urlUtils.AWS_MEMBER_URL;

public class PlayerActivity extends AppCompatActivity {

    ImageButton backButton, optionButton,
            repeatButton, prevButton, playButton, nextButton, shuffleButton,
            favoriteButton, commentButton, playingListButton;
    ImageView albumArt;
    TextView title, artist, duration, totalTime,
            userId, userComment;
    Song song;
    SeekBar seekBar;
    String mId;
    private SongInfoDao dao;
    private int overflowcounter = 0;
    private boolean isActivityPaused = false;
    private int playCount = 0;
    int mDelay = 20;
    PreferencesUtility mPreferences;
    CommentApiService apiService;
    private static String TAG = "PlayerActivity";
    private List<Comment> mCommentList;
    HashMap<String, String> mCommentMap;
    private boolean isFadeIn;
    private LinearLayout commentLayout;
    private AlphaAnimation fadeInAnim, fadingAnim, fadeOutAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // 음악 재생은 AudioApplication 의 서비스로 작동
        albumArt = findViewById(R.id.album_art);
        title = findViewById(R.id.music_title);
        artist = findViewById(R.id.artist_name);
        duration = findViewById(R.id.song_start_time);
        totalTime = findViewById(R.id.song_end_time);
        seekBar = findViewById(R.id.song_progress_normal);
        userId = findViewById(R.id.user_id);
        userComment = findViewById(R.id.user_comment);
        commentLayout = findViewById(R.id.layout_comment);

        // 애니메이션 설정
        fadeInAnim = new AlphaAnimation(0, 1);
        fadeInAnim.setDuration(1300);
        fadeInAnim.setRepeatCount(1);
        fadeInAnim.setRepeatMode(Animation.REVERSE);

//        fadingAnim = new AlphaAnimation(1, 1);
//        fadingAnim.setDuration(1500);
//
//        fadeOutAnim = new AlphaAnimation(1, 0);
//        fadeOutAnim.setDuration(800);

        registerBroadcast();

        // dao 연결
        dao = SongInfoDB.getInstance(this).songInfoDao();

        // width 보다 길 경우 텍스트 움직임 설정을 위해
        title.setSelected(true);

        mPreferences = PreferencesUtility.getInstance(getApplicationContext());
        mId = mPreferences.getString(PreferencesUtility.LOGIN_ID);

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

        // 코멘트 버튼
        commentButton = findViewById(R.id.button_comment);
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 다이얼로그 띄우기
                createCommentDialog(getWindowManager().getDefaultDisplay());
                // 코멘트와 노래 정보, 시간초 post

            }
        });

        // 코멘트 로드
        mCommentList = new ArrayList<>();
        // 처음 액티비티 진입시 현재 재생 곡 데이터 바인딩
        // TODO : 서비스에서 로딩하지 않고 인텐트로 바로 전달 받으면 바로 UI 업데이트가 가능
        updateUINextSong();

        getComment();

    }

    private void getComment() {
        // 1. 해당 곡 정보 토대로 코멘트 가져오기
        // 통신
        String tempString = "";
        if(getIntent().getExtras() != null){
            tempString = getIntent().getExtras().getString("songTitle");
        }
        if(AudioApplication.getInstance().getServiceInterface().isPlaying()){
            song = AudioApplication.getInstance().getServiceInterface().getAudioItem();
            tempString = song.title;
        }

        // api gateway 를 통해 전송시 자동으로 인코딩 됨.
        // 어느 서비스에서 되는지는 정확히 모르겠음.
        //String urlEnc = URLEncoder.encode(tempString);
        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl(AWS_COMMENT_URL)
                        .addConverterFactory(new NullOnEmptyConverterFactory())
                        .addConverterFactory(GsonConverterFactory.create()).build();
        apiService = retrofit.create(CommentApiService.class);
        Call<Object> res = apiService.getCommentByTitleId(tempString);
        res.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {

                if(response.body() != null){

                    Log.d(TAG, "통신 성공 발생 : " + response.body().toString());
                    String json = response.body().toString();

                    Gson gson = new Gson();

                    Type listType = new TypeToken<ArrayList<Comment>>() {
                    }.getType();
                    mCommentList = gson.fromJson(json, listType);
                    Log.d(TAG, "파싱 성공 발생 : " );

                    // 해쉬맵으로 시간 : {유저 아이디, 코멘트} 형태로 변형
                    mCommentMap = new HashMap<>();
                    long beforeMin = 0;
                    long beforeSec = 0;
                    Random random = new Random();
                    int ran = random.nextInt(2);
                    for(int i = ran; i < mCommentList.size(); i++){
                        Comment item = mCommentList.get(i);

                        String min = item.getComTime().split(":")[0];
                        String sec = item.getComTime().split(":")[1];
                        long curMin = Long.parseLong(min);
                        long curSec = Long.parseLong(sec);
                        if(i != 0){
                            if(curMin - beforeMin < 1 && curSec - beforeSec > 3){
                                String temp = item.getMemId() + "," + item.getComContent();
                                mCommentMap.put(item.getComTime(), temp);
                                beforeMin = curMin;
                                beforeSec = curSec;
                            }
                        } else {
                            String temp = item.getMemId() + "," + item.getComContent();
                            mCommentMap.put(item.getComTime(), temp);
                            beforeMin = curMin;
                            beforeSec = curSec;
                        }
                    }
                    Log.d(TAG, "맵핑 성공 발생 : " );
                    Toast.makeText(getApplicationContext(), "맵핑 성공 발생", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "통신 성공, body 비어있음");
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "통신 실패 발생", Toast.LENGTH_SHORT ).show();
                Log.d("RegisterActivity", "통신 실패 발생 : " + t.toString());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (seekBar != null){
            isActivityPaused = false;
            seekBar.postDelayed(mUpdateProgress, mDelay);
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

    // 호출할 다이얼로그 함수를 정의한다.
    public void createCommentDialog(Display display) {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(this);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.custom_comment_dialog);

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
        final TextView time = (TextView) dlg.findViewById(R.id.time);
        final EditText message = (EditText) dlg.findViewById(R.id.message);
        final Button okButton = (Button) dlg.findViewById(R.id.okButton);
        final Button cancelButton = (Button) dlg.findViewById(R.id.cancelButton);

        // 시간 저장
        time.setText(duration.getText());

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // '확인' 버튼 클릭시 저장
                String result = message.getText().toString();
                // 객체 담기
                Comment comment = new Comment();
                comment.setMemId(mId);
                comment.setComSongName(song.title);
                comment.setComArtist(song.artistName);
                comment.setComContent(result);
                comment.setComTime(time.getText().toString());
                // 통신
                final Call<Comment> res = apiService.postUser(comment);

                res.enqueue(new Callback<Comment>() {
                    @Override
                    public void onResponse(Call<Comment> call, Response<Comment> response) {
                        final  Object message = response.body();
                        Toast.makeText(getApplicationContext(), "서버에 값을 전달했습니다 : ", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(Call<Comment> call, Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(getApplicationContext(), "서버와 통신중 에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                    }
                });

                sendBroadcast(new Intent(BroadcastActions.PLAY_LIST_ADD));
                Toast.makeText(getApplicationContext(), "\"" +  message.getText().toString() + "\" 코멘트를 등록했습니다.", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getApplicationContext(), "updateUINextSong()", Toast.LENGTH_SHORT).show();

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
            Toast.makeText(getApplicationContext(), "updateUINextSongWhenStop()", Toast.LENGTH_SHORT).show();
            getComment();
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
            Toast.makeText(getApplicationContext(), "updateUIEndToFirst()", Toast.LENGTH_SHORT).show();
            getComment();

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

            // 코멘트 업데이트
            if (mCommentMap != null && mCommentMap.size() > 0){
                // 검사
                String temp = mCommentMap.get(duration.getText());
                if(temp != null && !temp.equals("") && !isFadeIn){
                    isFadeIn = true;
                    userId.setText(temp.split(",")[0]);
                    userComment.setText(temp.split(",")[1]);
                    commentLayout.setVisibility(View.VISIBLE);
                    commentLayout.startAnimation(fadeInAnim);
//                    commentLayout.startAnimation(fadingAnim);
//                    commentLayout.startAnimation(fadeOutAnim);
                    commentLayout.setVisibility(View.INVISIBLE);

                    isFadeIn = false;
                }

            }
        }
        overflowcounter--;
        // 서비스가 준비됬는지를 꼼꼼히 확인하면
        // 딜레이를 높게 잡지 않아도 된다.

        if (overflowcounter < 0 && !isActivityPaused) {
            overflowcounter++;
            seekBar.postDelayed(mUpdateProgress, mDelay);
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
            } else if(intent.getAction().equals(BroadcastActions.PREPARED)) {
                // 시작 초, duration, 시크바 초기화
                getComment();
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

        IntentFilter filterPrepared = new IntentFilter();
        filterStop.addAction(BroadcastActions.PREPARED);

        registerReceiver(mBroadcastReceiver, filterPlayState);
        registerReceiver(mBroadcastReceiver, filterNext);
        registerReceiver(mBroadcastReceiver, filterStop);
        registerReceiver(mBroadcastReceiver, filterPrepared);
    }

    public void unregisterBroadcast(){
        unregisterReceiver(mBroadcastReceiver);
    }

}
