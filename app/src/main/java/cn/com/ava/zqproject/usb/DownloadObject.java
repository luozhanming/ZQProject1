package cn.com.ava.zqproject.usb;

import androidx.annotation.IntDef;

public class DownloadObject<T> {

    public static final int IN_QUEUE = 1001;
    public static final int DOWNLOADING = 1002;
    public static final int SUCCESS = 1003;
    public static final int FAILED = 1004;
    public static final int CANCELED = 1005;

    @IntDef({IN_QUEUE,DOWNLOADING,SUCCESS,FAILED,CANCELED})
    public @interface DownloadState{}


    public DownloadObject(T obj) {
        this.obj = obj;
    }

    private T obj;
    private int state;
    private int downloadProgress;


    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getDownloadProgress() {
        return downloadProgress;
    }

    public void setDownloadProgress(int downloadProgress) {
        this.downloadProgress = downloadProgress;
    }
}
