/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.generator;

import com.fastjrun.codeg.common.CodeGException;
import com.fastjrun.codeg.common.CodeGMsgContants;
import com.fastjrun.codeg.common.FJColumn;
import com.fastjrun.codeg.common.FJTable;
import com.fastjrun.codeg.generator.common.BaseCMGenerator;
import com.fastjrun.codeg.helper.StringHelper;
import com.helger.jcodemodel.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Mybatis Plus FrameWork
 * @author fastjrun
 */
@Setter
@Getter
public class MybatisPlusGenerator extends BaseCMGenerator {

    static String PACKAGE_ENTITY_NAME = "entity.";

    static String PACKAGE_MAPPER_NAME = "mapper.";

    static String PACKAGE_MAPPER_MAPPING_NAME = PACKAGE_MAPPER_NAME+"mapping.";

    protected FJTable fjTable;

    protected JDefinedClass entityClass;

    protected JDefinedClass mapperClass;

    private String lowerCaseFirstOneClassName;

    protected void processEntity() {

        String className = this.packageNamePrefix + PACKAGE_ENTITY_NAME + fjTable.getClassName();

        try {
            this.entityClass = cm._class(className);

        } catch (JCodeModelException e) {
            String msg = "fjTable class：" + fjTable.getName() + " is already exists.";
            log.error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }
        this.addClassDeclaration(this.entityClass);
        this.entityClass.annotate(cm.ref("lombok.Data"));
        this.entityClass.annotate(cm.ref("com.baomidou.mybatisplus.annotation.TableName")).param(fjTable.getName());
        Map<String, FJColumn> columns = fjTable.getColumns();
        List<FJColumn> sortedList = new ArrayList<>(columns.values());
        Collections.sort(sortedList, (o1, o2) -> {
            // 如果o1在名称列表中而o2不在，则o1排在前面
            if (fjTable.getPrimaryKeyColumnNames().contains(o1.getName()) && !fjTable.getPrimaryKeyColumnNames().contains(o2.getName())) {
                return -1;
            }
            // 如果o2在名称列表中而o1不在，则o2排在前面
            if (fjTable.getPrimaryKeyColumnNames().contains(o2.getName()) && !fjTable.getPrimaryKeyColumnNames().contains(o1.getName())) {
                return 1;
            }
            // 如果两个都在名称列表中，或者都不在，则保持原来的顺序
            return 0;
        });

        int index = 1;
        for ( FJColumn fjColumn : sortedList) {
            String name = fjColumn.getFieldName();
            String dataType = fjColumn.getDatatype();
            AbstractJType jType = cm.ref(dataType);
            JFieldVar fieldVar = this.entityClass.field(JMod.PRIVATE, jType, name);

            String comment = fjColumn.getComment()!=null?fjColumn.getComment():"";


            if(fjTable.getPrimaryKeyColumnNames().contains(fjColumn.getName())){
                fieldVar.annotate(cm.ref("com.baomidou.mybatisplus.annotation.TableId"));
            }
            fieldVar.annotate(cm.ref("io.swagger.annotations.ApiModelProperty")).
                    param("value",JExpr.lit(comment)).
                    param("position",index++);

            JDocComment jdoc = fieldVar.javadoc();
            // 成员变量注释
            jdoc.add(fjColumn.getName());
            if (fjColumn.getComment() != null && fjColumn.getComment().length() > 0) {
                // 注释换行
                jdoc.add(":");
                jdoc.add(fjColumn.getComment());
            }
        }
    }

    protected void processMapper() {

        String className = this.packageNamePrefix + PACKAGE_MAPPER_NAME + fjTable.getClassName()+"Mapper";

        try {
            this.mapperClass = cm._class(className,EClassType.INTERFACE);

        } catch (JCodeModelException e) {
            String msg = "fjTable class：" + fjTable.getName() + " is already exists.";
            log.error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }
        this.addClassDeclaration(this.mapperClass);
         this.mapperClass._implements(cm.ref("com.baomidou.mybatisplus.core.mapper.BaseMapper").narrow(this.entityClass));
    }

    protected void processMappering() {

        String xmlName = this.packageNamePrefix + PACKAGE_MAPPER_MAPPING_NAME + fjTable.getClassName()+"Mapper.xml";
    }


    @Override
    public void generate() {
        this.lowerCaseFirstOneClassName = StringHelper.toLowerCaseFirstOne(fjTable.getClassName());
        this.processEntity();
        this.processMapper();

    }
}