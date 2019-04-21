/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.generator;

import java.util.Properties;

import com.fastjrun.codeg.generator.common.BaseControllerGenerator;

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
    }

}