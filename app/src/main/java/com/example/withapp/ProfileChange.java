package com.example.withapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.withapp.POJO.ProfileData;
import com.example.withapp.POJO.ProfileImg;
import com.example.withapp.RETROFIT.RetrofitInterface;
import com.example.withapp.RETROFIT.retrofit_client;

import org.w3c.dom.Text;

import java.util.Base64;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileChange extends AppCompatActivity {

    private EditText selfIntroEdit;
    private EditText nickNameEdit;
    private EditText ageEdit;
    private EditText genderEdit;
    private EditText countryEdit;
    private ImageView image;
    private Button changeButton;
    private RetrofitInterface service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_change);

        selfIntroEdit = (EditText) findViewById(R.id.selfIntroText);
        nickNameEdit = (EditText) findViewById(R.id.cNickNameEdit);
        ageEdit = (EditText) findViewById(R.id.cAgeEdit);
        genderEdit = (EditText) findViewById(R.id.cGenderEdit);
        countryEdit = (EditText) findViewById(R.id.cCountryEdit);
        image = (ImageView) findViewById(R.id.changeImageView);
        changeButton = (Button) findViewById(R.id.button);

        service = retrofit_client.retrofit.create(RetrofitInterface.class);
        Call<ProfileData> profileInfo = service.memberProfileInfo(getMemberId());
        Call<ProfileImg> profileImg = service.memberProfileImg(getMemberId());
        profileInfo.enqueue(new Callback<ProfileData>() {
            @Override
            public void onResponse(Call<ProfileData> call, Response<ProfileData> response) {
                nickNameEdit.setText(response.body().getNickName());
                ageEdit.setText(response.body().getAge());
                genderEdit.setText(response.body().getGender());
                countryEdit.setText(response.body().getCountry());
                selfIntroEdit.setText(response.body().getComment());
            }

            @Override
            public void onFailure(Call<ProfileData> call, Throwable t) {

            }
        });

        profileImg.enqueue(new Callback<ProfileImg>() {
            @Override
            public void onResponse(Call<ProfileImg> call, Response<ProfileImg> response) {

                String base64Image = response.body().getBase64Image();
                byte[] decodedString = Base64.getDecoder().decode(base64Image);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                image.setImageBitmap(bitmap);
            }

            @Override
            public void onFailure(Call<ProfileImg> call, Throwable t) {

            }
        });

        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeInfo();
                Move();
            }
        });

    }

    private String getMemberId() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        return sharedPreferences.getString("회원번호", "");
    }

    private void ChangeInfo() {
        Call<String> changeProfileInfo = service.changeProfileInfo(getMemberId(),nickNameEdit.getText().toString(),ageEdit.getText().toString(), genderEdit.getText().toString(), countryEdit.getText().toString(), selfIntroEdit.getText().toString());
        changeProfileInfo.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Toast.makeText(getApplicationContext(), response.body(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void Move() {
        Intent intent = new Intent(getApplicationContext(),BottomNavi.class);
        startActivity(intent);
    }

}