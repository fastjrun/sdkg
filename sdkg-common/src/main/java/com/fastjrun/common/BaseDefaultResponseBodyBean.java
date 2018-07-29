package com.fastjrun.common;

import com.fastjrun.packet.BaseResponseBody;

public class BaseDefaultResponseBodyBean extends BaseResponseBody {

    private BaseEntity baseEntity;

    public BaseEntity getBaseEntity() {
        return baseEntity;
    }

    public void setBaseEntity(BaseEntity baseEntity) {
        this.baseEntity = baseEntity;
    }
}
