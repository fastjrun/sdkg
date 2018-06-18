package com.fastjrun.web.controller;

import com.fastjrun.packet.BaseAppRequestHead;

public abstract class BaseAppController extends BaseController{

    protected void processHead(BaseAppRequestHead head) {

        log.debug("head=" + head);

    }

}
