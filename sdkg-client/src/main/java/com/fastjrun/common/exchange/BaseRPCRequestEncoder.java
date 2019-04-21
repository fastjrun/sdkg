/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.common.exchange;

public abstract class BaseRPCRequestEncoder extends BaseRequestEncoder {

    public abstract void processRequest(Class[]
                                                paramterTypes, Object[] paramerValues);

}
