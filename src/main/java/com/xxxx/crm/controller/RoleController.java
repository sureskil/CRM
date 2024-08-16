package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.dao.RoleMapper;
import com.xxxx.crm.model.RoleModel;
import com.xxxx.crm.po.Role;
import com.xxxx.crm.query.RoleQuery;
import com.xxxx.crm.service.Impl.RoleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/role")
public class RoleController extends BaseController {

    @Autowired
    private RoleServiceImpl roleService;

    @Autowired
    private RoleMapper roleMapper;

    /**
     * 访问角色管理页面
     * @return
     */
    @RequestMapping("index")
    public String roleIndex() {
        return "role/role";
    }

    /**
     * 返回角色的数据表格数据
     * @param roleQuery
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> queryRoleBatch(RoleQuery roleQuery) {
        return  roleService.queryRoleBatch(roleQuery);
    }


    /**
     * 访问添加/更新页面
     * @return
     */
    @RequestMapping("/addUpdate")
    public String addUpdate(HttpServletRequest request, Integer id) {

        if (id != null) {
            Role role1 = roleMapper.selectByPrimaryKey(id);
            request.setAttribute("role", role1);
        }

        return "role/add_update";
    }

    @PostMapping("/add")
    @ResponseBody
    public ResultInfo addRole(Role role) {
        roleService.addRole(role);
        return success();
    }

    /**
     * 编辑角色数据
     * @param role
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
    public ResultInfo updateRole(Role role) {
        roleService.updateRole(role);
        return success();
    }

    /**
     * 删除角色数据
     * @param role
     * @return
     */
    @PostMapping("/delete")
    @ResponseBody
    public ResultInfo deleteRole(Role role) {
        roleService.deleteRole(role);
        return success();
    }

    /**
     * 访问授权页面
     * @return
     */
    @RequestMapping("/toGrant")
    public String grantRole(Integer id, HttpServletRequest request) {
        request.setAttribute("roleId", id);
        return "role/grant";
    }


    /**
     * 查询所有的用户id与name
     * @return
     */
    @PostMapping("/roleIdAndName")
    @ResponseBody
    public List<RoleModel> roleIdAndName(Integer id) {

        List<RoleModel> roleModels = roleMapper.selectRoleIdAndName();

        if (id != null) {
            List<Integer> list = roleMapper.selectRoleId(id);
            for (RoleModel r : roleModels) {
                if (list.contains(r.getId())) {
                    r.setSelected("selected");
                }
            }

        }

        return roleModels;


    // public List<Map<String, Object>> roleIdAndName() {
        // return roleMapper.selectRn();
    }

}
