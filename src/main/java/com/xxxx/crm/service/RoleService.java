package com.xxxx.crm.service;

import com.xxxx.crm.po.Role;
import com.xxxx.crm.query.RoleQuery;

import java.util.Map;

public interface RoleService {

    /**
     * 实现用户数据表格数据的返回
     * @param roleQuery
     * @return
     */
    Map<String, Object> queryRoleBatch(RoleQuery roleQuery);

    /**
     * 添加用户数据
     * @param role
     * @return
     */
    Integer addRole(Role role);

    /**
     * 编辑角色数据
     * @param role
     * @return
     */
    Integer updateRole(Role role);

    /**
     * 删除角色数据
     * @param role
     * @return
     */
    Integer deleteRole(Role role);
}
