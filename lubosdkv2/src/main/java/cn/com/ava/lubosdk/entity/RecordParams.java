package cn.com.ava.lubosdk.entity;

public class RecordParams {

    /*频道*/
    private int channel;
    /*宽*/
    private int width;
    /*高*/
    private int height;
    /*波特率*/
    private int bps;
    /*帧数*/
    private int framePerSecond;

    private int mode;
    /*关键帧间隔*/
    int gop;

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getBps() {
        return bps;
    }

    public void setBps(int bps) {
        this.bps = bps;
    }

    public int getFramePerSecond() {
        return framePerSecond;
    }

    public void setFramePerSecond(int framePerSecond) {
        this.framePerSecond = framePerSecond;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getGop() {
        return gop;
    }

    public void setGop(int gop) {
        this.gop = gop;
    }
}
