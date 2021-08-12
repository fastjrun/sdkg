/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.executor;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

public abstract class BaseDataExecutor<T> extends BaseExecutor implements ExecutorManagerable {

    protected int fetchLimit = 1000;

    protected int pageTotal = 1;

    public int getPageTotal() {
        return pageTotal;
    }

    public int getFetchLimit() {
        return fetchLimit;
    }

    public void setFetchLimit(int fetchLimit) {
        this.fetchLimit = fetchLimit;
    }

    abstract public List<T> fetchItems(int pageIndex);

    abstract public int processItem(T item);

    public Future<Integer> batchProcess(T item) {
        try {
            this.semaphore.acquire();
        } catch (InterruptedException e) {
            log.error(semaphore.availablePermits() + "", e);
        }
        Callable<Integer> callable = () -> {
            int res =0 ;
            try {
                res=this.processItem(item);
            } catch (Exception e) {
                log.error("", e);
            } finally {

            }

            // 访问完后，释放
            this.semaphore.release();
            return res;
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
        return future;
    }
}
