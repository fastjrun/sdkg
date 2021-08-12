/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

public abstract class BaseJob {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());


    protected int semaphoreTotal = 20;

    protected ExecutorService executorService;

    protected Semaphore semaphore;

    public void setSemaphoreTotal(int semaphoreTotal) {
        this.semaphoreTotal = semaphoreTotal;
        this.semaphore = new Semaphore(this.semaphoreTotal,true);
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    /**
     *
     */
    abstract public void execute();
}
