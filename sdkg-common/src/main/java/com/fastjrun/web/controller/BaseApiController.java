package com.fastjrun.web.controller;

import com.fastjrun.packet.BaseApiRequestHead;

public abstract class BaseApiController extends BaseController {

    protected void processHead(BaseApiRequestHead head) {

        log.debug("head=" + head);

    }

}
