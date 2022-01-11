/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.service.impl;

import com.fastjrun.codeg.common.*;
import com.fastjrun.codeg.generator.PacketGenerator;
import com.fastjrun.codeg.generator.ServiceGenerator;
import com.fastjrun.codeg.generator.common.BaseControllerGenerator;
import com.fastjrun.codeg.helper.CodeGeneratorFactory;
import com.fastjrun.codeg.helper.IOHelper;
import com.fastjrun.codeg.util.BundleXMLParser;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.writer.AbstractCodeWriter;
import com.helger.jcodemodel.writer.FileCodeWriter;
import com.helger.jcodemodel.writer.JCMWriter;
import org.apache.commons.lang.StringEscapeUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class BaseCodeGServiceImpl implements CodeGConstants {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected String packageNamePrefix;
    protected String author;
    protected String company;
    protected String mockHelperClassName="com.fastjrun.example.service.helper.MockHelper";

    protected File srcDir;
    protected File testSrcDir;
    private String srcName = "/src/main/java";
    private String resourcesName = "/src/main/resources";
    private String testSrcName = "/src/test/java";
    private String testResourcesName = "/src/test/resources";
    private String testDataName = "/src/test/data";

    public String getMockHelperClassName() {
        return mockHelperClassName;
    }

    public void setMockHelperClassName(String mockHelperClassName) {
        this.mockHelperClassName = mockHelperClassName;
    }

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
                log.error("XMLUtil.close error: " + e);
            }
        }

        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                log.error("XMLUtil.close error: " + e);
            }
        }
    }

    void saveTestParams(String moduleName, Map<String, Properties> testParamMap) {
        if (testParamMap != null && testParamMap.size() > 0) {
            testParamMap.keySet().stream()
                    .parallel()
                    .forEach(
                            key -> {
                                Properties testParams = testParamMap.get(key);
                                File outFile =
                                        new File(
                                                moduleName + this.getTestDataName() + File.separator + key + ".properties");
                                outFile.getParentFile().mkdirs();
                                if (outFile.exists()) {
                                    outFile.delete();
                                }
                                try {
                                    FileWriter resFw = new FileWriter(outFile);
                                    for (String pKey : testParams.stringPropertyNames()) {
                                        resFw.write(
                                                pKey.concat("=")
                                                        .concat(StringEscapeUtils.unescapeJava(testParams.getProperty(pKey)))
                                                        .concat(System.lineSeparator()));
                                    }
                                    resFw.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
        }
    }

    protected Document generateTestngXml(
            Map<String, CommonController> controllerMap,
            int classThreadCount,
            int dataProviderThreadCount,
            int methodThreadCount) {
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
            classNode.addAttribute(
                    "name",
                    this.packageNamePrefix
                            + "client."
                            + clientName
                            + commonController.getControllerType().clientSuffix
                            + "Test");

            classesNode.add(classNode);
        }
        testNode.add(classesNode);
        return document;
    }

    protected Map<String, CommonController> generateApiCode(String bundleFiles, String moduleName) {
        return this.generateCode(
                bundleFiles, moduleName, MockModel.MockModel_Common, true, false);
    }

    protected Map<String, CommonController> generateClientCode(
            String bundleFiles, String moduleName) {
        return this.generateCode(
                bundleFiles, moduleName, MockModel.MockModel_Common, false, true);
    }

    protected Map<String, CommonController> generateBundleCode(
            String bundleFiles, String moduleName, MockModel mockModel) {
        return this.generateCode(bundleFiles, moduleName, mockModel, false, false);
    }

    private Map<String, CommonController> generateCode(
            String bundleFiles,
            String moduleName,
            MockModel mockModel,
            boolean isApi,
            boolean isClient) {

        JCodeModel cm = new JCodeModel();
        JCodeModel cmTest = new JCodeModel();

        Map<String, PacketObject> packetAllMap = new HashMap<>();
        Map<String, CommonService> serviceAllMap = new HashMap<>();
        Map<String, CommonController> controllerAllMap = new HashMap<>();

        Map<String, Properties> clientTestParamMap = new HashMap<>();

        BundleXMLParser bundleXMLParser = new BundleXMLParser();
        bundleXMLParser.init();
        bundleXMLParser.setBundleFiles(bundleFiles.split(","));
        bundleXMLParser.doParse();
        packetAllMap.putAll(bundleXMLParser.getPacketMap());
        serviceAllMap.putAll(bundleXMLParser.getServiceMap());
        controllerAllMap.putAll(bundleXMLParser.getControllerMap());

        for (PacketObject packetObject : packetAllMap.values()) {
            PacketGenerator packetGenerator =
                    CodeGeneratorFactory.createPacketGenerator(
                            this.packageNamePrefix, mockModel, this.author, this.company);
            packetGenerator.setCm(cm);
            packetGenerator.setCmTest(cmTest);
            packetGenerator.setPacketObject(packetObject);
            packetGenerator.generate();
        }

        Map<CommonService, ServiceGenerator> serviceGeneratorMap = new HashMap<>();
        for (CommonService commonService : serviceAllMap.values()) {
            ServiceGenerator serviceGenerator =
                    CodeGeneratorFactory.createServiceGenerator(
                            this.packageNamePrefix, mockModel, this.author, this.company);
            serviceGenerator.setCommonService(commonService);
            serviceGenerator.setApi(isApi);
            serviceGenerator.setClient(isClient);
            serviceGenerator.setCm(cm);
            serviceGenerator.setCmTest(cmTest);
            serviceGenerator.generate();
            serviceGeneratorMap.put(commonService, serviceGenerator);
        }

        for (CommonController commonController : controllerAllMap.values()) {
            BaseControllerGenerator baseControllerGenerator =
                    CodeGeneratorFactory.createBaseControllerGenerator(
                            this.packageNamePrefix, mockModel, this.author, this.company, commonController);
            baseControllerGenerator.setApi(isApi);
            baseControllerGenerator.setClient(isClient);
            CommonService commonService = commonController.getService();
            baseControllerGenerator.setServiceGenerator(serviceGeneratorMap.get(commonService));
            baseControllerGenerator.setCm(cm);
            baseControllerGenerator.setCmTest(cmTest);
            baseControllerGenerator.generate();
        }

        try {
            // 生成代码为UTF-8编码
            AbstractCodeWriter src = new FileCodeWriter(this.srcDir, Charset.forName("UTF-8"));
            // 自上而下地生成类、方法等
            new JCMWriter(cm).build(src);

        } catch (IOException e) {
            log.error("", e);
            throw new CodeGException(CodeGMsgContants.CODEG_CODEG_FAIL, "code generating failed", e);
        }

        this.saveTestParams(moduleName, clientTestParamMap);

        return controllerAllMap;
    }
}
