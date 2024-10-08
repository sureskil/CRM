package com.xxxx.crm.enums;

/**
 * 替换代码中的开发状态具体值
 */
public enum  DevResult {
    // 未开发
    UNDEV(0),
    // 开发中
    DEVING(1),
    // 开发成功
    DEV_SUCCESS(2),
    // 开发失败
    DEV_FAILED(3);

    private  Integer status;

    DevResult(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
}
