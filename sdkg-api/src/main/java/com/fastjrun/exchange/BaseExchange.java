package com.fastjrun.exchange;

import com.fastjrun.dto.BaseListPacket;
import com.fastjrun.dto.BasePacket;

public abstract class BaseExchange<T extends BasePacket<?, ?>, V extends BasePacket<?, ?>, M extends BaseListPacket<?,
        ?>> {
    private T request;

    private V response;

    private M listResponse;

    public T getRequest() {
        return request;
    }

    public void setRequest(T request) {
        this.request = request;
    }

    public V getResponse() {
        return response;
    }

    public void setResponse(V response) {
        this.response = response;
    }

    public M getListResponse() {
        return listResponse;
    }

    public void setListResponse(M listResponse) {
        this.listResponse = listResponse;
    }
}
