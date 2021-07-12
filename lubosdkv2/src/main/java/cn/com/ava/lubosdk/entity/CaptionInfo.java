package cn.com.ava.lubosdk.entity;

import java.util.List;


/**
 * 字幕信息
 * */
public class CaptionInfo implements QueryResult{
    /**是否显示*/
    private boolean isShowing;
    /**是否滚动*/
    private boolean isRolling;
    /**可选字幕列表*/
    private List<String> captions;

    private boolean osdpptdetect;

    public boolean isShowing() {
        return isShowing;
    }

    public void setShowing(boolean showing) {
        isShowing = showing;
    }

    public boolean isRolling() {
        return isRolling;
    }

    public void setRolling(boolean rolling) {
        isRolling = rolling;
    }

    public List<String> getCaptions() {
        return captions;
    }

    public void setCaptions(List<String> captions) {
        this.captions = captions;
    }

    public boolean isOsdpptdetect() {
        return osdpptdetect;
    }

    public void setOsdpptdetect(boolean osdpptdetect) {
        this.osdpptdetect = osdpptdetect;
    }
}
