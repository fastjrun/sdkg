package com.fastjrun.web.controller;

import com.fastjrun.dto.ApiRequestHead;

public abstract class BaseApiController extends BaseController {

    protected void processHead(ApiRequestHead head) {

        log.debug("head=" + head);

    }

}
