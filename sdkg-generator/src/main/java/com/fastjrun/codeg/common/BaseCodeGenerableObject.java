/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.common;

import com.fastjrun.codeg.generator.common.BaseGenerator;

public abstract class BaseCodeGenerableObject<T extends BaseGenerator> {

    T codeGenerator;

    public T getCodeGenerator() {
        return codeGenerator;
    }

    public void setCodeGenerator(T codeGenerator) {
        this.codeGenerator = codeGenerator;
    }

    public void genearte() {
        this.codeGenerator.generate();
    }
}