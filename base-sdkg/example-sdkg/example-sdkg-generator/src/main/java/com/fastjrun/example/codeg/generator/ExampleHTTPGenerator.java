/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.example.codeg.generator;

import com.fastjrun.codeg.generator.BaseHTTPGenerator;
import com.fastjrun.codeg.generator.method.BaseControllerMethodGenerator;
import com.fastjrun.codeg.generator.method.BaseHTTPMethodGenerator;
import com.fastjrun.codeg.generator.method.BaseServiceMethodGenerator;
import com.fastjrun.codeg.generator.method.DefaultHTTPMethodGenerator;
import com.fastjrun.codeg.processor.BaseRequestProcessor;
import com.fastjrun.codeg.processor.BaseResponseProcessor;
import com.fastjrun.codeg.processor.DefaultExchangeProcessor;
import com.fastjrun.example.codeg.processor.ExampleRequestProcessor;
import com.fastjrun.example.codeg.processor.ExampleResponseProcessor;

public class ExampleHTTPGenerator extends BaseHTTPGenerator {

  @Override
  public BaseControllerMethodGenerator prepareBaseControllerMethodGenerator(
          BaseServiceMethodGenerator baseServiceMethodGenerator) {
    BaseHTTPMethodGenerator baseHTTPMethodGenerator = new DefaultHTTPMethodGenerator();
    baseHTTPMethodGenerator.setPackageNamePrefix(this.packageNamePrefix);
    baseHTTPMethodGenerator.setMock(this.isMock());
    baseHTTPMethodGenerator.setSwaggerVersion(this.getSwaggerVersion());
    baseHTTPMethodGenerator.setServiceMethodGenerator(baseServiceMethodGenerator);
    baseHTTPMethodGenerator.setBaseControllerGenerator(this);
    BaseRequestProcessor baseRequestProcessor = new ExampleRequestProcessor();
    BaseResponseProcessor baseResponseProcessor = new ExampleResponseProcessor();
    DefaultExchangeProcessor<ExampleRequestProcessor, ExampleResponseProcessor>
        exchangeProcessor =
            new DefaultExchangeProcessor(baseRequestProcessor, baseResponseProcessor);
    exchangeProcessor.doParse(baseServiceMethodGenerator, this.packageNamePrefix);
    baseHTTPMethodGenerator.setExchangeProcessor(exchangeProcessor);
    return baseHTTPMethodGenerator;
  }
}
