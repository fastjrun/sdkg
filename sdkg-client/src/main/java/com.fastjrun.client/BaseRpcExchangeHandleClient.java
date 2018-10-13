package com.fastjrun.client;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fastjrun.dto.BaseListPacket;
import com.fastjrun.dto.BasePacket;
import com.fastjrun.exchange.DefaultRPCExchange;

/*
 * *
 *  * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *  *
 *  * @author 崔莹峰
 *  * @Copyright 2018 快嘉框架. All rights reserved.
 *
 */

public abstract class BaseRpcExchangeHandleClient extends BaseExchangeHandleClient<DefaultRPCClient> {

    DefaultRPCExchange defaultRPCExchange;

    public <T, V> V process(Class classType, String methodName) {
        return this.process(classType, methodName, null, null);
    }

    public <T, V> V process(Class classType, String methodName, Class[]
            paramterTypes, Object[] paramerValues) {

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
        this.baseClient = new DefaultRPCClient();
        this.baseClient.setApplicationContext(applicationContext);
        this.initExchange();

    }

}
