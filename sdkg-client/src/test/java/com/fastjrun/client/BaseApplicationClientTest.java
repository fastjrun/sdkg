package com.fastjrun.client;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.DataProvider;

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
}
