package com.fastjrun.codeg.processer;

import com.fastjrun.codeg.common.CodeModelConstants;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JInvocation;

public interface ResponseProcessor extends CodeModelConstants {

    void processResponse(JBlock methodBlk, JInvocation jInvocation);

    void parseResponseClass();
}
