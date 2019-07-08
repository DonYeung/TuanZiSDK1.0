package com.loanhome.lib.util;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.loanhome.lib.activity.LivenessBiz;
import com.loanhome.lib.bean.BizTokenResult;
import com.loanhome.lib.http.RetrofitUtils4test;
import com.loanhome.lib.listener.VerifyResultCallback;


/**
 * @Description Created by Don on 2019/7/1
 */
public class LivenessUtil {
    private static final String TAG = "LivenessUtil";

    public void getBizToken(final Activity activity,final VerifyResultCallback callback){

        String idName ="杨振东";
        String idNumber="440509199411291218";

        LivenessBiz livenessBiz = new LivenessBiz(activity);
        livenessBiz.getBizToken(idName, idNumber);
    }

}
