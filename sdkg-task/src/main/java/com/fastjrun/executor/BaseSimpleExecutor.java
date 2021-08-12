/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.executor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public abstract class BaseSimpleExecutor extends BaseExecutor {
    /**
     * 任务执行
     *
     * @return
     */
    protected abstract void execute();

    public Future<Integer> submitCallable() {
        try {
            this.semaphore.acquire();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        Callable<Integer> callable = () -> {
            try {
                this.execute();
            } catch (Exception e) {
                log.error("", e);
            }

            // 访问完后，释放
            this.semaphore.release();
            return 1;
        };
        return this.executorService.submit(callable);
    }
}
