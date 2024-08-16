package com.xxxx.crm.task;


import com.xxxx.crm.service.Impl.CustomerLossServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class JobTask {

    @Autowired
    private CustomerLossServiceImpl customerLossService;

    // @Scheduled(cron = "0/2 * * * * ?")
    public void job() {
        System.out.println("定时任务开始执行-->"  + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        customerLossService.updateCustomerState();
    }

}
