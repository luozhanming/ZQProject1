package cn.com.ava.lubosdk.entity;

/**
 * 创建互动设置
 * */
public class InteracSetting implements QueryResult {

    private String netparam = "2048";
    private String codemode = "0";
    private String protocal = "0";
    private String asswitch = "0";
    private boolean enableCloud;

    public String getNetparam() {
        return netparam;
    }

    public void setNetparam(String netparam) {
        this.netparam = netparam;
    }

    public String getCodemode() {
        return codemode;
    }

    public void setCodemode(String codemode) {
        this.codemode = codemode;
    }

    public String getProtocal() {
        return protocal;
    }

    public void setProtocal(String protocal) {
        this.protocal = protocal;
    }

    public String getAsswitch() {
        return asswitch;
    }

    public void setAsswitch(String asswitch) {
        this.asswitch = asswitch;
    }

    public boolean getEnableCloud() {
        return enableCloud;
    }

    public void setEnableCloud(boolean enableCloud) {
        this.enableCloud = enableCloud;
    }
}
