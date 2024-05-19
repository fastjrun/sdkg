/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.common;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
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

    private boolean requestIsBody=true;

    private PacketObject response;

    private boolean responseIsArray;

    private boolean responseIsPage;
}
