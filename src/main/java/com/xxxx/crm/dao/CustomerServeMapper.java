package com.xxxx.crm.dao;

import com.xxxx.crm.po.CustomerServe;
import com.xxxx.crm.query.CustomerServeQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CustomerServeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CustomerServe record);

    int insertSelective(CustomerServe record);

    CustomerServe selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CustomerServe record);

    int updateByPrimaryKey(CustomerServe record);

    // 多条件查询
    List<Map<String, Object>> selectCustomerServeByParams(CustomerServeQuery customerServeQuery);

    // 获取所有的客户经理，map中装用户的id与角色名（客户经理）
    List<Map<String, Object>> selectAllAccountManager();
}