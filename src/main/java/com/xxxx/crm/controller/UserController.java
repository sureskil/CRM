package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.dao.UserMapper;
import com.xxxx.crm.exceptions.ParamsException;
import com.xxxx.crm.model.UserModel;
import com.xxxx.crm.po.User;
import com.xxxx.crm.query.UserQuery;
import com.xxxx.crm.service.Impl.UserServiceImpl;
import com.xxxx.crm.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserMapper userMapper;

    /**
     * 登录功能
     * @param userName
     * @param userPwd
     * @return
     */
    @PostMapping("/login")
    @ResponseBody
    public ResultInfo userLogin(String userName, String userPwd) {

        ResultInfo resultInfo = new ResultInfo();

        /*try {
            // 这句代码必须放在try...catch...中，否则获取对象数据的过程中出错，这里的异常设置是不会起作用的！
            UserModel userModel = userService.userLogin(userName, userPwd);
            // 放入要返回的对象数据
            resultInfo.setResult(userModel);
        } catch (ParamsException e) {
            // ResultInfo中有默认成功的返回code、msg，如果出现异常需要重新定义code、msg
            // 如果这个异常是自定义异常，那么依据自定义异常的code、msg进行赋值
            resultInfo.setCode(e.getCode());
            resultInfo.setMsg(e.getMsg());
        } catch (Exception e) {
            // 如果异常不是自定义异常，那么设置统一的code、msg
            resultInfo.setCode(300);
            resultInfo.setMsg("操作失败!");
        }*/

        UserModel userModel = userService.userLogin(userName, userPwd);
        resultInfo.setResult(userModel);
        return resultInfo;
    }

    /**
     * 访问更改密码页面
     * @return
     */
    @RequestMapping("/toPasswordPage")
    public String password(HttpServletRequest request) {

        return "user/password";
    }

    /**
     * 密码修改功能
     * @param request
     * @param oldPwd
     * @param newPwd
     * @param rePwd
     * @return
     */
    @RequestMapping("/uup")
    @ResponseBody
    public ResultInfo updateUserPwd(HttpServletRequest request, String oldPwd, String newPwd, String rePwd) {

        ResultInfo resultInfo = new ResultInfo();

        /*try {
            int id = LoginUserUtil.releaseUserIdFromCookie(request);
            Integer integer1 = userService.updateUserPwd(id, oldPwd, newPwd, rePwd);
            // Integer integer1 = userService.updateUserPwd(81, oldPwd, newPwd, rePwd);
            if (integer1 > 0) {
                resultInfo.setMsg("密码修改成功！");
                resultInfo.setResult(integer1);
            }
        } catch (ParamsException e) {
            resultInfo.setCode(e.getCode());
            resultInfo.setMsg(e.getMsg());
        } catch (Exception e) {
            resultInfo.setCode(300);
            resultInfo.setMsg("密码修改失败！");
        }*/

        int id = LoginUserUtil.releaseUserIdFromCookie(request);
        Integer integer1 = userService.updateUserPwd(id, oldPwd, newPwd, rePwd);
        // 这个if中的内容可以不写的！
        if (integer1 > 0) {
            resultInfo.setMsg("密码修改成功！");
            resultInfo.setResult(integer1);
        }
        return resultInfo;
    }

    /**
     * 访问用户管理页面
     * @return
     */
    @RequestMapping("index")
    public String toUser() {
        return "user/user";
    }

    /**
     * 访问用户管理的的数据表格数据
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> queryUserList(UserQuery userQuery) {
        return userService.queryUserList(userQuery);
    }

    /**
     * 添加用户对象
     * @param user
     * @return
     */
    @PostMapping("add")
    @ResponseBody
    public ResultInfo addUser(User user, Integer[] roleIds) {
        userService.addUser(user, roleIds);
        return success();
    }

    /**
     * 访问添加/编辑用户页面
     * @return
     */
    @RequestMapping("/addUpdateUser")
    public String addUpdateUser(HttpServletRequest request, Integer id) {

        if (id != null) {
            User user1 = userMapper.selectByPrimaryKey(id);
            request.setAttribute("user", user1);
        }

        return "user/add_update";
    }

    /**
     * 更新用户数据
     * @param user
     * @return
     */
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateUser(User user,Integer[] roleIds) {
        userService.updateUser(user,roleIds);
        return success();
    }


    /**
     * 批量删除用户数据
     * @param ids
     * @return
     */
    @PostMapping("/deleteBatch")
    @ResponseBody
    public ResultInfo deleteBatch(Integer[] ids) {
        userService.deleteBatch(ids);
        return success();
    }

    /**
     * 单元格删除用户数据
     * @param user
     * @return
     */
    @PostMapping("/deleteUser")
    @ResponseBody
    public ResultInfo deleteUser(User user) {
        userService.deleteUser(user);
        return success();
    }

}
