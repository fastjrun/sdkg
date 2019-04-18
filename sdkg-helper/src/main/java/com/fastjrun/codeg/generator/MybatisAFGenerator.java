package com.fastjrun.codeg.generator;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.fastjrun.codeg.common.CodeGException;
import com.fastjrun.codeg.common.CodeGMsgContants;
import com.fastjrun.codeg.common.FJColumn;
import com.fastjrun.codeg.common.FJTable;
import com.fastjrun.codeg.generator.common.BaseCMGenerator;
import com.fastjrun.codeg.helper.MysqlSqlHelper;
import com.fastjrun.codeg.helper.SQLHelperFactory;
import com.fastjrun.codeg.helper.SqlHelper;
import com.fastjrun.helper.StringHelper;
import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.EClassType;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JClassAlreadyExistsException;
import com.helger.jcodemodel.JConditional;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JDocComment;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldRef;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JForLoop;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JVar;

/**
 * Mybatis Annotation FrameWork
 */
public class MybatisAFGenerator extends BaseCMGenerator {

    static String PACKAGE_ENTITY_NAME = "entity.";

    static String PACKAGE_DAO_WITH_BASE = "dao.Base";

    static String PACKAGE_SERVICE_WITH_BASE = "service.Base";

    static String PACKAGE_SERVICE_IMPL_WITH_BASE = "service.base.impl.Base";

    static String PACKAGE_CONTROLLER_WITH_BASE = "web.base.controller.Base";

    static String ENTITY_PARENT_CLASS_NAME = "com.fastjrun.entity.BaseEntity";

    protected FJTable fjTable;

    protected JDefinedClass entityClass;

    protected JDefinedClass daoClass;

    protected JDefinedClass sqlBuilderClass;

    protected JDefinedClass serviceClass;

    protected JDefinedClass serviceImplClass;

    protected JDefinedClass controllerClass;

    protected boolean supportController;

    protected boolean supportTest;

    protected Properties daoTestParam;

    public Properties getDaoTestParam() {
        return daoTestParam;
    }

    public void setDaoTestParam(Properties daoTestParam) {
        this.daoTestParam = daoTestParam;
    }

    public boolean isSupportController() {
        return supportController;
    }

    public void setSupportController(boolean supportController) {
        this.supportController = supportController;
    }

    public boolean isSupportTest() {
        return supportTest;
    }

    public void setSupportTest(boolean supportTest) {
        this.supportTest = supportTest;
    }

    public void setFjTable(FJTable fjTable) {
        this.fjTable = fjTable;
    }

    protected void processEntity() {

        String className = this.packageNamePrefix + PACKAGE_ENTITY_NAME
                + fjTable.getClassName();

        try {
            this.entityClass = cm._class(className);

        } catch (JClassAlreadyExistsException e) {
            String msg = "fjTable class：" + fjTable.getName() + " is already exists.";
            log.error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }

        this.entityClass._extends(cm.ref(ENTITY_PARENT_CLASS_NAME));
        this.entityClass._implements(cm.ref("java.io.Serializable"));
        long hashCode = 0l;
        hashCode += this.entityClass.getClass().getName().hashCode();
        this.addClassDeclaration(this.entityClass);
        Map<String, FJColumn> columns = fjTable.getColumns();
        JMethod toStringMethod = this.entityClass.method(JMod.PUBLIC, cm.ref("String"),
                "toString");
        toStringMethod.annotate(cm.ref("Override"));
        JBlock toStringMethodBlk = toStringMethod.body();
        JVar toStringSBVar = toStringMethodBlk.decl(
                cm.ref("StringBuilder"), "sb",
                JExpr._new(cm.ref("StringBuilder")));
        int index = 0;
        toStringMethodBlk.add(toStringSBVar.invoke("append").arg(
                JExpr.lit(fjTable.getClassName()).plus(JExpr.lit(" ["))));

        for (FJColumn FJColumn : columns.values()) {
            String name = FJColumn.getFieldName();
            hashCode += name.hashCode();
            String dataType = FJColumn.getDatatype();
            AbstractJType jType = cm.ref(dataType);
            JFieldVar fieldVar = this.entityClass.field(JMod.PRIVATE, jType, name);
            JDocComment jdoc = fieldVar.javadoc();
            // 成员变量注释
            jdoc.add(FJColumn.getName());
            if (FJColumn.getComment() != null
                    && FJColumn.getComment().length() > 0) {
                // 注释换行
                jdoc.add("<br/>\n");
                jdoc.add(FJColumn.getComment());
            }
            JFieldRef nameRef = JExpr.refthis(name);
            if (index > 0) {
                toStringMethodBlk.add(toStringSBVar.invoke("append").arg(
                        JExpr.lit(",")));
            }
            index++;
            toStringMethodBlk.add(toStringSBVar.invoke("append").arg(
                    JExpr.lit(name)));
            toStringMethodBlk.add(toStringSBVar.invoke("append").arg(
                    JExpr.lit("=")));
            toStringMethodBlk.add(toStringSBVar.invoke("append").arg(nameRef));

            // javabean命名规范：属性第二字母大写，则setter和getter方法首字母和第二字母都大写

            String tterMethodName = name;
            if (name.length() > 1) {
                String char2 = String.valueOf(name.charAt(1));
                if (!char2.equals(char2.toUpperCase())) {
                    tterMethodName = StringHelper.toUpperCaseFirstOne(name);
                }
            }
            JMethod getMethod = this.entityClass.method(JMod.PUBLIC, jType, "get"
                    + tterMethodName);
            hashCode += getMethod.name().hashCode();
            JBlock getMethodBlk = getMethod.body();
            getMethodBlk._return(nameRef);
            JMethod setMethod = this.entityClass.method(JMod.PUBLIC, cm.VOID, "set"
                    + tterMethodName);
            JVar jvar = setMethod.param(jType, name);
            hashCode += setMethod.name().hashCode();
            JBlock setMethodBlk = setMethod.body();
            setMethodBlk.assign(nameRef, jvar);
        }
        toStringMethodBlk.add(toStringSBVar.invoke("append").arg(
                JExpr.lit("]")));
        toStringMethodBlk._return(toStringSBVar.invoke("toString"));
        // private static final long serialVersionUID = 1L;
        this.entityClass.field(JMod.PRIVATE + JMod.STATIC + JMod.FINAL, cm.LONG,
                "serialVersionUID", JExpr.lit(hashCode));
    }

    protected void processDaoTest(MybatisDaoTestMethodGenerator mybatisDaoTestMethodGenerator) {
        // 生成测试类名：Base+fjTable.getClassName()+DaoTest
        JDefinedClass daoTestClass = null;
        try {
            daoTestClass = cmTest._class(
                    this.packageNamePrefix + PACKAGE_DAO_WITH_BASE + fjTable.getClassName()
                            + "DaoTest");
        } catch (JClassAlreadyExistsException e) {
            String msg = "fjTable dao test class：" + fjTable.getName() + " is already exists.";
            log.error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }
        daoTestClass._extends(cmTest.ref("com.fastjrun.test.AbstractAdVancedTestNGSpringContextTest"));
        this.addClassDeclaration(daoTestClass);
        JFieldVar fieldVar =
                daoTestClass.field(JMod.PRIVATE, this.daoClass,
                        "base" + fjTable.getClassName() + "Dao");
        fieldVar.annotate(cmTest.ref("org.springframework.beans.factory.annotation.Autowired"));
        mybatisDaoTestMethodGenerator.setDaoTestClass(daoTestClass);
        mybatisDaoTestMethodGenerator.setFieldVar(fieldVar);

    }

    protected void processDao() {
        String lowerCaseFirstOneClassName = StringHelper
                .toLowerCaseFirstOne(fjTable.getClassName());
        // 生成接口名：Base+fjTable.getClassName()+Dao
        try {
            this.daoClass = cm._class(
                    this.packageNamePrefix + PACKAGE_DAO_WITH_BASE + fjTable.getClassName()
                            + "Dao", EClassType.INTERFACE);
        } catch (JClassAlreadyExistsException e) {
            String msg = "fjTable dao class：" + fjTable.getName() + " is already exists.";
            log.error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }
        this.addClassDeclaration(this.daoClass);

        SqlHelper sqlHelper = SQLHelperFactory.getSQLHelper("mysql", fjTable);
        // insert方法
        JMethod insertMethod = this.daoClass.method(JMod.NONE, cm.INT, "insert");
        insertMethod.param(this.entityClass, lowerCaseFirstOneClassName);
        insertMethod.annotate(
                cm.ref("org.apache.ibatis.annotations.Insert")).param(
                "value", sqlHelper.getInsert());
        List<String> primaryKeyColumnNames = fjTable.getPrimaryKeyColumnNames();
        if (primaryKeyColumnNames != null) {
            if (primaryKeyColumnNames.size() == 1) {
                FJColumn fjColumn = fjTable.getColumns().get(primaryKeyColumnNames.get(0));
                if (fjColumn.isIdentity()) {
                    insertMethod
                            .annotate(
                                    cm.ref("org.apache.ibatis.annotations.Options"))
                            .param("useGeneratedKeys", true)
                            .param("keyProperty", fjColumn.getFieldName());
                }
            }
            // selectByPK方法
            JMethod selectByPKMethod = this.daoClass.method(JMod.NONE, this.entityClass,
                    "selectByPK");
            selectByPKMethod.annotate(
                    cm.ref("org.apache.ibatis.annotations.Select")).param(
                    "value", sqlHelper.getSelectByPK());
            selectByPKMethod.annotate(
                    cm.ref("org.apache.ibatis.annotations.Options")).param(
                    "flushCache", true);
            // deleteByPK方法
            JMethod deleteByPKMethod = this.daoClass.method(JMod.NONE, cm.INT,
                    "deleteByPK");
            deleteByPKMethod.annotate(
                    cm.ref("org.apache.ibatis.annotations.Delete")).param(
                    "value", sqlHelper.getDeleteByPK());
            // updateByPK方法
            JMethod updateByPKMethod = this.daoClass.method(JMod.NONE, cm.INT,
                    "updateByPK");
            updateByPKMethod.annotate(
                    cm.ref("org.apache.ibatis.annotations.Update")).param(
                    "value", sqlHelper.getUpdateByPK());
            updateByPKMethod.param(this.entityClass, lowerCaseFirstOneClassName);
            for (int i = 0; i < primaryKeyColumnNames.size(); i++) {
                String key = primaryKeyColumnNames.get(i);
                FJColumn fjColumn = fjTable.getColumns().get(key);
                String fieldName = fjColumn.getFieldName();
                JVar selectFieldNameParam = selectByPKMethod.param(
                        cm.ref(fjColumn.getDatatype()), fieldName);
                selectFieldNameParam.annotate(
                        cm.ref("org.apache.ibatis.annotations.Param"))
                        .param("value", fieldName);
                JVar deleteFieldNameParam = deleteByPKMethod.param(
                        cm.ref(fjColumn.getDatatype()), fieldName);
                deleteFieldNameParam.annotate(
                        cm.ref("org.apache.ibatis.annotations.Param"))
                        .param("value", fieldName);
            }

        }
        // totalCount总数
        JMethod totalCountMethod = this.daoClass.method(JMod.NONE, cm.INT,
                "totalCount");
        totalCountMethod.annotate(
                cm.ref("org.apache.ibatis.annotations.Select")).param(
                "value", sqlHelper.getTotalCount(0));
        // queryForList查询
        JMethod queryForListMethod = this.daoClass
                .method(JMod.NONE,
                        cm.ref("java.util.List").narrow(this.entityClass),
                        "queryForList");
        queryForListMethod.annotate(
                cm.ref("org.apache.ibatis.annotations.Select")).param(
                "value", sqlHelper.getQueryForList(0));
        queryForListMethod.annotate(
                cm.ref("org.apache.ibatis.annotations.Options")).param(
                "flushCache", true);
        // queryForLimitList分页查询
        JMethod queryForLimitListMethod = this.daoClass.method(JMod.NONE,
                cm.ref("java.util.List").narrow(this.entityClass),
                "queryForLimitList");
        queryForLimitListMethod.param(
                cm.ref("org.apache.ibatis.session.RowBounds"), "rowBounds");
        queryForLimitListMethod.annotate(
                cm.ref("org.apache.ibatis.annotations.Select")).param(
                "value", sqlHelper.getQueryForList(0));
        queryForLimitListMethod.annotate(
                cm.ref("org.apache.ibatis.annotations.Options")).param(
                "flushCache", true);

        // totalCountCondition总数
        JMethod totalCountConditionMethod = this.daoClass.method(JMod.NONE, cm.INT,
                "totalCountCondition");
        totalCountConditionMethod
                .annotate(
                        cm.ref("org.apache.ibatis.annotations.SelectProvider"))
                .param("type", this.sqlBuilderClass)
                .param("method", "totalCountCondition");
        JVar conditionVar = totalCountConditionMethod.param(
                cm.ref(String.class), "condition");
        conditionVar
                .annotate(cm.ref("org.apache.ibatis.annotations.Param"))
                .param("value", "condition");
        // selectOneCondition查询
        JMethod selectOneConditionMethod = this.daoClass.method(JMod.NONE,
                this.entityClass, "selectOneCondition");
        selectOneConditionMethod
                .annotate(
                        cm.ref("org.apache.ibatis.annotations.SelectProvider"))
                .param("type", this.sqlBuilderClass)
                .param("method", "queryWithCondition");
        conditionVar = selectOneConditionMethod.param(cm.ref("String"),
                "condition");
        conditionVar
                .annotate(cm.ref("org.apache.ibatis.annotations.Param"))
                .param("value", "condition");
        // queryForListCondition查询
        JMethod queryForListConditionMethod = this.daoClass.method(JMod.NONE, cm
                        .ref("java.util.List").narrow(this.entityClass),
                "queryForListCondition");
        queryForListConditionMethod
                .annotate(
                        cm.ref("org.apache.ibatis.annotations.SelectProvider"))
                .param("type", this.sqlBuilderClass)
                .param("method", "queryWithCondition");
        conditionVar = queryForListConditionMethod.param(cm.ref("String"),
                "condition");
        conditionVar
                .annotate(cm.ref("org.apache.ibatis.annotations.Param"))
                .param("value", "condition");
        // queryForLimitListCondition分页查询
        JMethod queryForLimitListConditionMethod = this.daoClass.method(JMod.NONE,
                cm.ref("java.util.List").narrow(this.entityClass),
                "queryForLimitListCondition");
        queryForLimitListConditionMethod
                .annotate(
                        cm.ref("org.apache.ibatis.annotations.SelectProvider"))
                .param("type", this.sqlBuilderClass)
                .param("method", "queryWithCondition");
        conditionVar = queryForLimitListConditionMethod.param(
                cm.ref("String"), "condition");
        conditionVar
                .annotate(cm.ref("org.apache.ibatis.annotations.Param"))
                .param("value", "condition");
        queryForLimitListConditionMethod.param(
                cm.ref("org.apache.ibatis.session.RowBounds"), "rowBounds");
        // insertAll方法批量插入
        JMethod insertAllMethod = this.daoClass.method(JMod.NONE, cm.INT,
                "insertAll");
        insertAllMethod.param(cm.ref("java.util.List").narrow(this.entityClass),
                lowerCaseFirstOneClassName + "s"); // 复数形式
        insertAllMethod
                .annotate(
                        cm.ref("org.apache.ibatis.annotations.InsertProvider"))
                .param("type", this.sqlBuilderClass).param("method", "insertAll");
    }

    protected void processService() {
        String lowerCaseFirstOneClassName = StringHelper
                .toLowerCaseFirstOne(fjTable.getClassName());
        // 生成接口名：Base+fjTable.getClassName()+Service
        try {
            this.serviceClass = cm._class(
                    this.packageNamePrefix + PACKAGE_SERVICE_WITH_BASE + fjTable.getClassName()
                            + "Service", EClassType.INTERFACE);
        } catch (JClassAlreadyExistsException e) {
            String msg = "fjTable service class：" + fjTable.getName() + " is already exists.";
            log.error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }
        this.addClassDeclaration(this.serviceClass);
        // insert方法
        JMethod insertMethod = this.serviceClass.method(JMod.NONE, cm.INT, "insert");
        insertMethod.param(this.entityClass, lowerCaseFirstOneClassName);

        List<String> primaryKeyColumnNames = fjTable.getPrimaryKeyColumnNames();
        if (primaryKeyColumnNames != null) {
            // selectByPK方法
            JMethod selectByPKMethod = this.serviceClass.method(JMod.NONE, this.entityClass,
                    "selectByPK");
            // deleteByPK方法
            JMethod deleteByPKMethod = this.serviceClass.method(JMod.NONE, cm.INT,
                    "deleteByPK");
            // updateByPK方法
            JMethod updateByPKMethod = this.serviceClass.method(JMod.NONE, cm.INT,
                    "updateByPK");
            updateByPKMethod.param(this.entityClass, lowerCaseFirstOneClassName);
            for (int i = 0; i < primaryKeyColumnNames.size(); i++) {
                String key = primaryKeyColumnNames.get(i);
                FJColumn fjColumn = fjTable.getColumns().get(key);
                String fieldName = fjColumn.getFieldName();
                selectByPKMethod.param(
                        cm.ref(fjColumn.getDatatype()), fieldName);
                deleteByPKMethod.param(
                        cm.ref(fjColumn.getDatatype()), fieldName);
            }

        }
        // totalCount总数
        JMethod totalCountMethod = this.serviceClass.method(JMod.NONE, cm.INT,
                "totalCount");

        // queryForLimitList分页查询
        JMethod queryForLimitListMethod = this.serviceClass.method(JMod.NONE,
                cm.ref("java.util.List").narrow(this.entityClass),
                "queryForLimitList");
        queryForLimitListMethod.param(cm.INT, "pageNum");
        queryForLimitListMethod.param(cm.INT, "pageSize");
    }

    protected void processServiceImpl() {
        String lowerCaseFirstOneClassName = StringHelper
                .toLowerCaseFirstOne(fjTable.getClassName());
        // 生成接口名：Base+fjTable.getClassName()+Service
        try {
            this.serviceImplClass = cm._class(
                    this.packageNamePrefix + PACKAGE_SERVICE_IMPL_WITH_BASE + fjTable.getClassName()
                            + "ServiceImpl");
        } catch (JClassAlreadyExistsException e) {
            String msg = "fjTable service impl class：" + fjTable.getName() + " is already exists.";
            log.error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }

        this.serviceImplClass._implements(this.serviceClass);
        this.serviceImplClass.annotate(cm.ref("org.springframework.stereotype.Service")).param("value",
                "base" + fjTable.getClassName() + "Service");
        this.addClassDeclaration(this.serviceImplClass);

        JFieldVar fieldVar =
                this.serviceImplClass.field(JMod.PRIVATE, this.daoClass,
                        "base" + fjTable.getClassName() + "Dao");
        fieldVar.annotate(cm.ref("org.springframework.beans.factory.annotation.Autowired"));

        // insert方法
        JMethod insertMethod = this.serviceImplClass.method(JMod.PUBLIC, cm.INT, "insert");
        insertMethod.annotate(cm.ref("java.lang.Override"));
        JVar insertParamJVar = insertMethod.param(this.entityClass, lowerCaseFirstOneClassName);
        insertMethod.body()._return(fieldVar.invoke("insert").arg(insertParamJVar));
        List<String> primaryKeyColumnNames = fjTable.getPrimaryKeyColumnNames();
        if (primaryKeyColumnNames != null) {
            // selectByPK方法
            JMethod selectByPKMethod = this.serviceImplClass.method(JMod.PUBLIC, this.entityClass,
                    "selectByPK");
            selectByPKMethod.annotate(cm.ref("java.lang.Override"));
            // deleteByPK方法
            JMethod deleteByPKMethod = this.serviceImplClass.method(JMod.PUBLIC, cm.INT,
                    "deleteByPK");
            deleteByPKMethod.annotate(cm.ref("java.lang.Override"));
            // updateByPK方法
            JMethod updateByPKMethod = this.serviceImplClass.method(JMod.PUBLIC, cm.INT,
                    "updateByPK");
            updateByPKMethod.annotate(cm.ref("java.lang.Override"));
            JVar updateByPKParamJVar = updateByPKMethod.param(this.entityClass, lowerCaseFirstOneClassName);
            updateByPKMethod.body()._return(fieldVar.invoke("insert").arg(updateByPKParamJVar));

            JInvocation jSelectByPKJInvocation = fieldVar.invoke("selectByPK");
            JInvocation jDeleteByPKJInvocation = fieldVar.invoke("deleteByPK");
            for (int i = 0; i < primaryKeyColumnNames.size(); i++) {
                String key = primaryKeyColumnNames.get(i);
                FJColumn fjColumn = fjTable.getColumns().get(key);
                String fieldName = fjColumn.getFieldName();
                JVar selectByPKParamJVar = selectByPKMethod.param(
                        cm.ref(fjColumn.getDatatype()), fieldName);
                jSelectByPKJInvocation.arg(selectByPKParamJVar);
                JVar deleteByPKParamJVar = deleteByPKMethod.param(
                        cm.ref(fjColumn.getDatatype()), fieldName);
                jDeleteByPKJInvocation.arg(deleteByPKParamJVar);
            }
            selectByPKMethod.body()._return(jSelectByPKJInvocation);
            deleteByPKMethod.body()._return(jDeleteByPKJInvocation);

        }
        // totalCount总数
        JMethod totalCountMethod = this.serviceImplClass.method(JMod.PUBLIC, cm.INT,
                "totalCount");
        totalCountMethod.annotate(cm.ref("java.lang.Override"));
        // queryForLimitList分页查询
        totalCountMethod.body()._return(fieldVar.invoke("totalCount"));
        JMethod queryForLimitListMethod = this.serviceImplClass.method(JMod.PUBLIC,
                cm.ref("java.util.List").narrow(this.entityClass),
                "queryForLimitList");
        queryForLimitListMethod.annotate(cm.ref("java.lang.Override"));
        JVar pageNumJVar = queryForLimitListMethod.param(cm.INT, "pageNum");
        JVar pageSizeJVar = queryForLimitListMethod.param(cm.INT, "pageSize");
        JBlock queryForLimitListMethodBlk = queryForLimitListMethod.body();
        JVar rowBoundsJVar = queryForLimitListMethodBlk.decl(cm.ref("org.apache.ibatis.session.RowBounds"),
                "rowBounds", JExpr._new(cm.ref("org.apache.ibatis.session.RowBounds")).arg(pageNumJVar.minus(JExpr.lit
                        (1)).mul(pageSizeJVar)).arg(pageNumJVar.mul(pageSizeJVar).minus(JExpr.lit
                        (1))));
        queryForLimitListMethodBlk._return(fieldVar.invoke("queryForLimitList").arg(rowBoundsJVar));
    }

    /**
     * 生成SqlBuilder类 类名：Base+fjTable.getClassName()+SqlBuilder
     *
     * @return 定义了sqlBuilder接口的三个方法：totalCountCondition,selectOneCondition,
     * queryForListCondition
     */
    private void processSQLBuilder() {
        // 生成：Base+fjTable.getClassName()+SQLBuilder

        String lowerCaseFirstOneClassName = StringHelper
                .toLowerCaseFirstOne(fjTable.getClassName());

        try {
            this.sqlBuilderClass = cm._class(this.packageNamePrefix + PACKAGE_DAO_WITH_BASE
                    + fjTable.getClassName() + "SqlBuilder");

        } catch (JClassAlreadyExistsException e) {
            String msg = "fjTable SqlBuilder class：" + fjTable.getName() + " is already exists.";
            log.error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }
        SqlHelper sqlHelper = SQLHelperFactory.getSQLHelper(
                "mysql", fjTable);
        // totalCountCondition方法

        JMethod totalCountConditionMethod = this.sqlBuilderClass.method(JMod.PUBLIC,
                cm.ref("String"), "totalCountCondition");

        JVar parameterVar = totalCountConditionMethod.param(
                cm.ref("java.util.Map").narrow(String.class)
                        .narrow(String.class), "parameter");
        JBlock totalCountConditionMethodBlk = totalCountConditionMethod
                .body();
        JVar totalCountConditionVar = totalCountConditionMethodBlk.decl(cm
                .ref("String"), "condition", parameterVar.invoke("get")
                .arg("condition"));
        JVar totalCountConditionSbVar = totalCountConditionMethodBlk.decl(
                cm.ref("StringBuilder"), "sb",
                JExpr._new(cm.ref("StringBuilder")));
        totalCountConditionMethodBlk.add(totalCountConditionSbVar.invoke("append").arg(
                sqlHelper.getTotalCount(1)));
        totalCountConditionMethodBlk.add(totalCountConditionSbVar.invoke("append").arg(
                totalCountConditionVar));
        totalCountConditionMethodBlk._return(totalCountConditionSbVar.invoke("toString"));

        // queryWithCondition方法
        JMethod queryWithConditionMethod = this.sqlBuilderClass.method(JMod.PUBLIC,
                cm.ref("String"), "queryWithCondition");
        parameterVar = queryWithConditionMethod.param(
                cm.ref("java.util.Map").narrow(String.class)
                        .narrow(String.class), "parameter");
        JBlock queryWithConditionMethodBlk = queryWithConditionMethod
                .body();
        JVar queryWithConditionVar = queryWithConditionMethodBlk.decl(cm
                .ref("String"), "condition", parameterVar.invoke("get")
                .arg("condition"));
        JVar queryWithConditionBbVar = queryWithConditionMethodBlk.decl(cm.ref("StringBuilder"),
                "sb", JExpr._new(cm.ref("StringBuilder")));
        queryWithConditionMethodBlk.add(queryWithConditionBbVar.invoke("append").arg(
                sqlHelper.getQueryForList(1)));
        queryWithConditionMethodBlk.add(queryWithConditionBbVar.invoke("append").arg(
                queryWithConditionVar));
        queryWithConditionMethodBlk._return(queryWithConditionBbVar.invoke("toString"));

        // insertAll
        JMethod insertAllMethod = this.sqlBuilderClass.method(JMod.PUBLIC, cm.ref("String"),
                "insertAll");
        parameterVar = insertAllMethod.param(cm
                        .ref("java.util.Map")
                        .narrow(cm.ref("String"))
                        .narrow(cm.ref("java.util.List").narrow(this.entityClass)),
                "parameter");
        JBlock insertAllMethodBlk = insertAllMethod.body();
        JVar insertAllParameterVar = insertAllMethodBlk.decl(cm.ref("java.util.List")
                        .narrow(this.entityClass), lowerCaseFirstOneClassName + "s",
                parameterVar.invoke("get").arg(lowerCaseFirstOneClassName + "s"));
        JVar insertAllSbVar = insertAllMethodBlk.decl(cm.ref("StringBuilder"), "sb",
                JExpr._new(cm.ref("StringBuilder")));
        String[] sqlParamAndValue = getAllFieldsForBatch(fjTable);
        JVar messageFormatVar = insertAllMethodBlk.decl(
                cm.ref("java.text.MessageFormat"),
                "messageFormat",
                JExpr._new(cm.ref("java.text.MessageFormat")).arg(
                        sqlParamAndValue[1]));
        // mysql数据库
        if (sqlHelper instanceof MysqlSqlHelper) {
            insertAllMethodBlk.add(insertAllSbVar.invoke("append").arg(
                    "INSERT INTO " + fjTable.getName() + sqlParamAndValue[0]
                            + " VALUES"));
            JForLoop forLoop = insertAllMethodBlk._for();
            JVar i = forLoop.init(cm.INT, "i", JExpr.lit(0));
            forLoop.test(i.lt(insertAllParameterVar.invoke("size")));
            forLoop.update(i.incr());
            JBlock forBlk = forLoop.body();
            forBlk.add(JExpr.ref("sb").invoke("append").arg(
                    messageFormatVar.invoke("format").arg(
                            JExpr.newArray(cm.ref("java.lang.Object")).add(
                                    JExpr.ref("i")))));
            JConditional jif = forBlk._if(JExpr.ref("i").lt(
                    insertAllParameterVar.invoke("size").minus(JExpr.lit(1))));
            JBlock ifBlk = jif._then();
            ifBlk.add(JExpr.ref("sb").invoke("append").arg(","));
        } else { // oracle数据库
            insertAllMethodBlk.add(insertAllSbVar.invoke("append").arg(
                    "INSERT INTO " + fjTable.getName() + sqlParamAndValue[0]
                            + " "));
            JForLoop forLoop = insertAllMethodBlk._for();
            JVar i = forLoop.init(cm.INT, "i", JExpr.lit(0));
            forLoop.test(i.lt(insertAllParameterVar.invoke("size")));
            forLoop.update(i.incr());
            JBlock forBlk = forLoop.body();
            forBlk.add(JExpr.ref("sb").invoke("append").arg("SELECT "));
            forBlk.add(JExpr.ref("sb").invoke("append").arg(
                    messageFormatVar.invoke("format").arg(
                            JExpr.newArray(cm.ref("java.lang.Object")).add(
                                    JExpr.ref("i")))));
            forBlk.add(JExpr.ref("sb").invoke("append").arg(" FROM DUAL "));
            JConditional jif = forBlk._if(JExpr.ref("i").lt(
                    insertAllParameterVar.invoke("size").minus(JExpr.lit(1))));
            JBlock ifBlk = jif._then();
            ifBlk.add(JExpr.ref("sb").invoke("append").arg(" UNION ALL "));
        }

        insertAllMethodBlk._return(insertAllSbVar.invoke("toString"));

    }

    private String[] getAllFieldsForBatch(FJTable FJTable) {
        StringBuilder sqlParam = new StringBuilder("(");
        StringBuilder sqlValue = new StringBuilder("(");
        Map<String, FJColumn> columns = FJTable.getColumns();
        int i = 0;
        for (FJColumn fjColumn : columns.values()) {
            String name = fjColumn.getName();
            if (fjColumn.isIdentity()) {
                continue;
            }
            if (i > 0) {
                sqlParam.append(",");
                sqlValue.append(",");
            }
            sqlParam.append("`").append(name).append("`");
            sqlValue.append("#'{'list[{0}].").append(fjColumn.getFieldName())
                    .append("}");
            i++;
        }
        sqlParam.append(")");
        sqlValue.append(")");
        return new String[] {sqlParam.toString(), sqlValue.toString()};
    }

    @Override
    public void generate() {
        this.processEntity();
        this.processSQLBuilder();
        this.processDao();
        this.processService();
        this.processServiceImpl();
        if (this.isSupportTest()) {
            MybatisDaoTestMethodGenerator mybatisDaoTestMethodGenerator = new MybatisDaoTestMethodGenerator();
            mybatisDaoTestMethodGenerator.setCmTest(this.cmTest);
            mybatisDaoTestMethodGenerator.setEntityClass(this.entityClass);
            mybatisDaoTestMethodGenerator.setFjTable(this.fjTable);
            this.processDaoTest(mybatisDaoTestMethodGenerator);
            mybatisDaoTestMethodGenerator.generate();
            this.daoTestParam = mybatisDaoTestMethodGenerator.getDaoTestParam();

        }

    }
}