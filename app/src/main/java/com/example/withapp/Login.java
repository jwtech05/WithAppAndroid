package com.example.withapp;



import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.withapp.POJO.CallbackData;
import com.example.withapp.POJO.UserInfo;
import com.example.withapp.RETROFIT.RetrofitInterface;
import com.example.withapp.RETROFIT.retrofit_client;
import com.navercorp.nid.NaverIdLoginSDK;
import com.navercorp.nid.oauth.view.NidOAuthLoginButton;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {
    private Context context;
    private NidOAuthLoginButton buttonOAuthLoginImg;
    public static NaverIdLoginSDK naverIdLoginSDK;
    private EditText userId;
    private EditText userPw;
    private String email;
    private TextView idValidation;
    private TextView loginWarning;
    private TextView register;
    private Button logout;
    private Button loginButton;
    private RetrofitInterface service;
    private RetrofitInterface nService;
    private String naverToken;
    private String naverEmail;
    private String emailValidation ="^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private CheckBox rememberId;
    private CheckBox autoLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        //앱 로그인 아이디 입력
        userId = (EditText) findViewById(R.id.editTextId);
        //앱 로그인 패스워드 입력
        userPw = (EditText) findViewById(R.id.editTextPassword);
        //아이디 입력 형식 검사
        idValidation = (TextView) findViewById(R.id.idValidation);
        //아이디 입력 형식 검사
        loginWarning = (TextView) findViewById(R.id.loginWarning);
        //회원가입 텍스트
        register = (TextView) findViewById(R.id.register);
        //앱 로그인 버튼
        loginButton = (Button) findViewById(R.id.loginButton);
        //네이버 전용 로그인 버튼
        buttonOAuthLoginImg = (NidOAuthLoginButton) findViewById(R.id.buttonOAuthLoginImg);
        //아이디 기억 버튼
        rememberId = (CheckBox) findViewById(R.id.rememberId);
        //자동로그인 버튼
        autoLogin = (CheckBox) findViewById(R.id.autoLogin);
        if(getAutoLoginState()){
            Intent intent = new Intent(context, BottomNavi.class);
            startActivity(intent);
        }
        String savedEmail = getEmail();
        if(savedEmail != null){
            userId.setText(savedEmail);
        }
        Log.d("오잉", String.valueOf(getCheckboxState()));
        if(getCheckboxState()){
            rememberId.setChecked(true);
        }
        //**************네이버 로그인 API 객체 선언 및 초기화***************
        naverIdLoginSDK = NaverIdLoginSDK.INSTANCE;
        naverIdLoginSDK.initialize(context, getString(R.string.naver_client_id), getString(R.string.naver_client_secret), getString(R.string.naver_client_name));

        //**************네이버 로그인**************
        nService = retrofit_client.naverRetrofit.create(RetrofitInterface.class);
        naverLoginClick();

        //**************앱 전용 로그인**************
        service = retrofit_client.retrofit.create(RetrofitInterface.class);
        appLoginClick();
        //아이디 입력 형식 검사
        userId.addTextChangedListener(idValidate(idValidation,userId));

        //**************회원가입 화면 이동***********
        //회원가입 화면 이동 버튼
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                move(1);
            }
        });
    }
    //앱 전용 로그인
    private void appLoginClick(){

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //이미 생성된 Retrofit 객체를 사용하여 API 호출을 수행한다.
                Call<CallbackData> loginCheck = service.sendData(userId.getText().toString(), userPw.getText().toString());
                loginCheck.enqueue(new Callback<CallbackData>() {
                    @Override
                    public void onResponse(Call<CallbackData> call, Response<CallbackData> response) {
                        int textColor = ContextCompat.getColor(getApplicationContext(), R.color.teal_200);
                        CallbackData result = response.body();
                        String str;
                        str = result.getMessage();
                        saveMemberId(str);
                        //쉐어드에서 갖고 와서 넣어주기
                        if(result.getSuccess()) {
                            if(rememberId.isChecked()) {
                                saveEmail(userId.getText().toString());
                            }else {
                                deleteEmail();
                            }
                            successMove();

                        }else{
                            loginWarning.setText(str);
                            loginWarning.setTextColor(Color.RED);
                        }
                    }

                    @Override
                    public void onFailure(Call<CallbackData> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getMessage());
                    }
                });
            }
        });
    }
    private void naverLoginClick(){
        naverIdLoginSDK.showDevelopersLog(true);
        buttonOAuthLoginImg.setOAuthLogin(launcher);
    }
    //**************아이디기억****************
    private void saveEmail(String email){
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("아이디기억", email);
        editor.putBoolean("체크박스상태", true);
        editor.apply();
    }
    private void deleteEmail() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("아이디기억");
        editor.putBoolean("체크박스상태", false);
        editor.apply();
    }
    private String getEmail(){
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        return sharedPreferences.getString("아이디기억", null);
    }
    private boolean getCheckboxState() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        return sharedPreferences.getBoolean("체크박스상태", false);
    }

    //*****************자동로그인*******************
    private void saveAutoLoginState(boolean state) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("자동로그인", state);
        editor.apply();
    }

    private boolean getAutoLoginState() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        return sharedPreferences.getBoolean("자동로그인", false);
    }
    private void setRegisterDistinguish(int state) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("회원가입유형", state);
        editor.apply();
    }
    private void move(int kind){
        if(kind == 1) {
            setRegisterDistinguish(1);
            Intent intent = new Intent(context, RegisterAgree.class);
            startActivity(intent);
        }else{
            setRegisterDistinguish(2);
            Intent intent = new Intent(context, RegisterAgree.class);
            intent.putExtra("아이디", naverEmail);
            intent.putExtra("비밀번호", naverToken);
            startActivity(intent);
        }
    }
    //*************회원번호저장****************
    private void saveMemberId(String state) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("회원번호", state);
        editor.apply();
    }
    //로그인 성공 시
    private void successMove(){
        if(autoLogin.isChecked()){
            saveAutoLoginState(true);
        }
        Intent intent = new Intent(context, BottomNavi.class);
        startActivity(intent);
    }
    private ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    switch (result.getResultCode()) {
                        case RESULT_OK:
                            String token = "Bearer " + naverIdLoginSDK.getAccessToken();
                            Call<UserInfo> naverLogin = nService.sendNaver(token);
                            naverLogin.enqueue(new Callback<UserInfo>() {
                                @Override
                                public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                                    if(response.isSuccessful()){
                                        UserInfo userInfo = response.body();
                                        //회원번호 갖고와서 쉐어드에 넣어주기
                                        naverEmail = userInfo.getResponse().getEmail();
                                        naverToken = naverIdLoginSDK.getAccessToken();
                                        new CheckEmailTask().execute(naverEmail);

                                    }else {

                                    }
                                }

                                @Override
                                public void onFailure(Call<UserInfo> call, Throwable t) {

                                }
                            });
                            Log.d("네이버", naverIdLoginSDK.getAccessToken());
                            // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
                            //tvAccessToken.setText(naverIdLoginSDK.getAccessToken());
                            //binding.tvRefreshToken.setText(NaverIdLoginSDK.getRefreshToken());
                            // binding.tvExpires.setText(String.valueOf(NaverIdLoginSDK.getExpiresAt()));
                            // binding.tvType.setText(NaverIdLoginSDK.getTokenType());
                            // binding.tvState.setText(String.valueOf(NaverIdLoginSDK.getState()));
                            break;
                        case RESULT_CANCELED:
                            // 실패 or 에러
                            //int errorCode = naverIdLoginSDK.getLastErrorCode().code;
                            //String errorDescription = naverIdLoginSDK.getLastErrorDescription();
                            //Toast.makeText(context, "errorCode:" + errorCode + ", errorDesc:" + errorDescription, Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
    );
    private TextWatcher idValidate(TextView idValidation, EditText userId){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                email = userId.getText().toString().trim();
                if(email.matches(emailValidation) && email.length() > 0){
                    idValidation.setText("올바른 이메일 형식입니다.");
                    int textColor = ContextCompat.getColor(getApplicationContext(), R.color.teal_200);
                    idValidation.setTextColor(textColor);
                }else{
                    idValidation.setText("이메일 형식으로 입력해주세요.");
                    idValidation.setTextColor(Color.RED);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }

    public class CheckEmailTask extends AsyncTask<String, Void, String> {
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
                if (!result.trim().equals("가입되어 있지 않습니다.")) {
                    saveMemberId(result.trim());
                    successMove();
                } else {
                    Toast.makeText(context,"회원가입을 진행합니다. \n전화번호를 인증해주세요.", Toast.LENGTH_SHORT).show();
                    move(2);
                }
            } else {
                Log.d("로그인여부", "이메일 확인 중 오류 발생");
            }
        }
    }

    private void logoutClick(){
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                naverIdLoginSDK.logout();
                Toast.makeText(context,"로그아웃",Toast.LENGTH_SHORT).show();
            }
        });
    }
}