package com.fastjrun.codeg.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.fastjrun.codeg.common.CodeGConstants;
import com.fastjrun.codeg.common.CodeGException;
import com.fastjrun.codeg.common.CodeGMsgContants;
import com.fastjrun.codeg.common.CommonController;
import com.fastjrun.codeg.common.CommonMethod;
import com.fastjrun.codeg.common.CommonService;
import com.fastjrun.codeg.common.DataBaseObject;
import com.fastjrun.codeg.common.FJTable;
import com.fastjrun.codeg.common.PacketObject;
import com.fastjrun.codeg.generator.MybatisAFGenerator;
import com.fastjrun.codeg.generator.PacketGenerator;
import com.fastjrun.codeg.generator.ServiceGenerator;
import com.fastjrun.codeg.generator.common.BaseControllerGenerator;
import com.fastjrun.codeg.generator.method.BaseControllerMethodGenerator;
import com.fastjrun.codeg.generator.method.ServiceMethodGenerator;
import com.fastjrun.codeg.helper.CodeGeneratorFactory;
import com.fastjrun.codeg.helper.IOHelper;
import com.fastjrun.codeg.service.CodeGService;
import com.fastjrun.codeg.util.BundleXMLParser;
import com.fastjrun.codeg.utils.SQLSchemaParse;
import com.fastjrun.helper.StringHelper;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.writer.AbstractCodeWriter;
import com.helger.jcodemodel.writer.FileCodeWriter;
import com.helger.jcodemodel.writer.JCMWriter;

public abstract class BaseCodeGServiceImpl implements CodeGService, CodeGConstants {

    static String TESTNG_XML_FILENAME = "testng.xml";

    static String DUBBO_PRPVIDER_FILENAME = "applicationContext-dubbo-provider.xml";

    static String DUBBO_CONSUME_FILENAME = "applicationContext-dubbo-consumer.xml";

    protected final Logger log = LogManager.getLogger(this.getClass());

    protected String packageNamePrefix;
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
        IOHelper.deleteDir(testDataDir.getPath());// deleta File
        IOHelper.deleteDir(new File(moduleName + this.testSrcName + "/" + TESTNG_XML_FILENAME).getPath());
        IOHelper.deleteDir(new File(moduleName + this.resourcesName + "/" + DUBBO_PRPVIDER_FILENAME).getPath());
        IOHelper.deleteDir(new File(moduleName + this.resourcesName + "/" + DUBBO_CONSUME_FILENAME).getPath());
        testSrcDir.mkdirs();
        this.setTestSrcDir(testSrcDir);
    }

    protected void saveRPCDocument(String moduleName, ControllerProtocol controllerProtocol, boolean isOnlyApi,
                                   Document document) {
        String fileName = DUBBO_PRPVIDER_FILENAME;
        if (isOnlyApi) {
            if (controllerProtocol == ControllerProtocol.ControllerProtocol_DUBBO) {
                fileName = DUBBO_CONSUME_FILENAME;
            }
        }

        File rpcfile = new File(moduleName + this.getResourcesName() + File
                .separator + fileName);
        this.saveDocument(rpcfile, document);
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
            testParamMap.keySet().stream().parallel().forEach(key -> {
                Properties testParams = testParamMap.get(key);
                File outFile = new File(moduleName + this.getTestDataName() + File.separator
                        + key + ".properties");
                outFile.getParentFile().mkdirs();
                if (outFile.exists()) {
                    outFile.delete();
                }
                try {
                    FileWriter resFw = new FileWriter(outFile);
                    for (String pKey : testParams.stringPropertyNames()) {
                        resFw.write(pKey.concat("=").concat(StringEscapeUtils.unescapeJavaScript(testParams
                                .getProperty(pKey))).concat
                                (System.lineSeparator()));
                    }
                    resFw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
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

    protected Map<String, CommonController> generateCode(String bundleFiles, String moduleName, MockModel mockModel,
                                                         boolean isApi,
                                                         boolean isClient) {

        JCodeModel cm = new JCodeModel();
        JCodeModel cmTest = new JCodeModel();

        Map<String, PacketObject> packetAllMap = new HashMap<>();
        Map<String, CommonService> serviceAllMap = new HashMap<>();
        Map<String, CommonController> controllerAllMap = new HashMap<>();

        Map<String, Properties> clientTestParamMap = new HashMap<>();

        String[] bundleFileArray = bundleFiles.split(",");
        for (String bundleFile : bundleFileArray) {
            BundleXMLParser bundleXMLParser = new BundleXMLParser();
            bundleXMLParser.init();
            bundleXMLParser.setBundleFile(bundleFile);
            bundleXMLParser.doParse();
            packetAllMap.putAll(bundleXMLParser.getPacketMap());
            serviceAllMap.putAll(bundleXMLParser.getServiceMap());
            controllerAllMap.putAll(bundleXMLParser.getControllerMap());
        }

        for (PacketObject packetObject : packetAllMap.values()) {
            PacketGenerator packetGenerator = CodeGeneratorFactory
                    .createPacketGenerator(this.packageNamePrefix, mockModel, this.author, this.company);
            packetGenerator.setCm(cm);
            packetGenerator.setCmTest(cmTest);
            packetGenerator.setPacketObject(packetObject);
            packetGenerator.generate();
        }

        Map<CommonService, ServiceGenerator> serviceGeneratorMap = new HashMap<>();

        for (CommonService commonService : serviceAllMap.values()) {
            ServiceGenerator serviceGenerator = CodeGeneratorFactory
                    .createServiceGenerator(this.packageNamePrefix, mockModel, this.author, this.company);
            serviceGenerator.setCommonService(commonService);
            serviceGenerator.setApi(isApi);
            serviceGenerator.setClient(isClient);
            serviceGenerator.setCm(cm);
            serviceGenerator.setCmTest(cmTest);
            serviceGenerator.generate();
            serviceGeneratorMap.put(commonService, serviceGenerator);

        }

        Map<CommonController, BaseControllerGenerator> baseControllerGeneratorMap = new HashMap<>();

        for (CommonController commonController : controllerAllMap.values()) {
            BaseControllerGenerator baseControllerGenerator = CodeGeneratorFactory
                    .createBaseControllerGenerator(this.packageNamePrefix, mockModel, this.author, this
                            .company, commonController);
            baseControllerGenerator.setApi(isApi);
            baseControllerGenerator.setClient(isClient);
            CommonService commonService = commonController.getService();
            baseControllerGenerator.setServiceGenerator(serviceGeneratorMap.get(commonService));
            baseControllerGenerator.setCm(cm);
            baseControllerGenerator.setCmTest(cmTest);
            baseControllerGenerator.generate();
            baseControllerGeneratorMap.put(commonController, baseControllerGenerator);
        }

        for (CommonService commonService : serviceGeneratorMap.keySet()) {
            ServiceGenerator serviceGenerator = serviceGeneratorMap.get(commonService);
            List<CommonMethod> commonMethods = commonService.getMethods();
            List<CommonController> commonControllers = commonService.getCommonControllers();

            for (CommonMethod commonMethod : commonMethods) {
                ServiceMethodGenerator serviceMethodGenerator = new ServiceMethodGenerator();
                serviceMethodGenerator.setPackageNamePrefix(packageNamePrefix);
                serviceMethodGenerator.setMockModel(mockModel);
                serviceMethodGenerator.setAuthor(author);
                serviceMethodGenerator.setCompany(company);
                serviceMethodGenerator.setApi(isApi);
                serviceMethodGenerator.setClient(isClient);
                serviceMethodGenerator.setServiceGenerator(serviceGenerator);
                serviceMethodGenerator.setCommonMethod(commonMethod);
                serviceMethodGenerator.setCm(cm);
                serviceMethodGenerator.setCmTest(cmTest);
                serviceMethodGenerator.generate();
                for (CommonController commonController : commonControllers) {
                    BaseControllerGenerator baseControllerGenerator =
                            baseControllerGeneratorMap.get(commonController);
                    BaseControllerMethodGenerator baseControllerMethodGenerator = baseControllerGenerator
                            .prepareBaseControllerMethodGenerator(serviceMethodGenerator);
                    baseControllerMethodGenerator.setApi(isApi);
                    baseControllerMethodGenerator.setClient(isClient);
                    baseControllerMethodGenerator.setCm(cm);
                    baseControllerMethodGenerator.setCmTest(cmTest);
                    baseControllerMethodGenerator.generate();
                    if (!isApi && isClient) {
                        clientTestParamMap.put(baseControllerGenerator.getClientName() + "Test",
                                baseControllerGenerator
                                        .getClientTestParam());
                    }

                }
            }

        }
        try {
            // 生成代码为UTF-8编码
            AbstractCodeWriter src = new FileCodeWriter(this.srcDir, Charset.forName("UTF-8"));
            // 自上而下地生成类、方法等
            new JCMWriter(cm).build(src);
            if (isClient) {
                AbstractCodeWriter srcTest = new FileCodeWriter(this.testSrcDir, Charset.forName("UTF-8"));
                new JCMWriter(cmTest).build(srcTest);
            }

        } catch (IOException e) {
            log.error("", e);
            throw new CodeGException(CodeGMsgContants.CODEG_CODEG_FAIL, "code generating failed", e);
        }

        this.saveTestParams(moduleName, clientTestParamMap);

        return controllerAllMap;
    }

    protected boolean generateMybatisAnnotationCode(String sqlFile, boolean supportController, boolean supportTest,
                                                    String moduleName) {

        JCodeModel cm = new JCodeModel();
        JCodeModel cmTest = new JCodeModel();
        Map<String, Properties> daoTestParamMap = new HashMap<>();
        DataBaseObject dataBaseObject =
                SQLSchemaParse.process(SQLSchemaParse.TargetType.TargetType_Mysql, sqlFile);

        Map<String, FJTable> fjTableMap = dataBaseObject.getTableMap();

        //        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(100);
        //        ExecutorService threadPool = new ThreadPoolExecutor(50, 150,
        //                0L, TimeUnit.MILLISECONDS,
        //                queue);
        //        CompletionService<Boolean> completionService = new ExecutorCompletionService<>(threadPool);
        //        for (String key : fjTableMap.keySet()) {
        //            FJTable fjTable = fjTableMap.get(key);
        //            Callable<Boolean> callable = () -> {
        //                MybatisAFGenerator mybatisAFGenerator =
        //                        CodeGeneratorFactory
        //                                .createBaseMybatisAFGenerator(this.packageNamePrefix, this.author, this
        // .company
        //                                        , fjTable);
        //                mybatisAFGenerator.setCm(cm);
        //                mybatisAFGenerator.setCmTest(cmTest);
        //                mybatisAFGenerator.setSupportController(supportController);
        //                mybatisAFGenerator.setSupportTest(supportTest);
        //                mybatisAFGenerator.generate();
        //                if (supportTest) {
        //                    daoTestParamMap.put(key, mybatisAFGenerator.getDaoTestParam());
        //                }
        //                return true;
        //            };
        //            completionService.submit(callable);
        //        }
        //
        //
        //        for (int i = 0; i < fjTableMap.keySet().size(); i++) {
        //            try {
        //                completionService.take().get();
        //            } catch (InterruptedException | ExecutionException e) {
        //                log.error("" + e);
        //            }
        //        }
        //
        //        threadPool.shutdown();

        //                fjTableMap.keySet().stream().parallel().forEach(key -> {
        //                    FJTable fjTable = fjTableMap.get(key);
        //                    MybatisAFGenerator mybatisAFGenerator =
        //                            CodeGeneratorFactory
        //                                    .createBaseMybatisAFGenerator(this.packageNamePrefix, this.author, this
        // .company
        //                                            , fjTable);
        //                    mybatisAFGenerator.setCm(cm);
        //                    mybatisAFGenerator.setCmTest(cmTest);
        //                    mybatisAFGenerator.setSupportController(supportController);
        //                    mybatisAFGenerator.setSupportTest(supportTest);
        //                    mybatisAFGenerator.generate();
        //                    if (supportTest) {
        //                        daoTestParamMap.put(key, mybatisAFGenerator.getDaoTestParam());
        //                    }
        //                });

        for (String key : fjTableMap.keySet()) {
            FJTable fjTable = fjTableMap.get(key);
            MybatisAFGenerator mybatisAFGenerator =
                    CodeGeneratorFactory
                            .createBaseMybatisAFGenerator(this.packageNamePrefix, this.author, this.company
                                    , fjTable);
            mybatisAFGenerator.setCm(cm);
            mybatisAFGenerator.setCmTest(cmTest);
            mybatisAFGenerator.setSupportController(supportController);
            mybatisAFGenerator.setSupportTest(supportTest);
            mybatisAFGenerator.generate();
            if (supportTest) {
                daoTestParamMap.put(key, mybatisAFGenerator.getDaoTestParam());
            }
        }

        try {
            // 生成代码为UTF-8编码
            AbstractCodeWriter src = new FileCodeWriter(this.srcDir, Charset.forName("UTF-8"));
            // 自上而下地生成类、方法等
            new JCMWriter(cm).build(src);

            if (supportTest) {
                AbstractCodeWriter srcTest = new FileCodeWriter(this.testSrcDir, Charset.forName("UTF-8"));
                new JCMWriter(cmTest).build(srcTest);
            }

        } catch (IOException e) {
            log.error("", e);
            throw new CodeGException(CodeGMsgContants.CODEG_CODEG_FAIL, "code generating failed", e);
        }

        this.saveTestParams(moduleName, daoTestParamMap);

        return true;
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
