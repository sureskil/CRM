package com.xxxx.crm.service;

import com.xxxx.crm.po.Customer;
import com.xxxx.crm.query.CustomerQuery;
import com.xxxx.crm.query.ReportQuery;

import java.util.List;
import java.util.Map;

public interface CustomerService {

    // 客户信息管理多条件查询
    Map<String, Object> selectCustomer(CustomerQuery customerQuery);

    // 添加客户信息管理数据
    Integer addCustomer(Customer customer);

    // 修改客户信息管理数据
    Integer updateCustomer(Customer customer);

    // 删除客户信息管理数据
    Integer deleteCustomer(Customer customer);

    // 订单信息查询
    Map<String, Object> selectOrder(Integer cusId);

    // 查询订单详情数据
    Map<String, Object> selectDetailByOrderDetail(Integer orderId);

    // 查询客户贡献分析中的数据，只要客户名和金额总数
    Map<String, Object> queryCustomerContributionByParams(ReportQuery reportQuery);

    // 查询客户level分别对应的客户数量（折线图）
    Map<String, Object> countCustomerMake();

    // 查询客户level分别对应的客户数量（饼状图）
    List<Map<String, Object>> countCustomerMake02();
}
