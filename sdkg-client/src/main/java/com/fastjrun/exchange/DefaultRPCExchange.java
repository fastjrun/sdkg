package com.fastjrun.exchange;

import java.util.List;

import com.fastjrun.dto.BaseListPacket;
import com.fastjrun.dto.BasePacket;

public class DefaultRPCExchange {

    private BaseRPCRequestEncoder requestEncoder;

    private BaseRPCResponseDecoder responseDecoder;

    public BaseRPCRequestEncoder getRequestEncoder() {
        return requestEncoder;
    }

    public void setRequestEncoder(BaseRPCRequestEncoder requestEncoder) {
        this.requestEncoder = requestEncoder;
    }

    public BaseRPCResponseDecoder getResponseDecoder() {
        return responseDecoder;
    }

    public void setResponseDecoder(BaseRPCResponseDecoder responseDecoder) {
        this.responseDecoder = responseDecoder;
    }

    public <T, V> V process(BasePacket<T, V> response) {
        return this.responseDecoder.process(response);
    }

    public <T, V> List<V> processList(BaseListPacket<T, V> response) {
        return this.responseDecoder.processList(response);
    }
}
