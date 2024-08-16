package com.xxxx.crm.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.dao.SaleChanceDao;
import com.xxxx.crm.dao.UserMapper;
import com.xxxx.crm.po.SaleChance;
import com.xxxx.crm.po.User;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.service.SaleChanceService;
import com.xxxx.crm.utils.AssertUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SaleChanceServiceImpl implements SaleChanceService {

    @Autowired
    private SaleChanceDao saleChanceDao;

    @Autowired
    private UserMapper userMapper;

    /**
     * 多条件查询实现
     * @param saleChanceQuery
     * @return
     */
    @Override
    public Map<String, Object> querySaleChanceByParams(SaleChanceQuery saleChanceQuery) {

        HashMap<String, Object> map = new HashMap<>();

        // 开启分页
        PageHelper.startPage(saleChanceQuery.getPage(), saleChanceQuery.getLimit());
        // 进行数据查询
        List<SaleChance> saleChancesList = saleChanceDao.selectByParams(saleChanceQuery);
        // 替换saleChance中assignMan的显示内容
        replaceAssignMan(saleChancesList);
        // 得到分页对象，进行分页
        PageInfo<SaleChance> pageInfo = new PageInfo<>(saleChancesList);

        // 把需要返回给数据表格的数据封装到数据模型map中
        map.put("code", 0);
        map.put("msg", "");
        map.put("count", pageInfo.getTotal());
        map.put("data", pageInfo.getList());

        return map;
    }

    private void replaceAssignMan(List<SaleChance> saleChancesList) {
        for (SaleChance saleChance : saleChancesList) {
            String assignMan = saleChance.getAssignMan();
            if (assignMan != null && assignMan != "" && !assignMan.isEmpty()) {
                System.out.println("assignMan = " + saleChance.getAssignMan());
                int i = Integer.parseInt(assignMan);
                User user = userMapper.selectByPrimaryKey(i);
                saleChance.setAssignMan(user.getTrueName());
            }
        }
    }

    /**
     * 添加功能实现
     * @param saleChance
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int insertSaleChance(SaleChance saleChance) {

        // 传入参数的非空判断
        checkParamas(saleChance);

        // 判断是否有指派人，根据不同情况对分配状态、开发状态、分配时间进行默认值设置
        judgeAssignMan(saleChance);

        // 设置创建、更新时间
        saleChance.setCreateDate(new Date());
        saleChance.setUpdateDate(new Date());

        int i = saleChanceDao.insertSelective(saleChance);

        AssertUtil.isTrue(i<0, "信息添加失败");

        return i;
    }

    private void judgeAssignMan(SaleChance saleChance) {
        if (StringUtils.isBlank(saleChance.getAssignMan())) {
            // 没有指派人的情况下，对分配状态、开发状态、分配时间设置默认值
            saleChance.setState(0);
            saleChance.setDevResult(0);
            saleChance.setAssignTime(null);
        } else {
            // 有指派人情况的默认值
            saleChance.setState(1);
            saleChance.setDevResult(1);
            saleChance.setAssignTime(new Date());
        }
    }

    private void checkParamas(SaleChance saleChance) {
        AssertUtil.isTrue(StringUtils.isBlank(saleChance.getCustomerName()), "用户名不能为空！");
        AssertUtil.isTrue(StringUtils.isBlank(saleChance.getChanceSource()), "机会来源不能为空！");
        AssertUtil.isTrue(StringUtils.isBlank(saleChance.getLinkMan()), "联系人不能为空！");
        AssertUtil.isTrue(StringUtils.isBlank(saleChance.getLinkPhone()), "联系电话不能为空！");
    }

    /**
     * 批量删除实现
     * @param ids
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int deleteBatch(Integer[] ids) {

        // 判断是否选择了数据
        AssertUtil.isTrue(ids == null || ids.length < 1, "请选择要删除的数据！");

        int i = saleChanceDao.deleteBatch(ids);
        // i != ids.length???
        AssertUtil.isTrue(i<0, "营销机会删除失败！");
        return i;
    }

    /**
     * 编辑功能实现
     * @param saleChance
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int updateSaleChance(SaleChance saleChance) {

        // 判断对象是否为null
        AssertUtil.isTrue(saleChance.getId() == null, "更新对象不存在！");

        Integer id = saleChance.getId();
        SaleChance saleChance1 = saleChanceDao.selectByPrimaryKey(id);
        if (!saleChance1.getAssignMan().equals(saleChance.getAssignMan())) {
            saleChance.setAssignTime(new Date());
            saleChance.setState(1);
            saleChance.setDevResult(1);
        }
        if (StringUtils.isBlank(saleChance.getAssignMan())) {
            saleChance.setAssignTime(null);
            saleChance.setState(0);
            saleChance.setDevResult(0);
        }

        saleChance.setUpdateDate(new Date());

        int i = saleChanceDao.updateByPrimaryKeySelective(saleChance);

        AssertUtil.isTrue(i<1, "营销机会数据更新失败！");

        return i;
    }


    /**
     * 删除单条数据
     * @param id
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int deleteSaleChance(Integer id) {

        AssertUtil.isTrue(id == null, "被删除的数据不存在！");

        SaleChance saleChance = saleChanceDao.selectByPrimaryKey(id);

        saleChance.setIsValid(0);
        saleChance.setUpdateDate(new Date());

        int i = saleChanceDao.updateByPrimaryKeySelective(saleChance);

        AssertUtil.isTrue(i<1, "删除营销机会数据失败！");

        return i;
    }

    /**
     * 查询所有User中的销售人员
     * @return
     */
    @Override
    public List<Map<String, Object>> queryAllSaleUser() {
        // 查询所有的User对象中的销售人员对象信息，放到域对象中
        // 这个方法即使时UserMapper中也是没有的，所以需要重新编写一个
        List<Map<String, Object>> maps = saleChanceDao.queryAllSaleUser();
        return maps;
    }

}
