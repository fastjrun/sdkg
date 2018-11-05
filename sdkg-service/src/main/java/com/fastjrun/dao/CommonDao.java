package com.fastjrun.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.session.RowBounds;

import com.fastjrun.mybatis.DynamicProvider;
import com.fastjrun.mybatis.declare.Declare;

public interface CommonDao {

    @SelectProvider(type = DynamicProvider.class, method = "sql")
    List<Map<String, Object>> select(Declare dec);

    @SelectProvider(type = DynamicProvider.class, method = "sqlLimit")
    List<Map<String, Object>> selectLimit(@Param("dec") Declare dec,
                                          RowBounds rowBounds);

    @SelectProvider(type = DynamicProvider.class, method = "sql")
    Map<String, Object> selectOne(Declare dec);

    @UpdateProvider(type = DynamicProvider.class, method = "sql")
    int update(Declare dec);

    @DeleteProvider(type = DynamicProvider.class, method = "sql")
    int delete(Declare dec);

    @InsertProvider(type = DynamicProvider.class, method = "sql")
    int insert(Declare dec);

    @SelectProvider(type = DynamicProvider.class, method = "sql")
    String first(Declare dec);

}