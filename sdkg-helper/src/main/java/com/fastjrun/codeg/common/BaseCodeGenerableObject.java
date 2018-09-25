package com.fastjrun.codeg.common;

import com.fastjrun.codeg.generator.BaseGenerator;

public class BaseCodeGenerableObject implements CodeGenerable {

    BaseGenerator codeGenerator;

    public BaseGenerator getCodeGenerator() {
        return codeGenerator;
    }

    public void setCodeGenerator(BaseGenerator codeGenerator) {
        this.codeGenerator = codeGenerator;
    }

    @Override
    public boolean genearte() {
        return this.codeGenerator.generate(this);
    }
}