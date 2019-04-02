/*
 * Copyright (C) 2019 Fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.processer;

import com.fastjrun.codeg.common.CodeGConstants;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;

public abstract class AbstractCMProcessor implements CodeGConstants {

    protected JCodeModel cm;

    public JCodeModel getCm() {
        return cm;
    }

    public void setCm(JCodeModel cm) {
        this.cm = cm;
    }
}
