/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.processer;

import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JInvocation;

public class WebResponseProcessor extends BaseResponseProcessor {
    @Override
    public void processResponse(JBlock methodBlk, JInvocation jInvocation, JCodeModel cm) {
        methodBlk._return(jInvocation);
    }

    @Override
    public void parseResponseClass(JCodeModel cm) {
        this.responseClass = this.elementClass;
    }
}
