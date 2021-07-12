package cn.com.ava.lubosdk.entity;

public class MeetingUser implements QueryResult {
    private String ubid;
    private String userName;
    private String usernickName;
    private String userPasswd;
    private String userNum;
    private String contact;
    private String telephone;
    private String mailbox;
    private String addrProvince;
    private String addrCity;
    private String addrDistrict;
    private String addrLocation;
    private String addrInstall;
    private String curSelTime;
    private String userStatus;
    private boolean isSelected;
    private boolean isOnline;
    private boolean isNumberSelect;

    public boolean isNumberSelect() {
        return isNumberSelect;
    }

    public void setNumberSelect(boolean numberSelect) {
        isNumberSelect = numberSelect;
    }

    public String getUbid() {
        return ubid;
    }

    public void setUbid(String ubid) {
        this.ubid = ubid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUsernickName() {
        return usernickName;
    }

    public void setUsernickName(String usernickName) {
        this.usernickName = usernickName;
    }

    public String getUserPasswd() {
        return userPasswd;
    }

    public void setUserPasswd(String userPasswd) {
        this.userPasswd = userPasswd;
    }

    public String getUserNum() {
        return userNum;
    }

    public void setUserNum(String userNum) {
        this.userNum = userNum;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getMailbox() {
        return mailbox;
    }

    public void setMailbox(String mailbox) {
        this.mailbox = mailbox;
    }

    public String getAddrProvince() {
        return addrProvince;
    }

    public void setAddrProvince(String addrProvince) {
        this.addrProvince = addrProvince;
    }

    public String getAddrCity() {
        return addrCity;
    }

    public void setAddrCity(String addrCity) {
        this.addrCity = addrCity;
    }

    public String getAddrDistrict() {
        return addrDistrict;
    }

    public void setAddrDistrict(String addrDistrict) {
        this.addrDistrict = addrDistrict;
    }

    public String getAddrLocation() {
        return addrLocation;
    }

    public void setAddrLocation(String addrLocation) {
        this.addrLocation = addrLocation;
    }

    public String getAddrInstall() {
        return addrInstall;
    }

    public void setAddrInstall(String addrInstall) {
        this.addrInstall = addrInstall;
    }

    public String getCurSelTime() {
        return curSelTime;
    }

    public void setCurSelTime(String curSelTime) {
        this.curSelTime = curSelTime;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public boolean equals( Object obj) {
        if(obj instanceof MeetingUser){
            MeetingUser user = (MeetingUser) obj;
            if(user.getUserNum().equals(getUserName())||user.getUserNum().equals(getUserNum())){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
}
