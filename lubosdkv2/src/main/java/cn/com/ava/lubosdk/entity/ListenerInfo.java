package cn.com.ava.lubosdk.entity;

import java.util.List;

import cn.com.ava.lubosdk.Constant;

public class ListenerInfo implements QueryResult{
    /**
     * 会议主题
     */
    private String meetingTheme = "";
    /**
     * 会议号
     */
    private String meetingNumber = "";
    /**
     * 会议密码
     */
    private String meetingPassword = "";
    /**
     * 角色(主讲端/听课端)
     */
    private String meetingRole;
    /**
     * 追踪模式
     */
    private String trackMode;
    /**
     * 互动状态
     */
    private String interaMode;
    /**
     * 录制状态
     */
    private int recordState;
    /**
     * 是否直播中
     */
    private boolean isLiving;
    /**
     * 当前导播布局
     */
    private String curLayout;
    /**
     * 最大连接数
     */
    private int maxLinkCount;


    private InteraInfo interaInfo;
    /**
     * 是否开启双流
     */
    private boolean isShareDualStream;
    /**
     * 当前分享双流的
     */
    private int curShareDualStreamUser;
    /**
     * 当前申请双流的
     */
    private List<Integer> curRequestShareStreamList;
    /**
     * 当前可发言列表（编号）
     */
    private List<Integer> curSpeakingList;
    /**
     * 当前申请发言的编号
     */
    private List<Integer> curRequestSpeakList;
    /**
     * 主流窗口
     */
    private List<PreviewVideoWindow> mainStreamWindows;
    /**
     * 辅流窗口
     */
    private List<PreviewVideoWindow> subStreamWindows;
    /**
     * 是否内置云
     */
    private boolean isInternalCloud;
    /**
     * 互动协议(0:AVA/1:H323/2:SIP)
     */
    private int protocol;
    /**
     * sip双流
     */
    private boolean sipShareDocStream;
    /**
     * 主流通道音频
     */
    private VolumeChannel mainVolumeChannel;


    /**
     * 窗口1能否抠像
     */
    private boolean isMatEnable;
    /**
     * 抠像1是否在抠像
     */
    private boolean isMatOpen;
    /**
     * 抠像2是否可用
     */
    private boolean isMat2Enable;
    /**
     * 抠像2是否打开
     */
    private boolean isMat2Open;
    /**
     * 是否旁听
     */
    private boolean isPanTing;


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

    public void setRecordState(int recordState) {
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

    public boolean isShareDualStream() {
        return isShareDualStream;
    }

    public void setShareDualStream(boolean shareDualStream) {
        isShareDualStream = shareDualStream;
    }

    public int getCurShareDualStreamUser() {
        return curShareDualStreamUser;
    }

    public void setCurShareDualStreamUser(int curShareDualStreamUser) {
        this.curShareDualStreamUser = curShareDualStreamUser;
    }

    public List<Integer> getCurRequestShareStreamList() {
        return curRequestShareStreamList;
    }

    public void setCurRequestShareStreamList(List<Integer> curRequestShareStreamList) {
        this.curRequestShareStreamList = curRequestShareStreamList;
    }

    public List<Integer> getCurSpeakingList() {
        return curSpeakingList;
    }

    public void setCurSpeakingList(List<Integer> curSpeakingList) {
        this.curSpeakingList = curSpeakingList;
    }

    public List<Integer> getCurRequestSpeakList() {
        return curRequestSpeakList;
    }

    public void setCurRequestSpeakList(List<Integer> curRequestSpeakList) {
        this.curRequestSpeakList = curRequestSpeakList;
    }

    public List<PreviewVideoWindow> getMainStreamWindows() {
        return mainStreamWindows;
    }

    public void setMainStreamWindows(List<PreviewVideoWindow> mainStreamWindows) {
        this.mainStreamWindows = mainStreamWindows;
    }

    public List<PreviewVideoWindow> getSubStreamWindows() {
        return subStreamWindows;
    }

    public void setSubStreamWindows(List<PreviewVideoWindow> subStreamWindows) {
        this.subStreamWindows = subStreamWindows;
    }


    public boolean isInternalCloud() {
        return isInternalCloud;
    }

    public void setInternalCloud(boolean internalCloud) {
        isInternalCloud = internalCloud;
    }

    public int getProtocol() {
        return protocol;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public VolumeChannel getMainVolumeChannel() {
        return mainVolumeChannel;
    }

    public void setMainVolumeChannel(VolumeChannel mainVolumeChannel) {
        this.mainVolumeChannel = mainVolumeChannel;
    }

    public boolean isSipShareDocStream() {
        return sipShareDocStream;
    }

    public void setSipShareDocStream(boolean sipShareDocStream) {
        this.sipShareDocStream = sipShareDocStream;
    }


    public boolean isMatEnable() {
        return isMatEnable;
    }

    public void setMatEnable(boolean matEnable) {
        isMatEnable = matEnable;
    }

    public boolean isMatOpen() {
        return isMatOpen;
    }

    public void setMatOpen(boolean matOpen) {
        isMatOpen = matOpen;
    }

    public boolean isMat2Enable() {
        return isMat2Enable;
    }

    public void setMat2Enable(boolean mat2Enable) {
        isMat2Enable = mat2Enable;
    }

    public boolean isMat2Open() {
        return isMat2Open;
    }

    public void setMat2Open(boolean mat2Open) {
        isMat2Open = mat2Open;
    }


    public boolean isPanTing() {
        return isPanTing;
    }

    public void setPanTing(boolean panTing) {
        isPanTing = panTing;
    }
}
