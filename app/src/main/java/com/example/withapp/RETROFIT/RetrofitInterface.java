package com.example.withapp.RETROFIT;

import com.example.withapp.POJO.CallbackData;
import com.example.withapp.POJO.SmsRequest;
import com.example.withapp.POJO.SmsResponse;
import com.example.withapp.POJO.UserInfo;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetrofitInterface {
    //앱 로그인 용 API
    @FormUrlEncoded
    @POST("login.php")
    Call<CallbackData> sendData(
            @Field("userId") String userid,
            @Field("userPw") String userpw
    );
    //이메일 체크용 API
    @FormUrlEncoded
    @POST("email_check.php")
    Call<CallbackData> sendEmail(
            @Field("userEmail") String userid
    );
    //인증번호 체크용 API
    @FormUrlEncoded
    @POST("ver_check.php")
    Call<CallbackData> sendVerNum(
            @Field("userVerNum") String userVerNum,
            @Field("userEmail") String userid
    );
    //네이버 로그버 용 API
    @GET("v1/nid/me")
    Call<UserInfo> sendNaver(
            @Header("Authorization") String token);
    //네이버 SMS 전송 API
    @POST("sms/v2/services/ncp:sms:kr:298441367140:withapp/messages")
    Call<SmsResponse> sendNaverSms(@Body SmsRequest smsRequest);
    //전화번호 인증용 API
    @FormUrlEncoded
    @POST("phone_save.php")
    Call<String> sendPhoneNum(
            @Field("phone") String phone,
            @Field("verificate") String verificate
    );
    //전화번호 인증번호 확인용 API
    @FormUrlEncoded
    @POST("phone_check.php")
    Call<CallbackData> sendPhoneVerNum(
            @Field("phone") String phone,
            @Field("verificate") String verificate
    );
    //회원가입 회원정보 등록용 API
    @FormUrlEncoded
    @POST("register.php")
    Call<String> registerInfo(
            @Field("회원가입유형") int status,
            @Field("email") String email,
            @Field("password") String password,
            @Field("nickName") String nickName,
            @Field("phone") String phone,
            @Field("birth") String birth,
            @Field("gender") String gender,
            @Field("country") String country
    );
    //회원가입 여부 확인 API
    @FormUrlEncoded
    @POST("sign_or_not_check.php")
    Call<String> checkSignOrNot(
            @Field("email") String email
    );
    @Multipart
    @POST("upload_image.php")
    Call<String> uploadImage(@Part MultipartBody.Part image, @Part("userId") RequestBody userId);

    @FormUrlEncoded
    @POST("callback.php")
    Call<String> memberInfo(
            @Field("memberId") String memberId
    );
}
