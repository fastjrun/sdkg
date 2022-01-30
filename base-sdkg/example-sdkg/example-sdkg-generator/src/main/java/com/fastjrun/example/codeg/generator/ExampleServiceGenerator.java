/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.example.codeg.generator;

import com.fastjrun.codeg.generator.BaseServiceGenerator;
import com.fastjrun.example.codeg.Constants;

public class ExampleServiceGenerator extends BaseServiceGenerator {

    @Override
    protected void init() {
        this.mockHelperName = Constants.MOCK_HELPER_CLASS_NAME;
        this.pageResultName = Constants.PAGE_RESULT_CLASS_NAME;

    }
}