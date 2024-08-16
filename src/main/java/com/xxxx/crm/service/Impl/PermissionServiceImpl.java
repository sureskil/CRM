package com.xxxx.crm.service.Impl;

import com.xxxx.crm.dao.ModuleMapper;
import com.xxxx.crm.dao.PermissionMapper;
import com.xxxx.crm.po.Module;
import com.xxxx.crm.po.Permission;
import com.xxxx.crm.service.PermissionService;
import com.xxxx.crm.utils.AssertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private ModuleMapper moduleMapper;


    /**
     * 实现权限数据的添加
     * @param rid role_id
     * @param mid module_id的集合
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void addPermission(Integer rid, Integer[] mid) {

        // AssertUtil.isTrue(mid == null, "添加失败1！");
        AssertUtil.isTrue(rid == null, "添加失败2！");

        // 在添加权限数据之前，删除对应rid的旧权限数据，然后重新添加权限数据
        Integer count = moduleMapper.selectPserByRoleId(rid);
        if (count > 1) {
            Integer i = moduleMapper.deleteByRoleId(rid);
            AssertUtil.isTrue(i != count, "旧数据删除失败！");
        }

        if (mid == null) {
            return;
        }

        for (Integer mi : mid) {
            Permission permission = new Permission();
            permission.setRoleId(rid);

            // 设置默认值
            permission.setCreateDate(new Date());
            permission.setUpdateDate(new Date());
            permission.setModuleId(mi);
            Module module = moduleMapper.selectByPrimaryKey(mi);
            // 执行添加操作
            permission.setAclValue(module.getOptValue());
            // 判读是否添加成功
            int i = permissionMapper.insertSelective(permission);

            AssertUtil.isTrue(i < 1, "数据添加失败！");
        }

    }

}
