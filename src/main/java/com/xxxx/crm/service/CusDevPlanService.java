package com.xxxx.crm.service;

import com.xxxx.crm.po.CusDevPlan;
import com.xxxx.crm.po.SaleChance;
import com.xxxx.crm.query.CusDevPlanQuery;
import com.xxxx.crm.query.SaleChanceQuery;

import java.util.Map;

public interface CusDevPlanService {
    // 根据saleChanceId查询客户开发计划对象
    Map<String, Object> queryCusDevPlan(CusDevPlanQuery cusDevPlanQuery);

    int insertCusDevPlan(CusDevPlan cusDevPlan);

    // 改变salaChance对象的开发状态
    void devResult(SaleChance saleChance);

    // 编辑功能
    int updateCusDevPlan(CusDevPlan cusDevPlan);

    // 删除功能
    int deleteCusDevPlan(CusDevPlan cusDevPlan);
}
