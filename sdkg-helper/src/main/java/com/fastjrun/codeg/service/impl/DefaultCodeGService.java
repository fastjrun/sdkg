package com.fastjrun.codeg.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

import com.fastjrun.codeg.common.CodeModelConstants;
import com.fastjrun.codeg.common.CommonController;
import com.fastjrun.codeg.service.CodeGService;

public class DefaultCodeGService extends BaseCodeGServiceImpl implements CodeGService, CodeModelConstants {

    @Override
    public boolean generateAPI(String moduleName) {
        Date begin = new Date();
        commonLog.getLog().info("begin genreate at " + begin);
        this.beforeGenerate(moduleName);

        Map<String, CommonController> controllerMap = this.generateCode(moduleName,
                MockModel.MockModel_Common, true);
        List<CommonController> rpcDubboList = new ArrayList<>();
        for (CommonController commonController : controllerMap.values()) {
            if (commonController.getControllerType().name.equals("Dubbo")) {
                rpcDubboList.add(commonController);
            }
        }

        Document document = this.generateTestngXml(controllerMap, 5, 5, 5);
        if (document != null) {
            File file = new File(moduleName + this.getTestResourcesName() + File
                    .separator + TESTNG_XML_FILENAME);
            this.saveDocument(file, document);
        }

        if (rpcDubboList != null && rpcDubboList.size() > 0) {
            Document dubboXml = this.generateDubboClientXml(rpcDubboList);
            File dubboFile = new File(moduleName + this.getResourcesName() + File
                    .separator + DUBBO_CONSUME_FILENAME);
            this.saveDocument(dubboFile, dubboXml);
        }

        Date end = new Date();

        commonLog.getLog()
                .info("end genreate at " + end + ",cast " + String.valueOf(end.getTime() - begin.getTime()) + " ms");

        return true;
    }

    @Override
    public boolean generateBundle(String moduleName, MockModel mockModel) {
        Date begin = new Date();
        commonLog.getLog().info("begin genreate at " + begin);
        this.beforeGenerate(moduleName);

        Map<String, CommonController> controllerMap = this.generateCode(moduleName, mockModel, false);
        List<CommonController> rpcDubboList = new ArrayList<>();

        for (CommonController commonController : controllerMap.values()) {
            if (commonController.getControllerType().name.equals("Dubbo")) {
                rpcDubboList.add(commonController);
            }
        }

        if (rpcDubboList != null && rpcDubboList.size() > 0) {
            Document dubboXml = this.generateDubboServerXml(rpcDubboList);
            File dubboFile = new File(moduleName + this.getResourcesName() + File
                    .separator + DUBBO_PRPVIDER_FILENAME);
            this.saveDocument(dubboFile, dubboXml);
        }

        Date end = new Date();

        commonLog.getLog()
                .info("end genreate at " + end + ",cast " + String.valueOf(end.getTime() - begin.getTime()) + " ms");

        return true;
    }

    @Override
    public boolean generateBase(String moduleName) {

        Date begin = new Date();
        commonLog.getLog().info("begin genreate at " + begin);
        this.beforeGenerate(moduleName);

        this.generateMybatisAnnotationCode(moduleName, false);

        Date end = new Date();

        commonLog.getLog()
                .info("end genreate at " + end + ",cast " + String.valueOf(end.getTime() - begin.getTime()) + " ms");

        return true;
    }

    @Override
    public boolean generateProvider(String moduleName) {
        return this.generateBundle(moduleName, MockModel.MockModel_Common);
    }
}
