/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.common.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseClient {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    public abstract void initSDKConfig();
}
