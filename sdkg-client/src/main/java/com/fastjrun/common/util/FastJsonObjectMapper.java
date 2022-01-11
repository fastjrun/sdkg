/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class FastJsonObjectMapper extends ObjectMapper {

    public FastJsonObjectMapper() {
        this.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        this.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

        //设置全局的时间转化
        SimpleDateFormat smt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.setDateFormat(smt);
        this.setTimeZone(TimeZone.getTimeZone("GMT+8"));//解决时区差8小时问题
    }
}