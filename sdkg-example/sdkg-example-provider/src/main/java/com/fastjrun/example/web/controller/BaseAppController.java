/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.example.web.controller;

import com.fastjrun.example.dto.AppRequestHead;
import com.fastjrun.web.controller.BaseController;

public abstract class BaseAppController extends BaseController {

    protected void processHead(AppRequestHead head) {

        log.debug("head={}",head);

    }

}
