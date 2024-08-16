package com.xxxx.crm.dao;

import com.xxxx.crm.po.SaleChance;
import com.xxxx.crm.po.User;
import com.xxxx.crm.query.SaleChanceQuery;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SaleChanceDao {
    int deleteByPrimaryKey(Integer id);

    int insert(SaleChance record);

    int insertSelective(SaleChance record);

    SaleChance selectByPrimaryKey(Integer id);

    // 单行数据删除
    int updateByPrimaryKeySelective(SaleChance record);

    int updateByPrimaryKey(SaleChance record);

    // 多条件查询，throws DataAccessException表示该方法的实现者有可能会遇到数据访问相关的错误
    List<SaleChance> selectByParams(SaleChanceQuery saleChanceQuery) throws DataAccessException;

    // 批量删除
    int deleteBatch(Integer[] ids) throws DataAccessException;

    // 查询User中所有的销售人员，需要用到联表查询
    List<Map<String, Object>> queryAllSaleUser();

}