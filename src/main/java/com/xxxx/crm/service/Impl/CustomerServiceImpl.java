package com.xxxx.crm.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.dao.CustomerMapper;
import com.xxxx.crm.po.Customer;
import com.xxxx.crm.po.CustomerOrder;
import com.xxxx.crm.po.OrderDetails;
import com.xxxx.crm.query.CustomerQuery;
import com.xxxx.crm.query.ReportQuery;
import com.xxxx.crm.service.CustomerService;
import com.xxxx.crm.utils.A;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.Md5Util;
import com.xxxx.crm.utils.PhoneUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    /**
     * 实现客户信息管理数据的多条件查询
     * @param customerQuery
     * @return
     */
    @Override
    public Map<String, Object> selectCustomer(CustomerQuery customerQuery) {

        AssertUtil.isTrue(customerQuery == null, "数据异常，查询失败！");

        PageHelper.startPage(customerQuery.getPage(), customerQuery.getLimit());
        List<Customer> customerList = customerMapper.selectCustomer(customerQuery);
        PageInfo<Customer> pageInfo = new PageInfo<>(customerList);

        HashMap<String, Object> map = new HashMap<>();
        map.put("code", 0);
        map.put("msg", "");
        map.put("count", pageInfo.getTotal());
        map.put("data", pageInfo.getList());

        return map;
    }


    /**
     * 实现客户信息数据添加
     * @param customer
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Integer addCustomer(Customer customer) {

        // 参数检测
        checkCustomerParams(customer);
        Customer byName = customerMapper.selectByName(customer.getName());
        // 添加的时候加这个判断，修改的时候不加
        AssertUtil.isTrue(byName != null || !(byName.getId()).equals(customer.getId()), "客户名已存在亲重新输入");
        // 设置默认参数
        setCustomerParams(customer);

        // 添加操作
        int i = customerMapper.insertSelective(customer);
        // 判断是否添加成功
        AssertUtil.isTrue(i != 1, "数据添加失败！");

        return i;
    }

    /**
     * 实现客户信息数据修改
     * @param customer
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Integer updateCustomer(Customer customer) {

        // 参数检测
        AssertUtil.isTrue(customer == null || customer.getId() == null, "数据异常，数据修改失败！");
        checkCustomerParams(customer);

        // 判断是否有值被修改
        Customer customer1 = customerMapper.selectByPrimaryKey(customer.getId());
        AssertUtil.isTrue((customer1.getName()).equals((customer.getName()))
                        && (customer1.getPhone()).equals((customer.getPhone()))
                        && (customer1.getFr()).equals((customer.getFr())), "没有数据被修改，无法提交");

        // 设置默认值
        customer.setUpdateDate(new Date());
        int i = customerMapper.updateByPrimaryKeySelective(customer);
        AssertUtil.isTrue(i != 1, "数据修改失败！");

        return i;
    }

    /**
     * 实现客户信息数据删除
     * @param customer
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Integer deleteCustomer(Customer customer) {

        AssertUtil.isTrue(customer == null || customer.getId() == null, "被删除数据不存在！");

        customer.setIsValid(0);
        int i = customerMapper.updateByPrimaryKeySelective(customer);
        AssertUtil.isTrue(i != 1, "数据删除失败！");

        return i;
    }


    /**
     * 实现订单信息查询
     * @return
     */
    @Override
    public Map<String, Object> selectOrder(Integer cusId) {

        PageHelper.startPage(1, 10);
        List<CustomerOrder> orderList = customerMapper.selectOrder(cusId);
        PageInfo<CustomerOrder> pageInfo = new PageInfo<>(orderList);

        HashMap<String, Object> map = new HashMap<>();
        map.put("code", 0);
        map.put("msg", "");
        map.put("count", pageInfo.getTotal());
        map.put("data", pageInfo.getList());

        return map;
    }


    /**
     * 查询订单详情数据实现
     * @param orderId
     * @return
     */
    @Override
    public Map<String, Object> selectDetailByOrderDetail(Integer orderId) {

        AssertUtil.isTrue(orderId == null, "数据查询不存在！");
        PageHelper.startPage(1, 10);
        List<OrderDetails> orderDetails = customerMapper.selectDetailByOrderDetail(orderId);
        PageInfo<OrderDetails> pageInfo = new PageInfo<>(orderDetails);

        HashMap<String, Object> map = new HashMap<>();
        map.put("code", 0);
        map.put("msg", "");
        map.put("total", pageInfo.getTotal());
        map.put("data", pageInfo.getList());

        return map;
    }


    /**
     * 查询客户贡献分析中的数据，只要客户名和金额总数
     * @return
     */
    @Override
    public Map<String, Object> queryCustomerContributionByParams(ReportQuery reportQuery) {

        List<Map<String, Object>> mapList = customerMapper.queryCustomerContributionByParams(reportQuery);

        HashMap<String, Object> map = new HashMap<>();
        map.put("code", 0);
        map.put("msg", "");
        map.put("count", mapList.size());
        map.put("data", mapList);

        return map;
    }

    /**
     * 实现查询客户level分别对应的客户数量（折线图）
     * @return
     */
    @Override
    public Map<String, Object> countCustomerMake() {

        // 这里每一个map对象中有，x轴需要的数据（这里是data1），series中需要显示的数据（这里是data2）
        List<Map<String, Object>> mapList = customerMapper.countCutomerMake();
        ArrayList<String> data1 = new ArrayList<>();
        ArrayList<Integer> data2 = new ArrayList<>();
        mapList.forEach(stringObjectMap -> {
            data1.add(stringObjectMap.get("level").toString());
            data2.add(Integer.parseInt(stringObjectMap.get("total").toString()));
        });

        HashMap<String, Object> map = new HashMap<>();
        map.put("data1", data1);
        map.put("data2", data2);

        return map;
    }

    /**
     * 实现查询客户level分别对应的客户数量（饼状图）
     *
     * 这里的实现思路有两个
     *      1.现在用的是：重写sql语句进行别名的配置，name与value，之前折线图的字段分别是level和total，查出的数据直接返回即可
     *      2.和折线图共用一个sql，查出的数据需要循环放到新的List<Map<String, Object>>中，目的是为了换字段名
     *      3.其实折线图没有字段名的要求，直接写成name与value即可
     *
     * @return
     */
    @Override
    public List<Map<String, Object>> countCustomerMake02() {

        List<Map<String, Object>> mapList = customerMapper.countCutomerMake02();

        return mapList;
    }


    private void setCustomerParams(Customer customer) {
        customer.setState(0);
        customer.setIsValid(1);
        customer.setKhno("KH" + System.currentTimeMillis());
        customer.setCreateDate(new Date());
        customer.setUpdateDate(new Date());
    }

    private void checkCustomerParams(Customer customer) {
        AssertUtil.isTrue(StringUtils.isBlank(customer.getName()), "客户名称不能为空！");
        AssertUtil.isTrue(StringUtils.isBlank(customer.getFr()), "法人不能为空！");
        AssertUtil.isTrue(StringUtils.isBlank(customer.getPhone()), "联系电话不能为空！");
        AssertUtil.isTrue(PhoneUtil.isMobile(customer.getPhone()) == false, "联系电话格式不正确！" );
    }
}
