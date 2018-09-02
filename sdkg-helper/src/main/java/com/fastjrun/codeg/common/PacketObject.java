package com.fastjrun.codeg.common;

import java.util.Map;

public class PacketObject {
    private String name;
    private String parent;
    private String _new;

    private String _class;

    private Map<String, PacketObject> objects;

    private Map<String, PacketField> fields;

    private Map<String, PacketObject> lists;

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

    public Map<String, PacketObject> getObjects() {
        return objects;
    }

    public void setObjects(Map<String, PacketObject> objects) {
        this.objects = objects;
    }

    public Map<String, PacketField> getFields() {
        return fields;
    }

    public void setFields(Map<String, PacketField> fields) {
        this.fields = fields;
    }

    public Map<String, PacketObject> getLists() {
        return lists;
    }

    public void setLists(Map<String, PacketObject> lists) {
        this.lists = lists;
    }

}
