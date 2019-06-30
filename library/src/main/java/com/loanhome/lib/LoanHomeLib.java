package com.loanhome.lib;

import android.content.Context;

import com.loanhome.lib.util.Global;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * sdk初始化，单例类
 * 使用前先调用init方法初始化数据
 * 通过getInstance()方法取得其对象
 */
public class LoanHomeLib {
    private static LoanHomeLib mInstance;
    private static Context mContext;

    /**
     * 获取数据采集类对象
     * @return
     */
    public static LoanHomeLib getInstance(){
        if (mInstance==null){
            mInstance=new LoanHomeLib();
        }
        return mInstance;
    }

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

}
