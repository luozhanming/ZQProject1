package cn.com.ava.lubosdk.entity;

public class IPv4NetConfiguration implements QueryResult{

    private String ip;

    private String mask;

    private String gateway;

    private  String macAddress;

    private String masterDNS;

    private String subDNS;


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getMasterDNS() {
        return masterDNS;
    }

    public void setMasterDNS(String masterDNS) {
        this.masterDNS = masterDNS;
    }

    public String getSubDNS() {
        return subDNS;
    }

    public void setSubDNS(String subDNS) {
        this.subDNS = subDNS;
    }
}
