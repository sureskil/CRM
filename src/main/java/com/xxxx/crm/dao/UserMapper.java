package com.xxxx.crm.dao;

import com.xxxx.crm.po.User;
import com.xxxx.crm.po.UserRole;
import com.xxxx.crm.query.UserQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    // 通过id查询对象
    User selectByPrimaryKey(Integer id);

    // 改变对象数据，值更改赋值了的字段，其他字段的数据保持不变
    int updateByPrimaryKeySelective(User record);

    // 改变对象数据，没有赋值的字段将都变为null
    int updateByPrimaryKey(User record);

    // 通过名字获得User对象
    User queryUserByUserName(String userName);

    // 获取用户对象数据表格数据
    List<User> queryUserList(UserQuery userQuery);

    // 批量删除功能
    Integer deleteBatch(Integer[] ids);

    // 删除"用户-角色中间表"中对应用户id的数据
    Integer deleteBatchUserRole(Integer userId);

    // 查询"用户-角色中间表"中对应用户id的数据条数
    Integer selectUserRole(Integer userId);

    // 添加"用户-角色中间表"中对应用户id的数据条数
    Integer insertUserRole(UserRole userRole);

    // 通过名字获得User对象的数量
    Integer selectByUserName(String userName);

    // 通过用户id查询对应的角色的权限码集合
    List<String> selectAclValueByUserId(Integer userId);
}