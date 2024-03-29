/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.common;

import java.util.List;

public class CommonMethod {
    private String name;

    private String remark;

    private List<PacketField> parameters;

    private List<PacketField> pathVariables;

    private List<PacketField> headVariables;

    private List<PacketField> cookieVariables;

    private List<PacketField> webParameters;

    private String version;

    private String path;

    private String httpMethod;

    private String reqType;

    private String resType;

    private boolean needApi=true;

    private boolean needResponse=true;

    private String httpStatus;

    private PacketObject request;

    private String requestName;

    private boolean requestIsArray;

    private boolean requestIsList;

    private PacketObject response;

    private boolean responseIsArray;

    private boolean responseIsPage;

    public boolean isRequestIsArray() {
        return requestIsArray;
    }

    public void setRequestIsArray(boolean requestIsArray) {
        this.requestIsArray = requestIsArray;
    }

    public boolean isRequestIsList() {
        return requestIsList;
    }

    public void setRequestIsList(boolean requestIsList) {
        this.requestIsList = requestIsList;
    }

    public boolean isResponseIsPage() {
        return responseIsPage;
    }

    public void setResponseIsPage(boolean responseIsPage) {
        this.responseIsPage = responseIsPage;
    }

    public boolean isResponseIsArray() {
        return responseIsArray;
    }

    public void setResponseIsArray(boolean responseIsArray) {
        this.responseIsArray = responseIsArray;
    }

    public List<PacketField> getCookieVariables() {
        return cookieVariables;
    }

    public void setCookieVariables(List<PacketField> cookieVariables) {
        this.cookieVariables = cookieVariables;
    }

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

    public List<PacketField> getWebParameters() {
        return webParameters;
    }

    public void setWebParameters(List<PacketField> webParameters) {
        this.webParameters = webParameters;
    }


    public boolean isNeedApi() {
        return needApi;
    }

    public void setNeedApi(boolean needApi) {
        this.needApi = needApi;
    }

    public boolean isNeedResponse() {
        return needResponse;
    }

    public void setNeedResponse(boolean needResponse) {
        this.needResponse = needResponse;
    }

    public String getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(String httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getRequestName() {
        return requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }
}
