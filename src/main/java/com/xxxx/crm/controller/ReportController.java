package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
public class ReportController extends BaseController {


    /**
     * 统计报表的页面访问
     * @param type
     * @return
     */
    @RequestMapping("report/{type}")
    public String toReport(@PathVariable Integer type) {

        if (type == 0) {

            return "report/customer_contri";

        } else if (type == 1) {

            return "report/customer_make";

        } else if (type == 3) {

            return "report/customer_loss";

        }

        return "error";
    }

}
