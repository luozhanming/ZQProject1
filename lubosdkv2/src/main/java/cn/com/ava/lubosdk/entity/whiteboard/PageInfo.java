package cn.com.ava.lubosdk.entity.whiteboard;

import java.util.List;

import cn.com.ava.lubosdk.entity.QueryResult;

public class PageInfo implements QueryResult {
    private int curPage;
    private List<Integer> pages;

    public int getCurPage() {
        return curPage;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }

    public List<Integer> getPages() {
        return pages;
    }

    public void setPages(List<Integer> pages) {
        this.pages = pages;
    }
}
