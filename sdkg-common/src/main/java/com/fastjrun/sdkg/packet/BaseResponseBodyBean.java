package com.fastjrun.sdkg.packet;

import com.fastjrun.sdkg.common.AbstractEntity;

public class BaseResponseBodyBean extends BaseResponseBody {

    private AbstractEntity abstractEntity;

    public AbstractEntity getAbstractEntity() {
        return abstractEntity;
    }

    public void setAbstractEntity(AbstractEntity abstractEntity) {
        this.abstractEntity = abstractEntity;
    }
}
