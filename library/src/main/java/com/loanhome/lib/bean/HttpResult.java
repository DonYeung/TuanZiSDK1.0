package com.loanhome.lib.bean;

/**
 * @Description TODO
 * Created by Don on 2019/5/30
 */
public class HttpResult {

    /**
     * status : 0
     * errorcode : 0
     * msg : 非法请求
     */

    private int status;
    private int errorcode;
    private String msg;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(int errorcode) {
        this.errorcode = errorcode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
