/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.example.dto;

public abstract class BasePacket<T, V> {

    private T head;

    private V body;

    public T getHead() {
        return head;
    }

    public void setHead(T head) {
        this.head = head;
    }

    public V getBody() {
        return body;
    }

    public void setBody(V body) {
        this.body = body;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        sb.append("[head=").append(this.head);
        sb.append(",body=").append(this.body);
        sb.append("]");
        return sb.toString();
    }
}
