package com.fastjrun.packet;

/*
 * *
 *  * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *  *
 *  * @author 崔莹峰
 *  * @Copyright 2018 快嘉框架. All rights reserved.
 *
 */

public abstract class BaseNoHeadResponse<T extends BaseBody> {
    private T body;

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        sb.append("[body=");
        sb.append(this.body);
        sb.append("]");
        return sb.toString();
    }
}
