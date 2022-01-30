/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

import javax.annotation.Resource;

public abstract class BaseService {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Resource
    protected MessageSource serviceMessageSource;
}
