/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.example.exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastjrun.client.exchange.BaseHTTPExchange;
import com.fastjrun.common.util.FastJsonObjectMapper;

public class ExampleExchange extends BaseHTTPExchange<ExampleRequestEncoder, ExampleResponseDecoder> {

    public ExampleExchange() {
        ObjectMapper objectMapper = new FastJsonObjectMapper();
        this.requestEncoder = new ExampleRequestEncoder();
        this.requestEncoder.setObjectMapper(objectMapper);
        this.responseDecoder = new ExampleResponseDecoder();
        this.responseDecoder.setObjectMapper(objectMapper);
    }
}
