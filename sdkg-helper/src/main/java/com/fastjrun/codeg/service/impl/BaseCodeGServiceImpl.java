
package com.fastjrun.codeg.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.fastjrun.codeg.common.CommonController;
import com.fastjrun.codeg.common.CommonLog;
import com.fastjrun.codeg.common.CommonService;
import com.fastjrun.codeg.common.PacketObject;
import com.fastjrun.codeg.generator.BaseHTTPGenerator;
import com.fastjrun.codeg.generator.BaseRPCGenerator;
import com.fastjrun.codeg.helper.BundleXMLParser;
import com.fastjrun.codeg.helper.CodeGeneratorFactory;
import com.fastjrun.codeg.helper.IOHelper;
import com.fastjrun.codeg.service.CodeGService;

public abstract class BaseCodeGServiceImpl implements CodeGService {

    protected CommonLog commonLog = new CommonLog();

    protected String packageNamePrefix;
    protected String[] bundleFiles;

    protected File srcDir;
    protected File testSrcDir;
    private String srcName = "/src/main/java";
    private String resourcesName = "/src/main/resources";
    private String testSrcName = "/src/test/java";
    private String testResourcesName = "/src/test/resources";
    private String testDataName = "/src/test/data";

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

    protected void saveRPCDocument(String moduleName, RpcType rpcType, boolean isOnlyApi, Document document) {
        String fileName = "applicationContext-dubbo-provider.xml";
        if (isOnlyApi) {
            if (rpcType == RpcType.RpcType_Dubbo) {
                fileName = "applicationContext-dubbo-consumer.xml";
            }
        } else {
            if (rpcType == RpcType.RpcType_Dubbo) {
                fileName = "applicationContext-dubbo-provider.xml";
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

    protected BaseRPCGenerator createRPCGenerator(RpcType rpcType, MockModel
            mockModel) {
        Map<String, PacketObject> packetAllMap = new HashMap<>();
        Map<String, CommonService> serviceAllMap = new HashMap<>();
        Map<String, CommonController> rpcAllMap = new HashMap<>();
        if (this.bundleFiles != null && this.bundleFiles.length > 0) {
            for (String bundleFile : bundleFiles) {
                Map<String, PacketObject> packetMap = BundleXMLParser.processPacket(bundleFile);
                packetAllMap.putAll(packetMap);
                Map<String, CommonService> serviceMap = BundleXMLParser.processService(bundleFile, packetMap);
                serviceAllMap.putAll(serviceMap);
                Map<String, CommonController> rpcMap =
                        BundleXMLParser.processPPCs(rpcType, bundleFile, serviceMap);
                rpcAllMap.putAll(rpcMap);
            }
        }
        BaseRPCGenerator rpcGenerator = CodeGeneratorFactory.createRPCGenerator(rpcType);
        rpcGenerator.setPackageNamePrefix(this.packageNamePrefix);
        rpcGenerator.setPacketMap(packetAllMap);
        rpcGenerator.setServiceMap(serviceAllMap);
        rpcGenerator.setControllerMap(rpcAllMap);
        rpcGenerator.setMockModel(mockModel);
        return rpcGenerator;
    }

    protected BaseHTTPGenerator createHTTPGenerator(ControllerType controllerType, MockModel mockModel) {
        Map<String, PacketObject> packetAllMap = new HashMap<>();
        Map<String, CommonService> serviceAllMap = new HashMap<>();
        Map<String, CommonController> controllerAllMap = new HashMap<>();
        if (this.bundleFiles != null && this.bundleFiles.length > 0) {
            for (String bundleFile : bundleFiles) {
                Map<String, PacketObject> packetMap = BundleXMLParser.processPacket(bundleFile);
                packetAllMap.putAll(packetMap);
                Map<String, CommonService> serviceMap = BundleXMLParser.processService(bundleFile, packetMap);
                serviceAllMap.putAll(serviceMap);
                Map<String, CommonController> controllerMap =
                        BundleXMLParser.processControllers(controllerType, bundleFile, serviceMap);
                controllerAllMap.putAll(controllerMap);
            }
        }
        BaseHTTPGenerator httpGenerator = CodeGeneratorFactory.createHTTPGenerator(controllerType);
        httpGenerator.setPackageNamePrefix(this.packageNamePrefix);
        httpGenerator.setPacketMap(packetAllMap);
        httpGenerator.setServiceMap(serviceAllMap);
        httpGenerator.setControllerMap(controllerAllMap);
        httpGenerator.setMockModel(mockModel);
        return httpGenerator;
    }
}
