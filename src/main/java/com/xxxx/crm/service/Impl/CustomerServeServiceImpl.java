package com.xxxx.crm.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.dao.CustomerServeMapper;
import com.xxxx.crm.enums.CustomerServeStatus;
import com.xxxx.crm.po.CustomerServe;
import com.xxxx.crm.query.CustomerServeQuery;
import com.xxxx.crm.service.CustomerServeService;
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
public class CustomerServeServiceImpl implements CustomerServeService {

    @Autowired
    private CustomerServeMapper customerServeMapper;

    /**
     * 实现多条件查询
     * @param customerServeQuery
     * @return
     */
    @Override
    public Map<String, Object> customerServeList(CustomerServeQuery customerServeQuery) {

        AssertUtil.isTrue(customerServeQuery == null, "查询对象不存在！");

        PageHelper.startPage(customerServeQuery.getPage(), customerServeQuery.getLimit());
        List<Map<String, Object>> customerServes = customerServeMapper.selectCustomerServeByParams(customerServeQuery);
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(customerServes);

        HashMap<String, Object> map = new HashMap<>();
        map.put("code", 0);
        map.put("msg", "");
        map.put("count", pageInfo.getTotal());
        map.put("data", pageInfo.getList());

        return map;
    }

    /**
     * 实现服务管理的服务创建
     * @param customerServe
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Integer addServe(CustomerServe customerServe) {

        AssertUtil.isTrue(customerServe == null, "添加数据不存在！");
        AssertUtil.isTrue(StringUtils.isBlank(customerServe.getServeType()), "服务类型不能为空！");
        AssertUtil.isTrue(StringUtils.isBlank(customerServe.getCustomer()), "客户姓名不能为空！");
        AssertUtil.isTrue(StringUtils.isBlank(customerServe.getServiceRequest()), "服务内容不能为空！");

        customerServe.setIsValid(1);
        customerServe.setCreateDate(new Date());
        customerServe.setUpdateDate(new Date());
        int i = customerServeMapper.insertSelective(customerServe);
        AssertUtil.isTrue(i != 1, "数据添加失败！");

        return i;
    }


    /**
     * 实现 分配/处理/反馈/归档 服务
     *
     * 按照现在的写法是用不上fw_005的，除非在归档的页面中继续操作数据，所以数据库中fw_004状态码的数据就是最终处理完成的数据
     * 这里作为测试，给scott用户分配的处理数据中只对两条进行了处理：86、91
     * 其中86还处于反馈阶段，91已经反馈完毕
     * 在归档页面中，可以看到数据处于哪个处理阶段
     * 现在的归档页面是查询了所有的数据，但是我觉得应该展示的数据是：已经反馈完毕并且state为fw_004的数据
     *
     *
     * @param customerServe
     * @return
     */
    @Override
    public void updateCustomerServe(CustomerServe customerServe) {

        AssertUtil.isTrue(customerServeMapper.selectByPrimaryKey(customerServe.getId()) == null || customerServe.getId() == null, "待更新的服务记录不存在！");

        if (CustomerServeStatus.ASSIGNED.getState().equals(customerServe.getState())) {

            AssertUtil.isTrue(StringUtils.isBlank(customerServe.getAssigner()), "分配人不能为空！");

            customerServe.setAssignTime(new Date());
            customerServe.setUpdateDate(new Date());
            int i = customerServeMapper.updateByPrimaryKeySelective(customerServe);
            AssertUtil.isTrue( i != 1, "服务分配失败！");

        } else if (CustomerServeStatus.PROCED.getState().equals(customerServe.getState())) {

            AssertUtil.isTrue(StringUtils.isBlank(customerServe.getServiceProce()), "服务处理不能为空！");

            customerServe.setServiceProceTime(new Date());
            customerServe.setUpdateDate(new Date());
            int i = customerServeMapper.updateByPrimaryKeySelective(customerServe);
            AssertUtil.isTrue( i != 1, "服务处理失败！");

        } else if (CustomerServeStatus.FEED_BACK.getState().equals(customerServe.getState())) {

            AssertUtil.isTrue(StringUtils.isBlank(customerServe.getServiceProceResult()), "处理结果不能为空！");
            AssertUtil.isTrue(StringUtils.isBlank(customerServe.getMyd()), "满意度不能为空！");

            customerServe.setUpdateDate(new Date());
            int i = customerServeMapper.updateByPrimaryKeySelective(customerServe);
            AssertUtil.isTrue( i != 1, "服务反馈失败！");

        }


    }


}
