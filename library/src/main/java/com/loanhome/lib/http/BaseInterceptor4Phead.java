package com.loanhome.lib.http;

import android.util.Log;

import com.loanhome.lib.util.Global;
import com.loanhome.lib.util.TestUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @Description TODO
 * Created by Don on 2019/5/27
 */
public class BaseInterceptor4Phead implements Interceptor {
    private static final String TAG = "BaseInterceptor4Phead";
    public static BaseInterceptor4Phead sIns;



    @Deprecated
    //没用了，通过请求headers里的Content-Encoding来判断客户端数据有没有压缩
    private final static int HANDLE = 0;
    private final static int SHANDLE = TestUtil.isDebug() ? 0 : 1;

    public BaseInterceptor4Phead(){
    }

    public static BaseInterceptor4Phead getInstance() {
        if (sIns == null) {
            synchronized (BaseInterceptor4Phead.class) {
                if (sIns == null) {
                    sIns = new BaseInterceptor4Phead();
                }
            }
        }
        return sIns;
    }


    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        Request.Builder builder =request.newBuilder();
        // TODO: 2019/6/28 从开发项目中传入pheadjson 然后进行请求
        JSONObject object =new JSONObject();
        Request newRequest = builder .addHeader("Content-Type","application/json;charset=utf-8")
//                .addHeader("phead",getParamJsonObject(getPostDataWithPhead()).toString())
                .build();

        return chain.proceed(newRequest);
    }

    public JSONObject getParamJsonObject(JSONObject data) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("handle", HANDLE);
            jsonObject.put("shandle", SHANDLE);
            jsonObject.put("data", data);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject = data;
        }
        Log.i(TAG, "getParamJsonObject: "+jsonObject);
        return jsonObject;
    }



    public JSONObject getPostDataWithPhead() {
        JSONObject data = new JSONObject();
        try {
            data.put("phead", getPheadJson());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    public  JSONObject getPheadJson() {
        // TODO: 2019/6/28

        JSONObject pheadJson = Global.writeProductInfoToJSON(Global.mInfo);
//        JSONObject pheadJson = null;
//        try {
//            pheadJson = new JSONObject(Global.pheadjson);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        return pheadJson;
    }

}
