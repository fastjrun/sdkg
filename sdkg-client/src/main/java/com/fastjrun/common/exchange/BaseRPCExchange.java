/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.common.exchange;

import java.util.List;

public abstract class BaseRPCExchange<U extends BaseRPCRequestEncoder, M extends
        BaseRPCResponseDecoder> {

    protected U requestEncoder;

    protected M responseDecoder;

    public void processRequest(Class[]
                                       paramterTypes, Object[] paramerValues) {
        this.requestEncoder.processRequest(paramterTypes, paramerValues);
    }

    public <T> T process(Object response, Class<T> valueType) {
        return this.responseDecoder.process(response, valueType);
    }

    public <T> List<T> processList(Object response, Class<T> valueType) {
        return this.responseDecoder.processList(response, valueType);
    }
}
