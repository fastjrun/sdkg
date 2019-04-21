/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.exchange;

import com.fastjrun.common.exchange.BaseRPCExchange;

public class DefaultRPCExchange extends BaseRPCExchange<DefaultRPCRequestEncoder, DefaultRPCResponseDecoder> {

    public DefaultRPCExchange() {
        this.requestEncoder = new DefaultRPCRequestEncoder();
        this.responseDecoder = new DefaultRPCResponseDecoder();
    }
}
