package cn.com.ava.lubosdk.entity;

import com.google.gson.annotations.SerializedName;

public class LinkedUser implements Comparable<Integer> {
    /**互动号码*/
    private int number;
    private String username;
    private String nickname;
    @SerializedName("short_num")
    private int shortNumer;
    @SerializedName("online_state")
    /**在线状态*/
    private int onlineState;
    /**是否在画面上*/
    private boolean isOnVideo;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getShortNumer() {
        return shortNumer;
    }

    public void setShortNumer(int shortNumer) {
        this.shortNumer = shortNumer;
    }

    public int getOnlineState() {
        return onlineState;
    }

    public void setOnlineState(int onlineState) {
        this.onlineState = onlineState;
    }

    public boolean isOnVideo() {
        return isOnVideo;
    }

    public void setOnVideo(boolean onVideo) {
        isOnVideo = onVideo;
    }


    @Override
    public int compareTo(Integer o) {
        return number-o;
    }

    @Override
    public boolean equals( Object obj) {
        if(obj==null)return false;
        else if(obj instanceof LinkedUser &&((LinkedUser)obj).getNumber()==number){
            return true;
        }
        return super.equals(obj);
    }
}
