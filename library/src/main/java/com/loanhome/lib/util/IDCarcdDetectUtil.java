package com.loanhome.lib.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.loanhome.lib.activity.IDCardDetectActivity;
import com.loanhome.lib.bean.VerifyInfo;
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
    private Activity activity;
    private VerifyInfo mInfo;

    public VerifyInfo getInfo() {
        return mInfo;
    }

    public void setInfo(VerifyInfo info) {
        this.mInfo = info;
    }

    public void gotoIDCardDetect(final Activity activity , final VerifyResultCallback callback) {
        this.activity = activity;
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
     * 获取Liscense
     */
    public void getOCRLiscense() {
        final IDCardQualityLicenseManager mIdCardLicenseManager = new IDCardQualityLicenseManager(activity);
        final Manager manager = new Manager(activity);
        manager.registerLicenseManager(mIdCardLicenseManager);
        String uuid = Util.getUUIDString(activity);
        final String authMsg = mIdCardLicenseManager.getContext(uuid);
        final SharedPreferences mSp = activity.getSharedPreferences(
                Constants.SharedPreferencesKey.GETOCR_LISCENSE,
                Context.MODE_PRIVATE);

        if(mSp.contains(Constants.SharedPreferencesKey.GETOCR_LISCENSE)){
            Log.i("Don", "getOCRLiscense: GETOCR_LISCENSE 有值了");
            return;
        }
        Log.i("Don", "getOCRLiscense: GETOCR_LISCENSE 无值去请求");
        ExecutorService executors = Executors.newCachedThreadPool();
        executors.execute(new Runnable() {
            @Override
            public void run() {
                manager.takeLicenseFromNetwork(authMsg);
                long status = mIdCardLicenseManager.checkCachedLicense();
                if (status > 0) {
                    mSp.edit().putLong(Constants.SharedPreferencesKey.GETOCR_LISCENSE, status)
                            .apply();
                }
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
            intent.putExtra("side",mInfo.getCameraType());
            intent.putExtra("idName", mInfo.getIdCardName());
            intent.putExtra("idNumber", mInfo.getIdCardNumber());
            intent.putExtra("isNeedCallBackFront", mInfo.getNeedCallBackFront());
            intent.putExtra("isNeedCallBackBack", mInfo.getNeedCallBackBack());
            intent.putExtra("functionId", mInfo.getFunctionId());
            intent.putExtra("contentId", mInfo.getContentId());
            intent.putExtra("api_id", mInfo.getApi_id());
            intent.putExtra("pPosition", mInfo.getpPosition());
            intent.putExtra("param1", mInfo.getParam1());
            intent.putExtra("param2", mInfo.getParam2());
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
                    intent.putExtra("side",mInfo.getCameraType());
                    intent.putExtra("idName", mInfo.getIdCardName());
                    intent.putExtra("idNumber", mInfo.getIdCardNumber());
                    intent.putExtra("isNeedCallBackFront", mInfo.getNeedCallBackFront());
                    intent.putExtra("isNeedCallBackBack", mInfo.getNeedCallBackBack());
                    intent.putExtra("functionId", mInfo.getFunctionId());
                    intent.putExtra("contentId", mInfo.getContentId());
                    intent.putExtra("api_id", mInfo.getApi_id());
                    intent.putExtra("pPosition", mInfo.getpPosition());
                    intent.putExtra("param1", mInfo.getParam1());
                    intent.putExtra("param2", mInfo.getParam2());
                    IDCardDetectActivity.setVerifyResultCallback(callback);
                    activity.startActivity(intent);
                }
            });
        }
    }

}
