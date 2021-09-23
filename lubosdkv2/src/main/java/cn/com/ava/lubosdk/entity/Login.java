package cn.com.ava.lubosdk.entity;


/**
 * 登录实体
 * */
public class Login implements QueryResult {
    /**用户名*/
    private String username="";
    /**密码*/
    private String password="";
    /**是否休眠状态*/
    private boolean isSleep;
    /**登录key*/
    private String key="";
    /**返回码*/
    private String resultCode;
    /**返回信息*/
    private String resultMsg;
    /**是否登录成功*/
    private boolean isLoginSuccess;
    /**token*/
    private String token="";

    private LuBoInfo.LoginIdStun rserverInfo;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isSleep() {
        return isSleep;
    }

    public void setSleep(boolean sleep) {
        isSleep = sleep;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public boolean isLoginSuccess() {
        return isLoginSuccess;
    }

    public void setLoginSuccess(boolean loginSuccess) {
        isLoginSuccess = loginSuccess;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LuBoInfo.LoginIdStun getRserverInfo() {
        return rserverInfo;
    }

    public void setRserverInfo(LuBoInfo.LoginIdStun rserverInfo) {
        this.rserverInfo = rserverInfo;
    }

    @Override
    public String toString() {
        return "Login{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", isSleep=" + isSleep +
                ", key='" + key + '\'' +
                ", resultCode='" + resultCode + '\'' +
                ", resultMsg='" + resultMsg + '\'' +
                ", isLoginSuccess=" + isLoginSuccess +
                '}';
    }
}
