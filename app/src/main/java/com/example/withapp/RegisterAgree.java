package com.example.withapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import okhttp3.Interceptor;

public class RegisterAgree extends AppCompatActivity {
    private Context context;
    private CheckBox agreeCheck;
    private Button confirmButton;
    Intent intent;
    private String naverEmail;
    private String accessToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_agree);

        context = this;
        //약관에 동의하는 체크박스
        agreeCheck = (CheckBox) findViewById(R.id.checkBoxAgree);
        //클릭시 체크박스에 체크 되어 있을 시 다음 화면으로 넘어감
        confirmButton = (Button) findViewById(R.id.buttonAgree);

        naverEmail = getIntent().getStringExtra("아이디");
        accessToken = getIntent().getStringExtra("비밀번호");

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(agreeCheck.isChecked()){
                    move();
                }else{
                    Toast.makeText(context, "약관에 대한 동의 체크를 해주세요.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void move(){
        Intent intent;
        if(getRegisterDistinguish() == 1){
            intent = new Intent(context, RegisterEmail.class);
            startActivity(intent);
        }else{
            intent = new Intent(context, RegisterPhone.class);
            intent.putExtra("아이디", naverEmail);
            intent.putExtra("비밀번호", accessToken);
            startActivity(intent);
        }

    }

    private int getRegisterDistinguish() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        return sharedPreferences.getInt("회원가입유형", 1);
    }
}