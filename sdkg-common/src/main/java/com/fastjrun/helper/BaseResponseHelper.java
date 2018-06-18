package com.fastjrun.helper;

import com.fastjrun.packet.BaseDefaultResponseBody;
import com.fastjrun.packet.BaseResponse;
import com.fastjrun.packet.BaseResponseHead;

public class BaseResponseHelper {

    public static BaseResponse<BaseDefaultResponseBody> getSuccessResult() {
        BaseResponse<BaseDefaultResponseBody> response = new BaseResponse<BaseDefaultResponseBody>();
        BaseResponseHead responseHead = new BaseResponseHead();
        responseHead.setCode("0000");
        responseHead.setMsg("ok");
        response.setHead(responseHead);
        return response;
    }

    public static BaseResponse<BaseDefaultResponseBody> getFailResult(String code, String msg) {
        BaseResponse<BaseDefaultResponseBody> response = new BaseResponse<BaseDefaultResponseBody>();
        BaseResponseHead responseHead = new BaseResponseHead();
        responseHead.setCode(code);
        responseHead.setMsg(msg);
        response.setHead(responseHead);
        return response;
    }

}
