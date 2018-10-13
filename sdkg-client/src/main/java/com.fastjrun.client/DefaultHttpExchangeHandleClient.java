package com.fastjrun.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastjrun.exchange.DefaultHTTPExchange;
import com.fastjrun.exchange.DefaultHttpRequestEncoder;
import com.fastjrun.exchange.DefaultHttpResponseDecoder;
import com.fastjrun.util.FastJsonObjectMapper;

/*
 * *
 *  * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *  *
 *  * @author 崔莹峰
 *  * @Copyright 2018 快嘉框架. All rights reserved.
 *
 */

public abstract class DefaultHttpExchangeHandleClient extends BaseHttpExchangeHandleClient {

    protected void initUtilClient(String baseUrl) {
        this.baseClient = new DefaultHttpClient();
        baseClient.setBaseUrl(baseUrl);
    }

    @Override
    protected void initExchange() {
        ObjectMapper objectMapper = new FastJsonObjectMapper();
        DefaultHTTPExchange defaultHTTPExchange = new DefaultHTTPExchange();
        DefaultHttpRequestEncoder requestEncoder = new DefaultHttpRequestEncoder();
        requestEncoder.setObjectMapper(objectMapper);
        defaultHTTPExchange.setRequestEncoder(requestEncoder);
        DefaultHttpResponseDecoder responseDecoder = new DefaultHttpResponseDecoder();
        responseDecoder.setObjectMapper(objectMapper);
        defaultHTTPExchange.setResponseDecoder(responseDecoder);
        this.defaultHTTPExchange = defaultHTTPExchange;
    }
}
