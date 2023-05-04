package com.example.withapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.withapp.POJO.CallbackData;
import com.example.withapp.RETROFIT.RetrofitInterface;
import com.example.withapp.RETROFIT.retrofit_client;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class test extends AppCompatActivity {
    Context context;
    Button logout;
    TextView show;
    String number;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        context = this;
        logout = findViewById(R.id.logoutButton);
        show = findViewById(R.id.showView);

        number = getMemberId();

        RetrofitInterface service = retrofit_client.retrofit.create(RetrofitInterface.class);
        Call<String> loginCheck = service.memberInfo(number);
        loginCheck.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String mention = response.body();
                show.setText(mention);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRidOfData();
                intent = new Intent(context, Login.class);
                startActivity(intent);
            }
        });
    }

    private void getRidOfData(){
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("자동로그인", false);
        editor.putString("회원번호", "");
        editor.apply();
    }

    private String getMemberId(){
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        return sharedPreferences.getString("회원번호", "");
    }
}