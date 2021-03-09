package com.kenny.mall.util;


import java.io.Serializable;
import java.util.List;

public class PageResult<T> implements Serializable {

    //总条数
    private int totalCount;

    //每页记录数
    private int pageSize;

    //总页数
    private int totalPage;
    //当前页
    private int currPage;
    //页面数据
    private List<T> list;

    public PageResult(int totalCount, int pageSize, int currPage, List<T> list) {
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.currPage = currPage;
        this.list = list;
        this.totalPage = (int)Math.ceil((double)totalCount/pageSize);
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getCurrPage() {
        return currPage;
    }

    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
