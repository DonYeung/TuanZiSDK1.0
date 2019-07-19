package com.loanhome.lib.util;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.loanhome.lib.activity.LivenessBiz;
import com.loanhome.lib.bean.BizTokenResult;
import com.loanhome.lib.bean.VerifyInfo;
import com.loanhome.lib.http.RetrofitUtils4test;
import com.loanhome.lib.listener.VerifyResultCallback;


/**
 * @Description Created by Don on 2019/7/1
 */
public class LivenessUtil {
    private static final String TAG = "LivenessUtil";
    private VerifyInfo info;

    public VerifyInfo getInfo() {
        return info;
    }

    public void setInfo(VerifyInfo info) {
        this.info = info;
    }

    public void getBizToken(final Activity activity, final VerifyResultCallback callback){

        LivenessBiz livenessBiz = new LivenessBiz(activity);
        livenessBiz.setVerifyResultCallback(callback);
        if (info!=null) {
            livenessBiz.getBizToken(info.getIdCardName(), info.getIdCardNumber(), info);
        }

    }

}
