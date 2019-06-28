package com.loanhome.lib.http;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import com.google.gson.JsonObject;
import com.loanhome.lib.BuildConfig;
import com.loanhome.lib.bean.IDCardResult;
import com.loanhome.lib.bean.UserType;
import com.loanhome.lib.util.Constants;
import com.loanhome.lib.util.TestUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.loanhome.lib.http.HttpGlobal.STATUS_BUSINESS_HANDLE_ERROR;
import static com.loanhome.lib.http.HttpGlobal.STATUS_OVERDUE;
import static com.loanhome.lib.http.HttpGlobal.STATUS_REGISTER;
import static com.loanhome.lib.http.HttpGlobal.STATUS_SERVER_HANDLE_ERROR;
import static com.loanhome.lib.http.HttpGlobal.STATUS_SUCCESS;

public class RetrofitUtils4test {
    private static final String TAG = "RetrofitUtils4test";
    private ApiService apiService;
    private static Context mContext;
    private static RetrofitUtils4test mInstance;
    private ResponseListener listener;
    @Deprecated
    //没用了，通过请求headers里的Content-Encoding来判断客户端数据有没有压缩
    private final static int HANDLE = 0;
    private final static int SHANDLE = TestUtil.isDebug() ? 0 : 1;

    public static RetrofitUtils4test getInstance(Context c) {
        if (mInstance == null) {
            mInstance = new RetrofitUtils4test();
            mContext = c.getApplicationContext();
        }
        return mInstance;
    }

    private RetrofitUtils4test() {
        //Log
        MyLogInterceptor httpLoggingInterceptor = new MyLogInterceptor();
        if (BuildConfig.DEBUG)
            httpLoggingInterceptor.setLevel(MyLogInterceptor.LoggingLevel.ALL);
        else
            httpLoggingInterceptor.setLevel(MyLogInterceptor.LoggingLevel.NONE);


        OkHttpClient.Builder okHttpClient;
        if (Build.VERSION.SDK_INT <= 19) {
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .addInterceptor(new BaseInterceptor4Phead())
                    .addInterceptor(httpLoggingInterceptor);
        } else {
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .addInterceptor(new BaseInterceptor4Phead())
                    .addInterceptor(httpLoggingInterceptor);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getBaseHost())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient.build())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    /**
     * 获取基础请求地址的方法
     *
     * @return
     */
    public static String getBaseHost() {
        return TestUtil.isTestServer() ? HttpGlobal.Net.SEVER_ADDRESS_LOCAL : HttpGlobal.Net.SEVER_ADDRESS;
    }

    /**
     * 请求获取激活时用户类型的方法
     */
    public Observable<UserType> getUserType() {
        return apiService.getUserType();
    }

    /**
     * 请求获取激活时用户类型的方法
     */
    public Observable<IDCardResult> getOCRResult(byte[] data
            ,int mark) {
          // TODO: 2019/6/28
        JSONObject phead = BaseInterceptor4Phead.getInstance().getPostDataWithPhead();
        try {
            phead.put("mark",mark);
            phead.put("image", Base64.encodeToString(data, Base64.DEFAULT));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject object = BaseInterceptor4Phead.getInstance().getParamJsonObject(phead);

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), object.toString());
        return apiService.getOCRResult(body);
    }


    public void getUserType_main() {
        final SharedPreferences mSp = mContext.getSharedPreferences(
                Constants.SharedPreferencesKey.DEVICE_USER_TYPE,
                Context.MODE_PRIVATE);
        if (mSp.contains(Constants.SharedPreferencesKey.DEVICE_GLOBAL_NEW_USER) && mSp.contains(Constants.SharedPreferencesKey.DEVICE_PRODUCT_NEW_USER)) {
            return;
        }
        RetrofitUtils4test.getInstance(mContext).getUserType()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserType>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(UserType userType) {
                        if (userType.getResult().getStatus()!= STATUS_SUCCESS) {
                            handleResult(userType.getResult());
                        }

                        Boolean device_global_new_user = userType.isDevice_global_new_user();
                        Boolean device_product_new_user = userType.isDevice_product_new_user();

                        mSp.edit()
                                .putBoolean(Constants.SharedPreferencesKey.DEVICE_GLOBAL_NEW_USER, device_global_new_user)
                                .putBoolean(Constants.SharedPreferencesKey.DEVICE_PRODUCT_NEW_USER, device_product_new_user)
                                .apply();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: "+e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    public interface ResponseListener {
        void onResponse(HttpResult response);

        void onErrorResponse(int errorcode, String msg);
    }

    private  void handleResult(HttpResult response) {
        int status = response.getStatus();
        if (status == STATUS_OVERDUE || status == STATUS_REGISTER) {
//            AccountContoller.getInstance().gotoLogin();
        } else if (status == STATUS_SERVER_HANDLE_ERROR || status == STATUS_BUSINESS_HANDLE_ERROR) {
            // TODO 服务器错误，上传统计
            int errorCode = response.getErrorcode();
            String msg = response.getMsg();
            if (listener != null) {
                listener.onErrorResponse(errorCode,msg);
            }
        }
    }


}
