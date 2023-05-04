package com.example.withapp.POJO;

import com.google.gson.annotations.SerializedName;

public class UserResponse {

    @SerializedName("id")
    private String id;

    @SerializedName("gender")
    private String gender;

    @SerializedName("email")
    private String email;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    @SerializedName("name")
    private String name;

    @SerializedName("birthday")
    private String birthday;


}
