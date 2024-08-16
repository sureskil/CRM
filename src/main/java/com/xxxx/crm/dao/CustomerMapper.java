package com.xxxx.crm.dao;

import com.xxxx.crm.po.Customer;
import com.xxxx.crm.po.CustomerOrder;
import com.xxxx.crm.po.OrderDetails;
import com.xxxx.crm.query.CustomerQuery;
import com.xxxx.crm.query.ReportQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CustomerMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Customer record);

    int insertSelective(Customer record);

    Customer selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Customer record);

    int updateByPrimaryKey(Customer record);

    // 客户信息管理的多条件查询
    List<Customer> selectCustomer(CustomerQuery customerQuery);

    // 通过客户名称查询对应的数据
    Customer selectByName(String name);

    // 查询所有的订单信息
    List<CustomerOrder> selectOrder(Integer cusId);

    // 查询订单详情数据
    List<OrderDetails> selectDetailByOrderDetail(Integer roleId);

    // 无条件通过客户名称查询对应的数据
    Customer selectByCustomerName(String name);

    // 查询客户贡献分析中的数据，只要客户名和金额总数
    List<Map<String, Object>> queryCustomerContributionByParams(ReportQuery reportQuery);

    // 查询客户level分别对应的客户数量（折线图）
    List<Map<String, Object>> countCutomerMake();

    // 查询客户level分别对应的客户数量（饼状图）
    List<Map<String, Object>> countCutomerMake02();
}