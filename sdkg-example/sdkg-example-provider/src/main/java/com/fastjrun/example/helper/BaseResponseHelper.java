/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.example.helper;

import com.fastjrun.common.BaseCodeMsgConstants;
import com.fastjrun.common.BaseException;
import com.fastjrun.example.dto.DefaultListResponse;
import com.fastjrun.example.dto.DefaultResponse;
import com.fastjrun.example.dto.DefaultResponseHead;

public class BaseResponseHelper {

    public static DefaultResponse getSuccessResult() {
        DefaultResponse response = getResult();
        return response;

    }

    public static DefaultListResponse getSuccessResultList() {

        DefaultListResponse response = getResultList();
        return response;
    }

    public static <T> DefaultResponse<T> getResult() {
        DefaultResponse<T> response = new DefaultResponse<>();
        DefaultResponseHead responseHead = new DefaultResponseHead();
        responseHead.setCode(BaseCodeMsgConstants.BaseCodeMsg.OK.getCode());
        responseHead.setMsg(BaseCodeMsgConstants.BaseCodeMsg.OK.getMsg());
        response.setHead(responseHead);
        return response;
    }

    public static <T> DefaultListResponse<T> getResultList() {
        DefaultListResponse<T> response = new DefaultListResponse<>();
        DefaultResponseHead responseHead = new DefaultResponseHead();
        responseHead.setCode(BaseCodeMsgConstants.BaseCodeMsg.OK.getCode());
        responseHead.setMsg(BaseCodeMsgConstants.BaseCodeMsg.OK.getMsg());
        response.setHead(responseHead);
        return response;
    }

    public static DefaultResponse getFailResult(BaseException codeException) {
        DefaultResponse response = new DefaultResponse();
        DefaultResponseHead responseHead = new DefaultResponseHead();
        responseHead.setCode(codeException.getCode());
        responseHead.setMsg(codeException.getMsg());
        response.setHead(responseHead);
        return response;
    }

    public static DefaultResponse getFailResult(String code, String msg) {
        DefaultResponse response = new DefaultResponse();
        DefaultResponseHead responseHead = new DefaultResponseHead();
        responseHead.setCode(code);
        responseHead.setMsg(msg);
        response.setHead(responseHead);
        return response;
    }

    public static DefaultListResponse getFailResultList(BaseException codeException) {
        DefaultListResponse response = new DefaultListResponse();
        DefaultResponseHead responseHead = new DefaultResponseHead();
        responseHead.setCode(codeException.getCode());
        responseHead.setMsg(codeException.getMsg());
        response.setHead(responseHead);
        return response;
    }

}
