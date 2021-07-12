package cn.com.ava.lubosdk.entity;

public class LastInteracSetting implements QueryResult{
    private String confType;
    private String videoCodec;
    private boolean isDual;
    private int lastMaxShow;

    public String getConfType() {
        return confType;
    }

    public void setConfType(String confType) {
        this.confType = confType;
    }

    public String getVideoCodec() {
        return videoCodec;
    }

    public void setVideoCodec(String videoCodec) {
        this.videoCodec = videoCodec;
    }

    public boolean isDual() {
        return isDual;
    }

    public void setDual(boolean dual) {
        isDual = dual;
    }

    public int getLastMaxShow() {
        return lastMaxShow;
    }

    public void setLastMaxShow(int lastMaxShow) {
        this.lastMaxShow = lastMaxShow;
    }
}
