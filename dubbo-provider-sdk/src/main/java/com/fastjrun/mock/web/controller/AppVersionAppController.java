package com.fastjrun.mock.web.controller;

import com.alibaba.testsdk.packet.app.VersionListResponseBody;
import com.alibaba.testsdk.service.VersionServiceRestApp;
import com.fastjrun.packet.BaseAppRequestHead;
import com.fastjrun.packet.BaseDefaultResponseBody;
import com.fastjrun.packet.BaseResponse;
import com.fastjrun.web.controller.BaseAppController;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping("/app/version/")
@Api(value = "\u7248\u672c\u63a5\u53e3", tags = "App\u63a5\u53e3")
public class AppVersionAppController
        extends BaseAppController {

    @Autowired
    private VersionServiceRestApp versionService;

    @RequestMapping(value = "check/{appKey}/{appVersion}/{appSource}/{deviceId}/{txTime}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @io.swagger.annotations.ApiOperation(value = "\u7248\u672c\u68c0\u6d4b", notes = "\u7248\u672c\u68c0\u6d4b")
    public BaseResponse<BaseDefaultResponseBody> check(
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
                    Long txTime) {
        BaseAppRequestHead requestHead = new BaseAppRequestHead();
        requestHead.setAppKey(appKey);
        requestHead.setAppVersion(appVersion);
        requestHead.setAppSource(appSource);
        requestHead.setDeviceId(deviceId);
        requestHead.setTxTime(txTime);
        this.processHead(requestHead);
        BaseResponse<BaseDefaultResponseBody> response = this.versionService.check();
        log.debug(response);
        return response;
    }

    @RequestMapping(value = "latests/{appKey}/{appVersion}/{appSource}/{deviceId}/{txTime}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @io.swagger.annotations.ApiOperation(value = "\u6700\u8fd1\u7248\u672c\u5217\u8868", notes = "\u6700\u8fd1\u7248\u672c\u5217\u8868")
    public BaseResponse<VersionListResponseBody> latests(
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
                    Long txTime) {
        BaseAppRequestHead requestHead = new BaseAppRequestHead();
        requestHead.setAppKey(appKey);
        requestHead.setAppVersion(appVersion);
        requestHead.setAppSource(appSource);
        requestHead.setDeviceId(deviceId);
        requestHead.setTxTime(txTime);
        this.processHead(requestHead);
        BaseResponse<VersionListResponseBody> response = this.versionService.latests();
        log.debug(response);
        return response;
    }

}
