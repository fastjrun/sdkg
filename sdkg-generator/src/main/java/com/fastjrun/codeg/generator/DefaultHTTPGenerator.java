/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.generator;

import com.fastjrun.codeg.generator.method.BaseControllerMethodGenerator;
import com.fastjrun.codeg.generator.method.BaseServiceMethodGenerator;

public class DefaultHTTPGenerator extends BaseHTTPGenerator {

    @Override
    public void generate() {
        this.genreateControllerPath();
        if (!this.isApi()) {
            this.processController();
        }
        this.generatorControllerMethod();
    }

    @Override
    public BaseControllerMethodGenerator prepareBaseControllerMethodGenerator(BaseServiceMethodGenerator baseServiceMethodGenerator) {
        return null;
    }
}
