package com.fastjrun.mock.web.controller;

import com.alibaba.testsdk.packet.generic.AutoLoginRestRequestBody;
import com.alibaba.testsdk.packet.generic.RegistserRestRequestBody;
import com.alibaba.testsdk.service.UserServiceRestGeneric;
import com.fastjrun.packet.BaseDefaultResponseBody;
import com.fastjrun.packet.BaseResponse;
import com.fastjrun.web.controller.BaseController;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *
 * @author cuiyingfeng
 * @Copyright 2018 快嘉框架. All rights reserved.
 */
@RestController
@RequestMapping("/generic/user/")
@Api(value = "\u7528\u6237\u7ba1\u7406\u63a5\u53e3", tags = "\u57fa\u672c\u63a5\u53e3")
public class UserGenericController
        extends BaseController {

    @Autowired
    private UserServiceRestGeneric userService;

    @RequestMapping(value = "register", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @io.swagger.annotations.ApiOperation(value = "\u6ce8\u518c", notes = "\u6ce8\u518c")
    public BaseResponse<BaseDefaultResponseBody> register(
            @RequestBody
            @javax.validation.Valid
                    RegistserRestRequestBody request) {
        BaseResponse<BaseDefaultResponseBody> response = this.userService.register(request);
        log.debug(response);
        return response;
    }

    @RequestMapping(value = "login", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @io.swagger.annotations.ApiOperation(value = "\u767b\u5f55", notes = "\u767b\u5f55")
    public com.alibaba.testsdk.packet.generic.LoginRestResponseBody login(
            @RequestBody
            @javax.validation.Valid
                    com.alibaba.testsdk.packet.generic.LoginRestRequestBody request) {
        com.alibaba.testsdk.packet.generic.LoginRestResponseBody response = this.userService.login(request);
        log.debug(response);
        return response;
    }

    @RequestMapping(value = "login/v1_1", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @io.swagger.annotations.ApiOperation(value = "\u767b\u5f55v1.1", notes = "\u767b\u5f55v1.1")
    public com.alibaba.testsdk.packet.generic.LoginRestResponseBody loginv1_1(
            @RequestBody
            @javax.validation.Valid
                    com.alibaba.testsdk.packet.generic.LoginRestRequestBody request) {
        com.alibaba.testsdk.packet.generic.LoginRestResponseBody response = this.userService.loginv1_1(request);
        log.debug(response);
        return response;
    }

    @RequestMapping(value = "autoLogin", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @io.swagger.annotations.ApiOperation(value = "\u81ea\u52a8\u767b\u5f55", notes = "\u81ea\u52a8\u767b\u5f55")
    public com.alibaba.testsdk.packet.generic.LoginRestResponseBody autoLogin(
            @RequestBody
            @javax.validation.Valid
                    AutoLoginRestRequestBody request) {
        com.alibaba.testsdk.packet.generic.LoginRestResponseBody response = this.userService.autoLogin(request);
        log.debug(response);
        return response;
    }

}
