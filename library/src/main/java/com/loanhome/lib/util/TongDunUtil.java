package com.loanhome.lib.util;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.loanhome.lib.activity.LivenessBiz;
import com.loanhome.lib.bean.VerifyInfo;
import com.loanhome.lib.http.AppDataCourceController;
import com.loanhome.lib.listener.TongDunResultCallback;
import com.loanhome.lib.listener.VerifyResultCallback;

import cn.tongdun.android.shell.FMAgent;
import cn.tongdun.android.shell.exception.FMException;

import static cn.tongdun.android.shell.FMAgent.STATUS_SUCCESSFUL;


/**
 * @Description Created by Don on 2019/7/1
 */
public class TongDunUtil {
    private static final String TAG = "TongDunUtil";

    public void intitTongDunSDK(Context context, final TongDunResultCallback callback) {

        try {
            Boolean isDebug = Global.IsDebug;
            FMAgent.init(context, isDebug ? FMAgent.ENV_SANDBOX : FMAgent.ENV_PRODUCTION);


            String balckBox = null;
            while (TextUtils.isEmpty(balckBox)) {
                String initStatus = FMAgent.getInitStatus();
                if (initStatus.equals(STATUS_SUCCESSFUL)) {
                    balckBox = FMAgent.onEvent(context);
                    AppDataCourceController.getInstance().upLoadFingerprint(balckBox,callback);
                    Log.i("TONGDUN", initStatus);
                    Log.i("TONGDUN", balckBox);
                }

            }


        } catch (FMException e) {
            e.printStackTrace();
        }

    }

}
