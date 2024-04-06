/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.service.impl;

import com.fastjrun.codeg.common.*;
import com.fastjrun.codeg.generator.BaseServiceGenerator;
import com.fastjrun.codeg.generator.PacketGenerator;
import com.fastjrun.codeg.generator.common.BaseControllerGenerator;
import com.fastjrun.codeg.helper.CodeGeneratorFactory;
import com.fastjrun.codeg.helper.IOHelper;
import com.fastjrun.codeg.util.BundleXMLParser;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.writer.AbstractCodeWriter;
import com.helger.jcodemodel.writer.FileCodeWriter;
import com.helger.jcodemodel.writer.JCMWriter;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public abstract class BaseCodeGServiceImpl implements CodeGConstants {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected String packageNamePrefix;
    protected String author;
    protected String company;

    protected File srcDir;
    private String srcName = "/src/main/java";
    private String resourcesName = "/src/main/resources";

    protected void beforeGenerate(String moduleName) {
        File srcDir = new File(moduleName + this.srcName);
        IOHelper.deleteDir(srcDir.getPath());
        srcDir.mkdirs();
        this.setSrcDir(srcDir);
    }

    protected Map<String, CommonController> generateApiCode(String bundleFiles) {
        return this.generateCode(
                bundleFiles, MockModel.MockModel_Common, true, false);
    }

    protected Map<String, CommonController> generateClientCode(
            String bundleFiles) {
        return this.generateCode(
                bundleFiles, MockModel.MockModel_Common, false, true);
    }

    protected Map<String, CommonController> generateBundleCode(
            String bundleFiles, MockModel mockModel) {
        return this.generateCode(bundleFiles, mockModel, false, false);
    }

    private Map<String, CommonController> generateCode(
            String bundleFiles,
            MockModel mockModel,
            boolean isApi,
            boolean isClient) {

        JCodeModel cm = new JCodeModel();

        Map<String, PacketObject> packetAllMap = new HashMap<>();
        Map<String, CommonService> serviceAllMap = new HashMap<>();
        Map<String, CommonController> controllerAllMap = new HashMap<>();

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
            packetGenerator.setPacketObject(packetObject);
            packetGenerator.generate();
        }

        Map<CommonService, BaseServiceGenerator> serviceGeneratorMap = new HashMap<>();

        for (CommonController commonController : controllerAllMap.values()) {
            BaseControllerGenerator baseControllerGenerator =
                    CodeGeneratorFactory.createBaseControllerGenerator(
                            this.packageNamePrefix, mockModel, this.author, this.company, commonController);
            baseControllerGenerator.setApi(isApi);
            baseControllerGenerator.setClient(isClient);
            CommonService commonService = commonController.getService();
            BaseServiceGenerator serviceGenerator =
                    CodeGeneratorFactory.createServiceGenerator(
                            this.packageNamePrefix, mockModel, this.author, this.company, commonController);
            serviceGenerator.setCommonService(commonService);
            serviceGenerator.setApi(isApi);
            serviceGenerator.setClient(isClient);
            serviceGenerator.setCm(cm);
            serviceGenerator.generate();
            serviceGeneratorMap.put(commonService, serviceGenerator);
            baseControllerGenerator.setServiceGenerator(serviceGeneratorMap.get(commonService));
            baseControllerGenerator.setCm(cm);
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


        return controllerAllMap;
    }
}
