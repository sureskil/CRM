package com.xxxx.crm.service;

import com.xxxx.crm.po.Customer;
import com.xxxx.crm.po.CustomerLoss;
import com.xxxx.crm.po.CustomerReprieve;
import com.xxxx.crm.query.CustomerLossQuery;

import java.util.List;
import java.util.Map;

public interface CustomerLossService {

    // 查询所有的流失数据
    Map<String, Object> customerLossList(CustomerLossQuery customerLossQuery);

    // 查询之前，先更新客户的流失状态
    void updateCustomerState();

    // 查询暂缓的数据表格数据
    Map<String, Object> selectReprieve();

    // 添加暂缓数据
    Integer addaddReprieve(CustomerReprieve customerReprieve);

    // 修改暂缓数据
    Integer updateReprieve(CustomerReprieve customerReprieve);

    // 删除暂缓护具
    Integer deleteReprieve(CustomerReprieve customerReprieve);

    // 添加流失
    Integer sureLoss(CustomerLoss customerLoss);
}
