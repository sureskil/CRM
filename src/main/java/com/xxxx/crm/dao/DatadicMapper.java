package com.xxxx.crm.dao;

import com.xxxx.crm.po.Datadic;
import org.springframework.stereotype.Repository;

@Repository
public interface DatadicMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Datadic record);

    int insertSelective(Datadic record);

    Datadic selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Datadic record);

    int updateByPrimaryKey(Datadic record);
}