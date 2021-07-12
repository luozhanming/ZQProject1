package cn.com.ava.lubosdk.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InteraInfo implements QueryResult{
    @SerializedName("rmma_id")
    private int rmmaId;
    @SerializedName("max_rcv_count")
    /**最大互动数*/
    private int maxRcvCount;
    @SerializedName("online_count")
    /**在线数*/
    private int onlineCount;
    @SerializedName("onlineList")
    /**在线互动用户列表*/
    private List<LinkedUser> onlineList;
    /**互动布局*/
    private List<Integer> layout;

    public int getRmmaId() {
        return rmmaId;
    }

    public void setRmmaId(int rmmaId) {
        this.rmmaId = rmmaId;
    }

    public int getMaxRcvCount() {
        return maxRcvCount;
    }

    public void setMaxRcvCount(int maxRcvCount) {
        this.maxRcvCount = maxRcvCount;
    }

    public int getOnlineCount() {
        return onlineCount;
    }

    public void setOnlineCount(int onlineCount) {
        this.onlineCount = onlineCount;
    }

    public List<LinkedUser> getOnlineList() {
        return onlineList;
    }

    public void setOnlineList(List<LinkedUser> onlineList) {
        this.onlineList = onlineList;
    }

    public List<Integer> getLayout() {
        return layout;
    }

    public void setLayout(List<Integer> layout) {
        this.layout = layout;
    }
}
