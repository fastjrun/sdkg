package com.fastjrun.exchange;

import java.util.List;

public class DefaultHTTPExchange {

    private DefaultHTTPRequestEncoder requestEncoder;

    private BaseHTTPResponseDecoder responseDecoder;

    public DefaultHTTPRequestEncoder getRequestEncoder() {
        return requestEncoder;
    }

    public void setRequestEncoder(DefaultHTTPRequestEncoder requestEncoder) {
        this.requestEncoder = requestEncoder;
    }

    public BaseHTTPResponseDecoder getResponseDecoder() {
        return responseDecoder;
    }

    public void setResponseDecoder(BaseHTTPResponseDecoder responseDecoder) {
        this.responseDecoder = responseDecoder;
    }

    public <T> T parseObjectFromResponse(String responseResult, Class<T> valueType) {

        return this.responseDecoder.parseObjectFromResponse(responseResult, valueType);

    }

    public <T> List<T> parseListFromResponse(String responseResult, Class<T> valueType) {
        return this.responseDecoder.parseListFromResponse(responseResult, valueType);

    }

    public String generateRequestBody(Object body) {
        return this.requestEncoder.generateRequestBody(body);

    }
}
