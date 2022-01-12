/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.generator.method;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fastjrun.codeg.common.PacketField;
import com.fastjrun.codeg.common.PacketObject;
import com.fastjrun.codeg.generator.common.BaseCMGenerator;
import com.fastjrun.codeg.utils.JacksonUtils;
import com.helger.jcodemodel.AbstractJClass;

import java.util.Map;

public abstract class AbstractMethodGenerator extends BaseCMGenerator {

    protected ObjectNode methodParamInJsonObject;

    public void setMethodParamInJsonObject(ObjectNode methodParamInJsonObject) {
        this.methodParamInJsonObject = methodParamInJsonObject;
    }

    protected ObjectNode composeRequestBody(PacketObject requestBody) {
        ObjectNode jsonRequestBody = this.composeRequestBodyField(requestBody);
        Map<String, PacketObject> robjects = requestBody.getObjects();
        if (robjects != null && robjects.size() > 0) {
            for (String reName : robjects.keySet()) {
                PacketObject ro = robjects.get(reName);
                ObjectNode jsonRe = this.composeRequestBody(ro);
                jsonRequestBody.set(reName, jsonRe);
            }
        }
        Map<String, PacketObject> roList = requestBody.getLists();
        if (roList != null && roList.size() > 0) {
            for (String listName : roList.keySet()) {
                PacketObject ro = roList.get(listName);
                ObjectNode jsonRe = this.composeRequestBody(ro);
                ArrayNode jsonAy = JacksonUtils.createArrayNode();
                jsonAy.add(jsonRe);
                jsonRequestBody.set(listName, jsonAy);
            }
        }
        return jsonRequestBody;
    }

    protected ObjectNode composeRequestBodyField(PacketObject requestBody) {
        ObjectNode jsonRequestBody = JacksonUtils.createObjectNode();
        Map<String, PacketField> fields = requestBody.getFields();
        if (fields != null) {
            for (String fieldName : fields.keySet()) {
                PacketField restField = fields.get(fieldName);
                String dataType = restField.getDatatype();
                AbstractJClass jType;
                AbstractJClass primitiveType;
                if (dataType.endsWith(":List")) {
                    primitiveType = cm.ref(dataType.split(":")[0]);
                    jType = cm.ref("java.util.List").narrow(primitiveType);
                } else {
                    // Integer、Double、Long、Boolean、Character、Float
                    jType = cm.ref(dataType);
                }
                jsonRequestBody.put(fieldName, jType.name());

            }
        }
        return jsonRequestBody;

    }
}