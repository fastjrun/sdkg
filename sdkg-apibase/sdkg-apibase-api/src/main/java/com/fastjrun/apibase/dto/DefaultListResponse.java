/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.apibase.dto;

import java.io.Serializable;

public class DefaultListResponse<V> extends BaseListPacket<DefaultResponseHead, V> implements
        Serializable {

    private static final long serialVersionUID = 340184860523759300L;
}
