package cn.com.ava.zqproject.usb;

import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import cn.com.ava.zqproject.vo.RecordFilesInfo;

/**
 * 资源文件下载状态事件
 * */
public class RecordFileDLStateEvent {

    public static final int STATE_DOWNLOADING = 1001;
    public static final int STATE_SUCCESS = 1002;
    public static final int STATE_FAILED = 1003;
    public static final int STATE_IN_QUEUEN = 1004;

    @IntDef({STATE_DOWNLOADING, STATE_SUCCESS, STATE_FAILED, STATE_IN_QUEUEN})
    public @interface State {
    }

    public RecordFilesInfo.RecordFile file;
    public String dstPath;
    public @State
    int state;
    public @IntRange(from = 0, to = 100)
    int progress;

    public RecordFileDLStateEvent(RecordFilesInfo.RecordFile file, String dstPath, @State int state, int progress) {
        this.file = file;
        this.dstPath = dstPath;
        this.state = state;
        this.progress = progress;
    }
}
