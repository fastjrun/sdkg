package com.fastjrun.packet;

import com.fastjrun.entity.BaseEntity;

public class BaseDefaultResponseBodyBean extends BaseBody {

    private BaseEntity baseEntity;

    public BaseEntity getBaseEntity() {
        return baseEntity;
    }

    public void setBaseEntity(BaseEntity baseEntity) {
        this.baseEntity = baseEntity;
    }
}
