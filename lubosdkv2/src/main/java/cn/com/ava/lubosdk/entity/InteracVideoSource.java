package cn.com.ava.lubosdk.entity;


import cn.com.ava.lubosdk.Constant;

/**
 * 授课互动视频源实体
 * */
public class InteracVideoSource {
    /**窗口索引*/
    private int windowIndex;
    /**窗口名*/
    private String windowName = "";
    /**指令命令索引*/
    private int commandIndex;
    /**多个源选择窗口索引*/
    private int selectedIndex;


    private @Constant.InteraSourceType int type;

    public int getWindowIndex() {
        return windowIndex;
    }

    public void setWindowIndex(int windowIndex) {
        this.windowIndex = windowIndex;
    }

    public String getWindowName() {
        return windowName;
    }

    public void setWindowName(String windowName) {
        this.windowName = windowName;
    }

    public int getCommandIndex() {
        return commandIndex;
    }

    public void setCommandIndex(int commandIndex) {
        this.commandIndex = commandIndex;
    }


    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public @Constant.InteraSourceType
    int getType() {
        return type;
    }

    public void setType(@Constant.InteraSourceType int type) {
        this.type = type;
    }
}
