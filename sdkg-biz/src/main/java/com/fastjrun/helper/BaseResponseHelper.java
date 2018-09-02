package com.fastjrun.helper;

import com.fastjrun.common.BaseCodeMsgConstants;
import com.fastjrun.common.CodeException;
import com.fastjrun.packet.BaseBody;
import com.fastjrun.packet.DefaultListResponse;
import com.fastjrun.packet.DefaultResponse;
import com.fastjrun.packet.DefaultResponseHead;
import com.fastjrun.packet.EmptyBody;

public class BaseResponseHelper {

    public static DefaultResponse<EmptyBody> getSuccessResult() {
        DefaultResponse<EmptyBody> response = getResult();
        return response;

    }

    public static DefaultListResponse<EmptyBody> getSuccessResultList() {

        DefaultListResponse<EmptyBody> response = getResultList();
        return response;
    }

    public static <T extends BaseBody> DefaultResponse<T> getResult() {
        DefaultResponse<T> response = new DefaultResponse<>();
        DefaultResponseHead responseHead = new DefaultResponseHead();
        responseHead.setCode(BaseCodeMsgConstants.BaseCodeMsg.OK.getCode());
        responseHead.setMsg(BaseCodeMsgConstants.BaseCodeMsg.OK.getMsg());
        response.setHead(responseHead);
        return response;
    }

    public static <T extends BaseBody> DefaultListResponse<T> getResultList() {
        DefaultListResponse<T> response = new DefaultListResponse<>();
        DefaultResponseHead responseHead = new DefaultResponseHead();
        responseHead.setCode(BaseCodeMsgConstants.BaseCodeMsg.OK.getCode());
        responseHead.setMsg(BaseCodeMsgConstants.BaseCodeMsg.OK.getMsg());
        response.setHead(responseHead);
        return response;
    }

    public static DefaultResponse<EmptyBody> getResult(CodeException codeException) {
        DefaultResponse<EmptyBody> response = new DefaultResponse<>();
        DefaultResponseHead responseHead = new DefaultResponseHead();
        responseHead.setCode(codeException.getCode());
        responseHead.setMsg(codeException.getMsg());
        response.setHead(responseHead);
        return response;
    }

    public static DefaultListResponse<EmptyBody> getResultList(CodeException codeException) {
        DefaultListResponse<EmptyBody> response = new DefaultListResponse<>();
        DefaultResponseHead responseHead = new DefaultResponseHead();
        responseHead.setCode(codeException.getCode());
        responseHead.setMsg(codeException.getMsg());
        response.setHead(responseHead);
        return response;
    }

}
