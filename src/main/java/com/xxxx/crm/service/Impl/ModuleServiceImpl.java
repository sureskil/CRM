package com.xxxx.crm.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.dao.ModuleMapper;
import com.xxxx.crm.model.TreeModel;
import com.xxxx.crm.po.Module;
import com.xxxx.crm.service.ModuleService;
import com.xxxx.crm.utils.AssertUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModuleServiceImpl implements ModuleService {

    @Autowired
    private ModuleMapper moduleMapper;

    /**
     * 查询所有Module对象
     * @return
     */
    @Override
    public Map<String, Object> allModule() {

        // 这里显示的效果是树形表格，用Layui的一个第三方组件实现，不需要分页！
        // 返回的格式数据与layui的数据表格相同，只是不分页！
        List<Module> moduleList = moduleMapper.selectAllModule();

        HashMap<String, Object> map = new HashMap<>();
        map.put("code", 0);
        map.put("msg", "");
        map.put("count", moduleList.size());
        map.put("data", moduleList);

        return map;
    }

    /**
     * 添加module对象
     * @param module
     * @param grade
     * @param parentId
     * @return
     */
    @Override
    public Integer addDirectory(Module module, Integer grade, Integer parentId) {

        // 参数校验
        checkAdParams(module);

        AssertUtil.isTrue(moduleMapper.selectByModuleName(module.getModuleName()) > 0, "菜单名已存在，请重新输入！");
        AssertUtil.isTrue(moduleMapper.selectByModuleOptValue(module.getOptValue()) > 0, "权限码已存在，请重新输入！");
        // TODO 添加url是否重复的判断
        AssertUtil.isTrue(grade == null, "数据异常！");
        AssertUtil.isTrue(parentId == null, "数据异常！");

        module.setIsValid((byte) 1);
        module.setCreateDate(new Date());
        module.setUpdateDate(new Date());
        module.setGrade(grade);
        module.setParentId(parentId);
        int i = moduleMapper.insertSelective(module);

        AssertUtil.isTrue(i < 1, "目录添加成功！");

        return i;
    }

    /**
     * 实现module数据的编辑
     * @param module
     * @return
     */
    @Override
    public Integer update(Module module) {
        // 必要字段不能为空
        checkAdParams(module);
        // 判断是否重复
        // AssertUtil.isTrue(moduleMapper.selectByModuleName(module.getModuleName()) >= 1, "菜单名已存在，请重新输入！");
        AssertUtil.isTrue(moduleMapper.selectByModuleOptValue(module.getOptValue()) >= 1, "权限码已存在，请重新输入！");
        // TODO 添加url是否重复的判断

        AssertUtil.isTrue(module == null, "数据异常，数据修改失败！");
        module.setUpdateDate(new Date());
        int i = moduleMapper.updateByPrimaryKeySelective(module);

        AssertUtil.isTrue(i<1, "数据更新失败！");

        return i;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Integer deleteModule(Module module) {

        AssertUtil.isTrue(module == null, "数据异常，被删除数据不存在！");
        AssertUtil.isTrue(module.getId() == null, "数据异常1！");

        module.setUpdateDate(new Date());
        module.setIsValid((byte) 0);
        int i = moduleMapper.updateByPrimaryKeySelective(module);

        AssertUtil.isTrue(i<1, "数据删除失败！");

        return i;
    }

    private void checkAdParams(Module module) {
        AssertUtil.isTrue(module == null, "添加目录不存在！");
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()), "目录名不能为空！");
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()), "权限吗不能为空！");
    }
}
