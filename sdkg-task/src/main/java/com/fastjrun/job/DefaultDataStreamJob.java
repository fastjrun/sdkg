/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 *
 * 可以通过更改终止条件，从而从任务终止
 */
package com.fastjrun.job;

import com.fastjrun.helper.LocalDateTimeHelper;
import com.google.common.collect.Lists;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultDataStreamJob extends BaseDataJob {

    @Override
    public void execute() {
        this.baseDataExecutor.start();

        while (true) {
            if (this.baseDataExecutor.canStop()) {
                this.baseDataExecutor.stop();
                break;
            }
            for (int pageIndex =
                 0; pageIndex <= this.baseDataExecutor.getFetchLimit(); pageIndex++) {
                List<?> items = this.baseDataExecutor.fetchItems(pageIndex);
                if (items != null && items.size() > 0) {
                    LocalDateTime start = LocalDateTime.now();
                    List<Future<Integer>> resList = Lists.newCopyOnWriteArrayList();
                    items.parallelStream().forEach(item -> {
                        Future<Integer> res = this.baseDataExecutor.batchProcess(item);
                        resList.add(res);
                    });
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
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


    }
}

