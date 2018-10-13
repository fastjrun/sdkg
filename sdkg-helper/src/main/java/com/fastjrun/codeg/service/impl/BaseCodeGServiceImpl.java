package com.fastjrun.codeg.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.fastjrun.codeg.common.CommonController;
import com.fastjrun.codeg.common.CommonLog;
import com.fastjrun.codeg.helper.IOHelper;
import com.fastjrun.codeg.service.CodeGService;
import com.fastjrun.helper.StringHelper;

public abstract class BaseCodeGServiceImpl implements CodeGService {

    protected CommonLog commonLog = new CommonLog();

    protected String packageNamePrefix;
    protected String[] bundleFiles;
    protected String author;
    protected String company;

    protected File srcDir;
    protected File testSrcDir;
    private String srcName = "/src/main/java";
    private String resourcesName = "/src/main/resources";
    private String testSrcName = "/src/test/java";
    private String testResourcesName = "/src/test/resources";
    private String testDataName = "/src/test/data";

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getTestResourcesName() {
        return testResourcesName;
    }

    public void setTestResourcesName(String testResourcesName) {
        this.testResourcesName = testResourcesName;
    }

    public String[] getBundleFiles() {
        return bundleFiles;
    }

    public void setBundleFiles(String[] bundleFiles) {
        this.bundleFiles = bundleFiles;
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

    protected void beforeGenerate(String moduleName) {
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

    protected void saveDocument(File file, Document document) {
        OutputFormat outputFormat = OutputFormat.createPrettyPrint();
        outputFormat.setEncoding("UTF-8");
        outputFormat.setIndent("    ");
        OutputStream outputStream = null;

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        XMLWriter xmlWriter = null;
        try {
            xmlWriter = new XMLWriter(outputStream, outputFormat);
            xmlWriter.write(document);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (xmlWriter != null) {
            try {
                xmlWriter.close();
            } catch (IOException e) {
                this.commonLog.getLog().error("XMLUtil.close error: " + e);
            }
            xmlWriter = null;
        }

        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                this.commonLog.getLog().error("XMLUtil.close error: " + e);
            }
            outputStream = null;
        }
    }

    void saveTestParams(String moduleName, Map<String, Properties> clientTestParamMap) {
        for (String key : clientTestParamMap.keySet()) {
            Properties testParams = clientTestParamMap.get(key);
            File outFile = new File(moduleName + this.getTestDataName() + File.separator
                    + key + ".properties");
            outFile.getParentFile().mkdirs();
            if (outFile.exists()) {
                outFile.delete();
            }
            try {
                FileWriter resFw = new FileWriter(outFile);
                testParams.store(resFw, "ok");
                resFw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    protected Document generateTestngXml(Map<String, CommonController> controllerMap, int classThreadCount, int
            dataProviderThreadCount, int methodThreadCount) {
        Document document = DocumentHelper.createDocument();
        document.addDocType("suite", "SYSTEM", "http://testng.org/testng-1.0.dtd");
        Element rootNode = DocumentHelper.createElement("suite");
        rootNode.addAttribute("name", "clientTest");
        rootNode.addAttribute("parallel", "classes");
        rootNode.addAttribute("thread-count", String.valueOf(classThreadCount));
        rootNode.addAttribute("data-provider-thread-count", String.valueOf(dataProviderThreadCount));
        document.add(rootNode);
        Element testNode = DocumentHelper.createElement("test");
        testNode.addAttribute("name", "${envName}");
        testNode.addAttribute("parallel", "methods");
        testNode.addAttribute("thread-count", String.valueOf(methodThreadCount));
        rootNode.add(testNode);
        Element paramNode = DocumentHelper.createElement("parameter");
        paramNode.addAttribute("name", "envName");
        paramNode.addAttribute("value", "${envName}");
        testNode.add(paramNode);
        Element classesNode = DocumentHelper.createElement("classes");
        for (String key : controllerMap.keySet()) {
            CommonController commonController = controllerMap.get(key);
            Element classNode = DocumentHelper.createElement("class");

            String clientName = commonController.getClientName();
            int lastDotIndex = clientName.lastIndexOf(".");
            if (lastDotIndex > 0) {
                clientName = clientName.substring(lastDotIndex + 1, clientName.length());
            }
            classNode.addAttribute("name",
                    this.packageNamePrefix + "client." + clientName + commonController.getControllerType().clientSuffix
                            + "Test");

            classesNode.add(classNode);

        }
        testNode.add(classesNode);
        return document;
    }

    protected Document generateDubboClientXml(List<CommonController> rpcDubboList) {
        Document document = DocumentHelper.createDocument();
        Element rootNode = this.generateDubboRoot();
        document.add(rootNode);
        if (rpcDubboList != null && rpcDubboList.size() > 0) {
            for (CommonController rpcDubbo : rpcDubboList) {
                String clientName = rpcDubbo.getClientName();
                int lastDotIndex = clientName.lastIndexOf(".");
                if (lastDotIndex > 0) {
                    clientName = clientName.substring(lastDotIndex + 1, clientName.length());
                }
                Element referenceNode = DocumentHelper.createElement("dubbo:reference");
                referenceNode.addAttribute("id", StringHelper.toLowerCaseFirstOne(clientName));

                if (rpcDubbo.is_new()) {
                    referenceNode
                            .addAttribute("interface",
                                    this.packageNamePrefix + "api." + rpcDubbo.getClientName());
                } else {
                    referenceNode
                            .addAttribute("interface", rpcDubbo.getClientName());
                }

                rootNode.add(referenceNode);

            }
        }
        return document;
    }

    protected Document generateDubboServerXml(List<CommonController> rpcDubboList) {

        Document document = DocumentHelper.createDocument();
        Element rootNode = this.generateDubboRoot();
        document.add(rootNode);
        for (CommonController rpcDubbo : rpcDubboList) {
            String clientName = rpcDubbo.getClientName();
            int lastDotIndex = clientName.lastIndexOf(".");
            if (lastDotIndex > 0) {
                clientName = clientName.substring(lastDotIndex + 1, clientName.length());
            }
            Element serviceNode = DocumentHelper.createElement("dubbo:service");
            serviceNode.addAttribute("ref", StringHelper.toLowerCaseFirstOne(clientName));
            if (rpcDubbo.is_new()) {
                serviceNode
                        .addAttribute("interface",
                                this.packageNamePrefix + "api." + rpcDubbo.getClientName());
            } else {
                serviceNode
                        .addAttribute("interface", rpcDubbo.getClientName());
            }
            rootNode.add(serviceNode);

        }
        Element contextComponentScan = DocumentHelper.createElement("context:component-scan");
        contextComponentScan.addAttribute("base-package", this.packageNamePrefix + "biz");

        rootNode.add(contextComponentScan);

        return document;

    }

    private Element generateDubboRoot() {
        Element rootNode = DocumentHelper.createElement("beans");
        rootNode.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        rootNode.addNamespace("context", "http://www.springframework.org/schema/context");
        rootNode.addNamespace("dubbo", "http://code.alibabatech.com/schema/dubbo");
        rootNode.addNamespace("", "http://www.springframework.org/schema/beans");
        rootNode.addAttribute("xsi:schemaLocation",
                "http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans"
                        + ".xsd http://www.springframework.org/schema/context http://www.springframework"
                        + ".org/schema/context/spring-context.xsd http://code.alibabatech.com/schema/dubbo "
                        + "http://code.alibabatech.com/schema/dubbo/dubbo.xsd");
        return rootNode;

    }

}
