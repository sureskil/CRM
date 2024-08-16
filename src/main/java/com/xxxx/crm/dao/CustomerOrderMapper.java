package com.xxxx.crm.dao;

import com.xxxx.crm.po.CustomerOrder;
import com.xxxx.crm.po.OrderDetails;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CustomerOrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CustomerOrder record);

    int insertSelective(CustomerOrder record);

    CustomerOrder selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CustomerOrder record);

    int updateByPrimaryKey(CustomerOrder record);

    // 订单详情页面的回显数据
    Map<String, Object> selectById(Integer id);

}