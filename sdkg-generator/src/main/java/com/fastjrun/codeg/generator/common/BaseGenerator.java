/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.generator.common;

import java.util.Calendar;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 生成
 */
@Getter
@Setter
public abstract class BaseGenerator {

    protected static String YEAR_CODEG_TIME = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    protected String packageNamePrefix;
    protected String author = "cuiyingfeng";
    protected String company = "快嘉";

    protected String notice = "注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的";
    protected boolean skipAuthor = false;
    protected boolean skipNotice = false;
    protected String yearCodegTime = "";
    protected boolean skipCopyright = false;

    public abstract void generate();
}
