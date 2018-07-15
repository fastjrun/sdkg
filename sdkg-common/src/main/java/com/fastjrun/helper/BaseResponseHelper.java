package com.fastjrun.helper;

import com.fastjrun.packet.*;

public class BaseResponseHelper {

    public static BaseDefaultResponse<EmptyResponseBody> getSuccessResult() {
        return getResult("0000","ok");
    }

    public static BaseDefaultResponse<EmptyResponseBody> getResult(String code, String msg) {
        BaseDefaultResponse<EmptyResponseBody> response = new BaseDefaultResponse<>();
        BaseDefaultResponseHead responseHead = new BaseDefaultResponseHead();
        responseHead.setCode(code);
        responseHead.setMsg(msg);
        response.setHead(responseHead);
        return response;
    }

}
