/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.client;

import java.util.ResourceBundle;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fastjrun.common.client.BaseRPCClient;
import com.fastjrun.exchange.DefaultRPCExchange;
import com.fastjrun.common.util.DefaultRPCUtilClient;

public class DefaultDubboClient extends BaseRPCClient<DefaultRPCExchange> {

    public DefaultDubboClient() {
        this.baseClient = new DefaultRPCUtilClient();
        this.baseExchange = new DefaultRPCExchange();
    }

    @Override
    protected void initUtilClient(String springFileName) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(springFileName);
        this.baseClient = new DefaultRPCUtilClient();
        this.baseClient.setApplicationContext(applicationContext);

    }

    @Override
    public void initSDKConfig() {
        ResourceBundle rb = ResourceBundle.getBundle("api-sdk");
        String springFileName = rb.getString("dubboServer.springFileName");
        this.initUtilClient(springFileName);
    }
}
