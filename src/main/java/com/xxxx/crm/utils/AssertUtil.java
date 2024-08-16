package com.xxxx.crm.utils;

import com.xxxx.crm.exceptions.ParamsException;

/**
 * 根据判断结果，抛出对应的异常
 */
public class AssertUtil {

    public  static void isTrue(Boolean flag,String msg){
        if(flag){
            throw  new ParamsException(msg);
        }
    }

}
