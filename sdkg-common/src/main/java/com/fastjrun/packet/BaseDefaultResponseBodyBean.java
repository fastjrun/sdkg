package com.fastjrun.packet;

import com.fastjrun.common.BaseEntity;

public class BaseDefaultResponseBodyBean extends BaseResponseBody {

    private BaseEntity baseEntity;

    public BaseEntity getBaseEntity() {
        return baseEntity;
    }

    public void setBaseEntity(BaseEntity baseEntity) {
        this.baseEntity = baseEntity;
    }
}
