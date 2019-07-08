package com.loanhome.lib.listener;

/**
 * 身份证OCR-认证失败（无图片）弹窗监听接口
 * Created by Don on 2019/7/1
 */
public interface LivenessDialogDismissListener {

    void onDismiss(boolean isTryAgain);
}
