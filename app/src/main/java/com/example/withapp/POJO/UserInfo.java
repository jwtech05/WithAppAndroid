package com.example.withapp.POJO;

import com.google.gson.annotations.SerializedName;

public class UserInfo {

    @SerializedName("resultcode")
    private String resultCode;

    @SerializedName("message")
    private String message;

    @SerializedName("response")
    private UserResponse response;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserResponse getResponse() {
        return response;
    }

    public void setResponse(UserResponse response) {
        this.response = response;
    }
}
