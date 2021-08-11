package cn.com.ava.lubosdk.entity;

/**
 * 音量通道信息
 */
public class VolumeChannel {
    //通道名
    private String channelName;
    //音量水平
    private int volumnLevel;
    //是否静音
    private boolean isSilent;

    private boolean enable = true;
    //是否可达200%音量
    private boolean canAmp = false;
    //最小音量
    private int min;
    //最大音量
    private int max;

    /**
     * 矫正静音音量
     * */
    private int adapteVolumeLevel;

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public int getVolumnLevel() {
        return volumnLevel;
    }

    public void setVolumnLevel(int volumnLevel) {
        this.volumnLevel = volumnLevel;
    }

    public boolean isSilent() {
        return volumnLevel>=256;
    }


    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isCanAmp() {
        return canAmp;
    }

    public void setCanAmp(boolean canAmp) {
        this.canAmp = canAmp;
    }


    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }


    public int getAdapteVolumeLevel() {
        return adapteVolumeLevel;
    }

    public void setAdapteVolumeLevel(int adapteVolumeLevel) {
        this.adapteVolumeLevel = adapteVolumeLevel;
    }
}
