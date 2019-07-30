package com.loanhome.lib.activity;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.loanhome.lib.R;
import com.loanhome.lib.bean.BizTokenResult;
import com.loanhome.lib.bean.LivenessVerifyResult;
import com.loanhome.lib.bean.VerifyInfo;
import com.loanhome.lib.http.RetrofitUtils4test;
import com.loanhome.lib.http.StatisticsController;
import com.loanhome.lib.listener.LivenessDialogDismissListener;
import com.loanhome.lib.listener.VerifyResultCallback;
import com.loanhome.lib.statistics.IStatisticsConsts;
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
    private VerifyResultCallback callback;
    private VerifyInfo mInfo;



    public LivenessBiz(Activity activity){
        this.mActivity = activity;
        megLiveManager = MegLiveManager.getInstance();
        String version = megLiveManager.getVersion();
        Log.i(TAG, "LivenessBiz version: "+version);
    }

    public void getBizToken(String idcardname, String idcardnumber, VerifyInfo info){
        idCardName = idcardname;
        idCardNumber = idcardnumber;
        mInfo = info;
        Log.i(TAG, "getBizToken: ");
        RetrofitUtils4test.getInstance().getBizTokenmain(idcardname, idcardnumber,
                new RetrofitUtils4test.ResponseListener<BizTokenResult>() {
                    @Override
                    public void onResponse(BizTokenResult response) {

                            bizToken = response.getToken();
//                            if (!TextUtils.isEmpty(bizToken)){
                            megLiveManager.preDetect(mActivity, bizToken, null, HOST, LivenessBiz.this);
//                            }else{
////                                event.setResultCode(LivenessEvent.LIVENESS_FAIL);
////                                event.setErrCode(1003);
////                                event.setMsg("认证出了点小问题，请稍后再试");
////                                EventBus.getDefault().post(event);
//                                callback.onVerifyFail("认证出了点小问题，请稍后再试");
//                            }

                    }

                    @Override
                    public void onErrorResponse(int errorcode, String msg) {
                        callback.onVerifyFail(mActivity.getResources().getString(R.string.livenessPreFailText4));
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

            //新活体埋点统计-调用相机
            StatisticsController.getInstance().newOCRRequestStatics(IStatisticsConsts.UmengEventId.Page.PAGE_LIVENESS_SHOT,
                    IStatisticsConsts.UmengEventId.LogType.LOG_TYPE_VIEW,
                    IStatisticsConsts.UmengEventId.CkModule.CK_MODULE_LIVENESS_ASK_CAMERA,
                    String.valueOf(-1), mInfo.getFunctionId(), mInfo.getContentId(),mInfo.getApi_id(), mInfo.getpPosition(), mInfo.getParam1(), mInfo.getParam2());
        }else{
            showFaileToast(errorCode,errorMessage);
        }

    }

    @Override
    public void onDetectFinish(String token, final int errorCode, String errorMessage, String data) {
        Log.i(TAG, "onDetectFinish errorCode: "+errorCode);
        Log.i(TAG, "onDetectFinish errorMessage: "+errorMessage);
        if (errorCode == 1000){
            //新活体埋点统计-开始抓拍
            StatisticsController.getInstance().newOCRRequestStatics(IStatisticsConsts.UmengEventId.Page.PAGE_LIVENESS_SHOT,
                    IStatisticsConsts.UmengEventId.LogType.LOG_TYPE_VIEW,
                    IStatisticsConsts.UmengEventId.CkModule.CK_MODULE_LIVENESS_SHOT,
                    String.valueOf(-1), mInfo.getFunctionId(), mInfo.getContentId(),mInfo.getApi_id(), mInfo.getpPosition(), mInfo.getParam1(), mInfo.getParam2());


            RetrofitUtils4test.getInstance().LivenessVerifymain(token, data.getBytes(), new RetrofitUtils4test.ResponseListener<LivenessVerifyResult>() {
                @Override
                public void onResponse(LivenessVerifyResult response) {
                    Boolean flag = response.getFlag();
                    if (flag){
                        callback.onVerifySuccess("刷脸验证成功");
                    } else{

                        int errorcode = response.getResult().getErrorcode();
                        String errorMsg = response.getResult().getMsg();
                        showFailDialog(errorcode,errorMsg);

                    }
                }

                @Override
                public void onErrorResponse(int errorcode, String msg) {
                    callback.onVerifyFail(mActivity.getResources().getString(R.string.livenessPreFailText4));
                }
            });
        }else{
            showFaileToast(errorCode,errorMessage);
        }
    }

    private void showFaileToast(int errorCode,String errorMessage) {
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
            callback.onVerifyCancel();
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
//            event.setResultCode(LivenessEvent.LIVENESS_FAIL);
//            event.setErrCode(errorCode);
//            event.setMsg(toastStr);
//            EventBus.getDefault().post(event);
            callback.onVerifyFail(toastStr);
        }
    }

    private void showFailDialog(final int errorCode, final String Msg){

          LivenessResultDialog dialog = new LivenessResultDialog();

                if (!mActivity.isDestroyed()){
                    dialog.show(mActivity.getFragmentManager(), "");
                    if (Msg.equals("NO_ID_CARD_NUMBER")||Msg.equals("NO_FACE_FOUND")||Msg.equals("NO_ID_PHOTO")||
                            Msg.equals("PHOTO_FORMAT_ERROR")||Msg.equals("DATA_SOURCE_ERROR")){
                        dialog.setDismissButton(true);
                    }else{
                        dialog.setDismissButton(false);
                    }

                    dialog.setReson(Msg);
                    dialog.setTitle("刷脸认证失败");
                    dialog.setLivenessDialogDismissListener(new LivenessDialogDismissListener() {
                        @Override
                        public void onDismiss(boolean isTryAgain) {
                            if(isTryAgain) {
                                getBizToken(idCardName,idCardNumber,mInfo);
                            } else {
//                                event.setResultCode(LivenessEvent.LIVENESS_FAIL);
//                                event.setErrCode(errorCode);
//                                event.setMsg(Msg);
//                                EventBus.getDefault().post(event);
                                callback.onVerifyFail(Msg);
                            }
                        }
                    });

                }


    }

    public void setVerifyResultCallback(VerifyResultCallback moxieResultCallback) {
        callback = moxieResultCallback;
    }
}
