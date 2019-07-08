package com.loanhome.lib.listener;

/**
 * 身份证OCR 回调接口
 * @Description Created by Don on 2019/7/2
 */
public interface VerifyResultCallback {
    int AUTH_FAIL = 0;
    int VERIFY_FAIL = 1;
    int ID_AUTH_FAIL = 2;
    int LIVENESS_AUTH_FAIL = 3;
    /**
     * 验证成功回调
     * @param result
     */
    void onVerifySuccess(String result);

    /**
     * 等待验证回调
     */
    void onVerifyWaitConfirm();

    /**
     * 验证失败回调
     *
     * @param type
     */
    void onVerifyFail(String type);

    void onVerifyCancel();

    void onVerifyStart();
}
