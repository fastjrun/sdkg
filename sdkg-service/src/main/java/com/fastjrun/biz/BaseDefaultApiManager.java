package com.fastjrun.biz;

import com.fastjrun.api.BaseDefaultApi;
import com.fastjrun.dto.DefaultResponse;
import com.fastjrun.helper.BaseResponseHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseDefaultApiManager implements BaseDefaultApi {

    protected final Logger log = LogManager.getLogger(this.getClass());

    @Override
    public DefaultResponse status() {
        return BaseResponseHelper.getSuccessResult();
    }
}
