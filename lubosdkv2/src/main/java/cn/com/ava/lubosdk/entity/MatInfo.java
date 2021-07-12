package cn.com.ava.lubosdk.entity;

public class MatInfo implements QueryResult{
    //抠像1是否可扣
    private boolean canMatImage1;
    //抠像2是否可抠
    private boolean canMatImage2;
    //哪个正在抠像0/1/2
    private int curMat;

    public boolean isCanMatImage1() {
        return canMatImage1;
    }

    public void setCanMatImage1(boolean canMatImage1) {
        this.canMatImage1 = canMatImage1;
    }

    public boolean isCanMatImage2() {
        return canMatImage2;
    }

    public void setCanMatImage2(boolean canMatImage2) {
        this.canMatImage2 = canMatImage2;
    }

    public int getCurMat() {
        return curMat;
    }

    public void setCurMat(int curMat) {
        this.curMat = curMat;
    }
}
