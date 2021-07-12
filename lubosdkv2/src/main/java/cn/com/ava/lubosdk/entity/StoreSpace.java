package cn.com.ava.lubosdk.entity;

public class StoreSpace {
    /*空余空间*/
    private String freeSize;

    /*录制文件空间*/
    private String nowSize;

    /*总空间*/
    private String totalSize;

    public String getFreeSize() {
        return freeSize;
    }

    public void setFreeSize(String freeSize) {
        this.freeSize = freeSize;
    }

    public String getNowSize() {
        return nowSize;
    }

    public void setNowSize(String nowSize) {
        this.nowSize = nowSize;
    }

    public String getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(String totalSize) {
        this.totalSize = totalSize;
    }
}
