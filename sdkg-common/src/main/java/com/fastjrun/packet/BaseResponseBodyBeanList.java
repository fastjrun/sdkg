package com.fastjrun.packet;

import com.fastjrun.common.AbstractEntity;

import java.util.List;

public class BaseResponseBodyBeanList<T extends AbstractEntity> extends BaseBody {

    private int totalCount;

    private List<T> list;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
