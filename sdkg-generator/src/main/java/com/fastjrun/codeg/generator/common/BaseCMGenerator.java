/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.generator.common;

import com.fastjrun.codeg.common.CodeGConstants;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JDocComment;

/**
 * 生成
 */
public abstract class BaseCMGenerator extends BaseGenerator implements Cloneable, CodeGConstants {

    public static String SERVICE_PACKAGE_NAME = "service.";
    public static String MOCK_PACKAGE_NAME = "com.fastjrun.mock.";
    public static String JSONOBJECTCLASS_NAME = "com.fasterxml.jackson.databind.JsonNode";
    protected JCodeModel cm;
    protected JCodeModel cmTest;
    protected MockModel mockModel = MockModel.MockModel_Common;
    private boolean api = false;

    public MockModel getMockModel() {
        return mockModel;
    }

    public void setMockModel(MockModel mockModel) {
        this.mockModel = mockModel;
    }

    public boolean isApi() {
        return api;
    }

    public void setApi(boolean api) {
        this.api = api;
    }

    protected void addClassDeclaration(JDefinedClass jClass) {
        if (this.skipNotice && this.skipCopyright) {
            return;
        }
        JDocComment jDoc = jClass.javadoc();
        if (!this.skipNotice) {
            jDoc.append(this.notice);
        }
        String tempYearCodegTime = YEAR_CODEG_TIME;
        if (this.yearCodegTime != null && !this.yearCodegTime.equals("")) {
            tempYearCodegTime = this.yearCodegTime;
        }
        if (!this.skipCopyright) {
            jDoc.addXdoclet("Copyright " + tempYearCodegTime + " " + this.company + ". All rights reserved.");
        }
        if (!this.skipAuthor) {
            jDoc.addXdoclet("author " + this.author);
        }
    }

    public JCodeModel getCm() {
        return cm;
    }

    public void setCm(JCodeModel cm) {
        this.cm = cm;
    }

    public JCodeModel getCmTest() {
        return cmTest;
    }

    public void setCmTest(JCodeModel cmTest) {
        this.cmTest = cmTest;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}

