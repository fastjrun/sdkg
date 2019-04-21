/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseJob {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    abstract public void execute();
}
