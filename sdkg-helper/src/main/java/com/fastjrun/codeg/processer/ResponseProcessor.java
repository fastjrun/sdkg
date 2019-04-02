package com.fastjrun.codeg.processer;

import com.fastjrun.codeg.common.CodeGConstants;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JInvocation;

public interface ResponseProcessor extends CodeGConstants {

    void processResponse(JBlock methodBlk, JInvocation jInvocation,JCodeModel cm);

    void parseResponseClass(JCodeModel cm);
}
