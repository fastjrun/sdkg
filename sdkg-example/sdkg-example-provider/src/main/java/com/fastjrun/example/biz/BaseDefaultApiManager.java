/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.example.biz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastjrun.example.api.BaseDefaultApi;
import com.fastjrun.example.dto.DefaultResponse;
import com.fastjrun.example.helper.BaseResponseHelper;

public abstract class BaseDefaultApiManager implements BaseDefaultApi {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public DefaultResponse status() {
        return BaseResponseHelper.getSuccessResult();
    }
}
