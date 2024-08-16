package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.dao.CustomerMapper;
import com.xxxx.crm.dao.CustomerOrderMapper;
import com.xxxx.crm.dao.OrderDetailsMapper;
import com.xxxx.crm.po.Customer;
import com.xxxx.crm.po.CustomerOrder;
import com.xxxx.crm.po.OrderDetails;
import com.xxxx.crm.query.CustomerQuery;
import com.xxxx.crm.query.ReportQuery;
import com.xxxx.crm.service.Impl.CustomerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("customer")
public class CustomerController extends BaseController {

    @Autowired
    private CustomerServiceImpl customerService;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private OrderDetailsMapper orderDetailsMapper;

    @Autowired
    private CustomerOrderMapper customerOrderMapper;

    /**
     * 访问客户管理页面
     * @return
     */
    @RequestMapping("/index")
    public String toCustomer() {
        return "customer/customer";
    }


    /**
     * 查询全部的客户管理信息
     * @param customerQuery
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> selectCustomer(CustomerQuery customerQuery) {
        return customerService.selectCustomer(customerQuery);
    }


    /**
     * 访问添加/修改页面
     * @return
     */
    @RequestMapping("/toAddUpdate")
    public String toAddUpdate(Integer id, HttpServletRequest request) {

        if (id != null) {
            Customer customer = customerMapper.selectByPrimaryKey(id);
            request.setAttribute("customer", customer);
        }

        return "customer/add_update";
    }


    /**
     * 添加客户信息管理数据
     * @param customer
     * @return
     */
    @PostMapping("/add")
    @ResponseBody
    public ResultInfo addCustomer(Customer customer) {
        customerService.addCustomer(customer);
        return success("客户管理数据添成功！");
    }


    /**
     * 修改客户信息管理数据
     * @param customer
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
    public ResultInfo updateCustomer(Customer customer) {
        customerService.updateCustomer(customer);
        return success("客户管理数据修改成功！");
    }


    /**
     * 删除客户信息管理数据
     * @param customer
     * @return
     */
    @PostMapping("/delete")
    @ResponseBody
    public ResultInfo deleteCustomer(Customer customer) {
        customerService.deleteCustomer(customer);
        return success("数据删除成功！");
    }


    /**
     * 访问订单查看页面
     * @return
     */
    @RequestMapping("/toOrder")
    public String toLinkMan(Integer id, HttpServletRequest request) {

        if (id != null) {
            Customer customer = customerMapper.selectByPrimaryKey(id);
            request.setAttribute("customer", customer);
        }

        return "customer/customer_order";
    }


    /**
     * 访问订单查看
     * @return
     */
    @RequestMapping("/selectOrder")
    @ResponseBody
    public Map<String, Object> selectOrder(Integer cusId) {
        return customerService.selectOrder(cusId);
    }


    /**
     * 访问订单详情页面
     * @return
     */
    @RequestMapping("/toOrderDetail")
    public String toOrderDetail(Integer id, HttpServletRequest request) {

        if (id != null) {
            Map<String, Object> map = customerOrderMapper.selectById(id);
            request.setAttribute("order", map);
        }

        return "customer/customer_order_detail";
    }


    /**
     * 查询订单详情数据
     * @param orderId
     * @return
     */
    @RequestMapping("/detailList")
    @ResponseBody
    public Map<String, Object> orderDetailList(Integer orderId) {
        return customerService.selectDetailByOrderDetail(orderId);
    }


    /**
     * 查询客户贡献分析中的数据，只要客户名和金额总数
     * @return
     */
    @RequestMapping("/queryCustomerContributionByParams")
    @ResponseBody
    public Map<String, Object> queryCustomerContributionByParams(ReportQuery reportQuery) {
        return customerService.queryCustomerContributionByParams(reportQuery);
    }


    /**
     * 查询客户level分别对应的客户数量（折线图）
     * @return
     */
    @PostMapping("/countCutomerMake")
    @ResponseBody
    public Map<String, Object> countCustomerMake() {
        return customerService.countCustomerMake();
    }

    /**
     * 查询客户level分别对应的客户数量（饼状图）
     * @return
     */
    @PostMapping("/countCutomerMake02")
    @ResponseBody
    public List<Map<String, Object>> countCustomerMake02() {
        return customerService.countCustomerMake02();
    }

}
