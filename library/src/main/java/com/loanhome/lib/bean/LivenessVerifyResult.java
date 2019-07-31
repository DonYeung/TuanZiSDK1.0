package com.loanhome.lib.bean;

/**
 * @Description Created by Don on 2019/7/8
 */
public class LivenessVerifyResult {

    /**
     * result : {"status":1,"errorcode":300}
     * flag : true
     */

    private HttpResult result;
    private boolean flag;

    public HttpResult getResult() {
        return result;
    }

    public void setResult(HttpResult result) {
        this.result = result;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

}
