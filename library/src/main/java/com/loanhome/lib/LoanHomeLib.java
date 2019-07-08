package com.loanhome.lib;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.loanhome.lib.util.Global;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * sdk初始化，单例类
 * 使用前先调用init方法初始化数据
 * 通过getInstance()方法取得其对象
 */
public class LoanHomeLib {
    private static final String TAG = "LoanHomeLib";
    private static LoanHomeLib mInstance;
    private static Context mContext;

    private String pheadjson;
    private String appKey;
    private String moxieKey;
    private String uuid;

    private boolean isTestVersion;
    private boolean isDebug;
    private boolean enableLog;


    public static Context getmContext() {
        return mContext;
    }

    public static void setmContext(Context mContext) {
        LoanHomeLib.mContext = mContext;
    }

    public String getPheadjson() {
        return pheadjson;
    }

    public void setPheadjson(String pheadjson) {
        this.pheadjson = pheadjson;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMoxieKey() {
        return moxieKey;
    }

    public void setMoxieKey(String moxieKey) {
        this.moxieKey = moxieKey;
    }

    public boolean isTestVersion() {
        return isTestVersion;
    }

    public void setTestVersion(boolean testVersion) {
        isTestVersion = testVersion;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public void setDebug(boolean debug) {
        isDebug = debug;
    }

    public boolean isEnableLog() {
        return enableLog;
    }

    public void setEnableLog(boolean enableLog) {
        this.enableLog = enableLog;
    }

/**
     * 获取数据采集类对象
     * @return
     */
//    public static LoanHomeLib getInstance(){
//        if (mInstance==null){
//            mInstance=new LoanHomeLib();
//        }
//        return mInstance;
//    }

    /**
     * 获取全局context
     * @return
     */
    private static Context getContext() {
        return mContext;
    }

    /**
     *
     * 初始化数据采集功能,启动APP时调用此方法
     * @param applicationContext 应用程序的ApplicationContext
     * @param pheadjson phead 信息
     * @param isTestVersion	是否测试版本（选择测试服务器），true为测试服务器
     * @param enableLog	是否开启文件log输出, true为打开log输出
     */
    public void init(Context applicationContext,String pheadjson,boolean isTestVersion,boolean isDebug,boolean enableLog){

        mContext = applicationContext;
        try {
            JSONObject jsonObject = new JSONObject(pheadjson);
            Global.parseFromJSONObject(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Global.IsTestVersion = isTestVersion;
        Global.IsDebug = isDebug;
        Global.IS_ENABLE_LOG = enableLog;
    }

    public LoanHomeLib(LoanHomeBuilder builder){
        this.pheadjson = builder.pheadjson;
        this.appKey = builder.appKey;
        this.moxieKey = builder.moxieKey;
        this.uuid = builder.uuid;
        this.isTestVersion = builder.isTestVersion;
        this.isDebug = builder.isDebug;
        this.enableLog = builder.enableLog;
    }


    public static class LoanHomeBuilder {

        private Context mContext;
        private String pheadjson;
        private String appKey;
        private String moxieKey;
        private String uuid;
        private boolean isTestVersion;
        private boolean isDebug;
        private boolean enableLog;

        public LoanHomeBuilder (Context context){
            this.mContext =context;
        }
        public LoanHomeBuilder setpheadjson(String pheadjson){
            this.pheadjson = pheadjson;
            return this;

        }
        public LoanHomeBuilder setappKey(String appKey){
            this.appKey = appKey;
            return this;

        }
        public LoanHomeBuilder setmoxieKey(String moxieKey){
            this.moxieKey = moxieKey;
            return this;
        }
        public LoanHomeBuilder setuuid(String uuid){
            this.uuid = uuid;
            return this;

        }
        public LoanHomeBuilder setisTestVersion(Boolean isTestVersion){
            this.isTestVersion = isTestVersion;
            return this;

        }
        public LoanHomeBuilder setisDebug(Boolean isDebug){
            this.isDebug = isDebug;
            return this;
        }
        public LoanHomeBuilder setenableLog(Boolean enableLog){
            this.enableLog = enableLog;
            return this;
        }

        public LoanHomeLib build() {
            // 由于Builder是非线程安全的，所以如果要在Builder内部类中检查一个参数的合法性，
            // 必需要在对象创建完成之后再检查
            LoanHomeLib loanHomeLib = new LoanHomeLib(this);
            if (TextUtils.isEmpty(loanHomeLib.pheadjson)){
                throw new IllegalStateException("pheadjson不能为空");// 线程安全
            }
            Global.pheadjson = loanHomeLib.pheadjson;
            try {
                JSONObject jsonObject = new JSONObject(loanHomeLib.pheadjson);
                Global.mInfo = Global.parseFromJSONObject(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "build getAccess_token: "+Global.mInfo.getAccess_token().toString());
            Global.appKey = loanHomeLib.appKey;
            Global.moxieKey = loanHomeLib.moxieKey;
            Global.uuid = loanHomeLib.uuid;
            Global.IsTestVersion = loanHomeLib.isTestVersion;
            Global.IsDebug = loanHomeLib.isDebug;
            Global.IS_ENABLE_LOG = loanHomeLib.enableLog;

            return loanHomeLib;
        }
    }
}
