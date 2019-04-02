package com.fastjrun.codeg.generator;

import com.fastjrun.codeg.generator.method.BaseControllerMethodGenerator;
import com.fastjrun.codeg.generator.method.BaseRPCMethodGenerator;
import com.fastjrun.codeg.generator.method.DefaultRPCMethodGenerator;
import com.fastjrun.codeg.generator.method.ServiceMethodGenerator;
import com.fastjrun.codeg.processer.AppRequestProcessor;
import com.fastjrun.codeg.processer.BaseRequestProcessor;
import com.fastjrun.codeg.processer.BaseResponseProcessor;
import com.fastjrun.codeg.processer.DefaultExchangeProcessor;
import com.fastjrun.codeg.processer.DefaultResponseWithHeadProcessor;
import com.fastjrun.codeg.processer.GenericRequestProcessor;

public class DefaultDubboGenerator extends BaseRPCGenerator {

    @Override
    public BaseControllerMethodGenerator prepareBaseControllerMethodGenerator(
            ServiceMethodGenerator serviceMethodGenerator) {
        BaseRPCMethodGenerator baseRPCMethodGenerator = new DefaultRPCMethodGenerator();
        baseRPCMethodGenerator.setClient(this.isClient());
        baseRPCMethodGenerator.setPackageNamePrefix(this.packageNamePrefix);
        baseRPCMethodGenerator.setMockModel(this.mockModel);
        baseRPCMethodGenerator.setServiceMethodGenerator(serviceMethodGenerator);
        baseRPCMethodGenerator.setBaseControllerGenerator(this);
        BaseRequestProcessor baseRequestProcessor = new GenericRequestProcessor();
        BaseResponseProcessor baseResponseProcessor = new DefaultResponseWithHeadProcessor();
        DefaultExchangeProcessor<AppRequestProcessor, DefaultResponseWithHeadProcessor> exchangeProcessor =
                new DefaultExchangeProcessor
                        (baseRequestProcessor,
                                baseResponseProcessor);
        exchangeProcessor.doParse(serviceMethodGenerator, this.packageNamePrefix);
        baseRPCMethodGenerator.setExchangeProcessor(exchangeProcessor);
        return baseRPCMethodGenerator;
    }
}