/*
 * Copyright (C) 2018 Fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.exchange;

public abstract class BaseRPCRequestEncoder extends BaseRequestEncoder {

    public abstract <T> void processRequest(Class[]
                                                    paramterTypes, Object[] paramerValues);

}
