/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseApplicationClient<T extends BaseClient> {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected T baseClient;

    public T getBaseClient() {
        return baseClient;
    }

    public void setBaseClient(T baseClient) {
        this.baseClient = baseClient;
    }

    public abstract void initSDKConfig();

}
