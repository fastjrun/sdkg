package com.fastjrun.codeg.generator;

import java.util.List;
import java.util.Properties;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fastjrun.codeg.common.FJColumn;
import com.fastjrun.codeg.common.FJTable;
import com.fastjrun.codeg.generator.common.BaseCMGenerator;
import com.fastjrun.helper.StringHelper;
import com.fastjrun.utils.JacksonUtils;
import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JAnnotationUse;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JForLoop;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JVar;

/**
 * Mybatis Annotation
 */
public class MybatisDaoTestMethodGenerator extends BaseCMGenerator {

    protected FJTable fjTable;
    protected AbstractJClass entityClass;
    protected JFieldVar fieldVar;
    protected JDefinedClass daoTestClass;
    protected Properties daoTestParam;
    private String lowerCaseFirstOneClassName;

    public Properties getDaoTestParam() {
        return daoTestParam;
    }

    public void setDaoTestParam(Properties daoTestParam) {
        this.daoTestParam = daoTestParam;
    }

    public AbstractJClass getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(AbstractJClass entityClass) {
        this.entityClass = entityClass;
    }

    public FJTable getFjTable() {
        return fjTable;
    }

    public void setFjTable(FJTable fjTable) {
        this.fjTable = fjTable;
    }

    public JDefinedClass getDaoTestClass() {
        return daoTestClass;
    }

    public void setDaoTestClass(JDefinedClass daoTestClass) {
        this.daoTestClass = daoTestClass;
    }

    public JFieldVar getFieldVar() {
        return fieldVar;
    }

    public void setFieldVar(JFieldVar fieldVar) {
        this.fieldVar = fieldVar;
    }

    protected void processInsert() {
        // testInsert方法
        JMethod method = this.daoTestClass.method(JMod.NONE, cmTest.VOID, "testInsert");
        JAnnotationUse jAnnotationUse = method
                .annotate(cmTest.ref("org.testng.annotations.Test"));
        JBlock methodBlk = method.body();
        jAnnotationUse.param("dataProvider", "loadParam");
        JVar reqParamsJsonStrAndAssertJVar = method.param(cmTest.ref("String"),
                "reqParamsJsonStrAndAssert");
        JVar paramsJsonJVar = methodBlk.decl(cmTest.ref(JSONObjectClassName), "paramsJson", JExpr._this()
                .invoke("generateParamJson").arg(reqParamsJsonStrAndAssertJVar));
        JVar entityVar =
                methodBlk.decl(this.entityClass, lowerCaseFirstOneClassName, JExpr._null());
        JVar entityStrVar = methodBlk.decl(cmTest.ref(JSONObjectClassName),
                lowerCaseFirstOneClassName + "Json",
                paramsJsonJVar.invoke("get").arg(JExpr.lit(lowerCaseFirstOneClassName)));
        JBlock jNotNullBlock =
                methodBlk._if(entityStrVar.ne(JExpr._null()))._then();
        jNotNullBlock.assign(entityVar, cmTest.ref(JacksonUtilsClassName).staticInvoke("readValue")
                .arg(paramsJsonJVar.invoke("toString"))
                .arg(this.entityClass.dotclass()));
        JInvocation jInvocationTest =
                JExpr.invoke(fieldVar, "insert").arg(entityVar);

        ObjectNode methodParamInJsonObject = JacksonUtils.createObjectNode();
        methodParamInJsonObject.put(lowerCaseFirstOneClassName, this.fjTable.parseDescToJson());

        StringBuilder sb = new StringBuilder(this.daoTestClass.name()).append(".testInsert.n");
        this.daoTestParam.put(sb.toString(), methodParamInJsonObject
                .toString().replaceAll("\n", "").replaceAll("\r", "")
                .trim());

        JVar resJVar = methodBlk.decl(cmTest.INT, "res", jInvocationTest);
        methodBlk.add(JExpr.ref("log").invoke("debug").arg(JExpr.lit("res={}")).arg(resJVar));

    }

    protected void processSelectByPK() {
        List<String> primaryKeyColumnNames = fjTable.getPrimaryKeyColumnNames();
        if (primaryKeyColumnNames != null) {
            JInvocation jInvocationTest =
                    JExpr.invoke(fieldVar, "selectByPK");
            // testSelectByPK方法
            JBlock methodBlk = this.processMethod("selectByPK", primaryKeyColumnNames, jInvocationTest);

            JVar entityJVar = methodBlk.decl(this.entityClass, lowerCaseFirstOneClassName, jInvocationTest);
            methodBlk.add(JExpr.ref("log").invoke("debug").arg(JExpr.lit(lowerCaseFirstOneClassName + "={}")).arg
                    (entityJVar));
        }

    }

    private JBlock processMethod(String methodName, List<String> primaryKeyColumnNames, JInvocation jInvocationTest) {
        JMethod method = this.prepareMethod(methodName);
        JBlock methodBlk = method.body();
        JVar reqParamsJsonStrAndAssertJVar = method.param(cmTest.ref("String"),
                "reqParamsJsonStrAndAssert");
        JVar paramsJsonJVar = methodBlk.decl(cmTest.ref(JSONObjectClassName), "paramsJson", JExpr._this()
                .invoke("generateParamJson").arg(reqParamsJsonStrAndAssertJVar));

        ObjectNode methodParamInJsonObject = JacksonUtils.createObjectNode();
        for (int i = 0; i < primaryKeyColumnNames.size(); i++) {
            String key = primaryKeyColumnNames.get(i);
            FJColumn fjColumn = fjTable.getColumns().get(key);
            String fieldName = fjColumn.getFieldName();
            AbstractJClass jType = cmTest.ref(fjColumn.getDatatype());
            methodParamInJsonObject.put(fieldName, jType.name());
            JVar selectFieldVar = methodBlk.decl(jType, fieldName, JExpr._null());
            JVar selectFieldStrVar = methodBlk.decl(cmTest.ref(JSONObjectClassName),
                    fieldName + "Json",
                    paramsJsonJVar.invoke("get").arg(JExpr.lit(fieldName)));
            String jsonInvokeMethodName = JacksonUtils.invokeMethodName(jType.name());
            JBlock jNotNullBlock =
                    methodBlk._if(selectFieldStrVar.ne(JExpr._null()))._then();
            jNotNullBlock.assign(selectFieldVar, selectFieldStrVar.invoke(jsonInvokeMethodName));
            jInvocationTest.arg(selectFieldVar);
        }
        StringBuilder sb = new StringBuilder(this.daoTestClass.name()).append(".test")
                .append(StringHelper.toUpperCaseFirstOne(methodName)).append(".n");
        this.daoTestParam.put(sb.toString(), methodParamInJsonObject
                .toString().replaceAll("\n", "").replaceAll("\r", "")
                .trim());
        return methodBlk;
    }

    private JMethod prepareMethod(String methodName) {
        JMethod method = this.daoTestClass.method(JMod.NONE, cmTest.VOID,
                "test" + StringHelper.toUpperCaseFirstOne(methodName));
        JAnnotationUse jAnnotationUse = method
                .annotate(cmTest.ref("org.testng.annotations.Test"));
        jAnnotationUse.param("dataProvider", "loadParam");
        return method;
    }

    protected void processUpdateByPK() {
        // testUpdateByPK方法
        JMethod method = this.prepareMethod("testUpdateByPK");
        JBlock methodBlk = method.body();
        JVar reqParamsJsonStrAndAssertJVar = method.param(cmTest.ref("String"),
                "reqParamsJsonStrAndAssert");
        JVar paramsJsonJVar = methodBlk.decl(cmTest.ref(JSONObjectClassName), "paramsJson", JExpr._this()
                .invoke("generateParamJson").arg(reqParamsJsonStrAndAssertJVar));
        JVar entityVar =
                methodBlk.decl(this.entityClass, lowerCaseFirstOneClassName, JExpr._null());
        JVar entityStrVar = methodBlk.decl(cmTest.ref(JSONObjectClassName),
                lowerCaseFirstOneClassName + "Json",
                paramsJsonJVar.invoke("get").arg(JExpr.lit(lowerCaseFirstOneClassName)));
        JBlock jNotNullBlock =
                methodBlk._if(entityStrVar.ne(JExpr._null()))._then();
        jNotNullBlock.assign(entityVar, cmTest.ref(JacksonUtilsClassName).staticInvoke("readValue")
                .arg(paramsJsonJVar.invoke("toString"))
                .arg(this.entityClass.dotclass()));
        JInvocation jInvocationTest =
                JExpr.invoke(fieldVar, "updateByPK").arg(entityVar);
        ObjectNode methodParamInJsonObject = JacksonUtils.createObjectNode();
        methodParamInJsonObject.put(lowerCaseFirstOneClassName, this.fjTable.parseDescToJson());

        StringBuilder sb = new StringBuilder(this.daoTestClass.name()).append(".testUpdateByPK.n");
        this.daoTestParam.put(sb.toString(), methodParamInJsonObject
                .toString().replaceAll("\n", "").replaceAll("\r", "")
                .trim());

        JVar resJVar = methodBlk.decl(cmTest.INT, "res", jInvocationTest);
        methodBlk.add(JExpr.ref("log").invoke("debug").arg(JExpr.lit("res={}")).arg(resJVar));
    }

    protected void processDeleteByPK() {
        List<String> primaryKeyColumnNames = fjTable.getPrimaryKeyColumnNames();
        if (primaryKeyColumnNames != null) {
            JInvocation jInvocationTest =
                    JExpr.invoke(fieldVar, "deleteByPK");
            // testDeleteByPK方法
            JBlock methodBlk = this.processMethod("deleteByPK", primaryKeyColumnNames, jInvocationTest);

            JVar resJVar = methodBlk.decl(cmTest.INT, "res", jInvocationTest);
            methodBlk.add(JExpr.ref("log").invoke("debug").arg(JExpr.lit("res={}")).arg(resJVar));
        }
    }

    protected void processTotalCount() {
        // testTotalCount总数
        JMethod method = this.prepareMethod("testTotalCount");
        JBlock methodBlk = method.body();

        JInvocation jInvocationTest =
                JExpr.invoke(fieldVar, "totalCount");

        ObjectNode methodParamInJsonObject = JacksonUtils.createObjectNode();

        StringBuilder sb = new StringBuilder(this.daoTestClass.name()).append(".testTotalCount.n");
        this.daoTestParam.put(sb.toString(), methodParamInJsonObject
                .toString().replaceAll("\n", "").replaceAll("\r", "")
                .trim());

        JVar resJVar = methodBlk.decl(cmTest.INT, "res", jInvocationTest);
        methodBlk.add(JExpr.ref("log").invoke("debug").arg(JExpr.lit("res={}")).arg(resJVar));
    }

    protected void processQueryForList() {
        // testQueryForList查询
        JMethod method = this.prepareMethod("testQueryForList");
        JBlock methodBlk = method.body();

        JInvocation jInvocationTest =
                JExpr.invoke(fieldVar, "queryForList");
        ObjectNode methodParamInJsonObject = JacksonUtils.createObjectNode();

        StringBuilder sb = new StringBuilder(this.daoTestClass.name()).append(".testQueryForList.n");
        this.daoTestParam.put(sb.toString(), methodParamInJsonObject
                .toString().replaceAll("\n", "").replaceAll("\r", "")
                .trim());

        JVar listJVar = methodBlk.decl(cmTest.ref("java.util.List").narrow(this.entityClass), "list", jInvocationTest);
        JBlock jNotNullBlock =
                methodBlk._if(listJVar.ne(JExpr._null()))._then();
        JForLoop forLoop = jNotNullBlock._for();
        JVar initIndexVar = forLoop.init(cmTest.INT, "index", JExpr.lit(0));
        forLoop.test(initIndexVar.lt(listJVar.invoke("size")));
        forLoop.update(initIndexVar.incr());
        JBlock forBlock1 = forLoop.body();
        JVar itemIndexVar =
                forBlock1.decl(this.entityClass, lowerCaseFirstOneClassName,
                        listJVar.invoke("get").arg(initIndexVar));
        forBlock1.add(JExpr.ref("log").invoke("debug").arg(JExpr.lit(lowerCaseFirstOneClassName + "{}={}"))
                .arg(initIndexVar).arg(itemIndexVar));
    }

    protected void processQueryForLimitList() {
        // testQueryForLimitList分页查询
        JMethod method = this.prepareMethod("testQueryForLimitList");
        JBlock methodBlk = method.body();

        JVar reqParamsJsonStrAndAssertJVar = method.param(cmTest.ref("String"),
                "reqParamsJsonStrAndAssert");
        JVar paramsJsonJVar = methodBlk.decl(cmTest.ref(JSONObjectClassName), "paramsJson", JExpr._this()
                .invoke("generateParamJson").arg(reqParamsJsonStrAndAssertJVar));
        JVar rowBoundsVar =
                methodBlk.decl(cmTest.ref("org.apache.ibatis.session.RowBounds"), "rowBounds", JExpr._null());
        JVar rowBoundsStrVar = methodBlk.decl(cmTest.ref(JSONObjectClassName),
                "rowBoundsJson",
                paramsJsonJVar.invoke("get").arg(JExpr.lit("rowBounds")));
        JBlock jNotNullBlock =
                methodBlk._if(rowBoundsStrVar.ne(JExpr._null()))._then();
        jNotNullBlock.assign(rowBoundsVar, cmTest.ref(JacksonUtilsClassName).staticInvoke("readValue")
                .arg(rowBoundsStrVar.invoke("toString"))
                .arg(cmTest.ref("org.apache.ibatis.session.RowBounds").dotclass()));
        JInvocation jInvocationTest =
                JExpr.invoke(fieldVar, "queryForLimitList").arg(rowBoundsVar);

        ObjectNode methodParamInJsonObject = JacksonUtils.createObjectNode();
        ObjectNode rowBoundsInJsonObject = JacksonUtils.createObjectNode();
        rowBoundsInJsonObject.put("offset", "int");
        rowBoundsInJsonObject.put("limit", "int");
        methodParamInJsonObject.put("rowBounds", rowBoundsInJsonObject.toString());

        StringBuilder sb = new StringBuilder(this.daoTestClass.name()).append(".testQueryForLimitList.n");
        this.daoTestParam.put(sb.toString(), methodParamInJsonObject
                .toString().replaceAll("\n", "").replaceAll("\r", "")
                .trim());

        JVar listJVar = methodBlk.decl(cmTest.ref("java.util.List").narrow(this.entityClass), "list", jInvocationTest);

        jNotNullBlock = methodBlk._if(listJVar.ne(JExpr._null()))._then();
        JForLoop forLoop = jNotNullBlock._for();
        JVar initIndexVar = forLoop.init(cmTest.INT, "index", JExpr.lit(0));
        forLoop.test(initIndexVar.lt(listJVar.invoke("size")));
        forLoop.update(initIndexVar.incr());
        JBlock forBlock1 = forLoop.body();
        JVar itemIndexVar =
                forBlock1.decl(this.entityClass, lowerCaseFirstOneClassName,
                        listJVar.invoke("get").arg(initIndexVar));
        forBlock1.add(JExpr.ref("log").invoke("debug").arg(JExpr.lit(lowerCaseFirstOneClassName + "{}={}"))
                .arg(initIndexVar).arg(itemIndexVar));
    }

    protected void processTotalCountCondition() {
        // testTotalCountCondition总数
        JMethod method = this.prepareMethod("testTotalCountCondition");
        JBlock methodBlk = method.body();

        JVar reqParamsJsonStrAndAssertJVar = method.param(cmTest.ref("String"),
                "reqParamsJsonStrAndAssert");
        JVar paramsJsonJVar = methodBlk.decl(cmTest.ref(JSONObjectClassName), "paramsJson", JExpr._this()
                .invoke("generateParamJson").arg(reqParamsJsonStrAndAssertJVar));
        JVar conditionVar =
                methodBlk.decl(cmTest.ref("String"), "condition", JExpr._null());
        JVar conditionStrVar = methodBlk.decl(cmTest.ref(JSONObjectClassName),
                "conditionJson",
                paramsJsonJVar.invoke("get").arg(JExpr.lit("condition")));
        JBlock jNotNullBlock =
                methodBlk._if(conditionStrVar.ne(JExpr._null()))._then();
        jNotNullBlock.assign(conditionVar, conditionStrVar.invoke("asText"));
        JInvocation jInvocationTest =
                JExpr.invoke(fieldVar, "totalCountCondition").arg(conditionVar);

        ObjectNode methodParamInJsonObject = JacksonUtils.createObjectNode();
        methodParamInJsonObject.put("condition", "String");

        StringBuilder sb = new StringBuilder(this.daoTestClass.name()).append(".testTotalCountCondition.n");
        this.daoTestParam.put(sb.toString(), methodParamInJsonObject
                .toString().replaceAll("\n", "").replaceAll("\r", "")
                .trim());

        JVar resJVar = methodBlk.decl(cmTest.INT, "res", jInvocationTest);
        methodBlk.add(JExpr.ref("log").invoke("debug").arg(JExpr.lit("res={}")).arg(resJVar));

    }

    protected void processSelectOneCondition() {
        // testSelectOneCondition查询
        JMethod method = this.prepareMethod("testSelectOneCondition");
        JBlock methodBlk = method.body();

        JVar reqParamsJsonStrAndAssertJVar = method.param(cmTest.ref("String"),
                "reqParamsJsonStrAndAssert");
        JVar paramsJsonJVar = methodBlk.decl(cmTest.ref(JSONObjectClassName), "paramsJson", JExpr._this()
                .invoke("generateParamJson").arg(reqParamsJsonStrAndAssertJVar));
        JVar conditionVar =
                methodBlk.decl(cmTest.ref("String"), "condition", JExpr._null());
        JVar conditionStrVar = methodBlk.decl(cmTest.ref(JSONObjectClassName),
                "conditionJson",
                paramsJsonJVar.invoke("get").arg(JExpr.lit("condition")));
        JBlock jNotNullBlock =
                methodBlk._if(conditionStrVar.ne(JExpr._null()))._then();
        jNotNullBlock.assign(conditionVar, conditionStrVar.invoke("asText"));
        JInvocation jInvocationTest =
                JExpr.invoke(fieldVar, "selectOneCondition").arg(conditionVar);

        ObjectNode methodParamInJsonObject = JacksonUtils.createObjectNode();
        methodParamInJsonObject.put("condition", "String");

        StringBuilder sb = new StringBuilder(this.daoTestClass.name()).append(".testSelectOneCondition.n");
        this.daoTestParam.put(sb.toString(), methodParamInJsonObject
                .toString().replaceAll("\n", "").replaceAll("\r", "")
                .trim());

        JVar entityJVar = methodBlk.decl(this.entityClass, lowerCaseFirstOneClassName, jInvocationTest);
        methodBlk.add(JExpr.ref("log").invoke("debug").arg(JExpr.lit(lowerCaseFirstOneClassName + "={}")).arg
                (entityJVar));
    }

    protected void processQueryForListCondition() {
        // testQueryForListCondition查询
        JMethod method = this.prepareMethod("testQueryForListCondition");
        JBlock methodBlk = method.body();

        JVar reqParamsJsonStrAndAssertJVar = method.param(cmTest.ref("String"),
                "reqParamsJsonStrAndAssert");
        JVar paramsJsonJVar = methodBlk.decl(cmTest.ref(JSONObjectClassName), "paramsJson", JExpr._this()
                .invoke("generateParamJson").arg(reqParamsJsonStrAndAssertJVar));
        JVar conditionVar =
                methodBlk.decl(cmTest.ref("String"), "condition", JExpr._null());
        JVar conditionStrVar = methodBlk.decl(cmTest.ref(JSONObjectClassName),
                "conditionJson",
                paramsJsonJVar.invoke("get").arg("condition"));
        JBlock jNotNullBlock =
                methodBlk._if(conditionStrVar.ne(JExpr._null()))._then();
        jNotNullBlock.assign(conditionVar, conditionStrVar.invoke("asText"));
        JInvocation jInvocationTest =
                JExpr.invoke(fieldVar, "queryForListCondition").arg(conditionVar);

        ObjectNode methodParamInJsonObject = JacksonUtils.createObjectNode();
        methodParamInJsonObject.put("condition", "String");

        StringBuilder sb = new StringBuilder(this.daoTestClass.name()).append(".testSelectOneCondition.n");
        this.daoTestParam.put(sb.toString(), methodParamInJsonObject
                .toString().replaceAll("\n", "").replaceAll("\r", "")
                .trim());

        JVar listJVar = methodBlk.decl(cmTest.ref("java.util.List").narrow(this.entityClass), "list", jInvocationTest);
        jNotNullBlock = methodBlk._if(listJVar.ne(JExpr._null()))._then();
        JForLoop forLoop = jNotNullBlock._for();
        JVar initIndexVar = forLoop.init(cmTest.INT, "index", JExpr.lit(0));
        forLoop.test(initIndexVar.lt(listJVar.invoke("size")));
        forLoop.update(initIndexVar.incr());
        JBlock forBlock1 = forLoop.body();
        JVar itemIndexVar =
                forBlock1.decl(this.entityClass, lowerCaseFirstOneClassName,
                        listJVar.invoke("get").arg(initIndexVar));
        forBlock1.add(JExpr.ref("log").invoke("debug").arg(JExpr.lit(lowerCaseFirstOneClassName + "{}={}"))
                .arg(initIndexVar).arg(itemIndexVar));
    }

    protected void processQueryForLimitListCondition() {
        // testQueryForLimitListCondition分页查询
        JMethod method = this.prepareMethod("testQueryForLimitListCondition");
        JBlock methodBlk = method.body();

        JVar reqParamsJsonStrAndAssertJVar = method.param(cmTest.ref("String"),
                "reqParamsJsonStrAndAssert");
        JVar paramsJsonJVar = methodBlk.decl(cmTest.ref(JSONObjectClassName), "paramsJson", JExpr._this()
                .invoke("generateParamJson").arg(reqParamsJsonStrAndAssertJVar));
        JVar conditionVar =
                methodBlk.decl(cmTest.ref("String"), "condition", JExpr._null());
        JVar conditionStrVar = methodBlk.decl(cmTest.ref(JSONObjectClassName),
                "conditionJson",
                paramsJsonJVar.invoke("get").arg(JExpr.lit("condition")));
        JBlock jNotNullBlock =
                methodBlk._if(conditionStrVar.ne(JExpr._null()))._then();
        jNotNullBlock.assign(conditionVar, conditionStrVar.invoke("asText"));
        JInvocation jInvocationTest =
                JExpr.invoke(fieldVar, "queryForLimitListCondition").arg(conditionVar);
        JVar rowBoundsVar =
                methodBlk.decl(cmTest.ref("org.apache.ibatis.session.RowBounds"), "rowBounds", JExpr._null());
        JVar rowBoundsStrVar = methodBlk.decl(cmTest.ref(JSONObjectClassName),
                "rowBoundsJson",
                paramsJsonJVar.invoke("get").arg(JExpr.lit("rowBounds")));
        jNotNullBlock =
                methodBlk._if(rowBoundsStrVar.ne(JExpr._null()))._then();
        jNotNullBlock.assign(rowBoundsVar, cmTest.ref(JacksonUtilsClassName).staticInvoke("readValue")
                .arg(rowBoundsStrVar.invoke("toString"))
                .arg(cmTest.ref("org.apache.ibatis.session.RowBounds").dotclass()));
        jInvocationTest.arg(rowBoundsVar);

        ObjectNode methodParamInJsonObject = JacksonUtils.createObjectNode();
        ObjectNode rowBoundsInJsonObject = JacksonUtils.createObjectNode();
        rowBoundsInJsonObject.put("offset", "int");
        rowBoundsInJsonObject.put("limit", "int");
        methodParamInJsonObject.put("rowBounds", rowBoundsInJsonObject.toString());
        methodParamInJsonObject.put("condition", "String");

        StringBuilder sb = new StringBuilder(this.daoTestClass.name()).append(".testQueryForLimitListCondition.n");
        this.daoTestParam.put(sb.toString(), methodParamInJsonObject
                .toString().replaceAll("\n", "").replaceAll("\r", "")
                .trim());

        JVar listJVar = methodBlk.decl(cmTest.ref("java.util.List").narrow(this.entityClass), "list", jInvocationTest);

        jNotNullBlock = methodBlk._if(listJVar.ne(JExpr._null()))._then();
        JForLoop forLoop = jNotNullBlock._for();
        JVar initIndexVar = forLoop.init(cmTest.INT, "index", JExpr.lit(0));
        forLoop.test(initIndexVar.lt(listJVar.invoke("size")));
        forLoop.update(initIndexVar.incr());
        JBlock forBlock1 = forLoop.body();
        JVar itemIndexVar =
                forBlock1.decl(this.entityClass, lowerCaseFirstOneClassName,
                        listJVar.invoke("get").arg(initIndexVar));
        forBlock1.add(JExpr.ref("log").invoke("debug").arg(JExpr.lit(lowerCaseFirstOneClassName + "{}={}"))
                .arg(initIndexVar).arg(itemIndexVar));
    }

    protected void processInsertAll() {
        // testInsertAll方法批量插入
        JMethod method = this.prepareMethod("testInsertAll");
        JBlock methodBlk = method.body();

        JVar reqParamsJsonStrAndAssertJVar = method.param(cmTest.ref("String"),
                "reqParamsJsonStrAndAssert");
        JVar paramsJsonJVar = methodBlk.decl(cmTest.ref(JSONObjectClassName), "paramsJson", JExpr._this()
                .invoke("generateParamJson").arg(reqParamsJsonStrAndAssertJVar));
        JVar listVar =
                methodBlk.decl(cmTest.ref("java.util.List").narrow(this.entityClass), lowerCaseFirstOneClassName + "s",
                        JExpr._null());
        JVar listStrVar = methodBlk.decl(cmTest.ref(JSONObjectClassName),
                "listJson",
                paramsJsonJVar.invoke("get").arg(JExpr.lit(lowerCaseFirstOneClassName + "s")));
        JBlock jNotNullBlock =
                methodBlk._if(listStrVar.ne(JExpr._null()))._then();
        jNotNullBlock.assign(listVar, cmTest.ref(JacksonUtilsClassName).staticInvoke("readList")
                .arg(listStrVar).arg(this.entityClass.dotclass()));
        JInvocation jInvocationTest =
                JExpr.invoke(fieldVar, "insertAll").arg(listVar);

        JVar resJVar = methodBlk.decl(cmTest.INT, "res", jInvocationTest);
        methodBlk.add(JExpr.ref("log").invoke("debug").arg(JExpr.lit("res={}")).arg(resJVar));
    }

    @Override
    public void generate() {
        this.lowerCaseFirstOneClassName = StringHelper
                .toLowerCaseFirstOne(fjTable.getClassName());

        this.daoTestParam = new Properties();
        this.processInsert();
        this.processSelectByPK();
        this.processUpdateByPK();
        this.processDeleteByPK();
        this.processTotalCount();
        this.processQueryForList();
        this.processQueryForLimitList();
        this.processTotalCountCondition();
        this.processSelectOneCondition();
        this.processQueryForListCondition();
        this.processQueryForLimitListCondition();
        this.processInsertAll();

    }
}