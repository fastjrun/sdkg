package com.fastjrun.mock.web.controller;

import com.alibaba.testsdk.packet.api.ArticleListResponseBody;
import com.alibaba.testsdk.service.ArticleServiceRestApi;
import com.fastjrun.packet.BaseApiRequestHead;
import com.fastjrun.packet.BaseDefaultResponseBody;
import com.fastjrun.packet.BaseResponse;
import com.fastjrun.web.controller.BaseApiController;
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
@RequestMapping("/api/article/")
@Api(value = "\u7248\u672c\u63a5\u53e3", tags = "\u7b2c\u4e09\u65b9\u63a5\u53e3")
public class ArticleApiController
        extends BaseApiController {

    @Autowired
    private ArticleServiceRestApi articleService;

    @RequestMapping(value = "check/{accessKey}/{txTime}/{md5Hash}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @io.swagger.annotations.ApiOperation(value = "\u662f\u5426\u6709\u65b0\u6587\u7ae0\u68c0\u6d4b", notes = "\u662f\u5426\u6709\u65b0\u6587\u7ae0\u68c0\u6d4b")
    public BaseResponse<BaseDefaultResponseBody> check(
            @PathVariable("accessKey")
            @io.swagger.annotations.ApiParam(name = "accessKey", value = "\u63a5\u5165\u5ba2\u6237\u7aef\u7684accessKey", required = true)
                    String accessKey,
            @PathVariable("txTime")
            @io.swagger.annotations.ApiParam(name = "txTime", value = "\u63a5\u53e3\u8bf7\u6c42\u65f6\u95f4\u6233", required = true)
                    Long txTime,
            @PathVariable("md5Hash")
            @io.swagger.annotations.ApiParam(name = "md5Hash", value = "md5Hash", required = true)
                    String md5Hash) {
        BaseApiRequestHead requestHead = new BaseApiRequestHead();
        requestHead.setAccessKey(accessKey);
        requestHead.setTxTime(txTime);
        requestHead.setMd5Hash(md5Hash);
        this.processHead(requestHead);
        BaseResponse<BaseDefaultResponseBody> response = this.articleService.check();
        log.debug(response);
        return response;
    }

    @RequestMapping(value = "latests/{accessKey}/{txTime}/{md5Hash}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @io.swagger.annotations.ApiOperation(value = "\u6700\u8fd1\u7248\u672c\u5217\u8868", notes = "\u6700\u8fd1\u7248\u672c\u5217\u8868")
    public BaseResponse<ArticleListResponseBody> latests(
            @PathVariable("accessKey")
            @io.swagger.annotations.ApiParam(name = "accessKey", value = "\u63a5\u5165\u5ba2\u6237\u7aef\u7684accessKey", required = true)
                    String accessKey,
            @PathVariable("txTime")
            @io.swagger.annotations.ApiParam(name = "txTime", value = "\u63a5\u53e3\u8bf7\u6c42\u65f6\u95f4\u6233", required = true)
                    Long txTime,
            @PathVariable("md5Hash")
            @io.swagger.annotations.ApiParam(name = "md5Hash", value = "md5Hash", required = true)
                    String md5Hash) {
        BaseApiRequestHead requestHead = new BaseApiRequestHead();
        requestHead.setAccessKey(accessKey);
        requestHead.setTxTime(txTime);
        requestHead.setMd5Hash(md5Hash);
        this.processHead(requestHead);
        BaseResponse<ArticleListResponseBody> response = this.articleService.latests();
        log.debug(response);
        return response;
    }

}
