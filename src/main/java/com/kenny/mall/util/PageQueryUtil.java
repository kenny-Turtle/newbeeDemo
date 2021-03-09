package com.kenny.mall.util;

import sun.management.snmp.jvmmib.JvmRTBootClassPathTableMeta;

import java.util.LinkedHashMap;
import java.util.Map;

public class PageQueryUtil extends LinkedHashMap<String,Object> {

    private int page;

    private int limit;

    public PageQueryUtil(Map<String,Object> params){
        this.putAll(params);
        this.page = Integer.parseInt(params.get("page").toString());//从map里获取page，再转为int类型
        this.limit = Integer.parseInt(params.get("limit").toString());
        this.put("start",(page-1)*limit);
        this.put("page",page);
        this.put("limit",limit);
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return "PageQueryUtil{" +
                "page=" + page +
                ", limit=" + limit +
                '}';
    }
}
