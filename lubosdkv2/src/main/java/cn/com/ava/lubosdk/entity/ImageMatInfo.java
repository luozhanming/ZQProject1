package cn.com.ava.lubosdk.entity;
/**
 * 录播抠像设置信息
 * */
public class ImageMatInfo implements Cloneable,QueryResult {

    private int keyingThreshold1 = 50;

    private int keyingThreshold2 = 50;

    private String keyingBG1 = "BLUE";

    private String keyingBG2 = "BLUE";

    private int keyingThreshold3 = 120;

    private int keyingThreshold4 = 120;

    private boolean keyingThreshold4Enable = false;

    private boolean keyingHDMI3VoEnable = false;

    private int keyingHDMI3Vo = 0;

    private int KeyingHDMI3VoColor = 150;

    private int keyingHDMI3VoML = 32;

    private int KeyingHDMI3VoSL = 16;


    public int getKeyingThreshold1() {
        return keyingThreshold1;
    }

    public void setKeyingThreshold1(int keyingThreshold1) {
        this.keyingThreshold1 = keyingThreshold1;
    }

    public int getKeyingThreshold2() {
        return keyingThreshold2;
    }

    public void setKeyingThreshold2(int keyingThreshold2) {
        this.keyingThreshold2 = keyingThreshold2;
    }

    public String getKeyingBG1() {
        return keyingBG1;
    }

    public void setKeyingBG1(String keyingBG1) {
        this.keyingBG1 = keyingBG1;
    }

    public String getKeyingBG2() {
        return keyingBG2;
    }

    public void setKeyingBG2(String keyingBG2) {
        this.keyingBG2 = keyingBG2;
    }

    public int getKeyingThreshold3() {
        return keyingThreshold3;
    }

    public void setKeyingThreshold3(int keyingThreshold3) {
        this.keyingThreshold3 = keyingThreshold3;
    }

    public int getKeyingThreshold4() {
        return keyingThreshold4;
    }

    public void setKeyingThreshold4(int keyingThreshold4) {
        this.keyingThreshold4 = keyingThreshold4;
    }

    public boolean isKeyingHDMI3VoEnable() {
        return keyingHDMI3VoEnable;
    }

    public void setKeyingHDMI3VoEnable(boolean keyingHDMI3VoEnable) {
        this.keyingHDMI3VoEnable = keyingHDMI3VoEnable;
    }

    public int getKeyingHDMI3Vo() {
        return keyingHDMI3Vo;
    }

    public void setKeyingHDMI3Vo(int keyingHDMI3Vo) {
        this.keyingHDMI3Vo = keyingHDMI3Vo;
    }

    public int getKeyingHDMI3VoColor() {
        return KeyingHDMI3VoColor;
    }

    public void setKeyingHDMI3VoColor(int keyingHDMI3VoColor) {
        KeyingHDMI3VoColor = keyingHDMI3VoColor;
    }

    public int getKeyingHDMI3VoML() {
        return keyingHDMI3VoML;
    }

    public void setKeyingHDMI3VoML(int keyingHDMI3VoML) {
        this.keyingHDMI3VoML = keyingHDMI3VoML;
    }

    public int getKeyingHDMI3VoSL() {
        return KeyingHDMI3VoSL;
    }

    public void setKeyingHDMI3VoSL(int keyingHDMI3VoSL) {
        KeyingHDMI3VoSL = keyingHDMI3VoSL;
    }

    public boolean isKeyingThreshold4Enable() {
        return keyingThreshold4Enable;
    }

    public void setKeyingThreshold4Enable(boolean keyingThreshold4Enable) {
        this.keyingThreshold4Enable = keyingThreshold4Enable;
    }

    @Override
    public ImageMatInfo clone() throws CloneNotSupportedException {
        return (ImageMatInfo) super.clone();
    }
}
