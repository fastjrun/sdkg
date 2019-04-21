/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.common.exchange;

import java.util.List;

public abstract class BaseRPCResponseDecoder extends BaseResponseDecoder {

    public <T> T process(Object response, Class<T> valueType) {
        Object object = this.parseDataFromResponse(response);
        return (T) object;
    }

    public <T> List<T> processList(Object response, Class<T> valueType) {
        Object object = this.parseDataFromResponse(response);
        return (List<T>) object;
    }

    protected abstract Object parseDataFromResponse(Object response);
}
