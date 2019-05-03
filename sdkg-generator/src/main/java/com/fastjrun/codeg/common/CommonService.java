/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.common;

import java.util.List;

public class CommonService {

    private String name;
    private String _class;
    private List<CommonMethod> methods;

    public List<CommonMethod> getMethods() {
        return methods;
    }

    public void setMethods(List<CommonMethod> methods) {
        this.methods = methods;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String get_class() {
        return _class;
    }

    public void set_class(String _class) {
        this._class = _class;
    }
}
