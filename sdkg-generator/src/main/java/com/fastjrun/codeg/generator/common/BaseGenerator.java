/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.generator.common;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 生成
 */
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
    private boolean client = true;

    public boolean isClient() {
        return client;
    }

    public void setClient(boolean client) {
        this.client = client;
    }

    public String getPackageNamePrefix() {
        return packageNamePrefix;
    }

    public void setPackageNamePrefix(String packageNamePrefix) {
        this.packageNamePrefix = packageNamePrefix;
    }

    public String getYearCodegTime() {
        return yearCodegTime;
    }

    public void setYearCodegTime(String yearCodegTime) {
        this.yearCodegTime = yearCodegTime;
    }

    public boolean isSkipCopyright() {
        return skipCopyright;
    }

    public void setSkipCopyright(boolean skipCopyright) {
        this.skipCopyright = skipCopyright;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public boolean isSkipAuthor() {
        return skipAuthor;
    }

    public void setSkipAuthor(boolean skipAuthor) {
        this.skipAuthor = skipAuthor;
    }

    public boolean isSkipNotice() {
        return skipNotice;
    }

    public void setSkipNotice(boolean skipNotice) {
        this.skipNotice = skipNotice;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public abstract void generate();
}
