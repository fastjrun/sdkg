/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.processor;

import com.fastjrun.codeg.common.CodeGConstants;
import com.fastjrun.codeg.generator.method.BaseServiceMethodGenerator;
import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;

public interface ExchangeProcessor extends CodeGConstants {

    String processHTTPRequest(JMethod jMethod, JInvocation jInvocation, MockModel mockModel, JCodeModel cm);

    void processResponse(JBlock methodBlk, JInvocation jInvocation, JCodeModel cm);

    AbstractJType getRequestClass();

    AbstractJType getResponseClass();

    void doParse(BaseServiceMethodGenerator serviceMethodGenerator, String packagePrefix);
}
