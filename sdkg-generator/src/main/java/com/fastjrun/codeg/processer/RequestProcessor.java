/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.processer;

import com.fastjrun.codeg.common.CodeGConstants;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;

public interface RequestProcessor extends CodeGConstants {

    String processHTTPRequest(JMethod method, JInvocation jInvocation, MockModel mockModel, JCodeModel cm);

    void processRPCRequest(JMethod method, JInvocation jInvocation, JCodeModel cm);

    void parseRequestClass(JCodeModel cm);

}