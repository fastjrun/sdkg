package com.fastjrun.codeg;

import java.io.File;
import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fastjrun.codeg.helper.IOHelper;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;

/**
 * 生成
 */
public abstract class CodeGenerator {
    protected static String YEAR_CODEG_TIME = "2016";

    static {
        YEAR_CODEG_TIME = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
    }

    protected String moduleName;
    protected String author = "cuiyingfeng";
    protected String company = "快嘉";
    protected String notice = "注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的";
    protected boolean skipAuthor = false;
    protected boolean skipNotice = false;
    protected String yearCodegTime = "";
    protected boolean skipCopyright = false;
    protected JCodeModel cm = new JCodeModel();
    protected JCodeModel cmTest = new JCodeModel();
    protected File srcDir;
    protected File testSrcDir;
    protected String packageNamePrefix;
    protected final Logger log = LogManager.getLogger(this.getClass());
    private String srcName = "/src/main/java";
    private String resourcesName = "/src/main/resources";
    private String testSrcName = "/src/test/java";
    private String testDataName = "/src/test/data";

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

    public String getResourcesName() {
        return resourcesName;
    }

    public void setResourcesName(String resourcesName) {
        this.resourcesName = resourcesName;
    }

    public String getSrcName() {
        return srcName;
    }

    public void setSrcName(String srcName) {
        this.srcName = srcName;
    }

    public String getTestSrcName() {
        return testSrcName;
    }

    public void setTestSrcName(String testSrcName) {
        this.testSrcName = testSrcName;
    }

    public String getTestDataName() {
        return testDataName;
    }

    public void setTestDataName(String testDataName) {
        this.testDataName = testDataName;
    }

    public String getYearCodegTime() {
        return yearCodegTime;
    }

    public void setYearCodegTime(String yearCodegTime) {
        this.yearCodegTime = yearCodegTime;
    }

    public boolean isSkipCopyright() {
        return skipCopyright;
    }

    public void setSkipCopyright(boolean skipCopyright) {
        this.skipCopyright = skipCopyright;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public boolean isSkipAuthor() {
        return skipAuthor;
    }

    public void setSkipAuthor(boolean skipAuthor) {
        this.skipAuthor = skipAuthor;
    }

    public boolean isSkipNotice() {
        return skipNotice;
    }

    public void setSkipNotice(boolean skipNotice) {
        this.skipNotice = skipNotice;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public File getSrcDir() {
        return srcDir;
    }

    public void setSrcDir(File srcDir) {
        this.srcDir = srcDir;
    }

    public File getTestSrcDir() {
        return testSrcDir;
    }

    public void setTestSrcDir(File testSrcDir) {
        this.testSrcDir = testSrcDir;
    }

    public String getPackageNamePrefix() {
        return packageNamePrefix;
    }

    public void setPackageNamePrefix(String packageNamePrefix) {
        this.packageNamePrefix = packageNamePrefix;
    }

    public void beforeGenerate() {
        File srcDir = new File(moduleName + this.srcName);
        IOHelper.deleteDir(srcDir.getPath());
        srcDir.mkdirs();
        this.setSrcDir(srcDir);
        File testSrcDir = new File(moduleName + this.testSrcName);
        IOHelper.deleteDir(testSrcDir.getPath());
        File testDataDir = new File(moduleName + this.testDataName);
        IOHelper.deleteDir(testDataDir.getPath());
        testSrcDir.mkdirs();
        this.setTestSrcDir(testSrcDir);
    }

    public abstract boolean generate();

    protected void addClassDeclaration(JDefinedClass jClass) {
        if (this.skipNotice && this.skipCopyright && this.skipNotice) {
            return;
        }
        JDocComment jDoc = jClass.javadoc();
        if (!this.skipNotice) {
            jDoc.append(this.notice);
        }
        String tempYearCodegTime = CodeGenerator.YEAR_CODEG_TIME;
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
}
