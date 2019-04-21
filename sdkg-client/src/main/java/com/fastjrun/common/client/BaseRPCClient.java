/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.common.client;

import java.util.List;

import com.fastjrun.common.exchange.BaseRPCExchange;
import com.fastjrun.common.util.DefaultRPCUtilClient;

public abstract class BaseRPCClient<M extends BaseRPCExchange> extends BaseClient {

    protected DefaultRPCUtilClient baseClient;

    protected M baseExchange;

    public <T> T process(Class classType, String methodName, Class<T> returnClassType) {
        return this.process(classType, methodName, null, null, returnClassType);
    }

    public <T> T process(Class classType, String methodName, Class[]
            paramterTypes, Object[] paramerValues, Class<T> returnClassType) {
        this.baseExchange.processRequest(paramterTypes, paramerValues);
        Object result = this.baseClient.process(classType, methodName, paramterTypes, paramerValues);
        Object object = baseExchange.process(result, returnClassType);
        return (T) object;

    }

    public <T> List<T> processList(Class classType, String methodName, Class<T> returnClassType) {
        return this.processList(classType, methodName, null, null, returnClassType);
    }

    public <T> List<T> processList(Class classType, String methodName, Class[]
            paramterTypes, Object[] paramerValues, Class<T> returnClassType) {

        Object result = this.baseClient.process(classType, methodName, paramterTypes, paramerValues);
        return baseExchange.processList(result, returnClassType);
    }

    protected abstract void initUtilClient(String springFileName);

}
