/*
 * Copyright (C) 2018 Fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.common;

public class ServiceException extends BaseException {

    private static final long serialVersionUID = -7634581094979099023L;

    public ServiceException(String code, String msg) {
        super(code, msg);
    }
}