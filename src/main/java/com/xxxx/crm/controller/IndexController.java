package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
// import org.springframework.http.HttpRequest;
import com.xxxx.crm.dao.UserMapper;
import com.xxxx.crm.po.User;
import com.xxxx.crm.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;


/**
 * IndexController的作用是提供index、welcome、main页面的访问
 */
@Controller
// BaseController中有得到项目路径的request域对象，和相关的返回数据方法
public class IndexController extends BaseController {

    @Autowired
    // 注入这个对象，用于通过id获取用户对象，从而在登陆后右上角显示对象的用户名称
    private UserMapper userMapper;


    /**
     * 访问到登录页面
     * @return
     */
    @RequestMapping("/index")
    public String index() {
        return "index";
    }

    /**
     * 登录成功后访问主页面
     * 在访问主页面之前，先设置域对象，其值用于显示登录用户的用户名
     * 而要设置这个域对象，就要先获取这个用户对象
     * 注意request对象的类型是HttpServletRequest，不是HttpRequest
     * @return
     */
    @RequestMapping("/main")
    public String main(HttpServletRequest request) {

        // 从cookie中获取用户的id
        Integer decoderUserID = LoginUserUtil.releaseUserIdFromCookie(request);

        // 然后通过id查询对应的对象
        User user = userMapper.selectByPrimaryKey(decoderUserID);

        // 最后把这个对象放到作用域对象中
        request.setAttribute("user", user);

        List<String> aclValueList = userMapper.selectAclValueByUserId(decoderUserID);
        HttpSession session = request.getSession();
        session.setAttribute("permissions", aclValueList);

        return "main";
    }

    /**
     * layui后台模板的默认设置，登录成功后首页会默认第一访问显示：后端路径为welcome的页面，也就是登陆后的首页！
     * @return
     */
    @RequestMapping("/welcome")
    public String welcome() {

        // int i = 1/0;

        return "welcome";
    }


}
