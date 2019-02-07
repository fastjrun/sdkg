/*
 * Copyright (C) 2019 Fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.job;

import com.fastjrun.executor.BaseSimpleJobExecutor;

public class DefaultSimpleJob extends BaseJob {

    BaseSimpleJobExecutor baseSimpleJobExecutor;

    public BaseSimpleJobExecutor getBaseSimpleJobExecutor() {
        return baseSimpleJobExecutor;
    }

    public void setBaseSimpleJobExecutor(BaseSimpleJobExecutor baseSimpleJobExecutor) {
        this.baseSimpleJobExecutor = baseSimpleJobExecutor;
    }

    @Override
    public void execute() {
        baseSimpleJobExecutor.execute();
    }
}
