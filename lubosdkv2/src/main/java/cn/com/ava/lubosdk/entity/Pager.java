package cn.com.ava.lubosdk.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**页码*/
public class Pager<T> {

    @SerializedName("userbook")
    private List<T> list;
    private String offset;
    private String stotal;
    private String total;
    private String curServerTime;
    @SerializedName("ErrorCode")
    private String errorCode;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getStotal() {
        return stotal;
    }

    public void setStotal(String stotal) {
        this.stotal = stotal;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getCurServerTime() {
        return curServerTime;
    }

    public void setCurServerTime(String curServerTime) {
        this.curServerTime = curServerTime;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
