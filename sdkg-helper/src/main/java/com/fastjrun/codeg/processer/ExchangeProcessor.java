package com.fastjrun.codeg.processer;

import com.fastjrun.codeg.common.CodeGConstants;
import com.fastjrun.codeg.generator.method.ServiceMethodGenerator;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;

public interface ExchangeProcessor extends CodeGConstants {

    void processRPCRequest(JMethod jMethod, JInvocation jInvocation, JCodeModel cm);

    String processHTTPRequest(JMethod jMethod, JInvocation jInvocation, MockModel mockModel, JCodeModel cm);

    void processResponse(JBlock methodBlk, JInvocation jInvocation, JCodeModel cm);

    JType getRequestClass();

    JClass getResponseClass();

    void doParse(ServiceMethodGenerator serviceMethodGenerator, String packagePrefix);
}
