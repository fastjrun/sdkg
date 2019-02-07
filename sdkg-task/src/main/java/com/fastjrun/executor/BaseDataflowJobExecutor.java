/*
 * Copyright (C) 2019 Fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.executor;

import java.util.List;

public abstract class BaseDataflowJobExecutor<T> extends BaseExecutor {

    abstract public List<T> fetchData();

    abstract public void processItem(T item);
}
