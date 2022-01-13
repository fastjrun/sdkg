/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.example.codeg.generator;

import com.fastjrun.codeg.generator.BaseServiceGenerator;

public class ExampleServiceGenerator extends BaseServiceGenerator {

    @Override
    protected void init() {
        this.mockHelperName="com.fastjrun.example.service.helper.MockHelper";
        this.serviceGeneratorName="com.fastjrun.example.codeg.generator.method.ExampleServiceMethodGenerator";
        this.pageResultName="com.fastjrun.example.dto.PageResult";

    }
}