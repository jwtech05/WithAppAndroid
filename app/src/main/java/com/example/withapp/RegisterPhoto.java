package com.example.withapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.withapp.RETROFIT.RetrofitInterface;
import com.example.withapp.RETROFIT.retrofit_client;

import java.io.ByteArrayOutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterPhoto extends AppCompatActivity {

    private static final int IMAGE_ONE = 1;
    private Context context;
    private ImageView imageSpace;
    private TextView cancel;
    private TextView noticePhoto;
    private ImageView selectedImageView;
    private Button lastButton;
    //************인텐트 자료형************
    private Intent intent;
    private String intentEmailId;
    private String intentPassword;
    private String intentPhone;
    private String intentNickName;
    private String intentBirth;
    private String intentGender;
    private String intentCountry;
    private int status;
    private ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Uri imageUri = result.getData().getData();
                            imageSpace.setImageURI(imageUri);
                            cancel.setVisibility(View.VISIBLE);
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_photo);
        context = this;
        imageSpace = findViewById(R.id.imageSpace);
        cancel = findViewById(R.id.cancelImage);
        lastButton = findViewById(R.id.lastButton);
        noticePhoto = findViewById(R.id.noticePhoto);



        intent = getIntent();
        intentEmailId = intent.getStringExtra("아이디");
        intentPassword = intent.getStringExtra("비밀번호");
        intentPhone = intent.getStringExtra("전화번호");
        intentNickName = intent.getStringExtra("닉네임");
        intentBirth = intent.getStringExtra("생년월일");
        intentGender = intent.getStringExtra("성별");
        intentCountry = intent.getStringExtra("나라");
        status = getRegisterDistinguish();


        View.OnClickListener imageClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageSpace = (ImageView) view;
                openGallery();
            }
        };

        View.OnClickListener cancelClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
                imageSpace.setImageDrawable(null);
            }
        };

        imageSpace.setOnClickListener(imageClickListener);
        cancel.setOnClickListener(cancelClickListener);


        lastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageSpace.getDrawable() == null){
                    noticePhoto.setText("사진을 업로드 해주셔야 합니다.");
                    noticePhoto.setTextColor(Color.RED);
                }else{
                    lastMove();
                    Intent intent = new Intent(context, Login.class);
                    startActivity(intent);
                    Toast.makeText(context,"축하드립니다!! 회원가입이 완료되었습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(galleryIntent);
    }

    private void lastMove() {

        RetrofitInterface service = retrofit_client.retrofit.create(RetrofitInterface.class);
        Call<String> sendAllInfo = service.registerInfo(status,intentEmailId,intentPassword,intentNickName,intentPhone,intentBirth,intentGender,intentCountry);
        sendAllInfo.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String result = response.body();
                Log.d("결과", result);
                uploadImage(imageSpace, "testImage");
                noticePhoto.setText("회원가입이 완료되었습니다.");
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }

    private void uploadImage(ImageView imageView, String userId) {
        //ImageView에서 이미지를 가져와 Bitmap 객체로 변환
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        //ByteArrayOutputStream 객체 생성. Bitmap 이미지를 바이트 배열로 변환
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //JPEG 형식으로 압축하고 ByteArrayOutputStream에 쓴다.
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        //압축된 이미지를 바이트 배열로 변환
        byte[] byteArray = stream.toByteArray();
        //바이트 배열로부터 RequestBody 객체를 생성. 이미지를 서버로 전송할 때 사용
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), byteArray);
        //이미지와 관련된 멀티파트 요청 부분을 생성
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", intentEmailId+".jpg", requestBody);
        //사용자 ID를 포함하는 RequestBody 객체를 생성합니다. 이 객체는 서버로 전송 될 때 사용됩니다.
        RequestBody userIdPart = RequestBody.create(MediaType.parse("text/plain"), userId);
        //Retrofit 인터페이스를 사용하여 서비스 객체를 생성합니다. 이 객체를 통해 서버에 HTTP 요청을 보낼 수 있습니다.
        RetrofitInterface service = retrofit_client.retrofit.create(RetrofitInterface.class);
        Call<String> call = service.uploadImage(imagePart, userIdPart);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("결과", "이미지 업로드 성공: " + response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("결과", "이미지 업로드 실패: " + t.getMessage());
            }
        });
    }

    private int getRegisterDistinguish() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        return sharedPreferences.getInt("회원가입유형", 1);
    }
}