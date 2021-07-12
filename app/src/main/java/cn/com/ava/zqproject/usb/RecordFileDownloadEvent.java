package cn.com.ava.zqproject.usb;


import cn.com.ava.zqproject.vo.RecordFilesInfo;

public class RecordFileDownloadEvent {
   public boolean isDownloading;
    public RecordFilesInfo.RecordFile file;
    public int progress;
    public String dstPath;

    public RecordFileDownloadEvent(boolean isDownloading, RecordFilesInfo.RecordFile file, int progress, String dstPath) {
        this.isDownloading = isDownloading;
        this.file = file;
        this.progress = progress;
        this.dstPath = dstPath;
    }
}
