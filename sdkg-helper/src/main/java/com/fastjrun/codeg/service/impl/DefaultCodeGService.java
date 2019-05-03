/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.service.impl;

import com.fastjrun.codeg.common.CommonController;
import com.fastjrun.codeg.service.CodeGService;
import org.dom4j.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DefaultCodeGService extends BaseCodeGServiceImpl implements CodeGService {

    @Override
    public boolean generateAPI(String bundleFiles, String moduleName) {
        Date begin = new Date();
        log.info("begin genreate at " + begin);
        this.beforeGenerate(moduleName);
        this.generateApiCode(bundleFiles, moduleName);

        Date end = new Date();

        log.info("end genreate at " + end + ",cost " + String.valueOf(
          end.getTime() - begin.getTime()) + " ms");

        return true;
    }

    @Override
    public boolean generateClient(String bundleFiles, String moduleName) {

        Date begin = new Date();
        log.info("begin genreate at " + begin);
        this.beforeGenerate(moduleName);

        Map<String, CommonController> controllerMap =
          this.generateClientCode(bundleFiles, moduleName);
        List<CommonController> rpcDubboList = new ArrayList<>();
        for (CommonController commonController : controllerMap.values()) {
            if (commonController.getControllerType().name.equals("Dubbo")) {
                rpcDubboList.add(commonController);
            }
        }

        Document document = this.generateTestngXml(controllerMap, 5, 5, 5);
        if (document != null) {
            String fileName = "testng.xml";
            File file =
              new File(moduleName + this.getTestResourcesName() + File.separator + fileName);
            this.saveDocument(file, document);
        }

        if (rpcDubboList != null && rpcDubboList.size() > 0) {
            Document dubboXml = this.generateDubboClientXml(rpcDubboList);
            String dubboFileName = "applicationContext-dubbo-consumer.xml";
            File dubboFile =
              new File(moduleName + this.getResourcesName() + File.separator + dubboFileName);
            this.saveDocument(dubboFile, dubboXml);
        }

        Date end = new Date();

        log.info("end genreate at " + end + ",cost " + String.valueOf(
          end.getTime() - begin.getTime()) + " ms");

        return true;
    }

    private boolean generateBundle(String bundleFiles, String moduleName, MockModel mockModel,
      boolean supportServiceTest) {
        Date begin = new Date();
        log.info("begin genreate at " + begin);
        this.beforeGenerate(moduleName);

        Map<String, CommonController> controllerMap =
          this.generateBundleCode(bundleFiles, moduleName, mockModel, supportServiceTest);
        List<CommonController> rpcDubboList = new ArrayList<>();
        for (CommonController commonController : controllerMap.values()) {
            if (commonController.getControllerType().name.equals("Dubbo")) {
                rpcDubboList.add(commonController);
            }
        }

        if (rpcDubboList != null && rpcDubboList.size() > 0) {
            Document dubboXml = this.generateDubboServerXml(rpcDubboList);
            File dubboFile = new File(
              moduleName + this.getResourcesName() + File.separator + DUBBO_PRPVIDER_FILENAME);
            this.saveDocument(dubboFile, dubboXml);
        }

        Date end = new Date();

        log.info("end genreate at " + end + ",cost " + String.valueOf(
          end.getTime() - begin.getTime()) + " ms");

        return true;
    }

    @Override
    public boolean generateBase(String sqlFile, String moduleName, boolean supportTest,
      boolean supportController) {

        Date begin = new Date();
        log.info("begin genreate at " + begin);
        this.beforeGenerate(moduleName);

        this.generateMybatisAnnotationCode(sqlFile, moduleName, supportController, supportTest);

        Date end = new Date();

        log.info("end genreate at " + end + ",cost " + String.valueOf(
          end.getTime() - begin.getTime()) + " ms");

        return true;
    }

    @Override
    public boolean generateProvider(String bundleFiles, String moduleName,
      boolean supportServiceTest) {
        return this.generateBundle(bundleFiles, moduleName, MockModel.MockModel_Common,
          supportServiceTest);
    }

    @Override
    public boolean generateProviderMock(String bundleFiles, String moduleName,
      MockModel mockModel) {
        return this.generateBundle(bundleFiles, moduleName, mockModel, false);
    }
}
