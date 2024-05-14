/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.generator;

import com.fastjrun.codeg.common.CodeGException;
import com.fastjrun.codeg.common.CodeGMsgContants;
import com.fastjrun.codeg.common.PacketField;
import com.fastjrun.codeg.common.PacketObject;
import com.fastjrun.codeg.generator.common.BaseCMGenerator;
import com.helger.jcodemodel.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class PacketGenerator extends BaseCMGenerator {

    private PacketObject packetObject;

    private AbstractJClass packetObjectJClass;

    @Override
    public void generate() {
        if (!this.packetObject.is_new()) {
            this.packetObjectJClass = cm.ref(packetObject.get_class());
        } else {
            JDefinedClass dc;
            try {
                dc = cm._class(this.packageNamePrefix + packetObject.get_class());
            } catch (JCodeModelException e) {
                String msg = packetObject.get_class() + " is already exists.";
                log.error(msg, e);
                throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
            }

            log.debug(packetObject.get_class());
            dc._implements(cm.ref("java.io.Serializable"));
            if (this.packetObject.getParent() != null && !this.packetObject.getParent().equals("")) {
                if (this.packetObject.getParent().endsWith(":New")) {
                    dc._extends(
                            cm.ref(
                                    this.packageNamePrefix
                                            + this.packetObject
                                            .getParent()
                                            .substring(0, this.packetObject.getParent().length() - 4)));
                } else {
                    dc._extends(cm.ref(this.packetObject.getParent()));
                }
            }

            dc.annotate(cm.ref("lombok.Getter"));
            dc.annotate(cm.ref("lombok.Setter"));
            dc.annotate(cm.ref("lombok.ToString"));
            if (this.packetObject.getRemark() != null && !this.packetObject.getRemark().equals("")) {
                if (!this.isApi()) {
                    if (this.swaggerVersion == SwaggerVersion.Swagger2) {
                        dc
                                .annotate(cm.ref("io.swagger.annotations.ApiModel"))
                                .param("value", this.packetObject.getRemark());
                    } else if (this.swaggerVersion == SwaggerVersion.Swagger3) {
                        dc
                                .annotate(cm.ref("io.swagger.v3.oas.annotations.media.Schema"))
                                .param("description", this.packetObject.getRemark());
                    }

                }

            }

            this.packetObjectJClass = dc;

            Map<String, PacketField> fields = packetObject.getFields();
            if (fields != null && fields.size() > 0) {
                for (PacketField field : fields.values()) {
                    this.processField(field, dc);
                }
            }

            Map<String, PacketObject> objects = packetObject.getObjects();
            if (objects != null && objects.size() > 0) {
                for (String key : objects.keySet()) {
                    PacketObject object = objects.get(key);
                    AbstractJClass jObjectClass =
                            this.processObjectORList(object);
                    dc.field(JMod.PRIVATE, jObjectClass, key);
                }
            }

            Map<String, PacketObject> lists = packetObject.getLists();
            if (lists != null && lists.size() > 0) {
                for (String key : lists.keySet()) {
                    PacketObject list = lists.get(key);
                    AbstractJClass jListClass =
                            this.processObjectORList(list);
                    dc.field(JMod.PRIVATE, cm.ref("java.util.List").narrow(jListClass), key);
                }
            }


            this.addClassDeclaration(dc);
        }
    }

    public void processField(
            PacketField field,
            JDefinedClass dc) {

        String fieldName = field.getFieldName();
        String dataType = field.getDatatype();
        AbstractJType jType;
        if (dataType.endsWith(":List")) {
            String primitiveType = dataType.split(":")[0];
            jType = cm.ref("java.util.List").narrow(cm.ref(primitiveType));
        } else {
            jType = cm.ref(dataType);
        }
        JFieldVar fieldVar = dc.field(JMod.PRIVATE, jType, fieldName);
        if (!fieldName.equals(field.getName())) {
            fieldVar
                    .annotate(cm.ref("com.fasterxml.jackson.annotation.JsonProperty"))
                    .param(field.getName());
        }
        if (!this.isApi()) {
            if (this.swaggerVersion == SwaggerVersion.Swagger2) {
                fieldVar
                        .annotate(cm.ref("io.swagger.annotations.ApiModelProperty"))
                        .param("value", field.getRemark())
                        .param("required", JExpr.lit(!field.isCanBeNull()));
            } else if (this.swaggerVersion == SwaggerVersion.Swagger3) {
                JAnnotationUse use =
                        fieldVar
                                .annotate(cm.ref("io.swagger.v3.oas.annotations.media.Schema"))
                                .param("description", field.getRemark());
                if (!field.isCanBeNull()) {
                    use.param("requiredMode", Schema.RequiredMode.REQUIRED);
                }
            }
        }


        JDocComment jdoc = fieldVar.javadoc();
        // 成员变量注释
        jdoc.add(field.getRemark());
    }

    public AbstractJClass processObjectORList(
            PacketObject po) {
        if (po.is_new()) {
            return cm.ref(this.packageNamePrefix + po.get_class());
        } else {
            return cm.ref(po.get_class());
        }
    }
}
