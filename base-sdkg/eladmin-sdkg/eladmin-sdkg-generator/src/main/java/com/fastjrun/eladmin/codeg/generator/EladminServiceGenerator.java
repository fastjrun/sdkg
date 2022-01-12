/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.eladmin.codeg.generator;

import com.fastjrun.codeg.generator.BaseServiceGenerator;

public class EladminServiceGenerator extends BaseServiceGenerator {

    @Override
    protected void init() {
        this.mockHelperName="com.fastjrun.eladmin.service.helper.MockHelper";
        this.serviceGeneratorName="com.fastjrun.eladmin.codeg.generator.method.EladminServiceMethodGenerator";
    }
}