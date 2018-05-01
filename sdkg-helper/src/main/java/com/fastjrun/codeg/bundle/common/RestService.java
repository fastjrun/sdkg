package com.fastjrun.codeg.bundle.common;

import java.util.List;

public class RestService {
    
    private String name;
    
    private String _class;
    
    private List<RestServiceMethod> methods;

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

    public List<RestServiceMethod> getMethods() {
        return methods;
    }

    public void setMethods(List<RestServiceMethod> methods) {
        this.methods = methods;
    }

}
