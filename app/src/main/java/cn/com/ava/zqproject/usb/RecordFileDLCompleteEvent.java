package cn.com.ava.zqproject.usb;


import cn.com.ava.zqproject.vo.RecordFilesInfo;

public class RecordFileDLCompleteEvent {

    public  boolean isSuccess;
    public RecordFilesInfo.RecordFile file;
    public  String dstPath;

    public RecordFileDLCompleteEvent(boolean isSuccess, RecordFilesInfo.RecordFile file, String dstPath) {
        this.isSuccess = isSuccess;
        this.file = file;
        this.dstPath = dstPath;
    }
}
