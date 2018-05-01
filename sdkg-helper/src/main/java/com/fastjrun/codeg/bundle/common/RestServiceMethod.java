package com.fastjrun.codeg.bundle.common;

import java.util.List;
import java.util.Map;

public class RestServiceMethod {
    private String name;

    private String version;

    private String path;

    private String httpMethod;

    private String remark;

    private Map<String, RestField> parameters;

    private List<RestField> pathVariables;

    private List<RestField> headVariables;

    private RestObject request;

    private RestObject response;

    public Map<String, RestField> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, RestField> parameters) {
        this.parameters = parameters;
    }

    public List<RestField> getHeadVariables() {
        return headVariables;
    }

    public void setHeadVariables(List<RestField> headVariables) {
        this.headVariables = headVariables;
    }

    public List<RestField> getPathVariables() {
        return pathVariables;
    }

    public void setPathVariables(List<RestField> pathVariables) {
        this.pathVariables = pathVariables;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public RestObject getRequest() {
        return request;
    }

    public void setRequest(RestObject request) {
        this.request = request;
    }

    public RestObject getResponse() {
        return response;
    }

    public void setResponse(RestObject response) {
        this.response = response;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }
    
    

}
