/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.job;

import com.fastjrun.executor.BaseDataExecutor;

public abstract class BaseDataJob<T> extends BaseJob {

    protected boolean canStart;

    protected BaseDataExecutor<T> baseDataExecutor;

    public boolean isCanStart() {
        return canStart;
    }

    public void setCanStart(boolean canStart) {
        this.canStart = canStart;
    }

    public BaseDataExecutor<T> getBaseDataExecutor() {
        return baseDataExecutor;
    }

    public void setBaseDataExecutor(BaseDataExecutor<T> baseDataExecutor) {
        this.baseDataExecutor = baseDataExecutor;
    }
}
