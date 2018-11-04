package com.fastjrun.codeg.generator;

import java.util.List;
import java.util.Map;

import com.fastjrun.codeg.common.CodeGException;
import com.fastjrun.codeg.common.CodeGMsgContants;
import com.fastjrun.codeg.common.FJColumn;
import com.fastjrun.codeg.common.FJTable;
import com.fastjrun.codeg.helper.MysqlSqlHelper;
import com.fastjrun.codeg.helper.SQLHelperFactory;
import com.fastjrun.codeg.helper.SqlHelper;
import com.fastjrun.helper.StringHelper;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JForLoop;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

/**
 * Mybatis Annotation
 */
public class BaseMybatisAFGenerator extends BaseCMGenerator {

    static String entityPackageName = "entity.";

    static String daoPackageName = "dao.";

    static String daoImplPackageName = "dao.impl.";

    static JClass parentClass = cm.ref("com.fastjrun.entity.BaseEntity");

    protected FJTable fjTable;

    protected JDefinedClass entityClass;

    protected JDefinedClass daoClass;

    protected JDefinedClass sqlBuilderClass;

    protected JDefinedClass daoImplClass;

    public FJTable getFjTable() {
        return fjTable;
    }

    public void setFjTable(FJTable fjTable) {
        this.fjTable = fjTable;
    }

    public JDefinedClass getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(JDefinedClass entityClass) {
        this.entityClass = entityClass;
    }

    public JDefinedClass getDaoClass() {
        return daoClass;
    }

    public void setDaoClass(JDefinedClass daoClass) {
        this.daoClass = daoClass;
    }

    public JDefinedClass getDaoImplClass() {
        return daoImplClass;
    }

    public void setDaoImplClass(JDefinedClass daoImplClass) {
        this.daoImplClass = daoImplClass;
    }

    protected void processEntity() {

        String className = this.packageNamePrefix + entityPackageName
                + fjTable.getClassName();

        try {
            this.entityClass = cm._class(className);

        } catch (JClassAlreadyExistsException e) {
            String msg = "fjTable class：" + fjTable.getName() + " is already exists.";
            this.commonLog.getLog().error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }

        this.entityClass._extends(parentClass);
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
        toStringMethodBlk.invoke(toStringSBVar, "append").arg(
                JExpr.lit(fjTable.getClassName()).plus(JExpr.lit(" [")));

        for (FJColumn FJColumn : columns.values()) {
            String name = FJColumn.getFieldName();
            hashCode += name.hashCode();
            String dataType = FJColumn.getDatatype();
            JType jType = cm.ref(dataType);
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
                toStringMethodBlk.invoke(toStringSBVar, "append").arg(
                        JExpr.lit(","));
            }
            index++;
            toStringMethodBlk.invoke(toStringSBVar, "append").arg(
                    JExpr.lit(name));
            toStringMethodBlk.invoke(toStringSBVar, "append").arg(
                    JExpr.lit("="));
            toStringMethodBlk.invoke(toStringSBVar, "append").arg(nameRef);

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
        toStringMethodBlk.invoke(toStringSBVar, "append").arg(
                JExpr.lit("]"));
        toStringMethodBlk._return(toStringSBVar.invoke("toString"));
        // private static final long serialVersionUID = 1L;
        this.entityClass.field(JMod.PRIVATE + JMod.STATIC + JMod.FINAL, cm.LONG,
                "serialVersionUID", JExpr.lit(hashCode));
    }

    protected void processDao() {
        String lowerCaseFirstOneClassName = StringHelper
                .toLowerCaseFirstOne(fjTable.getClassName());
        // 生成接口名：Base+fjTable.getClassName()+Dao
        try {
            this.daoClass = cm._class(
                    this.packageNamePrefix + "dao.Base" + fjTable.getClassName()
                            + "Dao", ClassType.INTERFACE);
        } catch (JClassAlreadyExistsException e) {
            String msg = "fjTable dao class：" + fjTable.getName() + " is already exists.";
            this.commonLog.getLog().error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }
        this.addClassDeclaration(this.daoClass);

        SqlHelper sqlHelper = SQLHelperFactory.getSQLHelper("mysql", fjTable);
        // insert方法
        JMethod insertMethod = this.daoClass.method(JMod.PUBLIC, cm.INT, "insert");
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
            JMethod selectByIdMethod = this.daoClass.method(JMod.PUBLIC, this.entityClass,
                    "selectByPK");
            selectByIdMethod.annotate(
                    cm.ref("org.apache.ibatis.annotations.Select")).param(
                    "value", sqlHelper.getSelectById());
            selectByIdMethod.annotate(
                    cm.ref("org.apache.ibatis.annotations.Options")).param(
                    "flushCache", true);
            // deleteById方法
            JMethod deleteByIdMethod = this.daoClass.method(JMod.PUBLIC, cm.INT,
                    "deleteByPK");
            deleteByIdMethod.annotate(
                    cm.ref("org.apache.ibatis.annotations.Delete")).param(
                    "value", sqlHelper.getDeleteById());
            // updateById方法
            JMethod updateByIdMethod = this.daoClass.method(JMod.PUBLIC, cm.INT,
                    "updateByPK");
            updateByIdMethod.annotate(
                    cm.ref("org.apache.ibatis.annotations.Update")).param(
                    "value", sqlHelper.getUpdateById());
            updateByIdMethod.param(this.entityClass, lowerCaseFirstOneClassName);
            for (int i = 0; i < primaryKeyColumnNames.size(); i++) {
                String key = primaryKeyColumnNames.get(i);
                FJColumn fjColumn = fjTable.getColumns().get(i);
                String fieldName = fjColumn.getFieldName();
                JVar selectFieldNameParam = selectByIdMethod.param(
                        cm.ref(fjColumn.getDatatype()), fieldName);
                selectFieldNameParam.annotate(
                        cm.ref("org.apache.ibatis.annotations.Param"))
                        .param("value", fieldName);
                JVar deleteFieldNameParam = deleteByIdMethod.param(
                        cm.ref(fjColumn.getDatatype()), fieldName);
                deleteFieldNameParam.annotate(
                        cm.ref("org.apache.ibatis.annotations.Param"))
                        .param("value", fieldName);
            }

        }
        // totalCount总数
        JMethod totalCountMethod = this.daoClass.method(JMod.PUBLIC, cm.INT,
                "totalCount");
        totalCountMethod.annotate(
                cm.ref("org.apache.ibatis.annotations.Select")).param(
                "value", sqlHelper.getTotalCount(0));
        totalCountMethod.annotate(
                cm.ref("org.apache.ibatis.annotations.Options")).param(
                "flushCache", true);
        // queryForList查询
        JMethod queryForListMethod = this.daoClass
                .method(JMod.PUBLIC,
                        cm.ref("java.util.List").narrow(this.entityClass),
                        "queryForList");
        queryForListMethod.annotate(
                cm.ref("org.apache.ibatis.annotations.Select")).param(
                "value", sqlHelper.getQueryForList(0));
        queryForListMethod.annotate(
                cm.ref("org.apache.ibatis.annotations.Options")).param(
                "flushCache", true);
        // queryForLimitList分页查询
        JMethod queryForLimitListMethod = this.daoClass.method(JMod.PUBLIC,
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
        JMethod totalCountConditionMethod = this.daoClass.method(JMod.PUBLIC, cm.INT,
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
        JMethod selectOneConditionMethod = this.daoClass.method(JMod.PUBLIC,
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
        JMethod queryForListConditionMethod = this.daoClass.method(JMod.PUBLIC, cm
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
        JMethod queryForLimitListConditionMethod = this.daoClass.method(JMod.PUBLIC,
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
        JMethod insertAllMethod = this.daoClass.method(JMod.PUBLIC, cm.INT,
                "insertAll");
        insertAllMethod.param(cm.ref("java.util.List").narrow(this.entityClass),
                lowerCaseFirstOneClassName + "s"); // 复数形式
        insertAllMethod
                .annotate(
                        cm.ref("org.apache.ibatis.annotations.InsertProvider"))
                .param("type", this.sqlBuilderClass).param("method", "insertAll");
    }

    /**
     * 生成SqlBuilder类 类名：Base+fjTable.getClassName()+SqlBuilder
     *
     * @return 定义了sqlBuilder接口的三个方法：totalCountCondition,selectOneCondition,
     * queryForListCondition
     */
    private void processSQLBuilder() {
        // 生成接口名：Base+fjTable.getClassName()+Dao
        try {
            this.sqlBuilderClass = cm._class(this.packageNamePrefix + "dao.Base"
                    + fjTable.getClassName() + "SqlBuilder", ClassType.CLASS);

        } catch (JClassAlreadyExistsException e) {
            String msg = "fjTable SqlBuilder class：" + fjTable.getName() + " is already exists.";
            this.commonLog.getLog().error(msg, e);
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
        JVar conditionVar = totalCountConditionMethodBlk.decl(cm
                .ref("String"), "condition", parameterVar.invoke("get")
                .arg("condition"));
        JVar sbVar = totalCountConditionMethodBlk.decl(
                cm.ref("StringBuilder"), "sb",
                JExpr._new(cm.ref("StringBuilder")));
        totalCountConditionMethodBlk.invoke(sbVar, "append").arg(
                sqlHelper.getTotalCount(1));
        totalCountConditionMethodBlk.invoke(sbVar, "append").arg(
                conditionVar);
        totalCountConditionMethodBlk._return(sbVar.invoke("toString"));
        // selectOneCondition方法
        JMethod queryWithConditionMethod = this.sqlBuilderClass.method(JMod.PUBLIC,
                cm.ref("String"), "queryWithCondition");
        parameterVar = queryWithConditionMethod.param(
                cm.ref("java.util.Map").narrow(cm.ref("String"))
                        .narrow(cm.ref("String")), "parameter");
        JBlock queryWithConditionMethodBlk = queryWithConditionMethod
                .body();
        conditionVar = queryWithConditionMethodBlk.decl(cm.ref("String"),
                "condition", parameterVar.invoke("get").arg("condition"));
        sbVar = queryWithConditionMethodBlk.decl(cm.ref("StringBuilder"),
                "sb", JExpr._new(cm.ref("StringBuilder")));
        queryWithConditionMethodBlk.invoke(sbVar, "append").arg(
                sqlHelper.getQueryForList(1));
        queryWithConditionMethodBlk.invoke(sbVar, "append").arg(
                conditionVar);
        queryWithConditionMethodBlk._return(sbVar.invoke("toString"));

        // insertAll
        JMethod insertAllMethod = this.sqlBuilderClass.method(JMod.PUBLIC, cm.ref("String"),
                "insertAll");
        parameterVar = insertAllMethod
                .param(cm
                                .ref("java.util.Map")
                                .narrow(cm.ref("String"))
                                .narrow(cm.ref("java.util.List").narrow(this.entityClass)),
                        "parameter");
        JBlock insertAllMethodBlk = insertAllMethod.body();
        JVar listVar = insertAllMethodBlk.decl(cm.ref("java.util.List")
                        .narrow(this.entityClass), fjTable.getClassName() + "s",
                parameterVar.invoke("get").arg("list"));
        sbVar = insertAllMethodBlk.decl(cm.ref("StringBuilder"), "sb",
                JExpr._new(cm.ref("StringBuilder")));
        String[] sqlParamAndValue = getAllFieldsForBatch(fjTable);
        JVar messageFormatVar = insertAllMethodBlk.decl(
                cm.ref("java.text.MessageFormat"),
                "messageFormat",
                JExpr._new(cm.ref("java.text.MessageFormat")).arg(
                        sqlParamAndValue[1]));
        // mysql数据库
        if (sqlHelper instanceof MysqlSqlHelper) {
            insertAllMethodBlk.invoke(sbVar, "append").arg(
                    "INSERT INTO " + fjTable.getName() + sqlParamAndValue[0]
                            + " VALUES");
            JForLoop forLoop = insertAllMethodBlk._for();
            JVar i = forLoop.init(cm.INT, "i", JExpr.lit(0));
            forLoop.test(i.lt(listVar.invoke("size")));
            forLoop.update(i.incr());
            JBlock forBlk = forLoop.body();
            forBlk.invoke(JExpr.ref("sb"), "append").arg(
                    messageFormatVar.invoke("format").arg(
                            JExpr.newArray(cm.ref("java.lang.Object")).add(
                                    JExpr.ref("i"))));
            JConditional jif = forBlk._if(JExpr.ref("i").lt(
                    listVar.invoke("size").minus(JExpr.lit(1))));
            JBlock ifBlk = jif._then();
            ifBlk.invoke(JExpr.ref("sb"), "append").arg(",");
        } else { // oracle数据库
            insertAllMethodBlk.invoke(sbVar, "append").arg(
                    "INSERT INTO " + fjTable.getName() + sqlParamAndValue[0]
                            + " ");
            JForLoop forLoop = insertAllMethodBlk._for();
            JVar i = forLoop.init(cm.INT, "i", JExpr.lit(0));
            forLoop.test(i.lt(listVar.invoke("size")));
            forLoop.update(i.incr());
            JBlock forBlk = forLoop.body();
            forBlk.invoke(JExpr.ref("sb"), "append").arg("SELECT ");
            forBlk.invoke(JExpr.ref("sb"), "append").arg(
                    messageFormatVar.invoke("format").arg(
                            JExpr.newArray(cm.ref("java.lang.Object")).add(
                                    JExpr.ref("i"))));
            forBlk.invoke(JExpr.ref("sb"), "append").arg(" FROM DUAL ");
            JConditional jif = forBlk._if(JExpr.ref("i").lt(
                    listVar.invoke("size").minus(JExpr.lit(1))));
            JBlock ifBlk = jif._then();
            ifBlk.invoke(JExpr.ref("sb"), "append").arg(" UNION ALL ");
        }
        JClass sys = cm.ref("java.lang.System");
        JFieldRef ot = sys.staticRef("out");
        insertAllMethodBlk.invoke(ot, "println").arg(
                sbVar.invoke("toString"));
        insertAllMethodBlk._return(sbVar.invoke("toString"));

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

    }
}