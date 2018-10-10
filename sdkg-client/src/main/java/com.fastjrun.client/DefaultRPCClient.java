package com.fastjrun.client;

/*
 * *
 *  * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *  *
 *  * @author 崔莹峰
 *  * @Copyright 2018 快嘉框架. All rights reserved.
 *
 */

import com.fastjrun.common.ClientException;
import com.fastjrun.common.CodeMsgConstants;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DefaultRPCClient extends BaseUtilClient {

    protected ApplicationContext applicationContext;

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Object process(Class classType, String methodName, Class[]
            paramterTypes, Object[] paramerValues) {
        Object service = applicationContext.getBean(classType);
        Method method;
        try {
            if (paramerValues == null) {
                method = classType.getDeclaredMethod(methodName);
            } else {
                method = classType.getDeclaredMethod(methodName, paramterTypes);
            }

        } catch (NoSuchMethodException e) {
            log.error("{}", e);
            throw new ClientException(CodeMsgConstants.CodeMsg.ClIENT_NETWORK_RESPONSE_NOT_OK);
        }
        Object result;
        try {
            if (paramerValues == null) {
                result = method.invoke(service);
            } else {
                result = method.invoke(service, paramerValues);
            }

        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("{}", e);
            throw new ClientException(CodeMsgConstants.CodeMsg.ClIENT_NETWORK_RESPONSE_NOT_OK);
        }
        return result;
    }
}
