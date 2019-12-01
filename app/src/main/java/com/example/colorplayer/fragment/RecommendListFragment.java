package com.example.colorplayer.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.colorplayer.R;
import com.example.colorplayer.activities.LoginActivity;
import com.example.colorplayer.adapter.EventAdapter;
import com.example.colorplayer.adapter.SongAdapter;
import com.example.colorplayer.dataloader.SongLoader;
import com.example.colorplayer.http.EventApiService;
import com.example.colorplayer.http.Example;
import com.example.colorplayer.http.NullOnEmptyConverterFactory;
import com.example.colorplayer.http.OpenApiService;
import com.example.colorplayer.model.Comment;
import com.example.colorplayer.model.Event;
import com.example.colorplayer.model.Member;
import com.example.colorplayer.model.PlayList;
import com.example.colorplayer.model.Song;
import com.example.colorplayer.utils.BroadcastActions;
import com.example.colorplayer.utils.PreferencesUtility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

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
    private EventAdapter adapter;
    PreferencesUtility mPreferences;
    private String mId;
    private ArrayList<Event> mList = new ArrayList<>();
    private static String TAG = "RecommendListFragment";
    EventApiService apiService;

    public RecommendListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend_list, container, false);

        try {

            mPreferences = PreferencesUtility.getInstance(getActivity());
            mId = mPreferences.getString(PreferencesUtility.LOGIN_ID);

            // xml 에서 리사이클러뷰 가로 설정 미리 함
            eventRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_event_list);
            eventRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            bestRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_best_ten_list);
            bestRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            favoriteRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_best_favorite_list);
            favoriteRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            // 통신
            Retrofit retrofit =
                    new Retrofit.Builder()
                            .baseUrl(AWS_EVENT_URL) // TODO : AWS_EVENT_URL 로 변경
                            .addConverterFactory(new NullOnEmptyConverterFactory())
                            .addConverterFactory(GsonConverterFactory.create()).build();
            apiService = retrofit.create(EventApiService.class);

            // 이벤트 관리자로 로그인 할 때 visible. 기본은 invisible 상태
            addEventButton = (ImageButton) view.findViewById(R.id.btn_add_event);
            if(mId.equals("EventAdmin")){
                addEventButton.setVisibility(View.VISIBLE);
                addEventButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createEventList(getActivity().getWindowManager().getDefaultDisplay());
                    }
                });
            } else {
                addEventButton.setVisibility(View.INVISIBLE);
            }

            apiService = retrofit.create(EventApiService.class);
            Call<Object> res = apiService.getEvents();

            // aws 서버에 등록된 이벤트 정보 가져오기
            try {
                res.enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {

                        String json = response.body().toString();
                        Gson gson = new Gson();
                        Type listType = new TypeToken<ArrayList<Event>>() {
                        }.getType();
                        mList = gson.fromJson(json, listType);
                        Log.d(TAG, "파싱 성공 발생. 이벤트 리스트 사이즈" + mList.size());
                        Toast.makeText(getActivity(), "통신, 파싱 성공 발생", Toast.LENGTH_SHORT ).show();
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        Toast.makeText(getActivity(), "통신 실패 발생", Toast.LENGTH_SHORT ).show();
                        Log.d(TAG, "통신 실패 발생 : " + t.toString());
                    }
                });
            } catch (Exception e) {
                Log.d(TAG, "통신 에러 발생 : " + e);
            }

            // TODO : 어뎁터 준비
            adapter = new EventAdapter(getActivity(), mList);
            eventRecyclerView.setAdapter(adapter);

        } catch (Exception e){
            Log.d("SongListFragment", "onCreateView 에러 발생 : " + e);
        }
        return view;
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
        int y = (int)(size.y * 0.27f);
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