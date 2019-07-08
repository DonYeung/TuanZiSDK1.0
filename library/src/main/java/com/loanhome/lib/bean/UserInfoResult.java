package com.loanhome.lib.bean;

/**
 * @Description Created by Don on 2019/7/2
 */
public class UserInfoResult {
    private HttpResult result;
    private int  brief_bill;
    private int detailed_bill;

    public HttpResult getResult() {
        return result;
    }

    public void setResult(HttpResult result) {
        this.result = result;
    }

    public int getBrief_bill() {
        return brief_bill;
    }

    public void setBrief_bill(int brief_bill) {
        this.brief_bill = brief_bill;
    }

    public int getDetailed_bill() {
        return detailed_bill;
    }

    public void setDetailed_bill(int detailed_bill) {
        this.detailed_bill = detailed_bill;
    }
}
