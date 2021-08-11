/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.generator;

import com.fastjrun.codeg.generator.common.BaseControllerGenerator;

import java.util.Properties;

public abstract class BaseHTTPGenerator extends BaseControllerGenerator {

    @Override
    public void generate() {
        this.genreateControllerPath();
        if (!this.isApi()) {
            if (this.isClient()) {
                this.processClient();
                this.processClientTest();
                this.clientTestParam = new Properties();
            } else {
                this.processController();
            }
        }
        this.generatorControllerMethod();
    }

}
