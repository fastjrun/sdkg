package com.fastjrun.codeg.generator;

import java.util.Properties;

public abstract class BaseHTTPGenerator extends BaseControllerGenerator {

    @Override
    public void generate() {
        this.genreateControllerPath();
        if (!this.isApi()) {
            if (this.isClient()) {
                this.processClient();
                this.processClientTest();
                this.clientTestParam = new Properties();
            } else {
                this.processController();
            }
        }
    }

}