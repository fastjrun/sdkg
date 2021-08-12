/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.apibase.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public abstract class BaseListPacket<T, V> {

    private T head;

    private List<V> body;

    public T getHead() {
        return head;
    }

    @XmlElement
    public void setHead(T head) {
        this.head = head;
    }

    public List<V> getBody() {
        return body;
    }

    @XmlElement
    public void setBody(List<V> body) {
        this.body = body;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        sb.append("[head=").append(this.head);
        sb.append(",body=");
        if (this.body != null) {
            sb.append("[");
            for (int i = 0; i < this.body.size(); i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(this.body.get(i));
            }
            sb.append("]");
        } else {
            sb.append(this.body);
        }
        sb.append("]");
        return sb.toString();
    }
}
