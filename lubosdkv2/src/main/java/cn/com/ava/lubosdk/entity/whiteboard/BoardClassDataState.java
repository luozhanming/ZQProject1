package cn.com.ava.lubosdk.entity.whiteboard;

import java.util.List;

import cn.com.ava.lubosdk.entity.QueryResult;

public class BoardClassDataState implements QueryResult {
    /*参与教室数量*/
    private int classNum;
    /*当前页码*/
    private int curPage;

    /*info*/
    private List<ClassroomControlEntity> entities;


    public int getClassNum() {
        return classNum;
    }

    public void setClassNum(int classNum) {
        this.classNum = classNum;
    }

    public int getCurPage() {
        return curPage;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }

    public List<ClassroomControlEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<ClassroomControlEntity> entities) {
        this.entities = entities;
    }
}
