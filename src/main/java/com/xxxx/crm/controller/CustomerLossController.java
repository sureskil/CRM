package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.dao.CustomerLossMapper;
import com.xxxx.crm.dao.CustomerReprieveMapper;
import com.xxxx.crm.po.CustomerLoss;
import com.xxxx.crm.po.CustomerReprieve;
import com.xxxx.crm.query.CustomerLossQuery;
import com.xxxx.crm.service.Impl.CustomerLossServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("customer_loss")
public class CustomerLossController extends BaseController {

    @Autowired
    private CustomerLossServiceImpl customerLossService;

    @Autowired
    private CustomerLossMapper customerLossMapper;

    @Autowired
    private CustomerReprieveMapper customerReprieveMapper;


    /**
     * 访问客户流失管理页面
     * @return
     */
    @RequestMapping("index")
    public String customerLoss() {
        return "customerLoss/customer_loss";
    }


    /**
     * 查询所有的流失数据
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> customerLossList(CustomerLossQuery customerLossQuery) {

        // 查询之前，先更新客户的流失状态; 这里只是测试运行，后面会在定时类中使用此方法！
        // customerLossService.updateCustomerState();

        return customerLossService.customerLossList(customerLossQuery);

    }

    /**
     * 访问客户流失管理--详情页面
     * @return
     */
    @RequestMapping("toRep")
    public String toRep(Integer id, HttpServletRequest request) {

        // 因为ftl页面中有作用域对象customerLoss的判断，所以这里必须要返回customerLoss，否则页面会乱码报错
        if (id != null) {
            CustomerLoss customerLoss = customerLossMapper.selectByPrimaryKey(id);
            request.setAttribute("customerLoss", customerLoss);
        }

        return "customerLoss/customer_rep";
    }


    /**
     * 查询暂缓的数据表格数据
     * @return
     */
    @RequestMapping("/selectReprieve")
    @ResponseBody
    public Map<String, Object> selectReprieve() {
        return customerLossService.selectReprieve();
    }


    /**
     * 访问添加/编辑暂缓页面
     * @return
     */
    @RequestMapping("/toRepAddUpdate")
    public String toRepAddUpdate(Integer lossId, HttpServletRequest request, Integer id) {

        if (lossId != null) {
            request.setAttribute("lossId", lossId);
        }
        if (id != null) {
            CustomerReprieve customerReprieve = customerReprieveMapper.selectByPrimaryKey(id);
            request.setAttribute("customerRep", customerReprieve);
        }

        return "customerLoss/customer_rep_add_update";
    }


    /**
     * 添加暂缓数据
     * @param customerReprieve
     * @return
     */
    @PostMapping("/add")
    @ResponseBody
    public ResultInfo addReprieve(CustomerReprieve customerReprieve) {
        customerLossService.addaddReprieve(customerReprieve);
        return success("暂缓数据添加成功！");
    }


    /**
     * 修改暂缓数据
     * @param customerReprieve
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
    public ResultInfo updateReprieve(CustomerReprieve customerReprieve) {
        customerLossService.updateReprieve(customerReprieve);
        return success("暂缓数据修改成功！");
    }


    /**
     * 删除暂缓数据
     * @param customerReprieve
     * @return
     */
    @PostMapping("/delete")
    @ResponseBody
    public ResultInfo deleteReprieve(CustomerReprieve customerReprieve) {
        customerLossService.deleteReprieve(customerReprieve);
        return success("暂缓数据删除成功！");
    }


    /**
     * 添加流失
     * @param customerLoss
     * @return
     */
    @PostMapping("/sureLoss")
    @ResponseBody
    public ResultInfo sureLoss(CustomerLoss customerLoss) {
        customerLossService.sureLoss(customerLoss);
        return success("数据确认流失成功！");
    }

}
