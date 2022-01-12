/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.eladmin.exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastjrun.client.exchange.BaseHTTPExchange;
import com.fastjrun.common.util.FastJsonObjectMapper;

public class EladminHTTPExchange extends BaseHTTPExchange<DefaultHTTPRequestEncoder, DefaultHTTPNoHeadResponseDecoder> {

    public EladminHTTPExchange() {
        ObjectMapper objectMapper = new FastJsonObjectMapper();
        this.requestEncoder = new DefaultHTTPRequestEncoder();
        this.requestEncoder.setObjectMapper(objectMapper);
        this.responseDecoder = new DefaultHTTPNoHeadResponseDecoder();
        this.responseDecoder.setObjectMapper(objectMapper);
    }
}
