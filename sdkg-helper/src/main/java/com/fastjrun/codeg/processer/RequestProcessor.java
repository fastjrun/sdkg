package com.fastjrun.codeg.processer;

import com.fastjrun.codeg.common.CodeGConstants;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;

public interface RequestProcessor extends CodeGConstants {

    String processHTTPRequest(JMethod method, JInvocation jInvocation, MockModel mockModel,JCodeModel cm);

    void processRPCRequest(JMethod method, JInvocation jInvocation,JCodeModel cm);

    void parseRequestClass(JCodeModel cm);

}