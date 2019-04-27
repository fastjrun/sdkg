/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fastjrun.helper.StringHelper;
import com.fastjrun.test.AbstractAdVancedTestNGSpringContextTest;
import org.testng.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class BaseApplicationClientTest<T extends BaseApplicationClient>
  extends AbstractAdVancedTestNGSpringContextTest {

    protected T baseApplicationClient;

    public abstract void prepareApplicationClient();

    protected <T> void processAssertion(JsonNode assertJson, Object responseBody,
      Class<T> classType) {

        if (assertJson != null && assertJson.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> it = assertJson.fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> entry = it.next();
                String key = entry.getKey();
                if (key.equals("code")) {
                    continue;
                }
                String jsonNodeText = entry.getValue().asText();
                processObject(key, responseBody, classType, jsonNodeText, "");
            }
        }

    }

    private <T> void processObject(String key, Object object, Class<T> classType,
      String expectedValue, String info) {

        String[] keyFields = key.split("\\.");
        // 只有一级属性
        int length = keyFields.length;
        // 有可能属性名称
        String tterMethodName = keyFields[0].split("\\[")[0];
        if (info.equals("")) {
            info = tterMethodName;
        } else {
            info = info + "." + tterMethodName;

        }
        if (tterMethodName.length() > 1) {
            String char2 = String.valueOf(tterMethodName.charAt(1));
            if (!char2.equals(char2.toUpperCase())) {
                tterMethodName = StringHelper.toUpperCaseFirstOne(tterMethodName);
            }
        }
        try {
            Method method = classType.getMethod("get" + tterMethodName);
            Object returnValue = method.invoke(object);
            Type returnClass = null;
            Class<T> newClassType = null;
            if (keyFields[0].indexOf("[") > 0) {
                returnClass = method.getGenericReturnType();
                Type[] typeArguments = ((ParameterizedType) returnClass).getActualTypeArguments();
                newClassType = (Class) typeArguments[0];
                int size = (int) List.class.getMethod("size").invoke(returnValue);
                String indexStr = keyFields[0].split("\\[")[1].split("]")[0];
                if (indexStr.length() > 0) {
                    info = info + "[" + indexStr + "]";
                    int index = Integer.parseInt(indexStr);
                    if (size > index) {
                        returnValue =
                          List.class.getMethod("get", int.class).invoke(returnValue, index);
                    } else {
                        Assert.fail(info + " is null");
                    }
                } else {
                    info = info + "[]";
                    boolean isEqual = false;
                    for (int i = 0; i < size; i++) {
                        returnValue = List.class.getMethod("get", int.class).invoke(returnValue, i);
                        if (expectedValue.equals(returnValue.toString())) {
                            isEqual = true;
                            break;
                        }
                    }
                    if (!isEqual) {
                        Assert.fail(info + " doesn't have" + " value:" + expectedValue);
                    }
                }

            } else {
                returnClass = method.getReturnType();
                newClassType = (Class) returnClass;
            }
            if (length == 1) {
                Assert.assertEquals(returnValue, expectedValue, info);
            } else {
                this.processObject(key.substring(key.indexOf(".") + 1), returnValue, newClassType,
                  expectedValue, info);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
