package com.example.colorplayer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.colorplayer.R;
import com.example.colorplayer.http.MemberData;
import com.example.colorplayer.http.OpenApiService;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonArray;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.colorplayer.utils.urlUtils.AWS_URL;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText textId, textPass1, textPass2;
    ImageButton backButton, confirmButton;
    String mId, mPass1, mPass2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        textId = findViewById(R.id.edit_id);
        textPass1 = findViewById(R.id.edit_password1);
        textPass2 = findViewById(R.id.edit_password2);



        backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        confirmButton = findViewById(R.id.button_confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO : 회원 가입을 위한 확인
                // 1. 아이디 중복 체크
                mId = textId.getText().toString();
                mPass1 = textPass1.getText().toString();
                mPass2 = textPass2.getText().toString();

                Retrofit retrofit =
                        new Retrofit.Builder()
                        .baseUrl(AWS_URL)
                        .addConverterFactory(GsonConverterFactory.create()).build();

                OpenApiService apiService = retrofit.create(OpenApiService.class);
                Call<MemberData> res = apiService.getUserInfo(mId);
//                try {
//                    System.out.println(res.execute().body());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                res.enqueue(new Callback<MemberData>() {
                    @Override
                    public void onResponse(Call<MemberData> call, Response<MemberData> response) {
                        Toast.makeText(getApplicationContext(), "통신 성공 발생 : ", Toast.LENGTH_SHORT ).show();
                        Log.d("RegisterActivity", "통신 성공 발생 : " + response.body().toString());
                    }

                    @Override
                    public void onFailure(Call<MemberData> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "통신 실패 발생", Toast.LENGTH_SHORT ).show();
                        Log.d("RegisterActivity", "통신 실패 발생 : " + t.toString());
                    }
                });


                mId = textId.getText().toString();

                // 2. 비밀번호 확인



            }
        });



    }
}
