package com.fastjrun.service;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;

public abstract class BaseService {
    protected final Logger log = LogManager.getLogger(this.getClass());

    @Resource
    protected MessageSource serviceMessageSource;
}
