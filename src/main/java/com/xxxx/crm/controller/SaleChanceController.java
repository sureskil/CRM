package com.xxxx.crm.controller;

import com.xxxx.crm.annoation.RequirePermission;
import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.dao.SaleChanceDao;
import com.xxxx.crm.dao.UserMapper;
import com.xxxx.crm.po.SaleChance;
import com.xxxx.crm.po.User;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.service.Impl.SaleChanceServiceImpl;
import com.xxxx.crm.utils.CookieUtil;
import com.xxxx.crm.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("sale_chance")
public class SaleChanceController extends BaseController {

    @Autowired
    private SaleChanceServiceImpl saleChanceService;

    @Autowired
    private SaleChanceDao saleChanceDao;


    /**
     * 进入营销机会管理页面
     * @return
     */
    @RequestMapping("/index")
    public String saleChanceIndex() {

        // int i = 1/0;

        return "saleChance/sale_chance";
    }

    /**
     * 多条件查询，返回数据给前端的layui数据表格
     * @param saleChanceQuery
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    @RequirePermission(code = "101001")
    public Map<String, Object> querySaleChanceByParams(SaleChanceQuery saleChanceQuery) {

        return saleChanceService.querySaleChanceByParams(saleChanceQuery);

    }

    /**
     * 添加数据
     * @param request
     * @param saleChance
     * @return
     */
    @PostMapping("/add")
    @ResponseBody
    public ResultInfo insertSaleChance(HttpServletRequest request, SaleChance saleChance) {
        String trueName = CookieUtil.getCookieValue(request, "trueName");
        saleChance.setCreateMan(trueName);
        saleChanceService.insertSaleChance(saleChance);
        return success();
    }

    /**
     * 访问数据添加或编辑页面
     * @return
     */
    @RequestMapping("/addOrUpdate")
    public String addOrUpdate(Integer id, HttpServletRequest request) {

        // 当前端请求中有id参数时，查询id对应的saleChance对象放到域对象中
        SaleChance saleChance = saleChanceDao.selectByPrimaryKey(id);
        request.setAttribute("saleChance", saleChance);

        return "saleChance/add_update";
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @PostMapping("/deleteBatch")
    @ResponseBody
    public ResultInfo deleteBatch(Integer[] ids) {
        saleChanceService.deleteBatch(ids);
        return success();
    }

    /**
     * 单条数据编辑
     * @param saleChance
     * @return
     */
    @PostMapping("/updateSaleChance")
    @ResponseBody
    public ResultInfo updateSaleChance(SaleChance saleChance) {
        saleChanceService.updateSaleChance(saleChance);
        return success();
    }

    /**
     * 删除单行数据
     * @param id
     * @return
     */
    @PostMapping("/deleteSaleChance")
    @ResponseBody
    public ResultInfo deleteSaleChance(Integer id) {
        saleChanceService.deleteSaleChance(id);
        return success();
    }

    /**
     * 查询所有User中的销售人员
     * @return
     */
    @PostMapping("/queryAllSaleUser")
    @ResponseBody
    // 因为要转换为json格式的数据，所以不能是Integer！
    // 这里拿到了user的id、name值
    public List<Map<String, Object>> queryAllSaleUser() {
        return saleChanceService.queryAllSaleUser();
    }

}
