package com.loanhome.lib.util;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.loanhome.lib.activity.IDCardDetectActivity;
import com.loanhome.lib.listener.VerifyResultCallback;
import com.megvii.idcardquality.IDCardQualityLicenseManager;
import com.megvii.licensemanager.Manager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @Description Created by Don on 2019/7/1
 */
public class IDCarcdDetectUtil {
    private static final String TAG = "IDCarcdDetectUtil";
    private IDCardQualityLicenseManager mIdCardLicenseManager;
    private int side;

    public int getSide() {
        return side;
    }

    public void setSide(int side) {
        this.side = side;
    }

    public void gotoIDCardDetect(final Activity activity , final VerifyResultCallback callback) {
        if (callback != null) {

        }
        ExecutorService executors = Executors.newCachedThreadPool();
        executors.execute(new Runnable() {
            @Override
            public void run() {
                startGetLiscense(activity,callback);
            }
        });
    }
    /**
     * 添加授权逻辑代码startGetLicense()，授权成功方可调用检测
     * @param activity
     */
    public void startGetLiscense(final Activity activity,final VerifyResultCallback callback){
        mIdCardLicenseManager = new IDCardQualityLicenseManager(activity);

        long status =0;
        try {
            status = mIdCardLicenseManager.checkCachedLicense();
        }catch (Throwable e){
            e.printStackTrace();
        }

        if (status > 0 ){
            Intent intent = new Intent(activity, IDCardDetectActivity.class);
            intent.putExtra("side",getSide());
            String msg = getSide()==0 ? "正面" : "反面";
            Log.i(TAG, "side : "+msg);
            intent.putExtra("idName", "杨振东");
            intent.putExtra("idNumber", "440509199411291218");
            IDCardDetectActivity.setVerifyResultCallback(callback);
            activity.startActivity(intent);
        }else{
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "没有缓存的授权信息，开始授权", Toast.LENGTH_SHORT).show();
//                    callback.onVerifyFail("互联网授权失败");
                }
            });

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        getLiscense(activity,callback);
                    }catch (Throwable e){
                        e.printStackTrace();
                    }

                }
            }).start();
        }
    }

    /**
     * 获取授权
     * @param activity
     */
    private void getLiscense(final Activity activity, final VerifyResultCallback callback){
        Manager manager = new Manager(activity);
        manager.registerLicenseManager(mIdCardLicenseManager);

        String uuid = Global.uuid;
        String authMsg = mIdCardLicenseManager.getContext(uuid);
        manager.takeLicenseFromNetwork(authMsg);
        if (mIdCardLicenseManager.checkCachedLicense()>0){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(activity, IDCardDetectActivity.class);
                    intent.putExtra("side",getSide());
                    String msg = getSide()==0 ? "正面" : "反面";
                    Log.i(TAG, "side : "+msg);
                    intent.putExtra("idName", "杨振东");
                    intent.putExtra("idNumber", "440509199411291218");
                    IDCardDetectActivity.setVerifyResultCallback(callback);
                    activity.startActivity(intent);
                }
            });
        }
    }

}
