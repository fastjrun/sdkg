package com.fastjrun.codeg.generator.method;

import java.util.List;

import com.fastjrun.codeg.common.CodeModelConstants;
import com.fastjrun.codeg.common.PacketField;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;

public class MethodGeneratorHelper implements CodeModelConstants {

    public static void processServiceMethodVariables(JMethod jmethod, List<PacketField> variables) {
        if (variables != null && variables.size() > 0) {
            for (int index = 0; index < variables.size(); index++) {
                PacketField variable = variables.get(index);
                JType jType = cm.ref(variable.getDatatype());
                jmethod.param(jType, variable.getName());
            }
        }
    }
}