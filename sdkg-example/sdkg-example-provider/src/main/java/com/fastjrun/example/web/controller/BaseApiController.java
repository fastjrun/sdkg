/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.example.web.controller;

import com.fastjrun.example.dto.ApiRequestHead;
import com.fastjrun.web.controller.BaseController;

public abstract class BaseApiController extends BaseController {

    protected void processHead(ApiRequestHead head) {

        log.debug("head=" + head);

    }

}
