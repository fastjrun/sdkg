/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.service.impl;

import com.fastjrun.codeg.common.CommonController;
import com.fastjrun.codeg.service.CodeGService;
import com.fastjrun.codeg.common.CodeGConstants;
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
        this.generateClientCode(bundleFiles, moduleName);

        Date end = new Date();

        log.info("end genreate at " + end + ",cost " + String.valueOf(
                end.getTime() - begin.getTime()) + " ms");

        return true;
    }

    private boolean generateBundle(String bundleFiles, String moduleName, CodeGConstants.MockModel mockModel) {
        Date begin = new Date();
        log.info("begin genreate at " + begin);
        this.beforeGenerate(moduleName);


        this.generateBundleCode(bundleFiles, moduleName, mockModel);


        Date end = new Date();

        log.info("end genreate at " + end + ",cost " + String.valueOf(
                end.getTime() - begin.getTime()) + " ms");

        return true;
    }

    @Override
    public boolean generateProvider(String bundleFiles, String moduleName) {
        return this.generateBundle(bundleFiles, moduleName, CodeGConstants.MockModel.MockModel_Common);
    }

    @Override
    public boolean generateProviderMock(String bundleFiles, String moduleName,
                                        CodeGConstants.MockModel mockModel) {
        return this.generateBundle(bundleFiles, moduleName, mockModel);
    }
}
