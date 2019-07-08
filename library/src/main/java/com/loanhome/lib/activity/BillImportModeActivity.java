package com.loanhome.lib.activity;

import android.app.ActivityManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.loanhome.lib.R;
import com.loanhome.lib.bean.ImportStateInfo;
import com.loanhome.lib.bean.TaskInfo;
import com.loanhome.lib.http.StatisticsController;
import com.loanhome.lib.listener.MoxieResultCallback;
import com.loanhome.lib.model.BillImportViewModel;
import com.loanhome.lib.statistics.IStatisticsConsts;
import com.loanhome.lib.util.AesUtil;
import com.loanhome.lib.util.Global;
import com.loanhome.lib.view.LoadingDialog;
import com.moxie.client.exception.MoxieException;
import com.moxie.client.manager.MoxieCallBack;
import com.moxie.client.manager.MoxieCallBackData;
import com.moxie.client.manager.MoxieContext;
import com.moxie.client.manager.MoxieSDK;
import com.moxie.client.manager.MoxieSDKRunMode;
import com.moxie.client.model.MxLoginCustom;
import com.moxie.client.model.MxParam;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillImportModeActivity extends AppCompatActivity {
    private String TAG = "BillImportWebActivity";
    public static final String TASK_CODE="task_code";
    public static final String TASK_INFO="task_info";
    public static final String TASK_TYPE="task_type";
    public static final String TASK_MODE ="task_mode";
    public static final String TASK_ACTION ="task_action";
    private String type;
    private BillImportViewModel model;
    private Observer<ImportStateInfo> observer;
    private Observer<Integer> observerInfo;
    private String taskId;
    private LoadingDialog dialog;
    private String appendResult;
    private String taskInfo;
    private String mTask;
    private String mMode;
    private String mAfterAction;
    private RelativeLayout mRlLoading;
    //解决魔蝎第一次调用不回调问题
    private boolean canCallBack;
    private boolean hasGoneOnce;
    private Handler handler;
    private MoxieCallBackData moxieCallBackData;
    private static MoxieResultCallback mMoxieResultCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (BillImportModeActivity.this != null){
                    finish();
                }
            }
        }, 3 * 60 * 1000);
        MoxieSDK.init(getApplication());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_layout_activity);
        mRlLoading = findViewById(R.id.rl_loading);
        getIntentData();
        dialog = new LoadingDialog();
        model = ViewModelProviders.of(this).get(BillImportViewModel.class);

        observer = new Observer<ImportStateInfo>() {
            @Override
            public void onChanged(@Nullable ImportStateInfo info) {
                if (info == null){
                    return;
                }
                Integer integer = info.getState();
                mAfterAction = info.getAction();
                if (integer != null && integer == BillImportViewModel.FETCH_SUCCESS) {
//                    if (AccountContoller.getInstance()
//                            .getUserInfo() != null) {
//                        AccountContoller.getInstance()
//                                .getUserInfo().setHasCard(true,BillImportModeActivity.this);}
//                    EventBus.getDefault()
//                            .post(new ReloadTabEvent());
                    importSuccess();

                } else if (integer != null && integer == BillImportViewModel.FETCH_PROGRESS) {
                    if (handler != null){
//                        EventBus.getDefault()
//                                .post(new ImportEvent(ImportEvent.PROGRESS));
                        mMoxieResultCallback.onProgress();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                model.getTaskState(type, taskId,mMode);
                                handler.removeCallbacks(this);
                            }
                        },2000);
                    }

                } else {
                    importFailed();
                }
            }
        };
        observerInfo = new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {

            }
        };


        //0为未添卡，1为添卡
        String function = "0";
//        if (AccountContoller.getInstance()
//                .isLogin() && AccountContoller.getInstance()
//                .getUserInfo() != null) {
//            function = AccountContoller.getInstance()
//                    .getUserInfo().isHasCard(this) ? "1" : "0";
//        }
        StatisticsController.getInstance().newRequestStatics(IStatisticsConsts.UmengEventId.Page.PAGE_ADD_CARD_PAGE
                , IStatisticsConsts.UmengEventId.LogType.LOG_TYPE_VIEW
                , IStatisticsConsts.UmengEventId.CkModule.CK_MODULE_VIEW_ADD_CARD_PAGE, 0, function, null);


    }

    private void getIntentData(){
        Intent intent = getIntent();
        if (intent == null){
            return;
        }
        taskInfo = intent.getStringExtra(TASK_INFO);
        mTask = intent.getStringExtra(TASK_TYPE);
        parseData(taskInfo);
    }

    public void parseData(String s){
        JSONObject accountInfo = null;
        try {
            accountInfo = new JSONObject(s);
            TaskInfo info = new TaskInfo();
            info.loginCode = accountInfo.optString("login_code");
            info.loginType = accountInfo.optString("login_type");
            info.loginTarget = accountInfo.optString("login_target");
            info.account = accountInfo.optString("username");
            info.password = accountInfo.optString("password");
            refreshMoxieData(mTask,info);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void refreshMoxieData(String task, TaskInfo info) {
        MoxieSDK.getInstance().finish();
        final MxParam param = new MxParam();
//        param.setApiKey(TestUtil.isTestServer() ? BuildConfig.MOXIE_DEBUG_APPKEY : BuildConfig.MOXIE_APPKEY);
        param.setApiKey(Global.moxieKey);
//        if (!AccountContoller.getInstance().isLogin()) {
//            AccountContoller.getInstance().gotoLogin();
//        }
//        param.setUserId(AccountContoller.getInstance().getAccessToken());
        // TODO: 2019/7/2
        param.setUserId(Global.mInfo.getAccess_token());
        param.setTaskType(task);
        param.setCallbackTaskInfo(true);
        param.setQuitDisable(true);
        MxLoginCustom loginCustom = new MxLoginCustom();
        loginCustom.setLoginCode(info.loginCode);
        Map<String, Object> loginInfo = new HashMap<>();
        String[] accountInfo = info.account.split(",");
        if (accountInfo.length > 1) {
            loginInfo.put("username", accountInfo[0]);
            loginInfo.put("username1", accountInfo[1]);
        } else {
            loginInfo.put("username", info.account);
        }
//        loginInfo.put("password", AesUtil.de(BuildConfig.KEY, info.password));
        loginInfo.put("password", AesUtil.en(Global.appKey, info.password));
        loginInfo.put(MxLoginCustom.LOGIN_PARAMS_K_SELECTED, MxParam.PARAM_COMMON_YES);
        loginCustom.setLoginType(info.loginTarget);
        //邮箱要用setLoginParams 否则会跳出页面
        if (task.equals(MxParam.PARAM_TASK_EMAIL)) {
            loginCustom.setLoginParams(loginInfo);
        } else {
            loginCustom.addLoginParams(info.loginType,
                    loginInfo);
        }

        param.setLoginCustom(loginCustom);
        MoxieSDK.getInstance().startInMode(this,
                MoxieSDKRunMode.MoxieSDKRunModeBackground, param, new MoxieCallBack() {
                    @Override
                    public void onStatusChange(MoxieContext moxieContext, MoxieCallBackData moxieCallBackData) {
                        super.onStatusChange(moxieContext, moxieCallBackData);
                        BillImportModeActivity.this.moxieCallBackData = moxieCallBackData;
                        Log.d("onStatusChange", moxieCallBackData.toString());
                        if (MoxieSDK.getInstance().getRunMode() == MoxieSDKRunMode.MoxieSDKRunModeBackground
                                && !MoxieSDK.getInstance().isForeground()
                                && moxieCallBackData.getWaitCode() != null) {
//                            Toast.makeText(BillImportModeActivity.this, "需要显示SDK", Toast.LENGTH_SHORT).show();
                            MoxieSDK.getInstance().show();
                        } else if (MoxieSDK.getInstance().getRunMode() == MoxieSDKRunMode.MoxieSDKRunModeBackground
                                && moxieCallBackData.getWaitCode() == null) {
                            showActivity();
                        }
                    }

                    @Override
                    public boolean callback(MoxieContext moxieContext, MoxieCallBackData
                            moxieCallBackData) {
                        BillImportModeActivity.this.moxieCallBackData = null;
                        if (MoxieSDK.getInstance().getRunMode() == MoxieSDKRunMode.MoxieSDKRunModeBackground) {
                            showActivity();
                        }
                        canCallBack = true;
                        if (moxieCallBackData != null) {
                            switch (moxieCallBackData.getCode()) {
                                case MxParam.ResultCode.IMPORT_UNSTART:
                                    importFailed();
                                    break;
                                case MxParam.ResultCode.IMPORTING:
                                    if (moxieCallBackData.isLoginDone()) {
                                        //状态为IMPORTING, 且loginDone为true，说明这个时候已经在采集中，已经登录成功

                                    } else {
                                        //状态为IMPORTING, 且loginDone为false，说明这个时候正在登录中
                                    }
                                case MxParam.ResultCode.IMPORT_FAIL:
                                    importFailed();
                                    break;
                                case MxParam.ResultCode.THIRD_PARTY_SERVER_ERROR:
                                    importFailed();
                                case MxParam.ResultCode.MOXIE_SERVER_ERROR:
                                    importFailed();
                                    break;
                                case MxParam.ResultCode.USER_INPUT_ERROR:
                                    importFailed();
                                    break;

                                case MxParam.ResultCode.IMPORT_SUCCESS:
                                    //根据taskType进行对应的处理
                                    switch (moxieCallBackData.getTaskType()) {
                                        case MxParam.PARAM_TASK_EMAIL:

                                            break;
                                        case MxParam.PARAM_TASK_ONLINEBANK:

                                            break;
                                        default:
                                            break;
                                    }
                                    queryTaskStatusLooper(moxieCallBackData.getTaskType(), moxieCallBackData.getTaskId());
                                    moxieContext.finish();
                                    return true;
                                default:
                                    return false;
                            }
                        }
                        return false;
                    }

                    @Override
                    public void onError(MoxieContext moxieContext, MoxieException e) {
                        super.onError(moxieContext, e);
                        if (moxieContext != null){
                            moxieContext.finish();
                        }
                        Toast.makeText(BillImportModeActivity.this, "【网络连接异常，请检查无误后重试】", Toast.LENGTH_SHORT).show();
                        finish();
//                        if (e.getExceptionType().equals(ExceptionType.SDK_HAS_STARTED)){
//                            if (moxieContext != null){
//                                moxieContext.finish();
//                            }
//                            finish();
//                            return;
//                        }
//                        importFailed();
                    }
                }
        );


    }

    public void showActivity() {
        if (!IsForeground(this)) {
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) {
                am.moveTaskToFront(getTaskId(), ActivityManager.MOVE_TASK_WITH_HOME);
            }
        }
    }

    private boolean IsForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = null;
        if (am != null) {
            tasks = am.getRunningTasks(1);
        }
        if (tasks != null && !tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            return topActivity.getClassName().equals(BillImportModeActivity.class.getName());
        }
        return false;
    }

    public void importFailed(){
//        EventBus.getDefault()
//                .post(new ImportEvent(ImportEvent.FAIL));
        mMoxieResultCallback.onFailed();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mRlLoading != null){
                    mRlLoading.setVisibility(View.GONE);
                }
//                Toast.makeText(BillImportModeActivity.this, "刷新失败", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }
    public void importSuccess(){
        mMoxieResultCallback.onSuccess("");
//        EventBus.getDefault()
//                .post(new ImportEvent(ImportEvent.SUCCESS));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mRlLoading != null){
                    mRlLoading.setVisibility(View.GONE);
                    finish();
                }
            }
        });
    }


    public void uploadingAccountInfo(String s, String taskId, String taskType) {
        if (s == null || TextUtils.isEmpty(s)) {
            return;
        }
        try {
            JSONObject object1 = new JSONObject(s);
            String task = object1.optString("taskInfo");
            JSONObject jsonObject = new JSONObject(task);
            String userId = jsonObject.optString("user_id");
            String bank = jsonObject.optString("loginCode");
            String loginTarget = jsonObject.optString("login_target");
            String loginType = jsonObject.optString("login_type");
            String origin = jsonObject.optString("origin");
            String account = jsonObject.optString("account");
            String password = jsonObject.optString("password");
            if (taskType.equals(MxParam.PARAM_TASK_EMAIL)) {
                JSONObject param = jsonObject.optJSONObject("param");
                JSONObject arguments = param.optJSONObject("arguments");
                account = arguments.optString("username");
                password = arguments.optString("password");
                origin = param.optString("origin");
            }

            JSONObject object = new JSONObject();
            object.put("login_code", bank);
            object.put("login_type", loginType);
            object.put("login_target", loginTarget);
            object.put("username", account);
//            object.put("password", AesUtil.en(BuildConfig.KEY, password));
            object.put("password", AesUtil.en(Global.appKey, password));
            object.put("user_id", userId);
            object.put("task_type", taskType);

            model.upLoadingAccount(object, taskId).observe(this, observerInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void queryTaskStatusLooper(String type, String taskId) {
        this.type = type;
        this.taskId = taskId;
        model.getTaskState(type, taskId, mMode).observe(BillImportModeActivity.this, observer);

    }

    @Override
    protected void onResume() {
        super.onResume();
//        //解决魔蝎第一次调用不回调问题
//        if (!canCallBack && hasGoneOnce){
//            finish();
//        }
        if (MoxieSDK.getInstance().isDoing()
                && MoxieSDK.getInstance().getRunMode() == MoxieSDKRunMode.MoxieSDKRunModeBackground
                && moxieCallBackData != null
                && moxieCallBackData.getWaitCode() != null) {
            MoxieSDK.getInstance().show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        hasGoneOnce = true;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void finish() {
        MoxieSDK.getInstance()
                .clear();
        MoxieSDK.getInstance()
                .finish();
        super.finish();
        overridePendingTransition(0,0);
    }

    public static void setMoxieResultCallback(MoxieResultCallback moxieResultCallback) {
        mMoxieResultCallback = moxieResultCallback;
    }

}

