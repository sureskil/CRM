package com.xxxx.crm.enums;

public enum CustomerServeStatus {

    // 创建
    CREATED("fw_0001"),
    // 分配
    ASSIGNED("fw_002"),
    // 处理
    PROCED("fw_003"),
    //反馈
    FEED_BACK("fw_004"),
    // 归档
    ARCHIVED("fw_005");

    private String state;

    public String getState() {
        return state;
    }

    CustomerServeStatus(String state) {
        this.state = state;
    }

}
