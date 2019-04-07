package com.fastjrun.web.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseController {
    protected final Logger log = LogManager.getLogger(this.getClass());

    protected final String PAGE_SIZE = "10";


}
