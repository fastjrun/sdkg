package com.fastjrun.packet;

/*
 * *
 *  * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *  *
 *  * @author 崔莹峰
 *  * @Copyright 2018 快嘉框架. All rights reserved.
 *
 */

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class BaseRequestBody<T extends BaseResponseBody> extends BaseBody {
    @JsonIgnore
    public abstract Class<T> getResponseBodyClass();
}
