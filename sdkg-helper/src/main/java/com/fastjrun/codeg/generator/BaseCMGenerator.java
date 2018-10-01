package com.fastjrun.codeg.generator;

import com.fastjrun.codeg.common.CodeModelConstants;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;

/**
 * 生成
 */
public abstract class BaseCMGenerator extends BaseGenerator implements Cloneable, CodeModelConstants {

    protected MockModel mockModel = MockModel.MockModel_Common;

    public MockModel getMockModel() {
        return mockModel;
    }

    public void setMockModel(MockModel mockModel) {
        this.mockModel = mockModel;
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

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}

