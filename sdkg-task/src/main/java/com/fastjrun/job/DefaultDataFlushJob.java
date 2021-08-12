/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.job;

import com.fastjrun.helper.LocalDateTimeHelper;
import com.google.common.collect.Lists;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultDataFlushJob<T> extends BaseDataJob {

    @Override
    public void execute() {
        if (!this.canStart) {
            return;
        }
        while (!this.baseDataExecutor.canStart()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.baseDataExecutor.start();
        log.debug("{} started", this.baseDataExecutor.getClass().getSimpleName());

        LocalDateTime start = LocalDateTime.now();

        AtomicInteger resTotal = new AtomicInteger();

        for (int pageIndex = 0; pageIndex <= this.baseDataExecutor.getPageTotal(); pageIndex++) {
            log.debug("semaphore.availablePermits()={}", semaphore.availablePermits());
            try {
                this.semaphore.acquire();
            } catch (InterruptedException e) {
                log.error(semaphore.availablePermits() + "", e);
            }

            final int pageFinal = pageIndex;
            Callable<Integer> callable = () -> {
                List<T> items = this.baseDataExecutor.fetchItems(pageFinal);
                List<Future<Integer>> resList = Lists.newCopyOnWriteArrayList();
                if (items != null) {
                    items.parallelStream().forEach(item -> {
                        resList.add(baseDataExecutor.batchProcess(item));
                    });
                }

                int total = resList.parallelStream().mapToInt(var -> {
                    try {
                        return var.get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }).sum();
                // 访问完后，释放
                semaphore.release();
                return total;
            };
            Future<Integer> future = null;
            // 因为队列太小，可能会导致线程提交被拒绝，这个时候要重复提交，直到成功为止
            while (future == null) {
                try {
                    future = this.executorService.submit(callable);
                } catch (RejectedExecutionException e) {
                    log.warn(semaphore.availablePermits() + "", e);
                }
            }
            try {
                resTotal.addAndGet(future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        LocalDateTime end = LocalDateTime.now();
        String duration = LocalDateTimeHelper.formatDuration(start, end);

        log.debug("{} ended with data size:{};cost time:{}",
          this.baseDataExecutor.getClass().getSimpleName(), resTotal.get(), duration);
        this.baseDataExecutor.stop();
    }

}