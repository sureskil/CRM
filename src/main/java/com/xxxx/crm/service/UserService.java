package com.xxxx.crm.service;

import com.xxxx.crm.model.UserModel;
import com.xxxx.crm.po.User;
import com.xxxx.crm.query.UserQuery;

import java.util.Map;

public interface UserService {

    /**
     * userLogin：登录功能
     *
     * 从这里可以看出，UserService接口存在的目的是因为
     *      service层主要是逻辑功能的实现层，需要用到dao层中的方法
     *      但是service层的不仅仅只用到dao层中的方法，还需要其他的逻辑判断
     *      所以service层的功能方法名是不和dao层中的方法名一样的
     *      这里的功能是登录功能，名字叫userLogin；需要用到dao层中的方法：queryUserByUserName
     *      在教学中，service和dao层中的方法名一样是因为：没有其他的功能逻辑，dao层的方法是什么，service层就干什么
     *
     * @param userName
     * @param userPwd
     * @return
     */
    UserModel userLogin(String userName, String userPwd);


    /**
     * 密码修改功能
     * @param id
     * @param oldPwd
     * @param newPwd
     * @param rePwd
     * @return
     */
    Integer updateUserPwd(Integer id, String oldPwd, String newPwd, String rePwd);

    /**
     * 获取用户管理的数据表格数据
     * @return
     */
    Map<String, Object> queryUserList(UserQuery userQuery);

    /**
     * 添加用户对象
     * @param user
     * @return
     */
    Integer addUser(User user, Integer[] roleIds);

    /**
     * 更新用户数据
     * @param user
     * @return
     */
    Integer updateUser(User user, Integer[] roleIds);


    /**
     * 批量删除用户数据
     * @param ids
     * @return
     */
    Integer deleteBatch(Integer[] ids);

    /**
     * 单元格删除用户数据
     * @param user
     * @return
     */
    Integer deleteUser(User user);
}
