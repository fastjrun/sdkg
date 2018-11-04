package com.fastjrun.codeg.processer;

import com.fastjrun.codeg.common.CodeModelConstants;
import com.fastjrun.codeg.generator.method.ServiceMethodGenerator;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;

public interface ExchangeProcessor extends CodeModelConstants {

    void processRPCRequest(JMethod jMethod, JInvocation jInvocation);

    String processHTTPRequest(JMethod jMethod, JInvocation jInvocation, MockModel mockModel);

    void processResponse(JBlock methodBlk, JInvocation jInvocation);

    JType getRequestClass();

    JClass getResponseClass();

    void doParse(ServiceMethodGenerator serviceMethodGenerator, String packagePrefix);
}
