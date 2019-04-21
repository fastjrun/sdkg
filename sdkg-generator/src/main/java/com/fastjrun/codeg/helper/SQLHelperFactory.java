/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.helper;

import com.fastjrun.codeg.common.FJTable;

public class SQLHelperFactory {

    public static SqlHelper getSQLHelper(String dbType, FJTable FJTable) {
        SqlHelper sqlHelper = null;
        if (dbType.toLowerCase().contains("mysql")) {
            sqlHelper = new MysqlSqlHelper(FJTable);
        } else if (dbType.toLowerCase().contains("oracle")) {
            sqlHelper = new OracleSqlHelper(FJTable);
        }
        return sqlHelper;

    }

}
