//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastjrun.dto;

import java.util.LinkedHashMap;

public class PageQueryDTO extends LinkedHashMap<String, Object> {
    private int page;
    private int limit;
    private int start;

    public PageQueryDTO(int page, int limit) {
        this.page = page;
        this.limit = limit;
        this.start = (page - 1) * limit;
    }

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return this.limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getStart() {
        return this.start;
    }

    public void setStart(int start) {
        this.start = start;
    }
}
