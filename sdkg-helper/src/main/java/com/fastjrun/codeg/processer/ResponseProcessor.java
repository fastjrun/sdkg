package com.fastjrun.codeg.processer;

import com.fastjrun.codeg.common.CodeGConstants;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JInvocation;

public interface ResponseProcessor extends CodeGConstants {

    void processResponse(JBlock methodBlk, JInvocation jInvocation, JCodeModel cm);

    void parseResponseClass(JCodeModel cm);
}
