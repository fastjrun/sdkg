/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
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
import com.fastjrun.codeg.processer.GenericRequestProcessor;

public class DefaultHTTPGeneriGenerator extends BaseHTTPGenerator {
    static final String WEB_PACKAGE_NAME = "web.generic.controller.";

    public DefaultHTTPGeneriGenerator() {
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
        BaseRequestProcessor baseRequestProcessor = new GenericRequestProcessor();
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