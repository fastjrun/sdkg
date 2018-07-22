package com.fastjrun.helper;

import com.fastjrun.packet.DefaultResponse;
import com.fastjrun.packet.DefaultResponseHead;
import com.fastjrun.packet.EmptyResponseBody;

public class BaseResponseHelper {

    public static DefaultResponse<EmptyResponseBody> getSuccessResult() {
        return getResult("0000", "ok");
    }

    public static DefaultResponse<EmptyResponseBody> getResult(String code, String msg) {
        DefaultResponse<EmptyResponseBody> response = new DefaultResponse<>();
        DefaultResponseHead responseHead = new DefaultResponseHead();
        responseHead.setCode(code);
        responseHead.setMsg(msg);
        response.setHead(responseHead);
        return response;
    }

}
