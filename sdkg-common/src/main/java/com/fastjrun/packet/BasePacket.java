package com.fastjrun.packet;

import javax.xml.bind.annotation.XmlElement;

public abstract class BasePacket<T extends BaseHead, V extends BaseBody> {

    private T head;

    private V body;

    public T getHead() {
        return head;
    }

    @XmlElement
    public void setHead(T head) {
        this.head = head;
    }

    public V getBody() {
        return body;
    }

    @XmlElement
    public void setBody(V body) {
        this.body = body;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        sb.append("[head");
        sb.append("=");
        sb.append(this.head);
        sb.append(",body=");
        sb.append(this.body);
        sb.append("]");
        return sb.toString();
    }
}
