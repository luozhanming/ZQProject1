package cn.com.ava.lubosdk.entity;

import java.util.List;

import cn.com.ava.lubosdk.Constant;


/**
 * 互动视频源老师/学生实体(授课模式)
 */
public class TsInteracSource implements QueryResult {

    private int type;
    private List<String> windowNames;
    private List<Integer> windowIndex;
    private int selectIndex;

    public int getType() {
        return type;
    }

    public void setType(@Constant.TSType int type) {
        this.type = type;
    }

    public List<String> getWindowNames() {
        return windowNames;
    }

    public void setWindowNames(List<String> windowNames) {
        this.windowNames = windowNames;
    }

    public List<Integer> getWindowIndex() {
        return windowIndex;
    }

    public void setWindowIndex(List<Integer> windowIndex) {
        this.windowIndex = windowIndex;
    }

    public int getSelectIndex() {
        return selectIndex;
    }

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
    }
}
