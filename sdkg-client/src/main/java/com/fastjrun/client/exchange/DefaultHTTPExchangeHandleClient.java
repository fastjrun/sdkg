/*
 * Copyright (C) 2018 Fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.client.exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastjrun.client.util.DefaultHTTPUtilClient;
import com.fastjrun.exchange.DefaultHTTPExchange;
import com.fastjrun.exchange.DefaultHTTPRequestEncoder;
import com.fastjrun.exchange.DefaultHTTPResponseDecoder;
import com.fastjrun.util.FastJsonObjectMapper;

public abstract class DefaultHTTPExchangeHandleClient extends BaseHTTPExchangeHandleClient {

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
        DefaultHTTPResponseDecoder responseDecoder = new DefaultHTTPResponseDecoder();
        responseDecoder.setObjectMapper(objectMapper);
        this.defaultHTTPExchange.setResponseDecoder(responseDecoder);
    }
}
