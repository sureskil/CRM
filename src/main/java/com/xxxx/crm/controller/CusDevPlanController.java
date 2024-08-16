package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.dao.CusDevPlanMapper;
import com.xxxx.crm.dao.SaleChanceDao;
import com.xxxx.crm.po.CusDevPlan;
import com.xxxx.crm.po.SaleChance;
import com.xxxx.crm.query.CusDevPlanQuery;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.service.Impl.CusDevPlanServiceImpl;
import com.xxxx.crm.service.Impl.SaleChanceServiceImpl;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("cus_dev_plan")
public class CusDevPlanController extends BaseController {

    @Autowired
    private SaleChanceServiceImpl saleChanceService;

    @Autowired
    private SaleChanceDao saleChanceDao;

    @Autowired
    private CusDevPlanServiceImpl cusDevPlanService;

    @Autowired
    private CusDevPlanMapper cusDevPlanMapper;

    /**
     * 访问客户开发计划页面
     * @return
     */
    @RequestMapping("/index")
    public String cdpIndex() {
        return "cusDevPlan/cus_dev_plan";
    }

    /**
     * 返回SaleChance对象数据表格数据
     * @param saleChanceQuery
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> queryDevPlanByParams(SaleChanceQuery saleChanceQuery, HttpServletRequest request) {

        // 这里设置只显示当前登录用户的数据，比如scott只有一条数据，admin是没有数据的（看数据很少，不要以为出错了）
        int i = LoginUserUtil.releaseUserIdFromCookie(request);
        saleChanceQuery.setAssignMan(i+"");

        return saleChanceService.querySaleChanceByParams(saleChanceQuery);
    }

    /**
     * 访问开发页面
     * @return
     */
    @RequestMapping("/cus_dev_plan_data")
    public String cdpDate(Integer id, HttpServletRequest request) {

        SaleChance saleChance = saleChanceDao.selectByPrimaryKey(id);
        request.setAttribute("saleChance", saleChance);

        return "cusDevPlan/cus_dev_plan_data";
    }

    /**
     * 返回CusDevPlan对象数据表格
     * @param cusDevPlanQuery
     * @return
     */
    @RequestMapping("/queryCusDevPlan")
    @ResponseBody
    public Map<String, Object> queryCusDevPlan(CusDevPlanQuery cusDevPlanQuery) {
        return cusDevPlanService.queryCusDevPlan(cusDevPlanQuery);
    }

    /**
     * 访问添加/更新页面
     * @return
     */
    @RequestMapping("/add_update")
    public String addUpdate(Integer sid, HttpServletRequest request, Integer cid) {
        request.setAttribute("sid", sid);

        if (cid != null) {
            CusDevPlan cusDevPlan = cusDevPlanMapper.selectByPrimaryKey(cid);
            request.setAttribute("cusDevPlan", cusDevPlan);
        }

        return "cusDevPlan/add_update";
    }

    /**
     * 添加CusDevPlan数据
     * @param cusDevPlan
     * @return
     */
    @PostMapping("add")
    @ResponseBody
    public ResultInfo addCusDevPlan(CusDevPlan cusDevPlan, Integer sid) {
            // 从前端隐藏域中拿来的SaleChanceId值赋予cusDevPlan对象
        AssertUtil.isTrue(sid == null, "sid值不能为空！");
        cusDevPlan.setSaleChanceId(sid);
        cusDevPlanService.insertCusDevPlan(cusDevPlan);
        return success();
    }

    /**
     * 改变SaleChance对象的开发状态
     * @param saleChance
     * @param flag
     * @return
     */
    @PostMapping("/devResult")
    @ResponseBody
    public ResultInfo devResult(SaleChance saleChance, Integer flag) {
        if (flag == 1) {
            saleChance.setDevResult(2);
        } else if (flag == 2) {
            saleChance.setDevResult(3);
        }

        cusDevPlanService.devResult(saleChance);
        return success();
    }

    /**
     * CusDevPlan对象更新功能
     * @param cusDevPlan
     * @return
     */
    @PostMapping("/updateCusDevPlan")
    @ResponseBody
    public ResultInfo updateCusDevPlan(CusDevPlan cusDevPlan) {
        cusDevPlanService.updateCusDevPlan(cusDevPlan);
        return success();
    }

    /**
     * CusDevPlan对象删除功能
     * @param cusDevPlan
     * @return
     */
    @PostMapping("/deleteCusDevPlan")
    @ResponseBody
    public ResultInfo deleteCusDevPlan(CusDevPlan cusDevPlan) {
        cusDevPlanService.deleteCusDevPlan(cusDevPlan);
        return success();
    }

}
