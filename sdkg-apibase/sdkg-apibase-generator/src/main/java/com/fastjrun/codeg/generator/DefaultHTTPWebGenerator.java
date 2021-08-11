/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.generator;

import com.fastjrun.codeg.generator.method.BaseControllerMethodGenerator;
import com.fastjrun.codeg.generator.method.BaseHTTPMethodGenerator;
import com.fastjrun.codeg.generator.method.DefaultHTTPMethodGenerator;
import com.fastjrun.codeg.generator.method.ServiceMethodGenerator;
import com.fastjrun.codeg.processer.*;

public class DefaultHTTPWebGenerator extends BaseHTTPGenerator {

    @Override
    public BaseControllerMethodGenerator prepareBaseControllerMethodGenerator(
      ServiceMethodGenerator serviceMethodGenerator) {
        BaseHTTPMethodGenerator baseHTTPMethodGenerator = new DefaultHTTPMethodGenerator();
        baseHTTPMethodGenerator.setClient(this.isClient());
        baseHTTPMethodGenerator.setPackageNamePrefix(this.packageNamePrefix);
        baseHTTPMethodGenerator.setMockModel(this.mockModel);
        baseHTTPMethodGenerator.setServiceMethodGenerator(serviceMethodGenerator);
        baseHTTPMethodGenerator.setBaseControllerGenerator(this);
        BaseRequestProcessor baseRequestProcessor = new WebRequestProcessor();
        BaseResponseProcessor baseResponseProcessor = new WebResponseProcessor();
        DefaultExchangeProcessor<WebRequestProcessor, WebResponseProcessor> exchangeProcessor =
          new DefaultExchangeProcessor(baseRequestProcessor, baseResponseProcessor);
        exchangeProcessor.doParse(serviceMethodGenerator, this.packageNamePrefix);
        baseHTTPMethodGenerator.setExchangeProcessor(exchangeProcessor);
        return baseHTTPMethodGenerator;
    }
}
