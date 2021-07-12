package cn.com.ava.lubosdk.entity;


import cn.com.ava.lubosdk.Constant;

public class RecordInfo implements QueryResult {

    /*课堂主题*/
    private String classTheme;
    /*主讲人*/
    private String teacher;
    /*录课时长*/
    private String recordTime;
    /*文件大小*/
    private String fileSize;
    /*录制状态*/
    private int recordState;
    /*是否正在直播*/
    private boolean isLiving;
    /*当前输出窗口*/
    private String currentOutputWindow;
    /*当前追踪模式*/
    private String trackMode;
    /*互动状态*/
    private String interaState;
    /*预览行数*/
    private int previewRow;
    /*预览列数*/
    private int previewLie;
    //窗口1能否抠像
    private boolean isMatEnable;
    //窗口1是否在抠像
    private boolean isMatOpen;

    private boolean isMat2Enable;

    private boolean isMat2Open;


    public String getClassTheme() {
        return classTheme;
    }

    public void setClassTheme(String classTheme) {
        this.classTheme = classTheme;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
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

    public String getCurrentOutputWindow() {
        return currentOutputWindow;
    }

    public void setCurrentOutputWindow(String currentOutputWindow) {
        this.currentOutputWindow = currentOutputWindow;
    }

    public @Constant.CamTrackMode
    String getTrackMode() {
        return trackMode;
    }

    public void setTrackMode(@Constant.CamTrackMode String trackMode) {
        this.trackMode = trackMode;
    }

    public String getInteraState() {
        return interaState;
    }

    public void setInteraState(@Constant.InteracMode String interaState) {
        this.interaState = interaState;
    }

    public int getPreviewRow() {
        return previewRow;
    }

    public void setPreviewRow(int previewRow) {
        this.previewRow = previewRow;
    }

    public int getPreviewLie() {
        return previewLie;
    }

    public void setPreviewLie(int previewLie) {
        this.previewLie = previewLie;
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
}
