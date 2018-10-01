package com.fastjrun.client;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/*
 * *
 *  * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *  *
 *  * @author 崔莹峰
 *  * @Copyright 2018 快嘉框架. All rights reserved.
 *
 */

public abstract class BaseRPCResponseHandleClient extends BaseResponseHandleClient<DefaultRPCClient> {

    protected ApplicationContext applicationContext;

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void initSDKConfig() {
        this.applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        this.baseClient = new DefaultRPCClient();
        this.baseClient.setApplicationContext(applicationContext);

    }

}
