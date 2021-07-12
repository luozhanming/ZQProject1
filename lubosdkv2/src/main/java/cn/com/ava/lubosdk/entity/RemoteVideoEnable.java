package cn.com.ava.lubosdk.entity;

import android.util.SparseArray;

public class RemoteVideoEnable implements QueryResult {


    private int maxRemote = 3;
    private SparseArray<Boolean> canControlRemotes;

    public int getMaxRemote() {
        return maxRemote;
    }

    public void setMaxRemote(int maxRemote) {
        this.maxRemote = maxRemote;
    }

    public SparseArray<Boolean> getCanControlRemotes() {
        return canControlRemotes;
    }

    public void setCanControlRemotes(SparseArray<Boolean> canControlRemotes) {
        this.canControlRemotes = canControlRemotes;
    }
}
