package com.fastjrun.exchange;

import java.util.List;

import com.fastjrun.dto.BaseListPacket;
import com.fastjrun.dto.BasePacket;

public abstract class BaseRPCResponseDecoder extends BaseResponseDecoder {

    public <T, V> V process(BasePacket<T, V> response) {
        this.parseResponseHead(response.getHead());
        return response.getBody();
    }

    public <T, V> List<V> processList(BaseListPacket<T, V> response) {
        this.parseResponseHead(response.getHead());
        return response.getBody();
    }

    protected abstract <T> void parseResponseHead(T head);
}
