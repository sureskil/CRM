package com.xxxx.crm.base;

public class ResultInfo {
    // 默认返回的状态值
    private Integer code=200;
    // 默认返回的信息
    private String msg="success";
    // 存放返回需要的数据，是一个Object类型的对象
    private Object result;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
