package com.fastjrun.packet;

import java.io.Serializable;

public class BaseAppRequest<T extends BaseRequestBody> implements Serializable {

    private static final long serialVersionUID = 7834907964983017562L;

    private BaseAppRequestHead head;

    private T body;

    public BaseAppRequest() {

    }

    public BaseAppRequest(BaseAppRequestHead head, T body) {
        super();
        this.head = head;
        this.body = body;
    }

    public BaseAppRequestHead getHead() {
        return head;
    }

    public void setHead(BaseAppRequestHead head) {
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
        sb.append(("BaseAppRequest"+" ["));
        sb.append("head");
        sb.append("=");
        sb.append(this.head);
        sb.append(",");
        sb.append("body");
        sb.append("=");
        sb.append(this.body);
        sb.append("]");
        return sb.toString();
    }
}
