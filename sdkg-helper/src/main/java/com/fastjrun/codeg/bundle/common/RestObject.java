package com.fastjrun.codeg.bundle.common;

import java.util.Map;

public class RestObject {
    private String name;
    private String parent;
    private String _new;
    private String _class;

    private Map<String, RestObject> objects;

    private Map<String, RestField> fields;

    private Map<String, RestObject> lists;

    public String get_new() {
        return _new;
    }

    public void set_new(String _new) {
        this._new = _new;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
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

    public Map<String, RestObject> getObjects() {
        return objects;
    }

    public void setObjects(Map<String, RestObject> objects) {
        this.objects = objects;
    }

    public Map<String, RestField> getFields() {
        return fields;
    }

    public void setFields(Map<String, RestField> fields) {
        this.fields = fields;
    }

    public Map<String, RestObject> getLists() {
        return lists;
    }

    public void setLists(Map<String, RestObject> lists) {
        this.lists = lists;
    }


}
