package com.xxxx.crm.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.dao.CusDevPlanMapper;
import com.xxxx.crm.dao.SaleChanceDao;
import com.xxxx.crm.po.CusDevPlan;
import com.xxxx.crm.po.SaleChance;
import com.xxxx.crm.query.CusDevPlanQuery;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.service.CusDevPlanService;
import com.xxxx.crm.utils.AssertUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CusDevPlanServiceImpl implements CusDevPlanService {

    @Autowired
    private CusDevPlanMapper cusDevPlanMapper;

    @Autowired
    private SaleChanceDao saleChanceDao;

    /**
     * 根据saleChanceId查询客户开发计划对象
     * @param cusDevPlanQuery
     * @return
     */
    @Override
    public Map<String, Object> queryCusDevPlan(CusDevPlanQuery cusDevPlanQuery) {

        AssertUtil.isTrue(cusDevPlanQuery.getSaleChanceId() == null, "查询数据不存在！");

        List<CusDevPlan> cusDevPlans = cusDevPlanMapper.selectBySaleChanceId(cusDevPlanQuery);

        PageHelper.startPage(cusDevPlanQuery.getPage(), cusDevPlanQuery.getLimit());
        PageInfo<CusDevPlan> pageInfo = new PageInfo<>(cusDevPlans);

        HashMap<String, Object> map = new HashMap<>();
        map.put("code", 0);
        map.put("msg", "");
        map.put("count", pageInfo.getTotal());
        map.put("data", pageInfo.getList());

        return map;
    }

    /**
     * 实现添加CusDevPlan数据
     * @param cusDevPlan
     * @return
     */
    @Override
    public int insertCusDevPlan(CusDevPlan cusDevPlan) {

        // 参数非空校验
        checkCusDevPlanParams(cusDevPlan);
        // 默认参数设置
        dateParams(cusDevPlan);

        int i = cusDevPlanMapper.insertSelective(cusDevPlan);

        AssertUtil.isTrue(i<1, "添加计划项数据失败！");

        return i;
    }

    /**
     * 改变SaleChance对象的开发状态
     * @param saleChance
     */
    @Override
    public void devResult(SaleChance saleChance) {
        AssertUtil.isTrue(saleChance == null, "更新对象不存在！");

        int i = saleChanceDao.updateByPrimaryKeySelective(saleChance);

        AssertUtil.isTrue(i<1, "数据更新失败！");
    }

    /**
     * 实现cusDevPlan编辑功能
     * @param cusDevPlan
     * @return
     */
    @Override
    public int updateCusDevPlan(CusDevPlan cusDevPlan) {

        AssertUtil.isTrue(cusDevPlan == null, "操作对象不存在！");

        int i = cusDevPlanMapper.updateByPrimaryKeySelective(cusDevPlan);

        AssertUtil.isTrue(i<1, "数据更新失败！");

        return i;
    }

    @Override
    public int deleteCusDevPlan(CusDevPlan cusDevPlan) {

        AssertUtil.isTrue(cusDevPlan == null, "操作对象不存在！");

        cusDevPlan.setIsValid(0);
        cusDevPlan.setUpdateDate(new Date());

        int i = cusDevPlanMapper.updateByPrimaryKeySelective(cusDevPlan);

        AssertUtil.isTrue(i<1, "数据删除失败！");

        return i;
    }

    private void dateParams(CusDevPlan cusDevPlan) {
        cusDevPlan.setCreateDate(new Date());
        cusDevPlan.setUpdateDate(new Date());

    }

    private void checkCusDevPlanParams(CusDevPlan cusDevPlan) {
        AssertUtil.isTrue(cusDevPlan == null, "添加对象不存在！");
        AssertUtil.isTrue(StringUtils.isBlank(cusDevPlan.getPlanItem()), "计划项内容不能为空！");
        AssertUtil.isTrue(cusDevPlan.getPlanDate() == null, "计划项时间不能为空！");
        AssertUtil.isTrue(StringUtils.isBlank(cusDevPlan.getExeAffect()), "执行效果不能为空！");
    }
}
