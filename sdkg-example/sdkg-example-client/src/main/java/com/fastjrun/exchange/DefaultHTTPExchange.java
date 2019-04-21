/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastjrun.common.exchange.BaseHTTPExchange;
import com.fastjrun.common.util.FastJsonObjectMapper;

public class DefaultHTTPExchange extends BaseHTTPExchange<DefaultHTTPRequestEncoder, DefaultHTTPResponseDecoder> {

    public DefaultHTTPExchange() {
        ObjectMapper objectMapper = new FastJsonObjectMapper();
        this.requestEncoder = new DefaultHTTPRequestEncoder();
        this.requestEncoder.setObjectMapper(objectMapper);
        this.responseDecoder = new DefaultHTTPResponseDecoder();
        this.responseDecoder.setObjectMapper(objectMapper);
    }
}
