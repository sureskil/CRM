package com.xxxx.crm.dao;

import com.xxxx.crm.po.CustomerReprieve;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerReprieveMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CustomerReprieve record);

    int insertSelective(CustomerReprieve record);

    CustomerReprieve selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CustomerReprieve record);

    int updateByPrimaryKey(CustomerReprieve record);

}