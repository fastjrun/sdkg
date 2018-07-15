package com.fastjrun.codeg.bundle.common;

public class CommonController {

    protected String name;
    protected String path;
    protected String clientName;
    protected String remark;
    protected String tags;
    protected String clientParent;

    protected String serviceName;

    protected CommonService service;
    protected ControllerType controllerType;

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

    public String getClientParent() {
        return clientParent;
    }

    public void setClientParent(String clientParent) {
        this.clientParent = clientParent;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public enum ControllerType {
        ControllerType_APP("App"), ControllerType_API("Api"), ControllerType_GENERIC("Generic"), ControllerType_RPC(
                "PRC");
        public String controllerType;

        ControllerType(String controllerType) {
            this.controllerType = controllerType;
        }

    }

}