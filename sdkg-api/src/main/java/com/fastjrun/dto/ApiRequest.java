package com.fastjrun.dto;

import java.io.Serializable;

public class ApiRequest<T> extends BasePacket<ApiRequestHead, T> implements Serializable {

    private static final long serialVersionUID = -2063835126152861254L;
}
