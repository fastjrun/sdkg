/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.generator.method;

import java.util.List;

import com.fastjrun.codeg.common.PacketField;
import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JMethod;

public class MethodGeneratorHelper {

    public static void processServiceMethodVariables(JMethod jmethod, List<PacketField> variables, JCodeModel cm) {
        if (variables != null && variables.size() > 0) {
            for (int index = 0; index < variables.size(); index++) {
                PacketField variable = variables.get(index);
                AbstractJType jType = cm.ref(variable.getDatatype());
                jmethod.param(jType, variable.getName());
            }
        }
    }
}