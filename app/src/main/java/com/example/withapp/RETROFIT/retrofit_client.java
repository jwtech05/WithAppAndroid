package com.example.withapp.RETROFIT;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class retrofit_client {
    private static final String BASE_URL = "http://3.39.156.87/";
    private static final String NAVER_URL = "https://openapi.naver.com/";
    private final String NAVER_SMS_URL = "https://sens.apigw.ntruss.com/";
    public String time = String.valueOf(System.currentTimeMillis());
    static Gson gson = new GsonBuilder()
            .setLenient()
            .create();
    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    public static Retrofit naverRetrofit = new Retrofit.Builder()
            .baseUrl(NAVER_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    public Retrofit naverLoginRetrofit = new Retrofit.Builder()
            .baseUrl(NAVER_SMS_URL)
            .client(getHttpClient())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    public OkHttpClient getHttpClient(){
        OkHttpClient httpClient;
        try {
            String time = String.valueOf(System.currentTimeMillis());
            httpClient = new OkHttpClient.Builder()
                    .addInterceptor(new SmsHeaderInterceptor(time, "3rFQ9eB7NJLFsRk5iiqE", makeSignature()))
                    .build();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        return httpClient;
    }

    public String makeSignature() throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String space = " ";					// one space
        String newLine = "\n";					// new line
        String method = "POST";					// method
        String url = "/sms/v2/services/ncp:sms:kr:298441367140:withapp/messages";	// url (include query string)
        String timestamp = time;			// current timestamp (epoch)
        String accessKey = "3rFQ9eB7NJLFsRk5iiqE";			// access key id (from portal or Sub Account)
        String secretKey = "tEUfJnkk71WmLuzowEqvXcRdmupz9DTU1oNrvhNN";

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        String encodeBase64String = Base64.getEncoder().encodeToString(rawHmac);
        Log.d("비밀", encodeBase64String);
        return encodeBase64String;
    }
}
