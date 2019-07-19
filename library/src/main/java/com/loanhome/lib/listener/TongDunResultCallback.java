package com.loanhome.lib.listener;

/**
 * 同盾SDK 回调接口
 * @Description Created by Don on 2019/7/2
 */
public interface TongDunResultCallback {
    void onSuccess(String data);
    void onFailed();
    void onProgress();
}
