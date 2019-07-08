package com.loanhome.lib.listener;

/**
 * 魔蝎SDK 回调接口
 * @Description Created by Don on 2019/7/2
 */
public interface MoxieResultCallback {
    void onSuccess(String action);
    void onFailed();
    void onProgress();
}
