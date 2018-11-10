/*
 * Copyright (C) 2018 Fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;

@ContextConfiguration(locations = {"classpath*:applicationContext.xml"})
public abstract class AbstractAdVancedTestNGSpringContextTest extends
        AbstractTestNGSpringContextTests {
    protected final Logger log = LogManager.getLogger(this.getClass());

    protected Properties propParams = new Properties();

    @BeforeTest
    @org.testng.annotations.Parameters({
            "envName"
    })
    protected void init(String envName) {
        try {
            InputStream inParam =
                    this.getClass().getResourceAsStream((("/testdata/" + envName) + ".properties"));
            propParams.load(inParam);
            inParam.close();
        } catch (IOException e) {
            log.error("load config for error:{}", e);
        }
    }

    @DataProvider(name = "loadParam")
    public Object[][] loadParam(Method method) {
        Set<String> keys = propParams.stringPropertyNames();
        List<String[]> parameters = new ArrayList<String[]>();
        for (String key : keys) {
            if (key.startsWith(((this.getClass().getSimpleName() + ".") + (method.getName() + ".")))) {
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
