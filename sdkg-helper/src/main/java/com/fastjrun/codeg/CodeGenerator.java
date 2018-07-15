package com.fastjrun.codeg;

import com.fastjrun.codeg.helper.IOHelper;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.Calendar;

/**
 * 生成
 */
public abstract class CodeGenerator {
    protected static String YEAR_CODEG_TIME = "2016";

    static {
        YEAR_CODEG_TIME = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
    }

    protected final Log log = LogFactory.getLog(this.getClass());
    protected String moduleName;
    protected String author = "fastjrun";
    protected String company = "快嘉框架";
    protected String notice = "注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的";
    protected boolean skipAuthor = false;
    protected boolean skipNotice = false;
    protected String year_codeg_time = "";
    protected boolean skipCopyright = false;
    protected JCodeModel cm = new JCodeModel();
    protected JCodeModel cmTest = new JCodeModel();
    protected File srcDir;
    protected File testSrcDir;
    protected String packageNamePrefix;
    private String srcName = "/src/main/java";
    private String testSrcName = "/src/test/java";
    private String testDataName = "/src/test/data";

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

    public String getYear_codeg_time() {
        return year_codeg_time;
    }

    public void setYear_codeg_time(String year_codeg_time) {
        this.year_codeg_time = year_codeg_time;
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
        String year_codeg_time = CodeGenerator.YEAR_CODEG_TIME;
        if (this.year_codeg_time != null && !this.year_codeg_time.equals("")) {
            year_codeg_time = this.year_codeg_time;
        }
        if (!this.skipCopyright) {
            jDoc.addXdoclet("Copyright " + year_codeg_time + " " + this.company + ". All rights reserved.");
        }
        if (!this.skipAuthor) {
            jDoc.addXdoclet("author " + this.author);
        }
    }
}
