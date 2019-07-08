package com.loanhome.lib.util;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.loanhome.lib.activity.BillImportModeActivity;
import com.loanhome.lib.activity.IDCardDetectActivity;
import com.loanhome.lib.bean.TaskInfo;
import com.loanhome.lib.listener.MoxieResultCallback;
import com.loanhome.lib.listener.VerifyResultCallback;
import com.megvii.idcardquality.IDCardQualityLicenseManager;
import com.megvii.licensemanager.Manager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.loanhome.lib.activity.BillImportModeActivity.TASK_INFO;
import static com.loanhome.lib.activity.BillImportModeActivity.TASK_TYPE;


/**
 * @Description Created by Don on 2019/7/1
 */
public class MoXieUtil {
    private static final String TAG = "MoXieUtil";

    public void gotoMoXie(Activity activity, String task, TaskInfo info, final MoxieResultCallback callback){
        JSONObject object  = new JSONObject();
        try {
            object.put("login_code",info.loginCode);
            object.put("login_target",info.loginTarget);
            object.put("login_type",info.loginType);
            object.put("username",info.account);
            object.put("password",info.password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(activity, BillImportModeActivity.class);
        intent.putExtra(TASK_TYPE,task);
        intent.putExtra(TASK_INFO,object.toString());

        BillImportModeActivity.setMoxieResultCallback(callback);

        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }

}
