package com.fastjrun.packet;

/*
 * *
 *  * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *  *
 *  * @author 崔莹峰
 *  * @Copyright 2018 快嘉框架. All rights reserved.
 *
 */

import java.util.List;

public abstract class BaseNoHeadListResponse<T extends BaseBody> {
    private List<T> body;

    public List<T> getBody() {
        return body;
    }

    public void setBody(List<T> body) {
        this.body = body;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        sb.append("[body=");
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
