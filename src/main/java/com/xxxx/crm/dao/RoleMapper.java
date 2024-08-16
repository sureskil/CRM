package com.xxxx.crm.dao;

import com.xxxx.crm.model.RoleModel;
import com.xxxx.crm.po.Role;
import com.xxxx.crm.query.RoleQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface RoleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Role record);

    int insertSelective(Role record);

    Role selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Role record);

    int updateByPrimaryKey(Role record);

    // role对象的多条件查询
    List<Role> queryRoleBatch(RoleQuery roleQuery);

    // 通过角色名查询角色
    Integer queryRoleByRoleName(String roleName);

    // 查询所有的用户id与name
    List<RoleModel> selectRoleIdAndName();

    // 查询所有的用户id与name，方法2
    List<Map<String, Object>> selectRn();

    // 通过userId查询对应RoleId的集合
    List<Integer> selectRoleId(Integer userId);

}