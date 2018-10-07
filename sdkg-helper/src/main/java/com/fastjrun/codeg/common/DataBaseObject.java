package com.fastjrun.codeg.common;

import java.util.Map;

/**
 * Table类
 */
public class DataBaseObject {

    private String targetType;

    private Map<String, FJTable> tableMap;

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Map<String, FJTable> getTableMap() {
        return tableMap;
    }

    public void setTableMap(Map<String, FJTable> tableMap) {
        this.tableMap = tableMap;
    }

}
