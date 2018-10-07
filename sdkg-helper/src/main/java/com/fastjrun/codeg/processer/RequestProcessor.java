package com.fastjrun.codeg.processer;

import com.fastjrun.codeg.common.CodeModelConstants;
import com.fastjrun.codeg.generator.method.BaseControllerMethodGenerator;
import com.sun.codemodel.JMethod;

public interface RequestProcessor extends CodeModelConstants {

    String processRequest(BaseControllerMethodGenerator baseControllerMethodGenerator, JMethod jcontrollerMethod,
                          MockModel mockModel);

    void parseRequestClass(BaseControllerMethodGenerator baseControllerMethodGenerator);
}
