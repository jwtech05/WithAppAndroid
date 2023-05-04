package com.example.withapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.withapp.POJO.CallbackData;
import com.example.withapp.RETROFIT.RetrofitInterface;
import com.example.withapp.RETROFIT.retrofit_client;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterEmail extends AppCompatActivity {
    Context context = this;
    EditText email;
    EditText vNumber;
    EditText password;
    EditText confirmPassword;
    Button vButton;
    Button cButton;
    Button continueButton;
    TextView nText;
    TextView cText;
    TextView noticePassword;
    TextView noticeConfirmPassword;
    RetrofitInterface service;
    int check;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_email);
        //가입시 사용할 이메일 입력
        email = (EditText) findViewById(R.id.editTextEmail);
        //인증번호 입력
        vNumber = (EditText) findViewById(R.id.editTextValidation);
        //인증하기 버튼
        vButton = (Button) findViewById(R.id.validationButton);
        //인증번호 확인 버튼
        cButton = (Button) findViewById(R.id.confirmButton);
        //계속하기 버튼
        continueButton = (Button) findViewById(R.id.continueButton);
        //안내텍스트
        nText = (TextView) findViewById(R.id.noticeText);
        //인증안내텍스트
        cText = (TextView) findViewById(R.id.cNoticeText);
        //비밀번호 입력
        password = (EditText) findViewById(R.id.editTextPassword);
        //비밀번호 형식 검사
        noticePassword = (TextView) findViewById(R.id.noticePassword);
        //비밀번호 확인 입력
        confirmPassword = (EditText) findViewById(R.id.editTextPasswordConfirm);
        //비밀번호 확인 형식 검사
        noticeConfirmPassword = (TextView) findViewById(R.id.confirmPasswordText);
        //retrofit 객체 선언
        service = retrofit_client.retrofit.create(RetrofitInterface.class);
        //이메일 가입 여부 판단해주는 값
        check = 1;
        //*************인증하기 버튼 클릭**************
        vButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailVerification();
            }
        });
        //*************인증번호 확인 버튼 클릭**************
        confirmVerification();
        //*************비밀번호/비밀번호 확인 형식 검사************
        PasswordTextWatcher passwordTextWatcher = new PasswordTextWatcher(password, confirmPassword);
        password.addTextChangedListener(passwordTextWatcher);
        confirmPassword.addTextChangedListener(passwordTextWatcher);
        //*************계속하기 버튼****************
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                move();
            }
        });
    }
    private void emailVerification(){

        new CheckEmailTask().execute(email.getText().toString());

        if(email.getText().toString().equals("")) {
            Toast.makeText(context,"이메일을 입력해주세요.",Toast.LENGTH_SHORT).show();
        }else if(check == 1){
            Call<CallbackData> emailCheck = service.sendEmail(email.getText().toString());
            emailCheck.enqueue(new Callback<CallbackData>() {
                @Override
                public void onResponse(Call<CallbackData> call, Response<CallbackData> response) {
                    Log.d("헤이", "검사야");
                    nText.setVisibility(View.VISIBLE);
                    vNumber.setVisibility(View.VISIBLE);
                    cButton.setVisibility(View.VISIBLE);
                    cText.setVisibility(View.VISIBLE);
                    CallbackData result = response.body();
                }
                @Override
                public void onFailure(Call<CallbackData> call, Throwable t) {
                    Log.d("헤이2", String.valueOf(t));
                }
            });
        }else{
            Toast.makeText(context,"이미 가입되어 있는 이메일 입니다.",Toast.LENGTH_SHORT).show();
        }
    };
    private void confirmVerification(){

        cButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Call<CallbackData> numCheck = service.sendVerNum(vNumber.getText().toString(),email.getText().toString());
                numCheck.enqueue(new Callback<CallbackData>() {
                    @Override
                    public void onResponse(Call<CallbackData> call, Response<CallbackData> response) {
                        CallbackData result = response.body();
                        String sResult = result.getMessage().toString();
                        Log.d("검증검증", sResult);
                        if(sResult.equals("1")){
                            cText.setText("인증번호 유효시간이 지났습니다.");
                            cText.setTextColor(Color.RED);
                        }else if(sResult.equals("2")){
                            cText.setText("인증번호가 올바르지 않습니다.");
                            cText.setTextColor(Color.RED);
                        }else if(sResult.equals("3")){
                            int textColor = ContextCompat.getColor(getApplicationContext(), R.color.teal_200);
                            cText.setText("인증되었습니다.");
                            cText.setTextColor(textColor);
                            email.setEnabled(false);
                            cButton.setEnabled(false);
                            cButton.setBackgroundColor(Color.GRAY);
                        }else{
                            cText.setText("");
                        }
                    }

                    @Override
                    public void onFailure(Call<CallbackData> call, Throwable t) {
                        Log.d("cButton오류", String.valueOf(t));
                    }
                });
            }
        });

    }
    private class PasswordTextWatcher implements TextWatcher {
        private EditText mPasswordField;
        private EditText mConfirmPasswordField;

        public PasswordTextWatcher(EditText passwordField, EditText confirmPasswordField) {
            mPasswordField = passwordField;
            mConfirmPasswordField = confirmPasswordField;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Add code to handle different EditText fields
            if (mPasswordField.isFocused()) {
                // Handle password field
                boolean isPasswordValid = isValidPassword(s.toString());
                if (isPasswordValid && mPasswordField.getText().toString().trim().length() > 0) {
                    int textColor = ContextCompat.getColor(getApplicationContext(), R.color.teal_200);
                    noticePassword.setTextColor(textColor);
                    noticePassword.setText("알맞은 형식입니다.");
                } else {
                    noticePassword.setText("[비밀번호 설정방법] \n 영문,숫자,특수문자 포함 8~16자");
                    noticePassword.setTextColor(Color.RED);
                }
            } else if (mConfirmPasswordField.isFocused()) {
                // Handle confirm password field
                String password = mPasswordField.getText().toString();
                String confirmPassword = mConfirmPasswordField.getText().toString();
                if (password.equals(confirmPassword)) {
                    int textColor = ContextCompat.getColor(getApplicationContext(), R.color.teal_200);
                    noticeConfirmPassword.setTextColor(textColor);
                    noticeConfirmPassword.setText("비밀번호가 일치합니다.");
                } else {
                    noticeConfirmPassword.setTextColor(Color.RED);
                    noticeConfirmPassword.setText("비밀번호가 일치하지 않습니다.");
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
        public boolean isValidPassword(String password) {
            if (password.length() < 8 || password.length() > 16) {
                return false;
            }

            Pattern letterPattern = Pattern.compile("[a-zA-z]");
            Pattern digitPattern = Pattern.compile("[0-9]");
            Pattern specialCharPattern = Pattern.compile("[!@#$%^&*()_+{}\\[\\]:;\"'<>,.?\\/`~-]");

            Matcher hasLetter = letterPattern.matcher(password);
            Matcher hasDigit = digitPattern.matcher(password);
            Matcher hasSpecialChar = specialCharPattern.matcher(password);

            return hasLetter.find() && hasDigit.find() && hasSpecialChar.find();
        }
    }
    private class CheckEmailTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... emails) {
            String email = emails[0];

            Call<String> checking = service.checkSignOrNot(email);

            try {
                Response<String> response = checking.execute();
                return response.body();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Log.d("리스폰값", result);
                if (result.trim().equals("이미 가입된 회원")) {
                    check = 2;
                } else {
                    check = 1;
                }
            } else {
                Log.d("로그인여부", "이메일 확인 중 오류 발생");
            }
        }
    }
    private void move() {
        Intent intent = new Intent(context, RegisterPhone.class);
        intent.putExtra("아이디", email.getText().toString());
        intent.putExtra("비밀번호", password.getText().toString());
        startActivity(intent);
    }
}