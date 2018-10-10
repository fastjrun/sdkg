package com.fastjrun.codeg.helper;

import com.fastjrun.codeg.common.FJColumn;
import com.fastjrun.codeg.common.FJTable;

import java.util.Map;

/**
 * 构造Sql语句
 */
public class OracleSqlHelper implements SqlHelper {

    FJTable fjTable;

    public OracleSqlHelper(FJTable fjTable) {
        this.fjTable = fjTable;
    }

    /* (non-Javadoc)
     * @see com.fastjrun.codeg.helper.SQLHelper#getInsert()
     */
    @Override
    public String getInsert() {
        StringBuffer sqlInsert = new StringBuffer("insert into ");
        sqlInsert.append(fjTable.getName()).append("(");

        StringBuffer sqlParam = new StringBuffer();
        StringBuffer sqlValue = new StringBuffer();
        Map<String, FJColumn> columns = fjTable.getColumns();
        int i = 0;
        for (FJColumn fjColumn : columns.values()) {
            String name = fjColumn.getName();
            String identity = fjColumn.getIdentity();
            if (identity.equals("1")) {
                continue;
            }
            if (i > 0) {
                sqlParam.append(",");
                sqlValue.append(",");
            }
            sqlParam.append("\"").append(name).append("\"");
            sqlValue.append("#{").append(fjColumn.getFieldName()).append("}");
            i++;
        }
        sqlInsert.append(sqlParam);
        sqlInsert.append(") values(");
        sqlInsert.append(sqlValue);
        sqlInsert.append(")");
        return sqlInsert.toString();
    }

    /* (non-Javadoc)
     * @see com.fastjrun.codeg.helper.SQLHelper#getUpdateById()
     */
    @Override
    public String getUpdateById() {
        StringBuffer sql = new StringBuffer();
        sql.append("update ").append(fjTable.getName()).append(" set ");
        Map<String, FJColumn> fields = fjTable.getColumns();
        Map<String, FJColumn> keyFields = fjTable.getPrimaryKeyColumns();
        int i = 0;
        for (FJColumn fjColumn : fields.values()) {
            String name = fjColumn.getName();
            if (keyFields.get(name) != null) {
                continue;
            }
            if (i > 0) {
                sql.append(",");
            }
            sql.append("\"").append(name).append("\" = #{").append(fjColumn.getFieldName()).append("}");
            i++;
        }
        sql.append(" where ");
        i = 0;
        for (FJColumn fjColumn : keyFields.values()) {
            String name = fjColumn.getName();
            if (i > 0) {
                sql.append(" and ");
            }
            sql.append("\"").append(name).append("\" = #{").append(fjColumn.getFieldName()).append("}");
            i++;
        }
        return sql.toString();
    }

    /* (non-Javadoc)
     * @see com.fastjrun.codeg.helper.SQLHelper#getSelectById()
     */
    @Override
    public String getSelectById() {
        StringBuffer sql = new StringBuffer();
        sql.append("select ");
        Map<String, FJColumn> fields = fjTable.getColumns();
        Map<String, FJColumn> keyFields = fjTable.getPrimaryKeyColumns();
        int i = 0;
        for (FJColumn fjColumn : fields.values()) {
            String name = fjColumn.getName();
            if (i > 0) {
                sql.append(",");
            }
            sql.append("\"").append(name).append("\" ").append(fjColumn.getFieldName());
            i++;
        }

        sql.append(" from ").append(fjTable.getName()).append(" where ");
        i = 0;
        for (FJColumn fjColumn : keyFields.values()) {
            String name = fjColumn.getName();
            if (i > 0) {
                sql.append(" and ");
            }
            sql.append("\"").append(name).append("\" = #{").append(fjColumn.getFieldName()).append("}");
            i++;
        }
        return sql.toString();
    }

    /* (non-Javadoc)
     * @see com.fastjrun.codeg.helper.SQLHelper#getSelectByCondition()
     */
    @Override
    public String getSelectByCondition() {
        StringBuffer sql = new StringBuffer();
        sql.append("select ");
        Map<String, FJColumn> fields = fjTable.getColumns();
        int i = 0;
        for (FJColumn fjColumn : fields.values()) {
            String name = fjColumn.getName();
            if (i > 0) {
                sql.append(",");
            }
            sql.append("\"").append(name).append("\" ").append(fjColumn.getFieldName());
            i++;
        }
        sql.append(" from ").append(fjTable.getName());
        sql.append(" where 1 = 1 ");
        return sql.toString();
    }

    /* (non-Javadoc)
     * @see com.fastjrun.codeg.helper.SQLHelper#getDeleteById()
     */
    @Override
    public String getDeleteById() {
        StringBuffer sql = new StringBuffer();
        sql.append("delete from ").append(fjTable.getName()).append(" where ");
        Map<String, FJColumn> keyFields = fjTable.getPrimaryKeyColumns();
        int i = 0;
        for (FJColumn fjColumn : keyFields.values()) {
            String name = fjColumn.getName();
            if (i > 0) {
                sql.append(" and ");
            }
            sql.append("\"").append(name).append("\" = #{").append(fjColumn.getFieldName()).append("}");
            i++;
        }
        return sql.toString();
    }

    /* (non-Javadoc)
     * @see com.fastjrun.codeg.helper.SQLHelper#getTotalCount(int)
     */
    @Override
    public String getTotalCount(int conditionAndlimit) {
        StringBuffer sql = new StringBuffer();
        sql.append("select count(*) from ").append(fjTable.getName());
        if (conditionAndlimit == 1) {
            sql.append(" where 1 = 1 ");
        }
        return sql.toString();
    }

    /* (non-Javadoc)
     * @see com.fastjrun.codeg.helper.SQLHelper#getQueryForList(int)
     */
    @Override
    public String getQueryForList(int conditionAndlimit) {
        StringBuffer sql = new StringBuffer();
        sql.append("select ");
        Map<String, FJColumn> fields = fjTable.getColumns();
        int i = 0;
        for (FJColumn fjColumn : fields.values()) {
            String name = fjColumn.getName();
            if (i > 0) {
                sql.append(",");
            }
            sql.append("\"").append(name).append("\" ").append(fjColumn.getFieldName());
            i++;
        }
        sql.append(" from ").append(fjTable.getName());
        if (conditionAndlimit == 1) {
            sql.append(" where 1 = 1 ");
        }
        return sql.toString();
    }
}
