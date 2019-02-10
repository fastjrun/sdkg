package com.fastjrun.util;

/*
 * *
 *  * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *  *
 *  * @Copyright 2018 快嘉框架. All rights reserved.
 *
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class FastJsonObjectMapper extends ObjectMapper {

    public FastJsonObjectMapper() {
        this.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }
}
