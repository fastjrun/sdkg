package com.fastjrun.codeg.generator;

import com.fastjrun.codeg.generator.method.BaseControllerMethodGenerator;
import com.fastjrun.codeg.generator.method.BaseHTTPMethodGenerator;
import com.fastjrun.codeg.generator.method.DefaultHTTPMethodGenerator;
import com.fastjrun.codeg.generator.method.ServiceMethodGenerator;
import com.fastjrun.codeg.processer.ApiRequestProcessor;
import com.fastjrun.codeg.processer.BaseRequestProcessor;
import com.fastjrun.codeg.processer.BaseResponseProcessor;
import com.fastjrun.codeg.processer.DefaultExchangeProcessor;
import com.fastjrun.codeg.processer.DefaultResponseWithHeadProcessor;

public class DefaultHTTPAPIGenerator extends BaseHTTPGenerator {

    static final String WEB_PACKAGE_NAME = "web.api.controller.";

    static final String API_REQUEST_CLASS_NAME = "com.fastjrun.dto.ApiRequest";

    static final String API_REQUEST_HEAD_CLASS_NAME = "com.fastjrun.dto.ApiRequestHead";

    public DefaultHTTPAPIGenerator() {
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
        BaseRequestProcessor baseRequestProcessor = new ApiRequestProcessor();
        baseRequestProcessor.setRequestHeadClass(cm.ref(API_REQUEST_HEAD_CLASS_NAME));
        baseRequestProcessor.setBaseRequestClassName(API_REQUEST_CLASS_NAME);
        BaseResponseProcessor baseResponseProcessor = new DefaultResponseWithHeadProcessor();

        DefaultExchangeProcessor<BaseRequestProcessor, BaseResponseProcessor> exchangeProcessor = new
                DefaultExchangeProcessor(baseRequestProcessor, baseResponseProcessor);
        exchangeProcessor.doParse(serviceMethodGenerator, this.packageNamePrefix);
        baseHTTPMethodGenerator.setExchangeProcessor(exchangeProcessor);
        return baseHTTPMethodGenerator;
    }
}