package com.loanhome.lib.bean;

public class BankCardResult {
    private HttpResult httpResult;

    /**
     * result : {"status":1}
     * bankName : 中国建设银行
     * number : 6217 0030 9000 3215 920
     * flag : true
     */

    private HttpResult result;
    private String bankName;
    private String number;
    private boolean flag;

    public HttpResult getResult() {
        return result;
    }

    public void setResult(HttpResult result) {
        this.result = result;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
