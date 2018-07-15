package com.fastjrun.mock.web.controller;

import com.alibaba.testsdk.service.VersionServiceRestGeneric;
import com.fastjrun.web.controller.BaseController;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *
 * @author cuiyingfeng
 * @Copyright 2018 快嘉框架. All rights reserved.
 */
@RestController
@RequestMapping("/generic/version/")
@Api(value = "\u7248\u672c\u63a5\u53e3", tags = "\u57fa\u672c\u63a5\u53e3")
public class AppVersionGenericController
        extends BaseController {

    @Autowired
    private VersionServiceRestGeneric versionService;

    @RequestMapping(value = "latests", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @io.swagger.annotations.ApiOperation(value = "\u6700\u8fd1\u7248\u672c\u5217\u8868", notes = "\u6700\u8fd1\u7248\u672c\u5217\u8868")
    public com.alibaba.testsdk.packet.generic.VersionListResponseBody latests() {
        com.alibaba.testsdk.packet.generic.VersionListResponseBody response = this.versionService.latests();
        log.debug(response);
        return response;
    }

    @RequestMapping(value = "latests/v2/{appKey}/{accessTime}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @io.swagger.annotations.ApiOperation(value = "\u6700\u8fd1\u7248\u672c\u5217\u8868", notes = "\u6700\u8fd1\u7248\u672c\u5217\u8868")
    public com.alibaba.testsdk.packet.generic.VersionListResponseBody latestsv2(
            @PathVariable("appKey")
            @io.swagger.annotations.ApiParam(name = "appKey", value = "\u5206\u914d\u5ba2\u6237\u7aefkey", required = true)
                    String appKey,
            @PathVariable("accessTime")
            @io.swagger.annotations.ApiParam(name = "accessTime", value = "\u8bbf\u95ee\u65f6\u95f4\u6233", required = true)
                    Long accessTime,
            @RequestParam(name = "pageIndex", required = true)
            @io.swagger.annotations.ApiParam(name = "pageIndex", value = "\u9875\u7d22\u5f15", required = true)
                    Integer pageIndex,
            @RequestParam(name = "pageNum", required = true)
            @io.swagger.annotations.ApiParam(name = "pageNum", value = "\u6bcf\u9875\u8fd4\u56de\u591a\u5c11\u6761\u8bb0\u5f55", required = true)
                    Integer pageNum) {
        com.alibaba.testsdk.packet.generic.VersionListResponseBody response = this.versionService.latestsv2(appKey, accessTime, pageIndex, pageNum);
        log.debug(response);
        return response;
    }

}
