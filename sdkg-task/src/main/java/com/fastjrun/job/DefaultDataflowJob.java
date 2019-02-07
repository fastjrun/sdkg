/*
 * Copyright (C) 2019 Fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.job;

import java.util.List;

import com.fastjrun.executor.BaseDataflowJobExecutor;

public class DefaultDataflowJob<T> extends BaseJob {

    BaseDataflowJobExecutor<T> baseDataflowJobExecutor;

    public BaseDataflowJobExecutor<T> getBaseDataflowJobExecutor() {
        return baseDataflowJobExecutor;
    }

    public void setBaseDataflowJobExecutor(BaseDataflowJobExecutor<T> baseDataflowJobExecutor) {
        this.baseDataflowJobExecutor = baseDataflowJobExecutor;
    }

    @Override
    public void execute() {
        List<T> list = this.baseDataflowJobExecutor.fetchData();
        list.parallelStream().forEach(var -> {
            this.baseDataflowJobExecutor.processItem(var);
        });
    }
}
