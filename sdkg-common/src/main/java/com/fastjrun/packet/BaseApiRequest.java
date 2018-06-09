package com.fastjrun.packet;

import java.io.Serializable;

public class BaseApiRequest<T extends BaseRequestBody> implements Serializable {
    
    private static final long serialVersionUID = 6799407636932332361L;

    private BaseApiRequestHead head;

    private T body;

    public BaseApiRequest() {

    }

    public BaseApiRequest(BaseApiRequestHead head, T body) {
        super();
        this.head = head;
        this.body = body;
    }

    public BaseApiRequestHead getHead() {
        return head;
    }

    public void setHead(BaseApiRequestHead head) {
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
        sb.append(("BaseApiRequest"+" ["));
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
