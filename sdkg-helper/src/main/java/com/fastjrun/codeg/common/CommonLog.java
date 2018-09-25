package com.fastjrun.codeg.common;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

public class CommonLog {

    private Log log;

    public Log getLog() {
        if (this.log == null) {
            this.log = new SystemStreamLog();
        }

        return this.log;
    }

    public void setLog(Log log) {
        this.log = log;
    }
}
