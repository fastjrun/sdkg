/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.generator.method;

public abstract class BaseHTTPMethodGenerator extends BaseControllerMethodGenerator {

    @Override
    public void generate() {
        if (!this.isApi()) {
            this.processControllerMethod(
                    this.baseControllerGenerator.getCommonController(),
                    this.baseControllerGenerator.getControlllerClass());
        }
    }
}
