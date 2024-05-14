/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.generator;

import com.baomidou.mybatisplus.annotation.FieldFill;
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

/**
 * Mybatis Plus FrameWork
 * @author fastjrun
 */
@Setter
@Getter
public class MybatisPlusCodeGenerator extends BaseCMGenerator {

    static String PACKAGE_ENTITY_NAME = "entity.";

    static String PACKAGE_MAPPER_NAME = "mapper.";

    static String PACKAGE_DAO_NAME = "dao.";

    static String PACKAGE_DAO_IMPL_NAME = "dao.impl.";

    protected FJTable fjTable;

    protected JDefinedClass entityClass;

    protected JDefinedClass mapperClass;

    protected JDefinedClass daoClass;

    protected JDefinedClass daoImplClass;


    static Set<String> needAutoInsertColumnNames = new HashSet<>(Arrays.asList(
            new String[]{"createDate".toLowerCase(),"createTime".toLowerCase(), "createUser".toLowerCase()}));

    static Set<String> needAutoUpdateColumnNames = new HashSet<>(Arrays.asList(
            new String[]{"updateDate".toLowerCase(), "updateUser".toLowerCase(),"updateTime".toLowerCase()}));

    static Set<String> needlogicDeleteColumnNames = new HashSet<>(Arrays.asList(
            new String[]{"deleteFlag".toLowerCase()}));

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
        this.entityClass.annotate(cm.ref("lombok.Getter"));
        this.entityClass.annotate(cm.ref("lombok.Setter"));
        this.entityClass.annotate(cm.ref("lombok.ToString"));
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
            FieldFill fieldFill = null;
            if(needAutoInsertColumnNames.contains(fjColumn.getFieldName().toLowerCase())) {
                fieldFill = FieldFill.INSERT;
            }else if(needAutoUpdateColumnNames.contains(fjColumn.getFieldName().toLowerCase())) {
                fieldFill = FieldFill.UPDATE;
            }else if(needlogicDeleteColumnNames.contains(fjColumn.getFieldName().toLowerCase())) {
                fieldFill = FieldFill.INSERT;
            }

            if(fieldFill!=null){
                fieldVar.annotate(cm.ref("com.baomidou.mybatisplus.annotation.TableField"))
                        .param("fill", fieldFill);
            }
            if(this.swaggerVersion==SwaggerVersion.Swagger2){
                fieldVar.annotate(cm.ref("io.swagger.annotations.ApiModelProperty")).
                        param("value",JExpr.lit(comment)).
                        param("position",index++);
            }else if(this.swaggerVersion==SwaggerVersion.Swagger3){
                fieldVar.annotate(cm.ref("io.swagger.v3.oas.annotations.media.Schema")).
                        param("description",JExpr.lit(comment));
            }


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
            String msg = "fjTable class：" + fjTable.getName() + "Mapper is already exists.";
            log.error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }
        this.addClassDeclaration(this.mapperClass);
         this.mapperClass._implements(cm.ref("com.github.yulichang.base.MPJBaseMapper").narrow(this.entityClass));
    }

    protected void processDao() {

        String className = this.packageNamePrefix + PACKAGE_DAO_NAME + fjTable.getClassName()+"Dao";

        try {
            this.daoClass = cm._class(className,EClassType.INTERFACE);

        } catch (JCodeModelException e) {
            String msg = "fjTable class：" + fjTable.getName() + "Dao is already exists.";
            log.error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }
        this.addClassDeclaration(this.daoClass);
        this.daoClass._implements(cm.ref("com.github.yulichang.base.MPJBaseService").narrow(this.entityClass));
    }

    protected void processDaoImpl() {

        String className = this.packageNamePrefix + PACKAGE_DAO_IMPL_NAME + fjTable.getClassName()+"DaoImpl";

        try {
            this.daoImplClass = cm._class(className);

        } catch (JCodeModelException e) {
            String msg = "fjTable class：" + fjTable.getName() + "DaoImpl is already exists.";
            log.error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }
        this.addClassDeclaration(this.daoImplClass);
        this.daoImplClass.annotate(cm.ref("org.springframework.stereotype.Repository"));
        this.daoImplClass._extends(
                cm.ref("com.github.yulichang.base.MPJBaseServiceImpl").
                        narrow(this.mapperClass).narrow(this.entityClass));
        this.daoImplClass._implements(this.daoClass);
    }

    protected void processMPJMapper() {

        String className = this.packageNamePrefix + PACKAGE_DAO_IMPL_NAME + fjTable.getClassName()+"DaoImpl";

        try {
            this.daoImplClass = cm._class(className);

        } catch (JCodeModelException e) {
            String msg = "fjTable class：" + fjTable.getName() + "DaoImpl is already exists.";
            log.error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }
        this.addClassDeclaration(this.daoImplClass);
        this.daoImplClass.annotate(cm.ref("org.springframework.stereotype.Repository"));
        this.daoImplClass._extends(
                cm.ref("com.baomidou.mybatisplus.extension.service.impl.ServiceImpl").
                        narrow(this.mapperClass).narrow(this.entityClass));
        this.daoImplClass._implements(this.daoClass);
    }


    @Override
    public void generate() {
        this.lowerCaseFirstOneClassName = StringHelper.toLowerCaseFirstOne(fjTable.getClassName());
        this.processEntity();
        this.processMapper();
        this.processDao();
        this.processDaoImpl();

    }
}
