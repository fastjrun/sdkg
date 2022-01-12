/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.example.codeg.generator;

import com.fastjrun.codeg.common.CodeGConstants;
import com.fastjrun.codeg.common.CommonMethod;
import com.fastjrun.codeg.generator.BaseServiceGenerator;
import com.fastjrun.codeg.generator.method.BaseServiceMethodGenerator;
import com.fastjrun.codeg.generator.method.ServiceMethodGenerator;
import com.fastjrun.example.codeg.generator.method.ExampleServiceMethodGenerator;
import com.helger.jcodemodel.JFieldVar;

import java.util.HashMap;

public class ExampleServiceGenerator extends BaseServiceGenerator {

    @Override
    protected void init() {
        this.mockHelperName="com.fastjrun.example.service.helper.MockHelper";
        this.serviceGeneratorName="com.fastjrun.example.codeg.generator.method.ExampleServiceMethodGenerator";

    }
}