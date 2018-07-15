package com.fastjrun.mock.web.controller;

import com.alibaba.testsdk.packet.api.AutoLoginRestRequestBody;
import com.alibaba.testsdk.packet.api.RegistserRestRequestBody;
import com.alibaba.testsdk.service.UserServiceRestApi;
import com.fastjrun.packet.BaseApiRequest;
import com.fastjrun.packet.BaseApiRequestHead;
import com.fastjrun.packet.BaseDefaultResponseBody;
import com.fastjrun.packet.BaseResponse;
import com.fastjrun.web.controller.BaseApiController;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *
 * @author cuiyingfeng
 * @Copyright 2018 快嘉框架. All rights reserved.
 */
@RestController
@RequestMapping("/api/user/")
@Api(value = "\u7528\u6237\u7ba1\u7406\u63a5\u53e3", tags = "\u7b2c\u4e09\u65b9\u63a5\u53e3")
public class UserApiController
        extends BaseApiController {

    @Autowired
    private UserServiceRestApi userService;

    @RequestMapping(value = "register/{accessKey}/{txTime}/{md5Hash}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @io.swagger.annotations.ApiOperation(value = "\u6ce8\u518c", notes = "\u6ce8\u518c")
    public BaseResponse<BaseDefaultResponseBody> register(
            @PathVariable("accessKey")
            @io.swagger.annotations.ApiParam(name = "accessKey", value = "\u63a5\u5165\u5ba2\u6237\u7aef\u7684accessKey", required = true)
                    String accessKey,
            @PathVariable("txTime")
            @io.swagger.annotations.ApiParam(name = "txTime", value = "\u63a5\u53e3\u8bf7\u6c42\u65f6\u95f4\u6233", required = true)
                    Long txTime,
            @PathVariable("md5Hash")
            @io.swagger.annotations.ApiParam(name = "md5Hash", value = "md5Hash", required = true)
                    String md5Hash,
            @RequestBody
            @javax.validation.Valid
                    RegistserRestRequestBody requestBody) {
        BaseApiRequest<RegistserRestRequestBody> request = new BaseApiRequest<RegistserRestRequestBody>();
        BaseApiRequestHead requestHead = new BaseApiRequestHead();
        requestHead.setAccessKey(accessKey);
        requestHead.setTxTime(txTime);
        requestHead.setMd5Hash(md5Hash);
        this.processHead(requestHead);
        request.setHead(requestHead);
        request.setBody(requestBody);
        BaseResponse<BaseDefaultResponseBody> response = this.userService.register(request);
        log.debug(response);
        return response;
    }

    @RequestMapping(value = "login/{accessKey}/{txTime}/{md5Hash}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @io.swagger.annotations.ApiOperation(value = "\u767b\u5f55", notes = "\u767b\u5f55")
    public BaseResponse<com.alibaba.testsdk.packet.api.LoginRestResponseBody> login(
            @PathVariable("accessKey")
            @io.swagger.annotations.ApiParam(name = "accessKey", value = "\u63a5\u5165\u5ba2\u6237\u7aef\u7684accessKey", required = true)
                    String accessKey,
            @PathVariable("txTime")
            @io.swagger.annotations.ApiParam(name = "txTime", value = "\u63a5\u53e3\u8bf7\u6c42\u65f6\u95f4\u6233", required = true)
                    Long txTime,
            @PathVariable("md5Hash")
            @io.swagger.annotations.ApiParam(name = "md5Hash", value = "md5Hash", required = true)
                    String md5Hash,
            @RequestBody
            @javax.validation.Valid
                    com.alibaba.testsdk.packet.api.LoginRestRequestBody requestBody) {
        BaseApiRequest<com.alibaba.testsdk.packet.api.LoginRestRequestBody> request = new BaseApiRequest<com.alibaba.testsdk.packet.api.LoginRestRequestBody>();
        BaseApiRequestHead requestHead = new BaseApiRequestHead();
        requestHead.setAccessKey(accessKey);
        requestHead.setTxTime(txTime);
        requestHead.setMd5Hash(md5Hash);
        this.processHead(requestHead);
        request.setHead(requestHead);
        request.setBody(requestBody);
        BaseResponse<com.alibaba.testsdk.packet.api.LoginRestResponseBody> response = this.userService.login(request);
        log.debug(response);
        return response;
    }

    @RequestMapping(value = "login/v1_1/{accessKey}/{txTime}/{md5Hash}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @io.swagger.annotations.ApiOperation(value = "\u767b\u5f55v1.1", notes = "\u767b\u5f55v1.1")
    public BaseResponse<com.alibaba.testsdk.packet.api.LoginRestResponseBody> loginv1_1(
            @PathVariable("accessKey")
            @io.swagger.annotations.ApiParam(name = "accessKey", value = "\u63a5\u5165\u5ba2\u6237\u7aef\u7684accessKey", required = true)
                    String accessKey,
            @PathVariable("txTime")
            @io.swagger.annotations.ApiParam(name = "txTime", value = "\u63a5\u53e3\u8bf7\u6c42\u65f6\u95f4\u6233", required = true)
                    Long txTime,
            @PathVariable("md5Hash")
            @io.swagger.annotations.ApiParam(name = "md5Hash", value = "md5Hash", required = true)
                    String md5Hash,
            @RequestBody
            @javax.validation.Valid
                    com.alibaba.testsdk.packet.api.LoginRestRequestBody requestBody) {
        BaseApiRequest<com.alibaba.testsdk.packet.api.LoginRestRequestBody> request = new BaseApiRequest<com.alibaba.testsdk.packet.api.LoginRestRequestBody>();
        BaseApiRequestHead requestHead = new BaseApiRequestHead();
        requestHead.setAccessKey(accessKey);
        requestHead.setTxTime(txTime);
        requestHead.setMd5Hash(md5Hash);
        this.processHead(requestHead);
        request.setHead(requestHead);
        request.setBody(requestBody);
        BaseResponse<com.alibaba.testsdk.packet.api.LoginRestResponseBody> response = this.userService.loginv1_1(request);
        log.debug(response);
        return response;
    }

    @RequestMapping(value = "autoLogin/{accessKey}/{txTime}/{md5Hash}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @io.swagger.annotations.ApiOperation(value = "\u81ea\u52a8\u767b\u5f55", notes = "\u81ea\u52a8\u767b\u5f55")
    public BaseResponse<com.alibaba.testsdk.packet.api.LoginRestResponseBody> autoLogin(
            @PathVariable("accessKey")
            @io.swagger.annotations.ApiParam(name = "accessKey", value = "\u63a5\u5165\u5ba2\u6237\u7aef\u7684accessKey", required = true)
                    String accessKey,
            @PathVariable("txTime")
            @io.swagger.annotations.ApiParam(name = "txTime", value = "\u63a5\u53e3\u8bf7\u6c42\u65f6\u95f4\u6233", required = true)
                    Long txTime,
            @PathVariable("md5Hash")
            @io.swagger.annotations.ApiParam(name = "md5Hash", value = "md5Hash", required = true)
                    String md5Hash,
            @RequestBody
            @javax.validation.Valid
                    AutoLoginRestRequestBody requestBody) {
        BaseApiRequest<AutoLoginRestRequestBody> request = new BaseApiRequest<AutoLoginRestRequestBody>();
        BaseApiRequestHead requestHead = new BaseApiRequestHead();
        requestHead.setAccessKey(accessKey);
        requestHead.setTxTime(txTime);
        requestHead.setMd5Hash(md5Hash);
        this.processHead(requestHead);
        request.setHead(requestHead);
        request.setBody(requestBody);
        BaseResponse<com.alibaba.testsdk.packet.api.LoginRestResponseBody> response = this.userService.autoLogin(request);
        log.debug(response);
        return response;
    }

}
