package com.fastjrun.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * *
 *  * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *  *
 *  * @author 崔莹峰
 *  * @Copyright 2018 快嘉框架. All rights reserved.
 *
 */

public abstract class BaseApplicationClient<T extends BaseExchangeHandleClient> {
    protected final Logger log = LogManager.getLogger(this.getClass());

    protected T baseClient;

    public T getBaseClient() {
        return baseClient;
    }

    public void setBaseClient(T baseClient) {
        this.baseClient = baseClient;
    }

    public abstract void initSDKConfig();

}
