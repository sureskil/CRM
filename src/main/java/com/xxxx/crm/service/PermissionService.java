package com.xxxx.crm.service;

import com.xxxx.crm.po.Permission;

public interface PermissionService {

    // 添加权限数据
    void addPermission(Integer rid, Integer[] mids);
}
