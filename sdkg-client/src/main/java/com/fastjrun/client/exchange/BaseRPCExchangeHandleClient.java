/*
 * Copyright (C) 2018 Fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.client.exchange;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fastjrun.client.util.DefaultRPCUtilClient;
import com.fastjrun.dto.BaseListPacket;
import com.fastjrun.dto.BasePacket;
import com.fastjrun.exchange.DefaultRPCExchange;

public abstract class BaseRPCExchangeHandleClient extends BaseExchangeHandleClient<DefaultRPCUtilClient> {

    DefaultRPCExchange defaultRPCExchange;

    public <T, V> V process(Class classType, String methodName) {
        return this.process(classType, methodName, null, null);
    }

    public <T, V> V process(Class classType, String methodName, Class[]
            paramterTypes, Object[] paramerValues) {
        this.defaultRPCExchange.getRequestEncoder().processRequest(paramterTypes, paramerValues);
        Object result = this.baseClient.process(classType, methodName, paramterTypes, paramerValues);
        BasePacket<T, V> response = (BasePacket<T, V>) result;
        return defaultRPCExchange.getResponseDecoder().process(response);
    }

    public <T, V> List<V> processList(Class classType, String methodName) {
        return this.processList(classType, methodName, null, null);
    }

    public <T, V> List<V> processList(Class classType, String methodName, Class[]
            paramterTypes, Object[] paramerValues) {

        Object result = this.baseClient.process(classType, methodName, paramterTypes, paramerValues);
        BaseListPacket<T, V> response = (BaseListPacket<T, V>) result;
        return defaultRPCExchange.getResponseDecoder().processList(response);
    }

    @Override
    public void initSDKConfig() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        this.baseClient = new DefaultRPCUtilClient();
        this.baseClient.setApplicationContext(applicationContext);
        this.initExchange();

    }

}
