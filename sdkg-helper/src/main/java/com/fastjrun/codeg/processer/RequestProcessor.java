package com.fastjrun.codeg.processer;

import com.fastjrun.codeg.common.CodeModelConstants;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;

public interface RequestProcessor extends CodeModelConstants {

    String processHTTPRequest(JMethod method, JInvocation jInvocation, MockModel mockModel);

    void processRPCRequest(JMethod method, JInvocation jInvocation);

    void parseRequestClass();

}