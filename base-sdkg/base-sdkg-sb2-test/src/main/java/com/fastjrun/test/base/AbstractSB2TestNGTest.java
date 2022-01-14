package com.fastjrun.test.base;

import com.fastjrun.test.util.TestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractSB2TestNGTest  extends AbstractTestNGSpringContextTests {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected Map<String, Object> propParams = new HashMap<>();

    @BeforeClass
    @Parameters({"envName"})
    protected void initParam(@Optional("unitTest") String envName) {
        log.debug("initParam");
        try {
            this.propParams = TestUtils.initParam("/testdata/" + envName + ".yaml");
        } catch (IOException e) {
            log.error("load config for error:{}", e);
        }
    }

    @DataProvider(name = "loadParam")
    public Object[][] loadParam(Method method) {
        log.debug("loadParam");
        return TestUtils.loadParam(this.propParams, this.getClass().getSimpleName(), method);
    }
}
