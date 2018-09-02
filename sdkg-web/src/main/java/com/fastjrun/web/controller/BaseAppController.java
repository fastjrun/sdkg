package com.fastjrun.web.controller;

import com.fastjrun.packet.AppRequestHead;

public abstract class BaseAppController extends BaseController {

    protected void processHead(AppRequestHead head) {

        log.debug("head=" + head);

    }

}
