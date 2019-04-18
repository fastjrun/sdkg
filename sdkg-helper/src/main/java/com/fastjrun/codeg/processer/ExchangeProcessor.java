package com.fastjrun.codeg.processer;

import com.fastjrun.codeg.common.CodeGConstants;
import com.fastjrun.codeg.generator.method.ServiceMethodGenerator;
import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;

public interface ExchangeProcessor extends CodeGConstants {

    void processRPCRequest(JMethod jMethod, JInvocation jInvocation, JCodeModel cm);

    String processHTTPRequest(JMethod jMethod, JInvocation jInvocation, MockModel mockModel, JCodeModel cm);

    void processResponse(JBlock methodBlk, JInvocation jInvocation, JCodeModel cm);

    AbstractJType getRequestClass();

    AbstractJClass getResponseClass();

    void doParse(ServiceMethodGenerator serviceMethodGenerator, String packagePrefix);
}
