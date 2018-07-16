package com.fastjrun.codeg.bundle.common;

import java.util.List;

public class CommonMethod {
    private String name;

    private String remark;

    private List<PacketField> parameters;

    private List<PacketField> pathVariables;

    private List<PacketField> headVariables;

    private String version;

    private String path;

    private String httpMethod;

    private String reqType;

    private String resType;

    private PacketObject request;

    private PacketObject response;

    public String getReqType() {
        return reqType;
    }

    public void setReqType(String reqType) {
        this.reqType = reqType;
    }

    public String getResType() {
        return resType;
    }

    public void setResType(String resType) {
        this.resType = resType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public PacketObject getRequest() {
        return request;
    }

    public void setRequest(PacketObject request) {
        this.request = request;
    }

    public PacketObject getResponse() {
        return response;
    }

    public void setResponse(PacketObject response) {
        this.response = response;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public List<PacketField> getParameters() {
        return parameters;
    }

    public void setParameters(List<PacketField> parameters) {
        this.parameters = parameters;
    }

    public List<PacketField> getPathVariables() {
        return pathVariables;
    }

    public void setPathVariables(List<PacketField> pathVariables) {
        this.pathVariables = pathVariables;
    }

    public List<PacketField> getHeadVariables() {
        return headVariables;
    }

    public void setHeadVariables(List<PacketField> headVariables) {
        this.headVariables = headVariables;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
