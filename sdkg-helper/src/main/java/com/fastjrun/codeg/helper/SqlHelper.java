package com.fastjrun.codeg.helper;

public interface SqlHelper {

    public abstract String getInsert();

    public abstract String getUpdateById();

    public abstract String getSelectById();

    public abstract String getSelectByCondition();

    public abstract String getDeleteById();

    public abstract String getTotalCount(int conditionAndlimit);

    public abstract String getQueryForList(int conditionAndlimit);

}