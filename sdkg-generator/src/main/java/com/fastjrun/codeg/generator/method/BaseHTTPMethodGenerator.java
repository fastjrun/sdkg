/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.generator.method;

import com.fastjrun.codeg.common.PacketField;
import com.fastjrun.codeg.helper.StringHelper;
import com.helger.jcodemodel.*;

import java.util.List;

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
