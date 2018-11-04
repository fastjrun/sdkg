/*
 * Copyright (C) 2018 Fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.client.exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastjrun.client.util.DefaultHTTPUtilClient;
import com.fastjrun.exchange.DefaultHTTPExchange;
import com.fastjrun.exchange.DefaultHTTPNoHeadResponseDecoder;
import com.fastjrun.exchange.DefaultHTTPRequestEncoder;
import com.fastjrun.util.FastJsonObjectMapper;

public abstract class DefaultHTTPNoHeadExchangeHandleClient extends BaseHTTPExchangeHandleClient {

    protected void initUtilClient(String baseUrl) {
        this.baseClient = new DefaultHTTPUtilClient();
        baseClient.setBaseUrl(baseUrl);
    }

    @Override
    protected void initExchange() {
        ObjectMapper objectMapper = new FastJsonObjectMapper();
        this.defaultHTTPExchange = new DefaultHTTPExchange();
        DefaultHTTPRequestEncoder requestEncoder = new DefaultHTTPRequestEncoder();
        requestEncoder.setObjectMapper(objectMapper);
        this.defaultHTTPExchange.setRequestEncoder(requestEncoder);
        DefaultHTTPNoHeadResponseDecoder responseDecoder = new DefaultHTTPNoHeadResponseDecoder();
        responseDecoder.setObjectMapper(objectMapper);
        this.defaultHTTPExchange.setResponseDecoder(responseDecoder);
    }
}
