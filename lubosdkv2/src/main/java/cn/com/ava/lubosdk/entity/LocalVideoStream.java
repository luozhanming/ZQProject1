package cn.com.ava.lubosdk.entity;

/**
 * 会议模式下的本地与会视频源选项
 * */
public class LocalVideoStream implements QueryResult{
    /**窗口索引*/
    private int windowIndex;
    /**窗口名*/
    private String windowName;
    /**是否主流*/
    private boolean isMain;
    /**是否当前所选主流*/
    private boolean isMainOutput;
    /**是否当前所选辅流*/
    private boolean isSubOutput;
    /**是否允许双流*/
    private boolean isEnableDualStream;

    public String getWindowName() {
        return windowName;
    }

    public void setWindowName(String windowName) {
        this.windowName = windowName;
    }

    public boolean isMain() {
        return isMain;
    }

    public void setMain(boolean main) {
        isMain = main;
    }

    public int getWindowIndex() {
        return windowIndex;
    }

    public void setWindowIndex(int windowIndex) {
        this.windowIndex = windowIndex;
    }

    public boolean isMainOutput() {
        return isMainOutput;
    }

    public void setMainOutput(boolean mainOutput) {
        isMainOutput = mainOutput;
    }

    public boolean isSubOutput() {
        return isSubOutput;
    }

    public void setSubOutput(boolean subOutput) {
        isSubOutput = subOutput;
    }

    public boolean isEnableDualStream() {
        return isEnableDualStream;
    }

    public void setEnableDualStream(boolean enableDualStream) {
        isEnableDualStream = enableDualStream;
    }
}
