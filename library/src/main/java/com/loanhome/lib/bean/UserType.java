package com.loanhome.lib.bean;

import com.loanhome.lib.http.HttpResult;

/**
 * @Description TODO
 * Created by Don on 2019/5/28
 */
public class UserType {


    /**
     * result : {"status":1}
     * device_product_new_user : true
     * device_global_new_user : true
     */

    private HttpResult result;
    private boolean device_product_new_user;
    private boolean device_global_new_user;

    public HttpResult getResult() {
        return result;
    }

    public void setResult(HttpResult result) {
        this.result = result;
    }

    public boolean isDevice_product_new_user() {
        return device_product_new_user;
    }

    public void setDevice_product_new_user(boolean device_product_new_user) {
        this.device_product_new_user = device_product_new_user;
    }

    public boolean isDevice_global_new_user() {
        return device_global_new_user;
    }

    public void setDevice_global_new_user(boolean device_global_new_user) {
        this.device_global_new_user = device_global_new_user;
    }
}
