package com.loanhome.lib.activity;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.loanhome.lib.R;
import com.loanhome.lib.bean.BizTokenResult;
import com.loanhome.lib.bean.HttpResult;
import com.loanhome.lib.bean.LivenessVerifyResult;
import com.loanhome.lib.http.RetrofitUtils4test;
import com.loanhome.lib.listener.LivenessDialogDismissListener;
import com.loanhome.lib.view.LivenessResultDialog;
import com.megvii.meglive_sdk.listener.DetectCallback;
import com.megvii.meglive_sdk.listener.PreCallback;
import com.megvii.meglive_sdk.manager.MegLiveManager;

public class LivenessBiz implements DetectCallback, PreCallback {

    private static final String TAG = "LivenessBiz";
    private static final String HOST = "https://api.megvii.com";
    private Activity mActivity;
    private MegLiveManager megLiveManager;
    private String bizToken;
    private String idCardName;
    private String idCardNumber;


    public LivenessBiz(Activity activity){
        this.mActivity = activity;
        megLiveManager = MegLiveManager.getInstance();
        String version = megLiveManager.getVersion();
        Log.i(TAG, "LivenessBiz version: "+version);
    }

    public void getBizToken(String idcardname, String idcardnumber){
        idCardName = idcardname;
        idCardNumber = idcardnumber;
        Log.i(TAG, "getBizToken: ");
        RetrofitUtils4test.getInstance().getBizTokenmain(idcardname, idcardnumber,
                new RetrofitUtils4test.ResponseListener<BizTokenResult>() {
                    @Override
                    public void onResponse(BizTokenResult response) {

                            bizToken = response.getToken();
                            if (!TextUtils.isEmpty(bizToken)){
                                megLiveManager.preDetect(mActivity, bizToken, null, HOST, LivenessBiz.this);
                            }

                    }

                    @Override
                    public void onErrorResponse(int errorcode, String msg) {
                        Toast.makeText(mActivity, "认证出了点小问题，请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                });

    }


    @Override
    public void onPreStart() {
        Log.i(TAG, "onPreStart: ");
    }

    @Override
    public void onPreFinish(String token, int errorCode, String errorMessage) {
        Log.i(TAG, "onPreFinish errorCode: "+errorCode);
        Log.i(TAG, "onPreFinish errorMessage: "+errorMessage);
        if (errorCode == 1000) {
            megLiveManager.setVerticalDetectionType(MegLiveManager.DETECT_VERITICAL_KEEP);
            megLiveManager.startDetect(this);
        }else{
            showFaileToast(errorCode,errorMessage);
        }

    }

    @Override
    public void onDetectFinish(String token, final int errorCode, String errorMessage, String data) {
        Log.i(TAG, "onDetectFinish errorCode: "+errorCode);
        Log.i(TAG, "onDetectFinish errorMessage: "+errorMessage);
        if (errorCode == 1000){
            RetrofitUtils4test.getInstance().LivenessVerifymain(token, data.getBytes(), new RetrofitUtils4test.ResponseListener<LivenessVerifyResult>() {
                @Override
                public void onResponse(LivenessVerifyResult response) {
                    Boolean flag = response.getFlag();
                    if (!flag){

                    } else{

                        int errorcode = response.getResult().getErrorcode();
                        String errorMsg = response.getResult().getMsg();
                        errorcode = 1002;
                        errorMsg = "2asdsadsada";
                        showFailDialog(errorcode,errorMsg);

                    }
                }

                @Override
                public void onErrorResponse(int errorcode, String msg) {
                    Toast.makeText(mActivity, "认证出了点小问题，请稍后再试", Toast.LENGTH_SHORT).show();

                }
            });
        }else{
            showFailDialog(errorCode,errorMessage);
        }
    }

    private void showFaileToast(int errorCode, String errorMessage) {
        String toastStr = "";
        if (errorMessage.equals("BIZ_TOKEN_DENIED")){
            toastStr = mActivity.getResources().getString(R.string.livenessPreFailText1);
        }else if(errorMessage.equals("ILLEGAL_PARAMETER")){
            toastStr = mActivity.getResources().getString(R.string.livenessPreFailText1);
        }else if(errorMessage.equals("AUTHENTICATION_FAIL")){
            toastStr = mActivity.getResources().getString(R.string.livenessPreFailText1);
        }else if(errorMessage.equals("MOBILE_PHONE_NOT_SUPPORT")){
            toastStr = mActivity.getResources().getString(R.string.livenessPreFailText2);
        }else if(errorMessage.equals("INVALID_BUNDLE_ID")){
            toastStr = mActivity.getResources().getString(R.string.livenessPreFailText3);
        }else if(errorMessage.equals("NETWORK_ERROR")){
            toastStr = mActivity.getResources().getString(R.string.livenessPreFailText4);
        }else if(errorMessage.equals("USER_CANCEL")){
            toastStr = "";
        }else if(errorMessage.equals("NO_CAMERA_PERMISSION")){
            toastStr = mActivity.getResources().getString(R.string.livenessPreFailText5);
        }else if(errorMessage.equals("DEVICE_NOT_SUPPORT")){
            toastStr = mActivity.getResources().getString(R.string.livenessPreFailText6);
        }else if(errorMessage.equals("FACE_INIT_FAIL")){
            toastStr = mActivity.getResources().getString(R.string.livenessPreFailText7);
        }else if(errorMessage.equals("NO_WRITE_EXTERNAL_STORAGE_PERMISSION")){
            toastStr = mActivity.getResources().getString(R.string.livenessPreFailText8);
        }else if(errorMessage.equals("LIVENESS_FAILURE")){
            toastStr = mActivity.getResources().getString(R.string.livenessPreFailText9);
        }else if(errorMessage.equals("LIVENESS_TIME_OUT")){
            toastStr = mActivity.getResources().getString(R.string.livenessPreFailText10);
        }

        if (!TextUtils.isEmpty(toastStr)) {

        }
    }

    private void showFailDialog(final int errorCode, final String Msg){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                LivenessResultDialog dialog = new LivenessResultDialog();
//
//                if (!mActivity.isDestroyed()){
//                    dialog.show(mActivity.getFragmentManager(), "");
//                    if (Msg.equals("NO_ID_CARD_NUMBER")||Msg.equals("NO_FACE_FOUND")||Msg.equals("NO_ID_PHOTO")||
//                            Msg.equals("PHOTO_FORMAT_ERROR")||Msg.equals("DATA_SOURCE_ERROR")){
//                        dialog.setDismissButton(true);
//                    }else{
//                        dialog.setDismissButton(false);
//                    }
//
//                    dialog.setReson(Msg);
//                    dialog.setTitle("刷脸认证失败");
//                    dialog.setLivenessDialogDismissListener(new LivenessDialogDismissListener() {
//                        @Override
//                        public void onDismiss(boolean isTryAgain) {
//                            if(isTryAgain) {
//                                getBizToken(idCardName,idCardNumber);
//                            } else {
//                                event.setResultCode(LivenessEvent.LIVENESS_FAIL);
//                                event.setErrCode(errorCode);
//                                event.setMsg(Msg);
//                                EventBus.getDefault().post(event);
//                            }
//                        }
//                    });
//
//                }

                LivenessResultDialog dialog = new LivenessResultDialog();
                dialog.show(mActivity.getFragmentManager(), "");
                dialog.setReson(Msg);
                dialog.setTitle("刷脸认证失败");
                dialog.setLivenessDialogDismissListener(new LivenessDialogDismissListener() {
                    @Override
                    public void onDismiss(boolean isTryAgain) {
                        if(isTryAgain) {
                            getBizToken(idCardName,idCardNumber);
                        } else {

                        }
                    }
                });

            }
        });

    }
}
