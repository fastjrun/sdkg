/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.dto;

import com.fastjrun.entity.BaseEntity;

public abstract class BaseDefaultResponseBodyBean<T extends BaseEntity> {

    private T baseEntity;

    public T getBaseEntity() {
        return baseEntity;
    }

    public void setBaseEntity(T baseEntity) {
        this.baseEntity = baseEntity;
    }
}
