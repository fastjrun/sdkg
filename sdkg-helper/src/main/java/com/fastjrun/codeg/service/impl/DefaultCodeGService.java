
package com.fastjrun.codeg.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import org.dom4j.Document;

import com.fastjrun.codeg.common.CodeGException;
import com.fastjrun.codeg.common.CodeGMsgContants;
import com.fastjrun.codeg.common.CodeModelConstants;
import com.fastjrun.codeg.generator.BaseHTTPGenerator;
import com.fastjrun.codeg.generator.BaseRPCGenerator;
import com.fastjrun.codeg.service.CodeGService;
import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.writer.FileCodeWriter;

public class DefaultCodeGService extends BaseCodeGServiceImpl implements CodeGService, CodeModelConstants {

    @Override
    public boolean generateAPI(String moduleName, RpcType rpcType, ControllerType controllerType) {

        Date begin = new Date();
        commonLog.getLog().info("begin genreate at " + begin);
        this.beforeGenerate(moduleName);

        BaseRPCGenerator generator = this.createRPCGenerator(rpcType, MOCK_MODEL_DEFAULT);
        generator.generate();

        Map<String, Properties> clientTestParamMap = generator.getClientTestParamMap();
        if (clientTestParamMap != null) {
            this.saveTestParams(moduleName, clientTestParamMap);
        }

        BaseHTTPGenerator httpGenerator = this.createHTTPGenerator(controllerType, MOCK_MODEL_DEFAULT);
        httpGenerator.setPacketClassMap(generator.getPacketClassMap());
        httpGenerator.setIgorePOService(true);
        httpGenerator.generate();

        try {
            // 生成代码为UTF-8编码
            CodeWriter src = new FileCodeWriter(this.srcDir, "UTF-8");
            CodeWriter srcTest = new FileCodeWriter(this.testSrcDir, "UTF-8");
            // 自上而下地生成类、方法等
            cm.build(src);
            cmTest.build(srcTest);
        } catch (IOException e) {
            this.commonLog.getLog().error("", e);
            throw new CodeGException(CodeGMsgContants.CODEG_CODEG_FAIL, "code generating failed", e);
        }

        if (generator.getTestngXml() != null) {
            String fileName = "testng-rpc.xml";
            File file = new File(moduleName + this.getTestResourcesName() + File
                    .separator + fileName);
            this.saveDocument(file, generator.getTestngXml());
        }

        Document document = generator.getClientXml();
        if (document != null) {
            String fileName = "applicationContext-dubbo-consumer.xml";
            if (rpcType == RpcType.RpcType_Grpc) {
                fileName = "applicationContext-grpc-consumer.xml";
            }
            File rpcfile = new File(moduleName + this.getResourcesName() + File
                    .separator + fileName);
            this.saveDocument(rpcfile, document);
        }

        Map<String, Properties> httpClientTestParamMap = httpGenerator.getClientTestParamMap();
        if (httpClientTestParamMap != null) {
            this.saveTestParams(moduleName, httpClientTestParamMap);
        }

        if (httpGenerator.getTestngXml() != null) {
            String fileName = "testng-http.xml";
            File file = new File(moduleName + this.getTestResourcesName() + File
                    .separator + fileName);
            this.saveDocument(file, httpGenerator.getTestngXml());
        }

        Date end = new Date();

        commonLog.getLog()
                .info("end genreate at " + end + ",cast " + String.valueOf(end.getTime() - begin.getTime()) + " ms");

        return true;
    }

    @Override
    public boolean generateBundle(String moduleName, RpcType rpcType, ControllerType controllerType,
                                  MockModel mockModel) {
        Date begin = new Date();
        commonLog.getLog().info("begin genreate at " + begin);
        this.beforeGenerate(moduleName);

        BaseRPCGenerator generator = this.createRPCGenerator(rpcType, mockModel);
        generator.setClient(false);
        generator.generate();

        BaseHTTPGenerator httpGenerator = this.createHTTPGenerator(controllerType, mockModel);
        httpGenerator.setClient(false);
        httpGenerator.setIgorePOService(true);
        httpGenerator.generate();

        try {
            // 生成代码为UTF-8编码
            CodeWriter src = new FileCodeWriter(this.srcDir, "UTF-8");
            CodeWriter srcTest = new FileCodeWriter(this.testSrcDir, "UTF-8");
            // 自上而下地生成类、方法等
            cm.build(src);
            cmTest.build(srcTest);
        } catch (IOException e) {
            this.commonLog.getLog().error("", e);
            throw new CodeGException(CodeGMsgContants.CODEG_CODEG_FAIL, "code generating failed", e);
        }

        Document document = generator.getServerXml();
        if (document != null) {
            String fileName = "applicationContext-dubbo-provider.xml";
            if (rpcType == RpcType.RpcType_Grpc) {
                fileName = "applicationContext-grpc-provider.xml";
            }
            File rpcfile = new File(moduleName + this.getResourcesName() + File
                    .separator + fileName);
            this.saveDocument(rpcfile, document);
        }

        Date end = new Date();

        commonLog.getLog()
                .info("end genreate at " + end + ",cast " + String.valueOf(end.getTime() - begin.getTime()) + " ms");

        return true;
    }

    @Override
    public boolean generateProvider(String moduleName, RpcType rpcType, ControllerType controllerType) {

        return this.generateBundle(moduleName, rpcType, controllerType, MockModel.MockModel_Common);
    }
}
