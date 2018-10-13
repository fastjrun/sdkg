package com.fastjrun.exchange;

import java.util.List;

public class DefaultHTTPExchange {

    private DefaultHttpRequestEncoder requestEncoder;

    private BaseHttpResponseDecoder responseDecoder;

    public DefaultHttpRequestEncoder getRequestEncoder() {
        return requestEncoder;
    }

    public void setRequestEncoder(DefaultHttpRequestEncoder requestEncoder) {
        this.requestEncoder = requestEncoder;
    }

    public BaseHttpResponseDecoder getResponseDecoder() {
        return responseDecoder;
    }

    public void setResponseDecoder(BaseHttpResponseDecoder responseDecoder) {
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
