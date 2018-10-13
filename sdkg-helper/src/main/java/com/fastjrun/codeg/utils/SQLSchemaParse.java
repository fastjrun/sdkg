package com.fastjrun.codeg.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fastjrun.codeg.common.CodeGException;
import com.fastjrun.codeg.common.CodeGMsgContants;
import com.fastjrun.codeg.common.DataBaseObject;
import com.fastjrun.codeg.common.FJColumn;
import com.fastjrun.codeg.common.FJTable;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.create.table.ColDataType;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;

public class SQLSchemaParse {

    protected static final Log logStatic = LogFactory.getLog(SQLSchemaParse.class);
    static final String[] javaKeyWords = {"return", "package",
            "describe", "order", "text", "FJTable", "private", "public", "class",
            "static", "test"};
    static Set<String> nameSet = new HashSet<>();
    static Set<String> otherSet = new HashSet<>();

    static {
        for (int i = 0; i < javaKeyWords.length; i++) {
            nameSet.add(javaKeyWords[i].toUpperCase());
        }
        otherSet.add("APPVERSION");
        otherSet.add("APPSOURCE");
        otherSet.add("APPKEY");
    }

    ;

    public static DataBaseObject process(String sqlFile) {
        DataBaseObject dataBaseObject = new DataBaseObject();
        Map<String, FJTable> tableMap = new HashMap<>();
        Statements statements;
        try {
            String content = readFile(sqlFile, Charset.forName("UTF-8"));
            statements = CCJSqlParserUtil.parseStatements(content);
        } catch (IOException | JSQLParserException e) {
            throw new CodeGException(CodeGMsgContants.CODEG_SQLFILE_INVALID, "sqlFiles is wrong");
        }
        dataBaseObject.setTargetType("mysql");
        List<CreateTable> createTables = new ArrayList<>();
        for (Statement statement : statements.getStatements()) {
            if (statement instanceof CreateTable) {
                createTables.add((CreateTable) statement);
            }
        }
        if (createTables.isEmpty()) {
            throw new RuntimeException("Only support create FJTable statement !!!");
        }

        for (CreateTable createTable : createTables) {
            Map<String, FJColumn> columns = new HashMap<>();
            FJTable fjTable = new FJTable();
            fjTable.setName(createTable.getTable().getName());
            createTable.getColumnDefinitions().forEach(it -> {
                FJColumn field = new FJColumn();
                // 字段名称
                String columnName = it.getColumnName();
                // 同时设置了 FieldName
                field.setName(columnName);

                // 字段类型
                ColDataType colDataType = it.getColDataType();
                // 同时设置了字段类型
                field.setDatatype(colDataType.getDataType());

                // comment注释
                //field.setComment(getColumnComment(it.getColumnSpecStrings()));

                columns.put(columnName, field);
            });
            Map<String, FJColumn> primaryKeyColumns = new HashMap<>();
            fjTable.setColumns(columns);
            fjTable.setPrimaryKeyColumns(primaryKeyColumns);
            tableMap.put(fjTable.getName(), fjTable);
        }

        dataBaseObject.setTableMap(tableMap);
        return dataBaseObject;
    }

    private static FJTable parseTable(CreateTable createTable) {
        FJTable table = new FJTable();
        // 表名
        String tbName = createTable.getTable().getName();

        table.setName(tbName);
        table.setClassName(parseTableName(tbName)); // 根据表名得到类名，
        return table;
    }

    private static FJColumn parseColumn(String targetType, ColumnDefinition columnDefinition) {
        if (targetType.toLowerCase().contains("mysql")) {
            return parseMysqlColumn(columnDefinition);
        } else if (targetType.toLowerCase().contains("oracle")) {
            return parseOracleColumn(columnDefinition);
        } else {
            return null;
        }
    }

    private static FJColumn parseMysqlColumn(ColumnDefinition columnDefinition) {
        FJColumn fjColumn = new FJColumn();
        fjColumn.setName(columnDefinition.getColumnName());
        fjColumn.setFieldName(parseFieldName(columnDefinition.getColumnName()));
        //fjColumn.setComment(comment);
        String dataType = columnDefinition.getColDataType().getDataType();
        fjColumn.setDatatype(dataType);
        //fjColumn.setIdentity(identity);
        return fjColumn;
    }

    private static FJColumn parseOracleColumn(ColumnDefinition columnDefinition) {

        FJColumn fjColumn = new FJColumn();
        fjColumn.setName(columnDefinition.getColumnName());
        fjColumn.setFieldName(parseFieldName(columnDefinition.getColumnName()));
        //fjColumn.setComment(comment);
        String dataType = columnDefinition.getColDataType().getDataType();
        fjColumn.setDatatype(dataType);
        //fjColumn.setIdentity(identity);
        return fjColumn;
    }

    /**
     * @param tableName
     *
     * @return 根据code得出类名 规则： 去掉 T_或t_；去掉下划线，每个单词首字母大写;
     * 例：t_user_demo，去掉“t_”,u和d大写，类名是UserDemo
     */
    private static String parseTableName(String tableName) {
        if ((tableName.startsWith("T_")) || (tableName.startsWith("t_"))) {
            tableName = tableName.substring(2);
        }
        StringBuilder sb = new StringBuilder();

        if ((tableName != null) && (tableName.length() > 0)) {
            String[] tName = tableName.toLowerCase().split("_");
            for (int i = 0; i < tName.length; i++) {
                sb.append(tName[i].substring(0, 1).toUpperCase()
                        + tName[i].substring(1));
            }
        }
        if (nameSet.contains(sb.toString().toUpperCase())) {
            sb.insert(0, "_");
        }
        if (otherSet.contains(sb.toString().toUpperCase())) {
            sb.insert(0, "_");
        }
        return sb.toString();
    }

    /**
     * @param columnName
     *
     * @return 根据字段code得到属性名 规则：去掉下划线；第一个字母小写，其他单词首字母大写
     */
    private static String parseFieldName(String columnName) {
        StringBuilder sb = new StringBuilder();

        if ((columnName != null) && (columnName.length() > 0)) {
            String[] cName = columnName.toLowerCase().split("_");
            if (cName.length > 1) {
                for (int i = 0; i < cName.length; i++) {
                    if ((cName[i] != null) && (cName[i].length() > 0)) {
                        if (i != 0) {
                            sb.append(cName[i].substring(0, 1).toUpperCase()
                                    + cName[i].substring(1));
                        } else {
                            sb.append(cName[i]);
                        }
                    }
                }
            } else {
                sb.append(columnName);
            }
        }

        if (nameSet.contains(sb.toString().toUpperCase())) {
            sb.insert(0, "_");
        }
        return sb.toString();
    }

    static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    static boolean isAutoIncrement(ColumnDefinition definition) {
        List<String> specList = definition.getColumnSpecStrings();

        if (specList != null) {
            String tmpStr;
            int conditionSize = specList.size();
            for (int i = 0; i < conditionSize; i++) {
                tmpStr = specList.get(i).toUpperCase();
            }
        }

        return false;
    }
}
