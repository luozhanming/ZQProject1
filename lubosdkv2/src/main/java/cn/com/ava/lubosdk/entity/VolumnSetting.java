package cn.com.ava.lubosdk.entity;

import java.util.List;

public class VolumnSetting {

    public static final int NSEL_EMPTY  = -1;
    public static final int NSEL_DMIC = 0;
    public static final int NSEL_DMIC_LINEIN1 = 1;
    public static final int NSEL_DMIC_LINEIN2 = 2;


    //音频效果
    private int apmset_nsel;
    //是否自动增益
    private boolean  isAutoAmp;
    //启动噪音抑制
    private boolean isNoiseRestrain;
    //开启EQ功能
    private boolean isEQOpen;

    private List<VolumeChannel> channels;

    public int getApmset_nsel() {
        return apmset_nsel;
    }

    public void setApmset_nsel(int apmset_nsel) {
        this.apmset_nsel = apmset_nsel;
    }

    public boolean isAutoAmp() {
        return isAutoAmp;
    }

    public void setAutoAmp(boolean autoAmp) {
        isAutoAmp = autoAmp;
    }

    public boolean isNoiseRestrain() {
        return isNoiseRestrain;
    }

    public void setNoiseRestrain(boolean noiseRestrain) {
        isNoiseRestrain = noiseRestrain;
    }

    public boolean isEQOpen() {
        return isEQOpen;
    }

    public void setEQOpen(boolean EQOpen) {
        isEQOpen = EQOpen;
    }

    public List<VolumeChannel> getChannels() {
        return channels;
    }

    public void setChannels(List<VolumeChannel> channels) {
        this.channels = channels;
    }
}
