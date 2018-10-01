package com.fastjrun.codeg.processer;

import com.fastjrun.codeg.common.CodeModelConstants;
import com.fastjrun.codeg.generator.method.BaseControllerMethodGenerator;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;

public interface ExchangeProcessor extends CodeModelConstants {

    String processRequest(BaseControllerMethodGenerator baseControllerMethodGenerator, JMethod jcontrollerMethod,
                          MockModel mockModel);

    void processResponse(BaseControllerMethodGenerator baseControllerMethodGenerator, JBlock methodBlk, JInvocation
            jInvocation);

    void parseResponseClass(BaseControllerMethodGenerator baseControllerMethodGenerator);

    void parseRequestClass(BaseControllerMethodGenerator baseControllerMethodGenerator);
}
