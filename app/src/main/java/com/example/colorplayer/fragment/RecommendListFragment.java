package com.example.colorplayer.fragment;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.colorplayer.R;
import com.example.colorplayer.adapter.BestSongListAdapter;
import com.example.colorplayer.adapter.EventAdapter;
import com.example.colorplayer.adapter.SongAdapter;
import com.example.colorplayer.dataloader.AlbumLoader;
import com.example.colorplayer.dataloader.SongLoader;
import com.example.colorplayer.db.SongInfoDB;
import com.example.colorplayer.db.SongInfoDao;
import com.example.colorplayer.http.EventApiService;
import com.example.colorplayer.http.NullOnEmptyConverterFactory;
import com.example.colorplayer.model.Album;
import com.example.colorplayer.model.Event;

import com.example.colorplayer.model.Song;
import com.example.colorplayer.model.SongInfo;
import com.example.colorplayer.utils.PreferencesUtility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.colorplayer.utils.urlUtils.AWS_EVENT_URL;

public class RecommendListFragment extends Fragment{
    ViewPager viewPager;
    RecyclerView eventRecyclerView;
    RecyclerView bestRecyclerView;
    RecyclerView favoriteRecyclerView;
    ImageButton addEventButton;
    ImageView bestSongAlbumArt;
    TextView bestSongYear, bestSongTitle, bestSongArtist, bestSongCount;

    private EventAdapter adapter;
    PreferencesUtility mPreferences;
    private String mId;
    private ArrayList<Event> mList = new ArrayList<>();
    private static String TAG = "RecommendListFragment";
    EventApiService apiService;
    Handler handler;
    private int adapterPosition;
    ViewFlipper viewFlipper;
    SongInfoDao dao;
    BestSongListAdapter bestSongAdapter;
    List<Song> mSongList, mFavSongList;


    public RecommendListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend_list, container, false);

        try {

            // 통신
            getEventInfoFromAws();

            bestSongAlbumArt = view.findViewById(R.id.best_song_albumArt);
            bestSongYear = view.findViewById(R.id.best_song_year);
            bestSongArtist = view.findViewById(R.id.best_song_artist);
            bestSongTitle = view.findViewById(R.id.best_song_title);
            bestSongCount = view.findViewById(R.id.best_song_count);

            mPreferences = PreferencesUtility.getInstance(getActivity());
            mId = mPreferences.getString(PreferencesUtility.LOGIN_ID);

            LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity());
            layoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);

            LinearLayoutManager layoutManager3 = new LinearLayoutManager(getActivity());
            layoutManager3.setOrientation(LinearLayoutManager.HORIZONTAL);

            viewFlipper = (ViewFlipper) view.findViewById(R.id.image_slide);

            bestRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_best_ten_list);
            bestRecyclerView.setLayoutManager(layoutManager2);

            favoriteRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_best_favorite_list);
            favoriteRecyclerView.setLayoutManager(layoutManager3);

            // 이벤트 관리자로 로그인 할 때 visible. 기본은 invisible 상태
            addEventButton = (ImageButton) view.findViewById(R.id.btn_add_event);
            if(mId.equals("EventAdmin")){
                Toast.makeText(getActivity(), "EventAdmin 확인 visible", Toast.LENGTH_SHORT ).show();
                addEventButton.setVisibility(View.VISIBLE);
                addEventButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createEventList(getActivity().getWindowManager().getDefaultDisplay());
                    }
                });
            } else {
                Toast.makeText(getActivity(), "EventAdmin 미확인 visible", Toast.LENGTH_SHORT ).show();
                addEventButton.setVisibility(View.INVISIBLE);
            }

            // BestSong, TOP10 정보 ROOM DB 에서 가져오기
            dao = SongInfoDB.getInstance(getContext()).songInfoDao();
            SongInfo song = dao.getMaxCountSong();
            List<SongInfo> idList = dao.getSongListCountTopTen();

            // BestSong 바인딩
            Song bestSong = SongLoader.getSongForID(getContext(), song.getId());
            Album album = AlbumLoader.getAlbum(getContext(), bestSong.albumId);

            // 앨범 이미지 로드
            Uri uri = ContentUris
                    .withAppendedId(
                            Uri.parse("content://media/external/audio/albumart")
                            , bestSong.albumId);
            Glide
                    .with(getContext())
                    .load(uri)
                    .error(R.drawable.test)
                    .into(bestSongAlbumArt);

            bestSongArtist.setText(bestSong.artistName);
            bestSongTitle.setText(bestSong.title);
            bestSongYear.setText("" + album.year);
            bestSongCount.setText("" + song.getPlayCount());

            mSongList = new ArrayList<>();
            for(int i = 0; i < idList.size(); i++){
                mSongList.add(SongLoader.getSongForID(getContext(), idList.get(i).getId()));
            }
            // TOP 10 바인딩
            bestSongAdapter = new BestSongListAdapter(
                    (AppCompatActivity)getContext(),
                            mSongList,
                    false,
                        false);
            bestRecyclerView.setAdapter(bestSongAdapter);

            // Favorite 데이터 가져오기
            List<SongInfo> fIdList = dao.getSongListFavorite();
            mFavSongList = new ArrayList<>();
            for(int i = 0; i < fIdList.size(); i++){
                mFavSongList.add(SongLoader.getSongForID(getContext(), fIdList.get(i).getId()));
            }
            // Favorite 바인딩
            bestSongAdapter = new BestSongListAdapter(
                    (AppCompatActivity)getContext(),
                    mFavSongList,
                    false,
                    false);
            favoriteRecyclerView.setAdapter(bestSongAdapter);


        } catch (Exception e){
            Log.d(TAG, "onCreateView 에러 발생 : " + e);
        }
        return view;
    }

    private void getEventInfoFromAws() {
        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl(AWS_EVENT_URL) // TODO : AWS_EVENT_URL 로 변경
                        .addConverterFactory(new NullOnEmptyConverterFactory())
                        .addConverterFactory(GsonConverterFactory.create()).build();
        apiService = retrofit.create(EventApiService.class);
        Call<Object> res = apiService.getEvents();

        // aws 서버에 등록된 이벤트 정보 가져오기
        try {
            res.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {

                    String json = response.body().toString();
                    Gson gson = new Gson();
                    Type listType = new TypeToken<ArrayList<Event>>() {}.getType();
                    mList = gson.fromJson(json, listType);
                    Log.d(TAG, "파싱 성공 발생. 이벤트 리스트 사이즈" + mList.size());
                    Toast.makeText(getActivity(), "통신, 파싱 성공 발생", Toast.LENGTH_SHORT).show();

                    // 뷰 플리퍼로 변경
                    // 앨범 이미지 로드
                    for(int i = 0; i < mList.size(); i++){
                        fllipperImages(i);
                    }
                }
                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Toast.makeText(getActivity(), "통신 실패 발생", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "통신 실패 발생 : " + t.toString());
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "통신 에러 발생 : " + e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // TODO : 어뎁터 준비
        Log.d(TAG, "onResume 진입 발생 : ");

    }

    public void fllipperImages(final int position) {
        ImageView imageView = new ImageView(getActivity());

        String awsUrl =
                "https://colorplayer-deployments-mobilehub-937790274.s3.ap-northeast-2.amazonaws.com/";
        Uri uri = Uri.parse(awsUrl + mList.get(position).EventId + ".jpg") ;
        Glide
                .with(getActivity())
                .load(uri)
                .centerCrop()
                .placeholder(R.drawable.test)
                .error(R.drawable.test)
                .into(imageView);

        viewFlipper.addView(imageView);      // 이미지 추가
        viewFlipper.setFlipInterval(4000);       // 자동 이미지 슬라이드 딜레이시간(1000 당 1초)
        viewFlipper.startFlipping();

        viewFlipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int curPosition = viewFlipper.getDisplayedChild();
                Intent i3 = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://" + mList.get(curPosition).EventUrl + "/"));
                startActivity(i3);
            }
        });

        // animation
        viewFlipper.setInAnimation(getActivity(),android.R.anim.slide_in_left);
        viewFlipper.setOutAnimation(getActivity(),android.R.anim.slide_out_right);
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void createEventList(Display display) {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(getActivity());

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.custom_event_dialog);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        // 다이얼로그 크기 재설정
        Point size =new Point();
        display.getSize(size);
        Window window = dlg.getWindow();
        int x = (int)(size.x * 0.9f);
        int y = (int)(size.y * 0.4f);
        window.setLayout(x, y);

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        final EditText eventId = (EditText) dlg.findViewById(R.id.et_event_id);
        final EditText eventUrl = (EditText) dlg.findViewById(R.id.et_event_url);
        final Button okButton = (Button) dlg.findViewById(R.id.okButton);
        final Button cancelButton = (Button) dlg.findViewById(R.id.cancelButton);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // postEvent
                Event event = new Event(eventId.getText().toString(), eventUrl.getText().toString());
                final Call<Event> res = apiService.postEvent(event);
                res.enqueue(new Callback<Event>() {
                    @Override
                    public void onResponse(Call<Event> call, Response<Event> response) {
                        final  Object message = response.body();
                        Toast.makeText(getActivity(), "서버에 값을 전달했습니다 : ", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(Call<Event> call, Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(getActivity(), "서버와 통신중 에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                    }
                });

                // 커스텀 다이얼로그를 종료한다.
                dlg.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "취소 했습니다.", Toast.LENGTH_SHORT).show();
                // 커스텀 다이얼로그를 종료한다.
                dlg.dismiss();
            }
        });
    }

//    private class loadSongs extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... params) {
//            if (getActivity() != null)
//                adapter = new SongAdapter((AppCompatActivity) getActivity(), SongLoader.getAllSongs(getActivity())
//                        , false, false);
//
//            return "Executed";
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            recyclerView.setAdapter(adapter);
//        }
//
//        @Override
//        protected void onPreExecute() {
//        }
//    }
}