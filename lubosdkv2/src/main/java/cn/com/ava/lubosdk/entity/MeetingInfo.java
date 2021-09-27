package cn.com.ava.lubosdk.entity;

import cn.com.ava.lubosdk.Constant;

public class MeetingInfo implements QueryResult{
    //会议主题
    private String meetingTheme = "";
    //会议号
    private String meetingNumber = "";
    //会议密码
    private String meetingPassword = "";
    //角色(主讲端/听课端)
    private String meetingRole;
    //追踪模式
    private String trackMode;
    //互动状态
    private String interaMode;
    //录制状态
    private int recordState = Constant.RECORD_STOP;
    //是否直播中
    private boolean isLiving;
    //当前导播布局
    private String curLayout;
    //最大连接数
    private int maxLinkCount;
    //互动信息
    private InteraInfo interaInfo;
    //主音量
    private VolumeChannel mainVolumeChannel;
    //EXT音量信息
    private String extVolumeInfo;

    private boolean isDualStream;

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

    public String getMeetingRole() {
        return meetingRole;
    }

    public void setMeetingRole(String meetingRole) {
        this.meetingRole = meetingRole;
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

    public String getCurLayout() {
        return curLayout;
    }

    public void setCurLayout(String curLayout) {
        this.curLayout = curLayout;
    }

    public String getTrackMode() {
        return trackMode;
    }

    public void setTrackMode(@Constant.CamTrackMode String trackMode) {
        this.trackMode = trackMode;
    }

    public String getInteraMode() {
        return interaMode;
    }

    public void setInteraMode(@Constant.InteracMode String interaMode) {
        this.interaMode = interaMode;
    }

    public int getMaxLinkCount() {
        return maxLinkCount;
    }

    public void setMaxLinkCount(int maxLinkCount) {
        this.maxLinkCount = maxLinkCount;
    }

    public InteraInfo getInteraInfo() {
        return interaInfo;
    }

    public void setInteraInfo(InteraInfo interaInfo) {
        this.interaInfo = interaInfo;
    }

    public VolumeChannel getMainVolumeChannel() {
        return mainVolumeChannel;
    }

    public void setMainVolumeChannel(VolumeChannel mainVolumeChannel) {
        this.mainVolumeChannel = mainVolumeChannel;
    }

    public String getExtVolumeInfo() {
        return extVolumeInfo;
    }

    public void setExtVolumeInfo(String extVolumeInfo) {
        this.extVolumeInfo = extVolumeInfo;
    }


    public boolean isDualStream() {
        return isDualStream;
    }

    public void setDualStream(boolean dualStream) {
        isDualStream = dualStream;
    }
}
