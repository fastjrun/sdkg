/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.example.dto;

import java.io.Serializable;

public class ApiRequest<T> extends BasePacket<ApiRequestHead, T> implements Serializable {

    private static final long serialVersionUID = -2063835126152861254L;
}
