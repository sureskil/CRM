package com.xxxx.crm.enums;

public enum A {
    YES(1),
    NO(0);

    private Integer type;

    A(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
