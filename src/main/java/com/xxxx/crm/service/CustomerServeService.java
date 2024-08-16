package com.xxxx.crm.service;

import com.xxxx.crm.po.CustomerServe;
import com.xxxx.crm.query.CustomerServeQuery;

import java.util.Map;

public interface CustomerServeService {

    // 多条件查询
    Map<String, Object> customerServeList(CustomerServeQuery customerServeQuery);

    // 添加服务管理数据
    Integer addServe(CustomerServe customerServe);

    // 分配服务
    void updateCustomerServe(CustomerServe customerServe);
}
