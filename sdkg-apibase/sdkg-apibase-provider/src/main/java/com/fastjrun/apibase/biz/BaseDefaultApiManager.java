/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.apibase.biz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastjrun.apibase.api.BaseDefaultApi;
import com.fastjrun.apibase.dto.DefaultResponse;
import com.fastjrun.apibase.helper.BaseResponseHelper;

public abstract class BaseDefaultApiManager implements BaseDefaultApi {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public DefaultResponse status() {
        return BaseResponseHelper.getSuccessResult();
    }
}
