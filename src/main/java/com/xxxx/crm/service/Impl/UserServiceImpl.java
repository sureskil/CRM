package com.xxxx.crm.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.dao.UserMapper;
import com.xxxx.crm.exceptions.ParamsException;
import com.xxxx.crm.model.UserModel;
import com.xxxx.crm.po.User;
import com.xxxx.crm.po.UserRole;
import com.xxxx.crm.query.UserQuery;
import com.xxxx.crm.service.UserService;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.Md5Util;
import com.xxxx.crm.utils.UserIDBase64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 登录功能实现
     * @param userName
     * @param userPwd
     * @return
     */
    @Override
    public UserModel userLogin(String userName, String userPwd) {

        // 判断用户名称、密码参数是否为空
        checkUserParams(userName, userPwd);

        // 通过userName从数据库得到对应的User对象
        User user = userMapper.queryUserByUserName(userName);

        // 判断这个对象是否为null
        // 我们要求name和pwd必须不能为空，但是在name、pwd都存在的条件下，name如果不是数据库中存在的内容，就会返回查询结果：null，所以要有null判断
        AssertUtil.isTrue( user==null, "用户不存在！");

        // 判断密码是否正确，因为一个是明文，一个是密文，所以要将明文密码（前端传递过来的）加密后在进行比较
        checkUserPwd(userPwd, user.getUserPwd());

        // 构建用户模型，只返回需要的数据
        UserModel userModel = buildUserModel(user);

        return userModel;
    }


    /**
     * 密码修改实现
     * @param id
     * @param newPwd
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Integer updateUserPwd(Integer id, String oldPwd, String newPwd, String rePwd) {

        // 输入参数不能为空
        checkPwdParams(oldPwd, newPwd, rePwd);

        // 新旧密码不能相同
        AssertUtil.isTrue(oldPwd.equals(newPwd), "新旧密码不能相同！");

        // 两次输入的新密码不同
        AssertUtil.isTrue(!newPwd.equals(rePwd), "两次输入的新密码不同！");

        // 通过id获取用户对象
        User user = userMapper.selectByPrimaryKey(id);

        // 判断用户对象是否存在
        AssertUtil.isTrue(user == null, "用户不存在！");

        // 判断旧密码是否正确
        checkOldPwd(user, oldPwd);

        // 修改密码，返回对应结果（这里的写法是默认修改成功的写法）
        String encode = Md5Util.encode(newPwd);
        user.setUserPwd(encode);
        // 修改密码的时候也把对应对象的update时间给更新了
        user.setUpdateDate(new Date());
        return userMapper.updateByPrimaryKeySelective(user);
    }

    /**
     * 获取用户数据表格数据
     * @param userQuery
     * @return
     */
    @Override
    public Map<String, Object> queryUserList(UserQuery userQuery) {

        AssertUtil.isTrue(userQuery == null, "1，参数异常！");

        PageHelper.startPage(userQuery.getPage(), userQuery.getLimit());

        List<User> users = userMapper.queryUserList(userQuery);

        PageInfo<User> userPageInfo = new PageInfo<>(users);

        HashMap<String, Object> map = new HashMap<>();
        map.put("code", 0);
        map.put("msg", "");
        map.put("count", userPageInfo.getTotal());
        map.put("data", userPageInfo.getList());

        return map;
    }

    /**
     * 实现用户添加
     * @param user
     * @return
     */
    @Override
    public Integer addUser(User user, Integer[] roleIds) {

        // 参数校验
        checkUserParams2(user);

        // 添加数据的名字是否存在
        User user1 = userMapper.queryUserByUserName(user.getUserName());
        AssertUtil.isTrue(user1 != null, "用户名已存在，请重新输入！");

        // 设置默认值
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        user.setUserPwd(Md5Util.encode("123456"));
        int i = userMapper.insertSelective(user);
        // 在user表操作完成后，就可以获得对应的user_id
        // 对"用户-角色中间表"进行操作
        addUserRole(user, roleIds);

        AssertUtil.isTrue(i<1, "添加用户数据失败！");

        return i;
    }

    private void addUserRole(User user, Integer[] roleIds) {
        // 查询"用户-角色中间表"中对应用户id的数据条数
        AssertUtil.isTrue(user.getId() == null, "user_id不存在！！！");
        Integer count = userMapper.selectUserRole(user.getId());
        if (count > 0) {
            AssertUtil.isTrue(userMapper.deleteBatchUserRole(user.getId()) != count, "数据异常233！");
        }
        // 对"用户-角色中间表"进行相关数据的添加
        for (Integer rid : roleIds) {
            UserRole userRole = new UserRole();
            userRole.setCreateDate(new Date());
            userRole.setUpdateDate(new Date());
            userRole.setUserId(user.getId());
            userRole.setRoleId(rid);
            userMapper.insertUserRole(userRole);
        }
    }

    /**
     * 实现用户数据更新
     * @param user
     * @return
     */
    @Override
    public Integer updateUser(User user, Integer[] roleIds) {

        // 参数校验
        AssertUtil.isTrue(user.getId() == null, "用户不存在！");
        checkUserParams2(user);

        // 更新数据的名字是否存在
        Integer i1 = userMapper.selectByUserName(user.getUserName());
        AssertUtil.isTrue(i1 != 1, "用户名已存在，请重新输入！");
        addUserRole(user, roleIds);

        user.setUpdateDate(new Date());
        int i = userMapper.updateByPrimaryKeySelective(user);
        AssertUtil.isTrue(i<1, "用户数据修改失败！");

        return i;
    }

    /**
     * 实现用户数据的批量删除
     * @param ids
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Integer deleteBatch(Integer[] ids) {

        // null判断必须写在前面！！
        AssertUtil.isTrue(ids == null || ids.length < 1, "没有选择数据！");

        Integer i = userMapper.deleteBatch(ids);

        for (Integer id : ids) {
            // 对"用户-角色中间表"进行操作
            Integer count = userMapper.selectUserRole(id);
            if (count > 0) {
                AssertUtil.isTrue(userMapper.deleteBatchUserRole(id) != count, "数据异常233！");
            }
        }

        AssertUtil.isTrue(i != ids.length, "数据删除失败！");

        return i;
    }

    /**
     * 实现单元格删除用户数据
     * @param user
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Integer deleteUser(User user) {

        AssertUtil.isTrue(user.getId() == null, "被删除用户不存在！");

        user.setIsValid(0);
        user.setUpdateDate(new Date());
        int i = userMapper.updateByPrimaryKeySelective(user);
        // 对"用户-角色中间表"进行操作
        Integer count = userMapper.selectUserRole(user.getId());
        if (count > 0) {
            AssertUtil.isTrue(userMapper.deleteBatchUserRole(user.getId()) != count, "数据异常233！");
        }

        AssertUtil.isTrue(i<1, "用户数据删除失败！");

        return i;
    }

    private void checkUserParams2(User user) {
        AssertUtil.isTrue(StringUtils.isBlank(user.getUserName()), "用户名不能为空！");
        AssertUtil.isTrue(StringUtils.isBlank(user.getTrueName()), "真实姓名不能为空！");
        AssertUtil.isTrue(StringUtils.isBlank(user.getEmail()), "邮箱不能为空！");
        AssertUtil.isTrue(StringUtils.isBlank(user.getPhone()), "手机号不能为空！");
    }

    private void checkOldPwd(User user, String oldPwd) {
        String userPwd = user.getUserPwd();
        String encode1 = Md5Util.encode(oldPwd);
        AssertUtil.isTrue(!encode1.equals(userPwd), "旧密码输入错误！");
    }

    private void checkPwdParams(String oldPwd, String newPwd, String rePwd) {
        AssertUtil.isTrue(StringUtils.isBlank(oldPwd), "旧密码不能为空！");
        AssertUtil.isTrue(StringUtils.isBlank(newPwd), "新密码不能为空！");
        AssertUtil.isTrue(StringUtils.isBlank(rePwd), "重复密码不能为空！");
    }


    private UserModel buildUserModel(User user) {
        UserModel userModel = new UserModel();
        // 给用户ID加密
        Integer id = user.getId();
        String enid = UserIDBase64.encoderUserID(id);
        userModel.setId(enid);
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());
        return userModel;
    }

    private void checkUserPwd(String userPwd, String spwd) {
        String encode = Md5Util.encode(userPwd);
        AssertUtil.isTrue(!encode.equals(spwd), "密码不正确!");
    }

    private void checkUserParams(String userName, String userPwd) {
        // AssertUtil是一个自定义工具类，用于判断并抛出对应的异常
        // StringUtils包含了对：null 与 空字符串 的判断
        AssertUtil.isTrue(StringUtils.isBlank(userName), "用户名称不能为空！");
        AssertUtil.isTrue(StringUtils.isBlank(userPwd), "用户密码不能为空！");
    }

}
