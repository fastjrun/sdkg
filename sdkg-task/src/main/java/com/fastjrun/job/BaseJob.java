/*
 * Copyright (C) 2019 Fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.job;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseJob {

    protected final Logger log = LogManager.getLogger(this.getClass());

    abstract public void execute();
}
