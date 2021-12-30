/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.common;

public class CommonController extends BaseCodeGenerableObject implements CodeGConstants {

    protected String name;
    protected String path;
    protected String version;
    protected String clientName;
    protected String remark;
    protected String[] tags;

    protected String serviceName;

    protected String serviceRef;

    protected CommonService service;
    protected ControllerType controllerType;

    private boolean _new = true;

    public boolean is_new() {
        return _new;
    }

    public void set_new(boolean _new) {
        this._new = _new;
    }

    public String getServiceRef() {
        return serviceRef;
    }

    public void setServiceRef(String serviceRef) {
        this.serviceRef = serviceRef;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ControllerType getControllerType() {
        return controllerType;
    }

    public void setControllerType(ControllerType controllerType) {
        this.controllerType = controllerType;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public CommonService getService() {
        return service;
    }

    public void setService(CommonService service) {
        this.service = service;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

}
