package cn.com.ava.lubosdk.entity;

import java.util.List;

public class ListWrapper<T> implements QueryResult {

    private List<T> datas;


    public ListWrapper(List<T> datas) {
        this.datas = datas;
    }

    public List<T> getDatas() {
        return datas;
    }
}
