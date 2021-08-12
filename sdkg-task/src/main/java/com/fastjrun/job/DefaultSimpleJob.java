/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.job;

import com.fastjrun.executor.BaseSimpleExecutor;
import com.fastjrun.helper.LocalDateTimeHelper;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Administrator
 */
public class DefaultSimpleJob extends BaseJob {

    BaseSimpleExecutor baseSimpleExecutor;

    public void setBaseSimpleExecutor(BaseSimpleExecutor baseSimpleExecutor) {
        this.baseSimpleExecutor = baseSimpleExecutor;
    }

    @Override
    public void execute() {
        log.debug("{} started", this.baseSimpleExecutor.getClass().getSimpleName());
        LocalDateTime start = LocalDateTime.now();
        Future<Integer> future = this.baseSimpleExecutor.submitCallable();
        try {
            Integer res = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        LocalDateTime end = LocalDateTime.now();
        String duration = LocalDateTimeHelper.formatDuration(start, end);
        log.debug("cost time:{}", duration);
    }
}
