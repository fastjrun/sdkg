package com.fastjrun.packet;

import java.io.Serializable;

public class BaseResponse<T extends BaseResponseBody> implements Serializable {
    
    private static final long serialVersionUID = -1612697269017956017L;

    private BaseResponseHead head;

    private T body;

    public BaseResponse() {

    }

    public BaseResponse(BaseResponseHead head, T body) {
        super();
        this.head = head;
        this.body = body;
    }

    public BaseResponseHead getHead() {
        return head;
    }

    public void setHead(BaseResponseHead head) {
        this.head = head;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(("BaseResponse" + " ["));
        sb.append("field [");
        sb.append("head");
        sb.append("=");
        sb.append(this.head);
        sb.append(",");
        sb.append("body");
        sb.append("=");
        sb.append(this.body);
        sb.append("]");
        sb.append("]");
        return sb.toString();
    }
}
