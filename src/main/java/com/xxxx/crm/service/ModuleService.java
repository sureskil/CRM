package com.xxxx.crm.service;

import com.xxxx.crm.po.Module;

import java.util.Map;

public interface ModuleService {
    // 在菜单管理中显示所有的module数据
    Map<String, Object> allModule();

    // 添加菜单目录
    Integer addDirectory(Module module, Integer grade, Integer parentId);

    // 编辑module数据
    Integer update(Module module);

    // 删除moudle数据
    Integer deleteModule(Module module);
}
