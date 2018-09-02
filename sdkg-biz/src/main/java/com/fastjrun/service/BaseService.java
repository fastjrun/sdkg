package com.fastjrun.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseService {
    protected final Logger log = LogManager.getLogger(this.getClass());
}
