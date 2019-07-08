package com.loanhome.lib.bean;

/**
 * @Description Created by Don on 2019/7/8
 */
public class LivenessVerifyResult {
    private HttpResult result;
    private Boolean flag;

    public HttpResult getResult() {
        return result;
    }

    public void setResult(HttpResult result) {
        this.result = result;
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }
}
