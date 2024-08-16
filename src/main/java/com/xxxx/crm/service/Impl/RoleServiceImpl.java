package com.xxxx.crm.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.dao.RoleMapper;
import com.xxxx.crm.po.Role;
import com.xxxx.crm.query.RoleQuery;
import com.xxxx.crm.service.RoleService;
import com.xxxx.crm.utils.AssertUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    /**
     * 实现角色对象的多条件查询
     * @param roleQuery
     * @return
     */
    @Override
    public Map<String, Object> queryRoleBatch(RoleQuery roleQuery) {

        AssertUtil.isTrue(roleQuery == null, "查询数据出错！");

        PageHelper.startPage(roleQuery.getPage(), roleQuery.getLimit());
        List<Role> roles = roleMapper.queryRoleBatch(roleQuery);
        PageInfo<Role> rolePageInfo = new PageInfo<>(roles);

        Map<String, Object> map = new HashMap<>();
        map.put("code", 0);
        map.put("msg", "");
        map.put("count", rolePageInfo.getTotal());
        map.put("data", rolePageInfo.getList());

        return map;
    }

    /**
     * 实现角色数据的添加
     * @param role
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Integer addRole(Role role) {

        // 参数校验
        checkRoleParams(role);

        role.setCreateDate(new Date());
        role.setUpdateDate(new Date());
        role.setIsValid(1);
        int i = roleMapper.insertSelective(role);

        AssertUtil.isTrue(i < 1, "用户数据添加失败！");

        return i;
    }


    /**
     * 实现角色数据的更新
     * @param role
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Integer updateRole(Role role) {
        AssertUtil.isTrue(role == null || role.getId() == null, "更新对象不存在！");

        role.setUpdateDate(new Date());
        int i = roleMapper.updateByPrimaryKeySelective(role);

        AssertUtil.isTrue(i<1, "角色数据更新失败！");

        return i;
    }

    /**
     * 实现角色数据删除
     * @param role
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Integer deleteRole(Role role) {

        AssertUtil.isTrue(role == null || role.getId() == null, "删除数据不存在！");

        role.setIsValid(0);
        role.setUpdateDate(new Date());
        int i = roleMapper.updateByPrimaryKeySelective(role);
        AssertUtil.isTrue(i<1, "数据删除失败！");

        return i;
    }

    private void checkRoleParams(Role role) {
        AssertUtil.isTrue(role == null, "数据添加失败！");
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()), "用户名不能为空！");
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleRemark()), "用户备不能为空！");
        String r = role.getRoleName();
        Integer i = roleMapper.queryRoleByRoleName(r);
        System.out.println("i = " + i);
        AssertUtil.isTrue(i > 0 , "已存在同名角色，请重新输入角色名！");
    }

}
