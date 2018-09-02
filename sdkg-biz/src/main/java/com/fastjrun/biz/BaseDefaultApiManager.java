package com.fastjrun.biz;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fastjrun.api.BaseDefaultApi;
import com.fastjrun.helper.BaseResponseHelper;
import com.fastjrun.packet.DefaultResponse;
import com.fastjrun.packet.EmptyBody;

public abstract class BaseDefaultApiManager implements BaseDefaultApi {

    protected final Logger log = LogManager.getLogger(this.getClass());

    @Override
    public DefaultResponse<EmptyBody> status() {
        return BaseResponseHelper.getSuccessResult();
    }
}
