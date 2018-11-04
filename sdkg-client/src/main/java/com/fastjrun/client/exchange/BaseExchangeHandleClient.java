/*
 * Copyright (C) 2018 Fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.client.exchange;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fastjrun.client.util.BaseUtilClient;

public abstract class BaseExchangeHandleClient<T extends BaseUtilClient> {

    protected final Logger log = LogManager.getLogger(this.getClass());

    protected T baseClient;

    public T getBaseClient() {
        return baseClient;
    }

    public void setBaseClient(T baseClient) {
        this.baseClient = baseClient;
    }

    public abstract void initSDKConfig();

    protected abstract void initExchange();
}
