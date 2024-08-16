package com.xxxx.crm.service;

import com.xxxx.crm.po.SaleChance;
import com.xxxx.crm.po.User;
import com.xxxx.crm.query.SaleChanceQuery;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Map;

public interface SaleChanceService {
    // 营销机会管理多条件查询
    Map<String, Object> querySaleChanceByParams(SaleChanceQuery saleChanceQuery);

    // 添加功能
    int insertSaleChance(SaleChance saleChance);

    // 批量删除功能
    int deleteBatch(Integer[] ids);

    // 编辑功能
    int updateSaleChance(SaleChance saleChance);

    // 单行数据删除
    int deleteSaleChance(Integer id);

    // 查询所有User中的营销人员
    List<Map<String, Object>> queryAllSaleUser();
}
