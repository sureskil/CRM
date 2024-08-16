package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.po.Permission;
import com.xxxx.crm.service.Impl.PermissionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("Permission")
public class PermissionController extends BaseController {

    @Autowired
    private PermissionServiceImpl permissionService;

    /**
     * 添加权限数据
     * @param rid
     * @param mid
     * @return
     */
    @PostMapping("/add")
    @ResponseBody
    public ResultInfo addPermission(Integer rid, Integer[] mid) {
        permissionService.addPermission(rid, mid);
        return success();
    }

}
