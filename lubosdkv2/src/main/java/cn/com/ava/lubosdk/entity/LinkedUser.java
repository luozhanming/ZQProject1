package cn.com.ava.lubosdk.entity;

import com.google.gson.annotations.SerializedName;

public class LinkedUser implements Comparable<Integer>,QueryResult {
    /**互动号码*/
    private String userId;
    private int number;
    private String username;
    private String nickname;
    @SerializedName("short_num")
    private String shortNumer;
    @SerializedName("online_state")
    /**在线状态*/
    private int onlineState;
    /**是否在画面上*/
    private boolean isOnVideo;

    private String sessionId;

    private String role;

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

    public String getShortNumer() {
        return shortNumer;
    }

    public void setShortNumer(String shortNumer) {
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "LinkedUser{" +
                "userId='" + userId + '\'' +
                ", number=" + number +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", shortNumer='" + shortNumer + '\'' +
                ", onlineState=" + onlineState +
                ", isOnVideo=" + isOnVideo +
                ", sessionId='" + sessionId + '\'' +
                ", role='" + role + '\'' +
                '}';
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
