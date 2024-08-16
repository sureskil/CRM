package com.xxxx.crm.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.dao.CustomerLossMapper;
import com.xxxx.crm.dao.CustomerMapper;
import com.xxxx.crm.dao.CustomerReprieveMapper;
import com.xxxx.crm.po.Customer;
import com.xxxx.crm.po.CustomerLoss;
import com.xxxx.crm.po.CustomerOrder;
import com.xxxx.crm.po.CustomerReprieve;
import com.xxxx.crm.query.CustomerLossQuery;
import com.xxxx.crm.service.CustomerLossService;
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
public class CustomerLossServiceImpl implements CustomerLossService {

    @Autowired
    private CustomerLossMapper customerLossMapper;

    @Autowired
    private CustomerReprieveMapper customerReprieveMapper;

    @Autowired
    private CustomerMapper customerMapper;

    /**
     * 查询之前，先更新客户的流失状态
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCustomerState() {

        // 查询待流失的客户数据
        List<Customer> customersList = customerLossMapper.updateCustomerState();

        // 把待流失数据放到客户流失表中
        if (customersList != null && customersList.size() > 0) {
            customersList.forEach(customer -> {
                CustomerLoss customerLoss = new CustomerLoss();
                customerLoss.setCreateDate(new Date());
                customerLoss.setCusManager(customer.getCusManager());
                customerLoss.setCusName(customer.getName());
                customerLoss.setCusNo(customer.getKhno());
                customerLoss.setIsValid(1);
                customerLoss.setUpdateDate(new Date());
                customerLoss.setState(0);
                // 客户最后下单时间，通过客户ID查询最后的订单记录
                CustomerOrder customerOrder = customerLossMapper.selectLastOrderById(customer.getId());
                if (customerOrder != null) {
                    customerLoss.setLastOrderTime(customerOrder.getOrderDate());
                }
                // 将customer对象添加到流失表中
                int i = customerLossMapper.insertSelective(customerLoss);
                AssertUtil.isTrue(i != 1, "添加失败！");

                // 最后将对应的customer对象的state设置为1
                AssertUtil.isTrue(customerLossMapper.updateCustState(customer.getId()) != 1, "添加失败2！");

            });
        }

    }

    /**
     * 查询暂缓的数据表格数据
     * @return
     */
    @Override
    public Map<String, Object> selectReprieve() {

        PageHelper.startPage(1, 10);
        List<CustomerReprieve> customerReprieveList = customerLossMapper.selectReprieve();
        PageInfo<CustomerReprieve> pageInfo = new PageInfo<>(customerReprieveList);

        HashMap<String, Object> map = new HashMap<>();
        map.put("code", 0);
        map.put("msg", "");
        map.put("count", pageInfo.getTotal());
        map.put("data", pageInfo.getList());

        return map;
    }

    /**
     * 添加暂缓数据
     * @param customerReprieve
     * @return
     */
    @Override
    public Integer addaddReprieve(CustomerReprieve customerReprieve) {

        AssertUtil.isTrue(customerReprieve == null || customerReprieve.getLossId() == null, "添加暂缓数据添不存在！");
        AssertUtil.isTrue(StringUtils.isBlank(customerReprieve.getMeasure()), "添加内容不能为空！");

        customerReprieve.setIsValid(1);
        customerReprieve.setCreateDate(new Date());
        customerReprieve.setUpdateDate(new Date());

        int i = customerReprieveMapper.insertSelective(customerReprieve);
        AssertUtil.isTrue(i != 1, "暂缓数据添加失败2！");

        return i;
    }


    /**
     * 实现暂缓数据的修改
     * @param customerReprieve
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Integer updateReprieve(CustomerReprieve customerReprieve) {

        AssertUtil.isTrue(customerReprieve == null || customerReprieve.getId() == null, "被修改的暂缓数据不存在！");
        AssertUtil.isTrue(StringUtils.isBlank(customerReprieve.getMeasure()), "暂缓数据内容不能为空");

        customerReprieve.setUpdateDate(new Date());
        int i = customerReprieveMapper.updateByPrimaryKeySelective(customerReprieve);
        AssertUtil.isTrue(i != 1, "暂缓数据修改失败！");

        return i;
    }


    /**
     * 实现暂缓数据的删除
     * @param customerReprieve
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Integer deleteReprieve(CustomerReprieve customerReprieve) {

        AssertUtil.isTrue(customerReprieve == null || customerReprieve.getId() == null, "被修改的暂缓数据不存在！");

        customerReprieve.setIsValid(0);
        customerReprieve.setUpdateDate(new Date());
        int i = customerReprieveMapper.updateByPrimaryKeySelective(customerReprieve);
        AssertUtil.isTrue(i != 1, "赞数数据删除失败！");

        return i;
    }


    /**
     * 实现添加流失数据
     * @param customerLoss
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Integer sureLoss(CustomerLoss customerLoss) {

        AssertUtil.isTrue(customerLoss == null || customerLoss.getId() == null, "确认流失数据不存在！");
        AssertUtil.isTrue(StringUtils.isBlank(customerLoss.getLossReason()), "流失原因不能为空！");

        customerLoss.setState(1);
        // 查询、设置最后下单时间
        selectLastOrderTime(customerLoss);
        // 如果没有最后确认时间，页面或乱码报错，最后确认时间只有年月日，没有时分秒，这是因为在数据表中最后确认时间的类型为date，有年月日的时间类型为datetime
        customerLoss.setConfirmLossTime(new Date());
        customerLoss.setUpdateDate(new Date());
        int i = customerLossMapper.updateByPrimaryKeySelective(customerLoss);
        AssertUtil.isTrue(i != 1, "确认流失失败！");

        return i;
    }

    /**
     * 查询、设置最后下单时间
     * @param customerLoss
     */
    private void selectLastOrderTime(CustomerLoss customerLoss) {
        CustomerLoss customerLoss1 = customerLossMapper.selectByPrimaryKey(customerLoss.getId());
        Customer customer = customerMapper.selectByCustomerName(customerLoss1.getCusName());

        CustomerOrder customerOrder = customerLossMapper.selectLastOrderById(customer.getId());

        // 要标记为确认流失的客户不一定会有订单，所以这里需要判断
        if (customerOrder == null) {
            customerLoss.setLastOrderTime(null);
        } else {
            customerLoss.setLastOrderTime(customerOrder.getOrderDate());
        }

    }

    /**
     * 返回客户流失信息的数据表格数据
     * @param customerLossQuery
     * @return
     */
    @Override
    public Map<String, Object> customerLossList(CustomerLossQuery customerLossQuery) {

        AssertUtil.isTrue(customerLossQuery == null, "数据异常！");
        PageHelper.startPage(customerLossQuery.getPage(), customerLossQuery.getLimit());
        List<CustomerLoss> customerLossList = customerLossMapper.selectCustomerLoss(customerLossQuery);
        PageInfo<CustomerLoss> pageInfo = new PageInfo<>(customerLossList);

        HashMap<String, Object> map = new HashMap<>();
        map.put("code", 0);
        map.put("msg", "");
        map.put("count", pageInfo.getTotal());
        map.put("data", pageInfo.getList());

        return map;
    }

}
