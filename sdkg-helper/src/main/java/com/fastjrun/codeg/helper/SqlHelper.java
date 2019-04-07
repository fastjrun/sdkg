package com.fastjrun.codeg.helper;

public interface SqlHelper {

    public abstract String getInsert();

    public abstract String getUpdateByPK();

    public abstract String getSelectByPK();

    public abstract String getSelectByCondition();

    public abstract String getDeleteByPK();

    public abstract String getTotalCount(int conditionAndlimit);

    public abstract String getQueryForList(int conditionAndlimit);

}