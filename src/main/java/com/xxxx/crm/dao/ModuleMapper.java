package com.xxxx.crm.dao;

import com.xxxx.crm.model.TreeModel;
import com.xxxx.crm.po.Module;
import com.xxxx.crm.po.Permission;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Module record);

    int insertSelective(Module record);

    Module selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Module record);

    int updateByPrimaryKey(Module record);

    /**
     * 查询所有需要字段的的module数据
     * @return
     */
    List<TreeModel> selectModule();

    /**
     * 通过role_id查询对应的Permissioin对象
     * @param roleId
     * @return
     */
    List<Integer> selectPser(Integer roleId);

    /**
     * 通过role_id查询对应数据的数量
     * @param roleId
     * @return
     */
    Integer selectPserByRoleId(Integer roleId);

    /**
     * 通过role_id删除对应的数据
     * @param roleId
     * @return
     */
    Integer deleteByRoleId(Integer roleId);

    /**
     * 查询所有存在的module数据
     * @return
     */
    List<Module> selectAllModule();

    /**
     * 查询module_name对应的数据条数
     * @return
     */
    Integer selectByModuleName(String moduleName);

    /**
     * 查询optValue对应的数据条数
     * @param optValue
     * @return
     */
    Integer selectByModuleOptValue(String optValue);
}