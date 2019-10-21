package com.cloudwise.project.vo;

public enum StatusEnum {

    /**
     * 成功
     * 失败
     * 存在
     * 不存在
     */
    SUCCESS("200", "操作成功"),
    FAILED("404", "操作失败");


    StatusEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private String code;
    private String msg;

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public String getMsg(String code) {
        for(StatusEnum statusEnum : StatusEnum.values()) {
            if(statusEnum.getCode().equals(code)) {
                return statusEnum.getMsg();
            }
        }
        return "null";
    }
}
