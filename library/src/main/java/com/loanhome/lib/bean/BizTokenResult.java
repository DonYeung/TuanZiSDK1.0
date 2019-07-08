package com.loanhome.lib.bean;

/**
 * @Description Created by Don on 2019/7/8
 */
public class BizTokenResult {
    private HttpResult result;
    private String token;

    public HttpResult getResult() {
        return result;
    }

    public void setResult(HttpResult result) {
        this.result = result;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
