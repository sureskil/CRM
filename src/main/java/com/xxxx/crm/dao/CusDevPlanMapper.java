package com.xxxx.crm.dao;

import com.xxxx.crm.po.CusDevPlan;
import com.xxxx.crm.query.CusDevPlanQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CusDevPlanMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CusDevPlan record);

    int insertSelective(CusDevPlan record);

    CusDevPlan selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CusDevPlan record);

    int updateByPrimaryKey(CusDevPlan record);

    // 根据SaleChanceId查询CusDevPlan对象的集合
    List<CusDevPlan> selectBySaleChanceId(CusDevPlanQuery cusDevPlanQuery);
}