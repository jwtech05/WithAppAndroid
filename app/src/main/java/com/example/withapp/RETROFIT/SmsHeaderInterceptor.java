package com.example.withapp.RETROFIT;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class SmsHeaderInterceptor implements Interceptor{
    private String timeStamp;
    private  String accessKey;
    private  String signature;

    public SmsHeaderInterceptor(String timeStamp, String accessKey, String signature) {
        this.timeStamp = timeStamp;
        this.accessKey = accessKey;
        this.signature = signature;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Interceptor.Chain chain) throws IOException {

        Request request = chain.request()
                .newBuilder()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("x-ncp-apigw-timestamp", timeStamp)
                .addHeader("x-ncp-iam-access-key", accessKey)
                .addHeader("x-ncp-apigw-signature-v2", signature)
                .build();

        return chain.proceed(request);
    }
}
