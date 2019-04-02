package com.fastjrun.codeg.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

import com.fastjrun.codeg.common.CommonController;

public class DefaultCodeGService extends BaseCodeGServiceImpl {

    @Override
    public boolean generateAPI(String bundleFiles, String moduleName) {
        Date begin = new Date();
        log.info("begin genreate at " + begin);
        this.beforeGenerate(moduleName);
        this.generateCode(bundleFiles, moduleName, MockModel.MockModel_Common, true, false);

        Date end = new Date();

        log
                .info("end genreate at " + end + ",cast " + String.valueOf(end.getTime() - begin.getTime()) + " ms");

        return true;
    }

    @Override
    public boolean generateClient(String bundleFiles, String moduleName) {

        Date begin = new Date();
        log.info("begin genreate at " + begin);
        this.beforeGenerate(moduleName);

        Map<String, CommonController> controllerMap = this.generateCode(bundleFiles, moduleName,
                MockModel.MockModel_Common, false, true);
        List<CommonController> rpcDubboList = new ArrayList<>();
        for (CommonController commonController : controllerMap.values()) {
            if (commonController.getControllerType().name.equals("Dubbo")) {
                rpcDubboList.add(commonController);
            }
        }

        Document document = this.generateTestngXml(controllerMap, 5, 5, 5);
        if (document != null) {
            String fileName = "testng.xml";
            File file = new File(moduleName + this.getTestResourcesName() + File
                    .separator + fileName);
            this.saveDocument(file, document);
        }

        if (rpcDubboList != null && rpcDubboList.size() > 0) {
            Document dubboXml = this.generateDubboClientXml(rpcDubboList);
            String dubboFileName = "applicationContext-dubbo-consumer.xml";
            File dubboFile = new File(moduleName + this.getResourcesName() + File
                    .separator + dubboFileName);
            this.saveDocument(dubboFile, dubboXml);
        }

        Date end = new Date();

        log
                .info("end genreate at " + end + ",cast " + String.valueOf(end.getTime() - begin.getTime()) + " ms");

        return true;
    }

    @Override
    public boolean generateBundle(String bundleFiles, String moduleName, MockModel mockModel) {
        Date begin = new Date();
        log.info("begin genreate at " + begin);
        this.beforeGenerate(moduleName);

        Map<String, CommonController> controllerMap = this.generateCode(bundleFiles, moduleName, mockModel, false,
                false);
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

        log
                .info("end genreate at " + end + ",cast " + String.valueOf(end.getTime() - begin.getTime()) + " ms");

        return true;
    }

    @Override
    public boolean generateBase(String sqlFile, String moduleName) {

        Date begin = new Date();
        log.info("begin genreate at " + begin);
        this.beforeGenerate(moduleName);

        this.generateMybatisAnnotationCode(sqlFile, moduleName, false);

        Date end = new Date();

        log
                .info("end genreate at " + end + ",cast " + String.valueOf(end.getTime() - begin.getTime()) + " ms");

        return true;
    }

    @Override
    public boolean generateProvider(String bundleFiles, String moduleName) {
        return this.generateBundle(bundleFiles, moduleName, MockModel.MockModel_Common);
    }
}
