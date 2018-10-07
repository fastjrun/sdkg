package com.fastjrun.codeg.processer;

import com.fastjrun.codeg.common.CodeModelConstants;
import com.fastjrun.codeg.generator.method.BaseControllerMethodGenerator;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JInvocation;

public interface ResponseProcessor extends CodeModelConstants {

    void processResponse(BaseControllerMethodGenerator baseControllerMethodGenerator, JBlock methodBlk, JInvocation
            jInvocation);

    void parseResponseClass(BaseControllerMethodGenerator baseControllerMethodGenerator);
}
