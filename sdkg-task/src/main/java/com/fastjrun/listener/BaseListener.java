/*
 * Copyright (C) 2019 Fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseListener {
    protected final Logger log = LogManager.getLogger(this.getClass());
}
