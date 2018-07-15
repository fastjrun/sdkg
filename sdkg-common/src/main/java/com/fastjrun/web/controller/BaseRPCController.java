package com.fastjrun.web.controller;

import com.fastjrun.packet.EmptyRequestHead;

public abstract class BaseRPCController extends BaseController {

    protected void processHead(EmptyRequestHead head) {

        log.debug("head=" + head);

    }

}
