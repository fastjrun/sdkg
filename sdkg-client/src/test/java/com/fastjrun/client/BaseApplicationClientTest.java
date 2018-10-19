package com.fastjrun.client;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.DataProvider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fastjrun.helper.StringHelper;

/*
 * *
 *  * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *  *
 *  * @author 崔莹峰
 *  * @Copyright 2018 快嘉框架. All rights reserved.
 *
 */

public abstract class BaseApplicationClientTest<T extends BaseApplicationClient> {

    protected final Logger log = LogManager.getLogger(this.getClass());

    protected Properties propParams = new Properties();

    protected T baseApplicationClient;

    public abstract void prepareApplicationClient(String envName);

    protected void init(String envName) {
        baseApplicationClient.initSDKConfig();
        try {
            InputStream inParam =
                    this.getClass().getResourceAsStream((("/testdata/" + envName) + ".properties"));
            propParams.load(inParam);
        } catch (IOException e) {
            log.error("load config for error:{}", e);
        }
    }

    @DataProvider(name = "loadParam")
    public Object[][] loadParam(Method method) {
        Set<String> keys = propParams.stringPropertyNames();
        List<String[]> parameters = new ArrayList<>();
        for (String key : keys) {
            if (key.startsWith(((baseApplicationClient.getClass().getSimpleName() + ".") + (method.getName() + ".")))) {
                String value = propParams.getProperty(key);
                parameters.add(new String[] {value});
            }
        }
        Object[][] object = new Object[parameters.size()][];
        for (int i = 0; (i < object.length); i++) {
            String[] str = parameters.get(i);
            object[i] = new String[str.length];
            for (int j = 0; (j < str.length); j++) {
                object[i][j] = str[j];
            }
        }
        return object;
    }

    protected <T> void processAssertion(JsonNode assertJson, Object responseBody, Class<T> classType) {

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

    private <T> void processObject(String key, Object object, Class<T> classType, String expectedValue, String info) {

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
                        returnValue = List.class.getMethod("get", int.class).invoke(returnValue, index);
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
                        Assert.fail(info + " doesn't have"
                                + " value:" + expectedValue);
                    }
                }

            } else {
                returnClass = method.getReturnType();
                newClassType = (Class) returnClass;
            }
            if (length == 1) {
                Assert.assertEquals(returnValue, expectedValue, info);
            } else {
                this.processObject(key.substring(key.indexOf(".") + 1), returnValue, newClassType, expectedValue, info);
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
