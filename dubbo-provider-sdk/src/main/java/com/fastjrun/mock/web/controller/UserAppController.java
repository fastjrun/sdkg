package com.fastjrun.mock.web.controller;

import com.alibaba.testsdk.packet.app.AutoLoginRestRequestBody;
import com.alibaba.testsdk.packet.app.RegistserRestRequestBody;
import com.alibaba.testsdk.service.UserServiceRestApp;
import com.fastjrun.packet.BaseAppRequest;
import com.fastjrun.packet.BaseAppRequestHead;
import com.fastjrun.packet.BaseDefaultResponseBody;
import com.fastjrun.packet.BaseResponse;
import com.fastjrun.web.controller.BaseAppController;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *
 * @author cuiyingfeng
 * @Copyright 2018 快嘉框架. All rights reserved.
 */
@RestController
@RequestMapping("/app/user/")
@Api(value = "\u7528\u6237\u7ba1\u7406\u63a5\u53e3", tags = "App\u63a5\u53e3")
public class UserAppController
        extends BaseAppController {

    @Autowired
    private UserServiceRestApp userService;

    @RequestMapping(value = "register/v2/{appKey}/{appVersion}/{appSource}/{deviceId}/{txTime}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @io.swagger.annotations.ApiOperation(value = "\u6ce8\u518c", notes = "\u6ce8\u518c")
    public BaseResponse<BaseDefaultResponseBody> registerv2(
            @PathVariable("appKey")
            @io.swagger.annotations.ApiParam(name = "appKey", value = "app\u5206\u914d\u7684key", required = true)
                    String appKey,
            @PathVariable("appVersion")
            @io.swagger.annotations.ApiParam(name = "appVersion", value = "\u5f53\u524dapp\u7248\u672c\u53f7", required = true)
                    String appVersion,
            @PathVariable("appSource")
            @io.swagger.annotations.ApiParam(name = "appSource", value = "\u5f53\u524dapp\u6e20\u9053\uff1aios,android", required = true)
                    String appSource,
            @PathVariable("deviceId")
            @io.swagger.annotations.ApiParam(name = "deviceId", value = "\u8bbe\u5907Id", required = true)
                    String deviceId,
            @PathVariable("txTime")
            @io.swagger.annotations.ApiParam(name = "txTime", value = "\u63a5\u53e3\u8bf7\u6c42\u65f6\u95f4\u6233", required = true)
                    Long txTime,
            @RequestBody
            @javax.validation.Valid
                    RegistserRestRequestBody requestBody) {
        BaseAppRequest<RegistserRestRequestBody> request = new BaseAppRequest<RegistserRestRequestBody>();
        BaseAppRequestHead requestHead = new BaseAppRequestHead();
        requestHead.setAppKey(appKey);
        requestHead.setAppVersion(appVersion);
        requestHead.setAppSource(appSource);
        requestHead.setDeviceId(deviceId);
        requestHead.setTxTime(txTime);
        this.processHead(requestHead);
        request.setHead(requestHead);
        request.setBody(requestBody);
        BaseResponse<BaseDefaultResponseBody> response = this.userService.registerv2(request);
        log.debug(response);
        return response;
    }

    @RequestMapping(value = "login/{appKey}/{appVersion}/{appSource}/{deviceId}/{txTime}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @io.swagger.annotations.ApiOperation(value = "\u767b\u5f55", notes = "\u767b\u5f55")
    public BaseResponse<com.alibaba.testsdk.packet.app.LoginRestResponseBody> login(
            @PathVariable("appKey")
            @io.swagger.annotations.ApiParam(name = "appKey", value = "app\u5206\u914d\u7684key", required = true)
                    String appKey,
            @PathVariable("appVersion")
            @io.swagger.annotations.ApiParam(name = "appVersion", value = "\u5f53\u524dapp\u7248\u672c\u53f7", required = true)
                    String appVersion,
            @PathVariable("appSource")
            @io.swagger.annotations.ApiParam(name = "appSource", value = "\u5f53\u524dapp\u6e20\u9053\uff1aios,android", required = true)
                    String appSource,
            @PathVariable("deviceId")
            @io.swagger.annotations.ApiParam(name = "deviceId", value = "\u8bbe\u5907Id", required = true)
                    String deviceId,
            @PathVariable("txTime")
            @io.swagger.annotations.ApiParam(name = "txTime", value = "\u63a5\u53e3\u8bf7\u6c42\u65f6\u95f4\u6233", required = true)
                    Long txTime,
            @RequestBody
            @javax.validation.Valid
                    com.alibaba.testsdk.packet.app.LoginRestRequestBody requestBody) {
        BaseAppRequest<com.alibaba.testsdk.packet.app.LoginRestRequestBody> request = new BaseAppRequest<com.alibaba.testsdk.packet.app.LoginRestRequestBody>();
        BaseAppRequestHead requestHead = new BaseAppRequestHead();
        requestHead.setAppKey(appKey);
        requestHead.setAppVersion(appVersion);
        requestHead.setAppSource(appSource);
        requestHead.setDeviceId(deviceId);
        requestHead.setTxTime(txTime);
        this.processHead(requestHead);
        request.setHead(requestHead);
        request.setBody(requestBody);
        BaseResponse<com.alibaba.testsdk.packet.app.LoginRestResponseBody> response = this.userService.login(request);
        log.debug(response);
        return response;
    }

    @RequestMapping(value = "login/v1_1/{appKey}/{appVersion}/{appSource}/{deviceId}/{txTime}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @io.swagger.annotations.ApiOperation(value = "\u767b\u5f55v1.1", notes = "\u767b\u5f55v1.1")
    public BaseResponse<com.alibaba.testsdk.packet.app.LoginRestResponseBody> loginv1_1(
            @PathVariable("appKey")
            @io.swagger.annotations.ApiParam(name = "appKey", value = "app\u5206\u914d\u7684key", required = true)
                    String appKey,
            @PathVariable("appVersion")
            @io.swagger.annotations.ApiParam(name = "appVersion", value = "\u5f53\u524dapp\u7248\u672c\u53f7", required = true)
                    String appVersion,
            @PathVariable("appSource")
            @io.swagger.annotations.ApiParam(name = "appSource", value = "\u5f53\u524dapp\u6e20\u9053\uff1aios,android", required = true)
                    String appSource,
            @PathVariable("deviceId")
            @io.swagger.annotations.ApiParam(name = "deviceId", value = "\u8bbe\u5907Id", required = true)
                    String deviceId,
            @PathVariable("txTime")
            @io.swagger.annotations.ApiParam(name = "txTime", value = "\u63a5\u53e3\u8bf7\u6c42\u65f6\u95f4\u6233", required = true)
                    Long txTime,
            @RequestBody
            @javax.validation.Valid
                    com.alibaba.testsdk.packet.app.LoginRestRequestBody requestBody) {
        BaseAppRequest<com.alibaba.testsdk.packet.app.LoginRestRequestBody> request = new BaseAppRequest<com.alibaba.testsdk.packet.app.LoginRestRequestBody>();
        BaseAppRequestHead requestHead = new BaseAppRequestHead();
        requestHead.setAppKey(appKey);
        requestHead.setAppVersion(appVersion);
        requestHead.setAppSource(appSource);
        requestHead.setDeviceId(deviceId);
        requestHead.setTxTime(txTime);
        this.processHead(requestHead);
        request.setHead(requestHead);
        request.setBody(requestBody);
        BaseResponse<com.alibaba.testsdk.packet.app.LoginRestResponseBody> response = this.userService.loginv1_1(request);
        log.debug(response);
        return response;
    }

    @RequestMapping(value = "autoLogin/{appKey}/{appVersion}/{appSource}/{deviceId}/{txTime}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @io.swagger.annotations.ApiOperation(value = "\u81ea\u52a8\u767b\u5f55", notes = "\u81ea\u52a8\u767b\u5f55")
    public BaseResponse<com.alibaba.testsdk.packet.app.LoginRestResponseBody> autoLogin(
            @PathVariable("appKey")
            @io.swagger.annotations.ApiParam(name = "appKey", value = "app\u5206\u914d\u7684key", required = true)
                    String appKey,
            @PathVariable("appVersion")
            @io.swagger.annotations.ApiParam(name = "appVersion", value = "\u5f53\u524dapp\u7248\u672c\u53f7", required = true)
                    String appVersion,
            @PathVariable("appSource")
            @io.swagger.annotations.ApiParam(name = "appSource", value = "\u5f53\u524dapp\u6e20\u9053\uff1aios,android", required = true)
                    String appSource,
            @PathVariable("deviceId")
            @io.swagger.annotations.ApiParam(name = "deviceId", value = "\u8bbe\u5907Id", required = true)
                    String deviceId,
            @PathVariable("txTime")
            @io.swagger.annotations.ApiParam(name = "txTime", value = "\u63a5\u53e3\u8bf7\u6c42\u65f6\u95f4\u6233", required = true)
                    Long txTime,
            @RequestBody
            @javax.validation.Valid
                    AutoLoginRestRequestBody requestBody) {
        BaseAppRequest<AutoLoginRestRequestBody> request = new BaseAppRequest<AutoLoginRestRequestBody>();
        BaseAppRequestHead requestHead = new BaseAppRequestHead();
        requestHead.setAppKey(appKey);
        requestHead.setAppVersion(appVersion);
        requestHead.setAppSource(appSource);
        requestHead.setDeviceId(deviceId);
        requestHead.setTxTime(txTime);
        this.processHead(requestHead);
        request.setHead(requestHead);
        request.setBody(requestBody);
        BaseResponse<com.alibaba.testsdk.packet.app.LoginRestResponseBody> response = this.userService.autoLogin(request);
        log.debug(response);
        return response;
    }

}
