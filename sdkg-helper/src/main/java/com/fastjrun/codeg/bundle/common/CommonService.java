package com.fastjrun.codeg.bundle.common;

import java.util.List;

public class CommonService {

    private String name;
    private String _class;
    private List<CommonMethod> methods;
    private ServiceType serviceType;

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

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

    public enum ServiceType {
        ServiceType_Controller("1"), ServiceType_RPC("2"), ServiceType_Task("3"), ServiceType_Consumer("4");
        public String serviceType;

        ServiceType(String serviceType) {
            this.serviceType = serviceType;
        }

    }

}
