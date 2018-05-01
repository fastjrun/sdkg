package com.fastjrun.codeg.bundle.common;

public class RestPacket {

    private String _class;

    private String remark;

    private String parent;

    private RestObject restObject;

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public RestObject getRestObject() {
        return restObject;
    }

    public void setRestObject(RestObject restObject) {
        this.restObject = restObject;
    }

    public String get_class() {
        return _class;
    }

    public void set_class(String _class) {
        this._class = _class;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
