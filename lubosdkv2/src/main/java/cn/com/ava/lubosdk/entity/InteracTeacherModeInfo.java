package cn.com.ava.lubosdk.entity;


import cn.com.ava.lubosdk.Constant;

/**
 * 互动授课主讲信息
 */
public class InteracTeacherModeInfo implements QueryResult {
    /*互动主题*/
    private String meetingTheme;
    /*互动会议号*/
    private String meetingNumber;
    /*互动密码*/
    private String meetingPassword;
    /*当前追踪模式*/
    private String trackMode;
    /*互动状态*/
    private String interaState;
    /*录制状态*/
    private int recordState;
    /*是否正在直播*/
    private boolean isLiving;
    /*录制时间*/
    private String recordTime;


    public String getMeetingTheme() {
        return meetingTheme;
    }

    public void setMeetingTheme(String meetingTheme) {
        this.meetingTheme = meetingTheme;
    }

    public String getMeetingNumber() {
        return meetingNumber;
    }

    public void setMeetingNumber(String meetingNumber) {
        this.meetingNumber = meetingNumber;
    }

    public String getMeetingPassword() {
        return meetingPassword;
    }

    public void setMeetingPassword(String meetingPassword) {
        this.meetingPassword = meetingPassword;
    }

    public String getTrackMode() {
        return trackMode;
    }

    public void setTrackMode(@Constant.CamTrackMode String trackMode) {
        this.trackMode = trackMode;
    }

    public String getInteraState() {
        return interaState;
    }

    public void setInteraState(String interaState) {
        this.interaState = interaState;
    }

    public int getRecordState() {
        return recordState;
    }

    public void setRecordState(@Constant.RecordState int recordState) {
        this.recordState = recordState;
    }

    public boolean isLiving() {
        return isLiving;
    }

    public void setLiving(boolean living) {
        isLiving = living;
    }

    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }
}
