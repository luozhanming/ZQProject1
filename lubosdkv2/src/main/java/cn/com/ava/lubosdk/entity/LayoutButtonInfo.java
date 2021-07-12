package cn.com.ava.lubosdk.entity;



/**
 * 布局按钮信息
 * */
public class LayoutButtonInfo implements QueryResult{
    /**按钮指令*/
    private String cmd;
    /**按钮资源*/
    private int drawableId;
    /**是否当前输出*/
    private boolean isCurLayout;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    public boolean isCurLayout() {
        return isCurLayout;
    }

    public void setCurLayout(boolean curLayout) {
        isCurLayout = curLayout;
    }


    @Override
    public String toString() {
        return "LayoutButtonInfo{" +
                "cmd='" + cmd + '\'' +
                ", drawableId=" + drawableId +
                ", isCurLayout=" + isCurLayout +
                '}';
    }
}
