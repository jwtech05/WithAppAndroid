package com.example.withapp.POJO;

public class SearchRecyclerData {

    private String memberId;
    private String nickName;
    private String imagePath;

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public SearchRecyclerData(String memberId, String nickName, String imagePath) {
        this.memberId = memberId;
        this.nickName = nickName;
        this.imagePath = imagePath;
    }
}
