package com.fastjrun.codeg;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fastjrun.codeg.helper.IOHelper;
import com.sun.codemodel.JCodeModel;

/**
 * 生成
 */
public abstract class CodeGenerator {
    protected final Log log = LogFactory.getLog(this.getClass());
	public static final String SRCNAME="/src/main/java";
    public static final String TESTSRCNAME="/src/test/java";
    public static final String TESTDATANAME="/src/test/data";
    protected String moduleName;

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }	
	protected JCodeModel cm = new JCodeModel();
    protected JCodeModel cmTest = new JCodeModel();
	protected File srcDir;
    protected File testSrcDir;
	protected String packageNamePrefix;

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
	
	public void beforeGenerate(){
        File srcDir = new File(moduleName + CodeGenerator.SRCNAME);
        IOHelper.deleteDir(srcDir.getPath());
        srcDir.mkdirs();
        this.setSrcDir(srcDir);
        File testSrcDir = new File(moduleName + CodeGenerator.TESTSRCNAME);
        IOHelper.deleteDir(testSrcDir.getPath());
        File testDataDir = new File(moduleName + CodeGenerator.TESTDATANAME);
        IOHelper.deleteDir(testDataDir.getPath());
        testSrcDir.mkdirs();
        this.setTestSrcDir(testSrcDir);
	}

	public abstract boolean generate();
}
