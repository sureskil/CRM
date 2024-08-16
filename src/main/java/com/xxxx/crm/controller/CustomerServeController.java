package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.dao.CustomerServeMapper;
import com.xxxx.crm.po.CustomerServe;
import com.xxxx.crm.query.CustomerServeQuery;
import com.xxxx.crm.service.Impl.CustomerServeServiceImpl;
import com.xxxx.crm.utils.LoginUserUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("customer_serve")
public class CustomerServeController extends BaseController {


    @Autowired
    private CustomerServeServiceImpl customerServeService;

    @Autowired
    private CustomerServeMapper customerServeMapper;


    /**
     * 访问服务管理页面
     * @param type
     * @return
     */
    @RequestMapping("/index/{type}")
    public String index(@PathVariable Integer type) {

        if (type != null) {
            if (type == 1) {
                // 服务创建页面
                return "customerServe/customer_serve";

            } else if (type == 2) {

                return "customerServe/customer_serve_assign";

            } else if (type == 3) {

                return "customerServe/customer_serve_proce";

            } else if (type == 4) {

                return "customerServe/customer_serve_feed_back";

            } else if (type == 5) {

                return "customerServe/customer_serve_archive";

            }
        }

        return "error";
    }


    /**
     * 返回服务管理数据表格数据
     * @param customerServeQuery
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> customerServeList(CustomerServeQuery customerServeQuery, Integer flag, HttpServletRequest request) {

        System.out.println("flag = " + flag);

        if (flag != null && flag == 1) {
            int i = LoginUserUtil.releaseUserIdFromCookie(request);
            customerServeQuery.setAssigner(i+"");
        }

        return customerServeService.customerServeList(customerServeQuery);
    }


    /**
     * 访问服务创建添加页面
     * @return
     */
    @RequestMapping("/toServeAdd")
    public String toServeAdd() {
        return "customerServe/customer_serve_add";
    }


    /**
     * 添加服务管理数据
     * @return
     */
    @PostMapping("/add")
    @ResponseBody
    public ResultInfo addServe(CustomerServe customerServe) {
        customerServeService.addServe(customerServe);
        return success("服务管理数据添加成功！");
    }


    /**
     * 访问服务分配添加页面
     * @return
     */
    @RequestMapping("/toServeAssignAdd")
    public String toServeAssignAdd(Integer id, HttpServletRequest request) {

        if (id != null) {
            CustomerServe customerServe = customerServeMapper.selectByPrimaryKey(id);
            request.setAttribute("customerServe", customerServe);
        }

        return "customerServe/customer_serve_assign_add";
    }


    /**
     * 获取所有客户经理的map集合<id, roleName>
     * @return
     */
    @PostMapping("/selectAllAccountManager")
    @ResponseBody
    public List<Map<String, Object>> selectAllAccountManager() {
        return customerServeMapper.selectAllAccountManager();
    }


    /**
     * 分配服务
     * @param customerServe
     * @return
     */
    @PostMapping("/updateCustomerServe")
    @ResponseBody
    public ResultInfo updateCustomerServe(CustomerServe customerServe) {
        customerServeService.updateCustomerServe(customerServe);
        return success("服务操作成功！");
    }


    /**
     * 访问处理添加页面
     * @return
     */
    @RequestMapping("/toProceAdd")
    public String toProceAdd(Integer id, HttpServletRequest request) {

        if (id != null) {
            CustomerServe customerServe = customerServeMapper.selectByPrimaryKey(id);
            request.setAttribute("customerServe", customerServe);
        }

        return "customerServe/customer_serve_proce_add";
    }


    /**
     * 访问反馈添加页面
     * @return
     */
    @RequestMapping("/toBack")
    public String toBack(Integer id, HttpServletRequest request) {

        if (id != null) {
            CustomerServe customerServe = customerServeMapper.selectByPrimaryKey(id);
            request.setAttribute("customerServe", customerServe);
        }

        return "customerServe/customer_serve_feed_back_add";
    }

}
