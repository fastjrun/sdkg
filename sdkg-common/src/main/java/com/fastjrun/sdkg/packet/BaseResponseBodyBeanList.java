package com.fastjrun.sdkg.packet;

import java.util.List;

import com.fastjrun.sdkg.common.AbstractEntity;

public class BaseResponseBodyBeanList<T extends AbstractEntity> extends BaseResponseBody {

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
