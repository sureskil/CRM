package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.dao.ModuleMapper;
import com.xxxx.crm.model.TreeModel;
import com.xxxx.crm.po.Module;
import com.xxxx.crm.po.Permission;
import com.xxxx.crm.service.Impl.ModuleServiceImpl;
import com.xxxx.crm.utils.AssertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("module")
public class ModuleController extends BaseController {

    @Autowired
    private ModuleMapper moduleMapper;

    @Autowired
    private ModuleServiceImpl moduleService;

    /**
     * 查询所有的module数据
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    public ResultInfo selectModules(Integer rid) {

        AssertUtil.isTrue(rid == null, "数据异常2！");

        List<Integer> list = moduleMapper.selectPser(rid);
        List<TreeModel> modules = moduleMapper.selectModule();

        for (TreeModel model : modules) {
            if (list.contains(model.getId())) {
                model.setChecked(true);
            }
        }

        ResultInfo resultInfo = new ResultInfo();
        resultInfo.setResult(modules);

        return resultInfo;
    }

    /**
     * 访问菜单管理页面
     * @return
     */
    @RequestMapping("index")
    public String toModule() {
        return "module/module";
    }


    /**
     * 在菜单管理中显示所有的module数据
     * @return
     */
    @RequestMapping("/list2")
    @ResponseBody
    public Map<String, Object> allModule() {
        return moduleService.allModule();
    }


    /**
     * 访问菜单管理的添加页面
     * @return
     */
    @RequestMapping("/toAddUpdate")
    public String toAddUpdate(Integer grade, Integer parentId, HttpServletRequest request) {
        request.setAttribute("grade", grade);
        request.setAttribute("parentId", parentId);
        return "module/add";
    }


    /**
     * 添加菜单目录
     * @param module
     * @return
     */
    @PostMapping("/addDirectory")
    @ResponseBody
    public ResultInfo addDirectory(Module module, Integer grade, Integer parentId) {
        moduleService.addDirectory(module, grade, parentId);
        return success();
    }

    /**
     * 访问数据编辑页面
     * @return
     */
    @RequestMapping("/updateModule")
    public String updateModule(Integer mid, HttpServletRequest request) {

        Module module = moduleMapper.selectByPrimaryKey(mid);
        request.setAttribute("module", module);

        return "module/update";
    }


    /**
     * 编辑module数据
     * @param module
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
    public ResultInfo update(Module module) {
        moduleService.update(module);
        return success();
    }


    /**
     * 删除module数据
     * @param module
     * @return
     */
    @PostMapping("/delete")
    @ResponseBody
    public ResultInfo delete(Module module) {
        moduleService.deleteModule(module);
        return success();
    }

}
