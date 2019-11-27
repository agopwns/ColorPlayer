package com.example.colorplayer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.colorplayer.R;
import com.example.colorplayer.http.Example;
import com.example.colorplayer.http.Member;
import com.example.colorplayer.http.OpenApiService;
import com.example.colorplayer.utils.AES256Chiper;
import com.example.colorplayer.utils.PreferencesUtility;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.colorplayer.utils.AES256Chiper.AES_Encode;
import static com.example.colorplayer.utils.urlUtils.AWS_URL;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText textId, textPass1;
    ImageButton backButton, confirmButton;
    String mId, mPass;
    boolean isExistId, isEqualPassword;
    OpenApiService apiService;
    PreferencesUtility mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        textId = findViewById(R.id.login_id);
        // 회원가입에 성공해서 해당 액티비티로 이동했다면 아이디 값 뿌려주기
        if(getIntent().getExtras() != null)
            textId.setText(getIntent().getExtras().getString("id"));

        textPass1 = findViewById(R.id.login_password);

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
                // 1. 아이디 체크
                mId = textId.getText().toString();
                mPass = textPass1.getText().toString();

                Retrofit retrofit =
                        new Retrofit.Builder()
                                .baseUrl(AWS_URL)
                                .addConverterFactory(GsonConverterFactory.create()).build();

                apiService = retrofit.create(OpenApiService.class);
                Call<Object> res = apiService.getUserInfo(mId);
                res.enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        Log.d("RegisterActivity", "통신 성공 발생 : " + response.body().toString());
                        // 어차피 여기서는 서버에 아이디가 있는지만 확인하면 되므로 response body 가
                        // null 인지만 빠르게 체크하는 것이 속도면에서 나음
                        if(!response.body().toString().equals("{}")){
                            isExistId = true;

                            String json = response.body().toString();
                            Gson gson = new Gson();
                            json = json.replace("\n", "");



//                            JsonReader reader = new JsonReader(new StringReader(json));
//                            reader.setLenient(true);
                            json = json.trim();

                            Example data = gson.fromJson(json, Example.class);
                            Log.d("RegisterActivity", "파싱 성공 발생 : " + data.getItem().getId());
                            String password = data.getItem().getPassword();
                            String realPass = "";
                            // 복호화
                            try {
                                realPass = AES256Chiper.AES_Decode(password);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            } catch (NoSuchPaddingException e) {
                                e.printStackTrace();
                            } catch (InvalidKeyException e) {
                                e.printStackTrace();
                            } catch (InvalidAlgorithmParameterException e) {
                                e.printStackTrace();
                            } catch (IllegalBlockSizeException e) {
                                e.printStackTrace();
                            } catch (BadPaddingException e) {
                                e.printStackTrace();
                            }
                            if(realPass.equals(""))
                                Log.d("RegisterActivity", "복호화 실패 발생");
                            else
                                Log.d("RegisterActivity", "복호화 성공 발생");

                            // 패스워드 검사
                            if(mPass != null && !mPass.equals("") && mPass.equals(realPass)){
                                isEqualPassword = true;
//                                Toast.makeText(getApplicationContext(), "비번 같음"
//                                        , Toast.LENGTH_SHORT ).show();
                            } else {
                                isEqualPassword = false;
//                                Toast.makeText(getApplicationContext(), "비번 다름"
//                                        , Toast.LENGTH_SHORT ).show();
                                textPass1.setError("비밀 번호가 다릅니다.");
                            }

                        } else {
                            textId.setError("존재하지 않는 아이디입니다.");
                            isExistId = false;
                        }

                        // 모두 성공일 때만 로그인
                        if(isExistId && isEqualPassword){
                            // 쉐어드로 아이디와 로그인 상태 저장
                            mPreferences = PreferencesUtility.getInstance(getApplicationContext());
                            mPreferences.setString(PreferencesUtility.LOGIN_ID, mId);
                            mPreferences.setLoginStatus(true);
                            // 로그인 성공시 메인 액티비티로 id 넘겨주기
                            Intent moveIntent = new Intent(getApplicationContext(), MainActivity.class);
                            moveIntent.putExtra("id", mId);
                            moveIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(moveIntent);
                        }
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "통신 실패 발생", Toast.LENGTH_SHORT ).show();
                        Log.d("RegisterActivity", "통신 실패 발생 : " + t.toString());
                    }
                });



            }
        });




    }
}
