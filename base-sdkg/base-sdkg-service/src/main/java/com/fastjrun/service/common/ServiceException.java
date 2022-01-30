/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.service.common;

import com.fastjrun.common.BaseException;

public class ServiceException extends BaseException {

    private static final long serialVersionUID = -7634581094979099023L;

    public ServiceException(String code, String msg) {
        super(code, msg);
    }
}