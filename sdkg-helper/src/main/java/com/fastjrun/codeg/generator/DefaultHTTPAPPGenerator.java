/*
 * Copyright (C) 2018 Fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.generator;

import com.fastjrun.codeg.generator.method.BaseControllerMethodGenerator;
import com.fastjrun.codeg.generator.method.BaseHTTPMethodGenerator;
import com.fastjrun.codeg.generator.method.DefaultHTTPMethodGenerator;
import com.fastjrun.codeg.generator.method.ServiceMethodGenerator;
import com.fastjrun.codeg.processer.AppRequestProcessor;
import com.fastjrun.codeg.processer.BaseRequestProcessor;
import com.fastjrun.codeg.processer.BaseResponseProcessor;
import com.fastjrun.codeg.processer.DefaultExchangeProcessor;
import com.fastjrun.codeg.processer.DefaultResponseWithHeadProcessor;

public class DefaultHTTPAPPGenerator extends BaseHTTPGenerator {

    static final String WEB_PACKAGE_NAME = "web.app.controller.";

    static final String APP_REQUEST_CLASS_NAME = "com.fastjrun.dto.AppRequest";

    static final String APP_REQUEST_HEAD_CLASS_NAME = "com.fastjrun.dto.AppRequestHead";

    public DefaultHTTPAPPGenerator() {
        this.webPackageName = WEB_PACKAGE_NAME;
    }

    @Override
    public BaseControllerMethodGenerator prepareBaseControllerMethodGenerator(
            ServiceMethodGenerator serviceMethodGenerator) {
        BaseHTTPMethodGenerator baseHTTPMethodGenerator = new DefaultHTTPMethodGenerator();
        baseHTTPMethodGenerator.setClient(this.isClient());
        baseHTTPMethodGenerator.setPackageNamePrefix(this.packageNamePrefix);
        baseHTTPMethodGenerator.setMockModel(this.mockModel);
        baseHTTPMethodGenerator.setServiceMethodGenerator(serviceMethodGenerator);
        baseHTTPMethodGenerator.setBaseControllerGenerator(this);
        BaseRequestProcessor baseRequestProcessor = new AppRequestProcessor();
        baseRequestProcessor.setRequestHeadClass(cm.ref(APP_REQUEST_HEAD_CLASS_NAME));
        baseRequestProcessor.setBaseRequestClassName(APP_REQUEST_CLASS_NAME);
        BaseResponseProcessor baseResponseProcessor = new DefaultResponseWithHeadProcessor();
        DefaultExchangeProcessor<AppRequestProcessor, DefaultResponseWithHeadProcessor> exchangeProcessor =
                new DefaultExchangeProcessor
                        (baseRequestProcessor,
                                baseResponseProcessor);
        exchangeProcessor.doParse(serviceMethodGenerator, this.packageNamePrefix);
        baseHTTPMethodGenerator.setExchangeProcessor(exchangeProcessor);
        return baseHTTPMethodGenerator;
    }
}