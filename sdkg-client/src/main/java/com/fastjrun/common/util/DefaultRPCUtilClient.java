/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.common.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.context.ApplicationContext;

import com.fastjrun.common.ClientException;
import com.fastjrun.common.CodeMsgConstants;

public class DefaultRPCUtilClient extends BaseUtilClient {

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
