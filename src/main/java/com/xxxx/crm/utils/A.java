package com.xxxx.crm.utils;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.base.BaseQuery;
import com.xxxx.crm.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
public class A {
    public Map<String, Object> queryByParamsForTable(BaseQuery baseQuery, BaseMapper baseMapper) {
        Map<String,Object> result = new HashMap<String,Object>();
        PageHelper.startPage(baseQuery.getPage(),baseQuery.getLimit());
        PageInfo pageInfo =new PageInfo(baseMapper.selectByParams(baseQuery));
        result.put("count",pageInfo.getTotal());
        result.put("data",pageInfo.getList());
        result.put("code",0);
        result.put("msg","");
        return result;
    }
}
