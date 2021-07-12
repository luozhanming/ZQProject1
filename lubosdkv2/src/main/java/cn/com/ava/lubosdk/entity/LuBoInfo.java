package cn.com.ava.lubosdk.entity;


import cn.com.ava.lubosdk.Constant;

public class LuBoInfo implements QueryResult{
    //设备型号318
    private String devModel;
    //录播型号321
    private String machineModel;
    //录播软件版本320
    private int version;
    //录播版本
    private String versionStr;
    //是否中性版本
    private boolean isNetural;
    //录播ip
    private String ip;
    //能否互动1V3
    private boolean canInterac1V3;
    //电脑窗口索引
    private String computerIndex;
    //能否支持互动
    private boolean canInterac;
    //录播Rserver信息
    private LoginIdStun stun;
    //当前互动模式
    private @Constant.InteracMode
    String interacMode =Constant.INTERAC_MODE_RECORD;
    private @Constant.RecordState int recordState;
    private boolean isLiving = false;
    //网络MTU
    private int mtu = 1500;




    public String getDevModel() {
        return devModel;
    }

    public void setDevModel(String devModel) {
        this.devModel = devModel;
    }

    public String getMachineModel() {
        return machineModel;
    }

    public void setMachineModel(String machineModel) {
        this.machineModel = machineModel;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }


    public boolean isNetural() {
        return isNetural;
    }

    public void setNetural(boolean netural) {
        isNetural = netural;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }


    public boolean isCanInterac1V3() {
        return canInterac1V3;
    }

    public void setCanInterac1V3(boolean canInterac1V3) {
        this.canInterac1V3 = canInterac1V3;
    }

    public String getComputerIndex() {
        return computerIndex;
    }

    public void setComputerIndex(String computerIndex) {
        this.computerIndex = computerIndex;
    }

    public boolean isCanInterac() {
        return canInterac;
    }

    public void setCanInterac(boolean canInterac) {
        this.canInterac = canInterac;
    }

    public LoginIdStun getStun() {
        return stun;
    }

    public void setStun(LoginIdStun stun) {
        this.stun = stun;
    }


    public String getVersionStr() {
        return versionStr;
    }

    public void setVersionStr(String versionStr) {
        this.versionStr = versionStr;
    }


    public String getInteracMode() {
        return interacMode;
    }

    public int getRecordState() {
        return recordState;
    }

    public void setRecordState(int recordState) {
        this.recordState = recordState;
    }

    public void setInteracMode(String interacMode) {
        this.interacMode = interacMode;
    }


    public boolean isLiving() {
        return isLiving;
    }

    public void setLiving(boolean living) {
        isLiving = living;
    }

    public int getMtu() {
        return mtu;
    }

    public void setMtu(int mtu) {
        this.mtu = mtu;
    }

    public static class LoginIdStun{
        private String usr;
        private String pwd;
        private String ip;
        private String localport;
        private String globalport;
        private String backupip;
        private String nickname;
        private String connectState;
        private String udpport;
        private String shortnum;

        public String getUsr() {
            return usr;
        }

        public void setUsr(String usr) {
            this.usr = usr;
        }

        public String getPwd() {
            return pwd;
        }

        public void setPwd(String pwd) {
            this.pwd = pwd;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getLocalport() {
            return localport;
        }

        public void setLocalport(String localport) {
            this.localport = localport;
        }

        public String getGlobalport() {
            return globalport;
        }

        public void setGlobalport(String globalport) {
            this.globalport = globalport;
        }

        public String getBackupip() {
            return backupip;
        }

        public void setBackupip(String backupip) {
            this.backupip = backupip;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getUdpport() {
            return udpport;
        }

        public void setUdpport(String udpport) {
            this.udpport = udpport;
        }

        public String getConnectState() {
            return connectState;
        }

        public void setConnectState(String connectState) {
            this.connectState = connectState;
        }

        public String getShortnum() {
            return shortnum;
        }

        public void setShortnum(String shortnum) {
            this.shortnum = shortnum;
        }

    }
}
