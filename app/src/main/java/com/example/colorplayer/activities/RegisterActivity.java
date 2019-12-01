package com.example.colorplayer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.colorplayer.R;
import com.example.colorplayer.model.Member;
import com.example.colorplayer.http.NullOnEmptyConverterFactory;
import com.example.colorplayer.http.OpenApiService;
import com.google.android.material.textfield.TextInputEditText;

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

import static com.example.colorplayer.utils.AES256Chiper.AES_Decode;
import static com.example.colorplayer.utils.AES256Chiper.AES_Encode;
import static com.example.colorplayer.utils.urlUtils.AWS_MEMBER_URL;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText textId, textPass1, textPass2;
    ImageButton backButton, confirmButton;
    String mId, mPass1, mPass2;
    boolean isNotExistId, isEqualPassword;
    OpenApiService apiService;
    private static String TAG = "RegisterActivity";

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
                        .baseUrl(AWS_MEMBER_URL)
                        .addConverterFactory(new NullOnEmptyConverterFactory())
                        .addConverterFactory(GsonConverterFactory.create()).build();

                apiService = retrofit.create(OpenApiService.class);
                Call<Object> res = apiService.getUserInfo(mId);

                try {
                    res.enqueue(new Callback<Object>() {
                        @Override
                        public void onResponse(Call<Object> call, Response<Object> response) {
                            //Log.d("RegisterActivity", "통신 성공 발생 : " + response.body().toString());
                            // 어차피 여기서는 서버에 아이디가 있는지만 확인하면 되므로 response body 가
                            // null 인지만 빠르게 체크하는 것이 속도면에서 나음
                            if(response.body() != null){
                                textId.setError("이미 있는 아이디입니다.");
                                isNotExistId = false;
                            } else {
                                Toast.makeText(getApplicationContext(), "해당 아이디 없음 : "
                                        , Toast.LENGTH_SHORT ).show();
                                isNotExistId = true;
                            }

                            // 패스워드 검사
                            if(mPass1 != null && !mPass1.equals("") && mPass1.equals(mPass2)){
                                isEqualPassword = true;
                            } else {
                                isEqualPassword = false;
                                textPass2.setError("비밀 번호가 다릅니다.");
                            }

                            // 모두 성공일 때만 회원 정보 전송
                            if(isNotExistId && isEqualPassword){
                                String aesPass = "";
                                try {
                                    aesPass = AES_Encode(mPass1);
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

                                if(aesPass.equals("")) return;

                                String test = "";
                                try {
                                    test = AES_Decode(aesPass);
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

                                System.out.println(test);
                                //aesPass = aesPass.replace("==\n","");
                                //aesPass = aesPass.replace("\\","");
                                //aesPass = aesPass.trim();
                                Member member = new Member(mId, aesPass);
                                final Call<Member> res = apiService.postUser(member);

                                res.enqueue(new Callback<Member>() {
                                    @Override
                                    public void onResponse(Call<Member> call, Response<Member> response) {
                                        final  Object message = response.body();
                                        Toast.makeText(getApplicationContext(), "서버에 값을 전달했습니다 : ", Toast.LENGTH_SHORT).show();

                                        // 저장 성공시 로그인 페이지로 id 넘겨주기
                                        Intent moveIntent = new Intent(getApplicationContext(), LoginActivity.class);
                                        moveIntent.putExtra("id", mId);
                                        moveIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                        startActivity(moveIntent);
                                    }
                                    @Override
                                    public void onFailure(Call<Member> call, Throwable t) {
                                        t.printStackTrace();
                                        Toast.makeText(getApplicationContext(), "서버와 통신중 에러가 발생했습니다", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<Object> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "통신 실패 발생", Toast.LENGTH_SHORT ).show();
                            Log.d("RegisterActivity", "통신 실패 발생 : " + t.toString());
                        }
                    });
                } catch (Exception e) {
                    Log.d(TAG, "통신 에러 발생 : " + e);
                }



            }
        });



    }
}
