/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.service.impl;

import com.fastjrun.codeg.common.*;
import com.fastjrun.codeg.generator.MybatisPlusGenerator;
import com.fastjrun.codeg.helper.CodeGeneratorFactory;
import com.fastjrun.codeg.service.CodeGService;
import com.fastjrun.codeg.utils.SQLSchemaParse;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.writer.AbstractCodeWriter;
import com.helger.jcodemodel.writer.FileCodeWriter;
import com.helger.jcodemodel.writer.JCMWriter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Map;

public class DefaultCodeGService extends BaseCodeGServiceImpl implements CodeGService {

    @Override
    public boolean generateAPI(String bundleFiles, String moduleName) {
        Date begin = new Date();
        log.info("begin genreate at " + begin);
        this.beforeGenerate(moduleName);
        this.generateApiCode(bundleFiles);

        Date end = new Date();

        log.info("end genreate at " + end + ",cost " + String.valueOf(
                end.getTime() - begin.getTime()) + " ms");

        return true;
    }

    private boolean generateBundle(String bundleFiles, String moduleName, CodeGConstants.MockModel mockModel) {
        Date begin = new Date();
        log.info("begin genreate at " + begin);
        this.beforeGenerate(moduleName);


        this.generateBundleCode(bundleFiles, mockModel);


        Date end = new Date();

        log.info("end genreate at " + end + ",cost " + String.valueOf(
                end.getTime() - begin.getTime()) + " ms");

        return true;
    }

    protected boolean generateMybatisPlusCode(String sqlFile, String moduleName) {

        this.beforeGenerate(moduleName);

        JCodeModel cm = new JCodeModel();
        DataBaseObject dataBaseObject =
                SQLSchemaParse.process(SQLSchemaParse.TargetType.TargetType_Mysql, sqlFile);

        Map<String, FJTable> fjTableMap = dataBaseObject.getTableMap();

        for (String key : fjTableMap.keySet()) {
            FJTable fjTable = fjTableMap.get(key);
            MybatisPlusGenerator mybatisPlusGenerator =
                    CodeGeneratorFactory.createMybatisPlusGenerator(this.packageNamePrefix, this.author,
                            this.company, fjTable);
            mybatisPlusGenerator.setCm(cm);
            mybatisPlusGenerator.generate();
        }

        try {
            // 生成代码为UTF-8编码
            AbstractCodeWriter src = new FileCodeWriter(this.srcDir, Charset.forName("UTF-8"));
            // 自上而下地生成类、方法等
            new JCMWriter(cm).build(src);

        } catch (IOException e) {
            log.error("", e);
            throw new CodeGException(CodeGMsgContants.CODEG_CODEG_FAIL, "code generating failed",
                    e);
        }

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

    @Override
    public boolean generateMybatisPlus(String sqlFile, String moduleName) {
        Date begin = new Date();
        log.info("begin genreate at " + begin);
        this.beforeGenerate(moduleName);

        this.generateMybatisPlusCode(sqlFile, moduleName);

        Date end = new Date();

        log.info("end genreate at " + end + ",cost " + String.valueOf(
                end.getTime() - begin.getTime()) + " ms");

        return true;
    }
}
