package com.xxxx.crm.dao;

import com.xxxx.crm.po.Customer;
import com.xxxx.crm.po.CustomerLoss;
import com.xxxx.crm.po.CustomerOrder;
import com.xxxx.crm.po.CustomerReprieve;
import com.xxxx.crm.query.CustomerLossQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerLossMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CustomerLoss record);

    int insertSelective(CustomerLoss record);

    CustomerLoss selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CustomerLoss record);

    int updateByPrimaryKey(CustomerLoss record);

    // 多条件查询
    List<CustomerLoss> selectCustomerLoss(CustomerLossQuery customerLossQuery);

    // 查询之前，先更新客户的流失状态
    List<Customer> updateCustomerState();

    // 通过客户id查询其最后一条下单记录
    CustomerOrder selectLastOrderById(Integer cusId);

    // 最后将对应的customer对象的state设置为1
    Integer updateCustState(Integer cusId);

    // 查询暂缓的数据表格数据
    List<CustomerReprieve> selectReprieve();
}