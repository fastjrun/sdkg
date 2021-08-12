/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.job;

import com.fastjrun.helper.LocalDateTimeHelper;
import com.google.common.collect.Lists;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultDataFlowJob<T> extends BaseDataJob {

    @Override
    public void execute() {
        LocalDateTime start = LocalDateTime.now();
        List<Future<Integer>> resList = Lists.newCopyOnWriteArrayList();
        List<T> items = this.baseDataExecutor.fetchItems(1);
        if (items != null && items.size() > 0) {
            items.parallelStream().forEach(item -> {
                Future<Integer> res = this.baseDataExecutor.batchProcess(item);
                resList.add(res);
            });
        }
        AtomicInteger resTotal = new AtomicInteger();
        resList.parallelStream().forEach(var -> {
            try {
                resTotal.addAndGet(var.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        LocalDateTime end = LocalDateTime.now();
        String duration = LocalDateTimeHelper.formatDuration(start, end);
        log.debug("data size:{};cost time:{}", resTotal.get(), duration);

    }
}
