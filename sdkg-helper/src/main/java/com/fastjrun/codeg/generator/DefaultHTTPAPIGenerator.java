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
    @Override
    public BaseControllerMethodGenerator prepareBaseControllerMethodGenerator(
            ServiceMethodGenerator serviceMethodGenerator) {
        BaseHTTPMethodGenerator baseHTTPMethodGenerator = new DefaultHTTPMethodGenerator();
        baseHTTPMethodGenerator.setClient(this.isClient());
        baseHTTPMethodGenerator.setPackageNamePrefix(this.packageNamePrefix);
        baseHTTPMethodGenerator.setMockModel(this.mockModel);
        baseHTTPMethodGenerator.setServiceMethodGenerator(serviceMethodGenerator);
        baseHTTPMethodGenerator.setBaseControllerGenerator(this);
        DefaultExchangeProcessor exchangeProcessor = new DefaultExchangeProcessor();
        BaseRequestProcessor baseRequestProcessor = new ApiRequestProcessor();
        BaseResponseProcessor baseResponseProcessor = new DefaultResponseWithHeadProcessor();
        exchangeProcessor.setResponseProcessor(baseResponseProcessor);
        exchangeProcessor.setRequestProcessor(baseRequestProcessor);
        exchangeProcessor.doParse(serviceMethodGenerator, this.packageNamePrefix);
        baseHTTPMethodGenerator.setExchangeProcessor(exchangeProcessor);
        return baseHTTPMethodGenerator;
    }
}