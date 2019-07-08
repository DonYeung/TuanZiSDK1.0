package com.loanhome.lib.http;

import android.content.Context;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import com.loanhome.lib.BuildConfig;
import com.loanhome.lib.bean.BankCardResult;
import com.loanhome.lib.bean.BizTokenResult;
import com.loanhome.lib.bean.HttpResult;
import com.loanhome.lib.bean.IDCardResult;
import com.loanhome.lib.bean.StatisticResult;
import com.loanhome.lib.bean.TypeStateResult;
import com.loanhome.lib.bean.UserInfoResult;
import com.loanhome.lib.http.cert.TrustAllHostnameVerifier;
import com.loanhome.lib.util.TestUtil;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
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
    @Deprecated
    //没用了，通过请求headers里的Content-Encoding来判断客户端数据有没有压缩
    private final static int HANDLE = 0;
    private final static int SHANDLE = TestUtil.isDebug() ? 0 : 1;

    public static RetrofitUtils4test getInstance() {
        if (mInstance == null) {
            mInstance = new RetrofitUtils4test();
//            mContext = c.getApplicationContext();
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
                    .addInterceptor(httpLoggingInterceptor)
                    .sslSocketFactory(HttpsUtils.createSSLSocketFactory())
                    .hostnameVerifier(new TrustAllHostnameVerifier());
            ;
        } else {
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .addInterceptor(new BaseInterceptor4Phead())
                    .addInterceptor(httpLoggingInterceptor)
                    .sslSocketFactory(HttpsUtils.createSSLSocketFactory())
                    .hostnameVerifier(new TrustAllHostnameVerifier());
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
     * OCR请求接口
     * @param data
     * @param mark
     * @return
     */
    public Observable<IDCardResult> getOCRResult(byte[] data,byte[] imageRef
            ,int mark) {
          // TODO: 2019/6/28
        JSONObject phead = BaseInterceptor4Phead.getInstance().getPostDataWithPhead();
        try {
            phead.put("mark",mark);
            phead.put("image", Base64.encodeToString(data, Base64.DEFAULT));
            phead.put("imageRef", Base64.encodeToString(imageRef, Base64.DEFAULT));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject object = BaseInterceptor4Phead.getInstance().getParamJsonObject(phead);

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), object.toString());
        return apiService.getOCRResult(body);
    }


    /**
     * 活体请求接口
     * @param map
     * @param name
     * @param number
     * @param delta
     * @return
     */
    public Observable<HttpResult> upLoadingLivenessInfo_New(Map<String, byte[]> map
            , String name
            , String number
            , String delta) {
        // TODO: 2019/6/28
        JSONObject phead = BaseInterceptor4Phead.getInstance().getPostDataWithPhead();
        try {
            phead.put("idCardName", name);
            phead.put("idCardNumber", number);
            phead.put("imageBest", Base64.encodeToString(map.get("image_best"), Base64.DEFAULT));
            phead.put("imageAction1", Base64.encodeToString(map.get("image_action1"), Base64.DEFAULT));
            phead.put("imageAction2", Base64.encodeToString(map.get("image_action2"), Base64.DEFAULT));
            phead.put("imageAction3", Base64.encodeToString(map.get("image_action3"), Base64.DEFAULT));
            phead.put("delta", delta);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject object = BaseInterceptor4Phead.getInstance().getParamJsonObject(phead);

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), object.toString());
        return apiService.upLoadingLivenessInfo_New(body);
    }

    /**
     * bank OCR请求接口
     * @param data
     * @return
     */
    public Observable<BankCardResult> getBankOCRResult(byte[] data) {
        // TODO: 2019/6/28
        JSONObject phead = BaseInterceptor4Phead.getInstance().getPostDataWithPhead();
        try {
            phead.put("image", Base64.encodeToString(data, Base64.DEFAULT));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject object = BaseInterceptor4Phead.getInstance().getParamJsonObject(phead);

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), object.toString());
        return apiService.getBankOCRResult(body);
    }


    /**
     * biztoken
     * @param data
     * @return
     */
    public Observable<BizTokenResult> getBizToken(String name,String number) {
        // TODO: 2019/6/28
        JSONObject phead = BaseInterceptor4Phead.getInstance().getPostDataWithPhead();
        try {
            phead.put("name", name);
            phead.put("idCardNum", number);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject object = BaseInterceptor4Phead.getInstance().getParamJsonObject(phead);

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), object.toString());
        return apiService.getBizToken(body);
    }

    /**
     * verify
     * @param data
     * @return
     */
    public Observable<HttpResult> LivenessVerify(String token, byte[] data) {
        // TODO: 2019/6/28
        JSONObject phead = BaseInterceptor4Phead.getInstance().getPostDataWithPhead();
        try {
            phead.put("token", token);
            phead.put("imageBest",  Base64.encodeToString(data, Base64.DEFAULT));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject object = BaseInterceptor4Phead.getInstance().getParamJsonObject(phead);

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), object.toString());
        return apiService.LivenessVerify(body);
    }
    /**
     * 上传用户信息，魔蝎sdk后请求30接口使用
     * @param account
     * @param taskId
     * @return
     */
    public Observable<UserInfoResult> uploadUserInfo(final JSONObject account, String taskId) {
        // TODO: 2019/6/28
        JSONObject phead = BaseInterceptor4Phead.getInstance().getPostDataWithPhead();
        try {
            phead.put("account",account);
            phead.put("taskId",taskId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject object = BaseInterceptor4Phead.getInstance().getParamJsonObject(phead);
        Map<String,String> map = new HashMap<String,String>();
        map.put("phead",object.toString());
//        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), object.toString());
        return apiService.uploadUserInfo(map);
    }


    /**
     * 魔蝎sdk 请求24接口轮询任务状态
     * @param type
     * @param taskId
     * @param mode
     * @return
     */
    public Observable<TypeStateResult> fetchTypeState(final String type, String taskId, String mode) {
        // TODO: 2019/6/28
        JSONObject phead = BaseInterceptor4Phead.getInstance().getPostDataWithPhead();
        try {
            phead.put("type",type);
            phead.put("taskId",taskId);
            phead.put("mode",mode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject object = BaseInterceptor4Phead.getInstance().getParamJsonObject(phead);
        Map<String,String> map = new HashMap<String,String>();
        map.put("phead",object.toString());
//        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), object.toString());
        return apiService.fetchTypeState(map);
    }

    /**
     * 新OCR增加的统计接口，增加了api_id
     * @param page
     * @param logType
     * @param ckModule
     * @param index
     * @param functionid
     * @param contentid
     */
    public Observable<StatisticResult> newOCRRequestStatics(String page, String logType, String ckModule, String index, String functionid, String contentid, String api_id,
                                                            String pPosition, String param1, String param2) {

        // TODO: 2019/6/28
        JSONObject data = BaseInterceptor4Phead.getInstance().getPheadJson();
        JSONObject newdata = new JSONObject();

        try {
            data.put("product_id",contentid);
            data.put("api_id",api_id);

            newdata.put("phead", data);


            newdata.put("page",page);
            newdata.put("position", index.equals(String.valueOf(-1)) ? "": index);
            newdata.put("log_type", logType);
            newdata.put("ck_module",ckModule);
            newdata.put("functionid", functionid == null ? "" : functionid);
//                data.put("contentid", contentid == null ? "" : contentid); //写到phead中
            newdata.put("p_position", pPosition == null ? "" : pPosition);
            newdata.put("param1", param1 == null ? "" : param1);
            newdata.put("param2", param2 == null ? "" : param2);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject object = BaseInterceptor4Phead.getInstance().getParamJsonObject(newdata);

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), object.toString());
        return apiService.newOCRRequestStatics(body);
    }

    /**
     * 新的统计接口
     * @param page
     * @param logType
     * @param ckModule
     * @param index
     * @param functionid
     * @param contentid
     */
    public Observable<StatisticResult> newRequestStatics(String page,String logType, String ckModule, int index, String functionid, String contentid) {

        // TODO: 2019/6/28
        JSONObject data = BaseInterceptor4Phead.getInstance().getPostDataWithPhead();

        try {
            data.put("page",page);
            data.put("position", index == -1 ? "": index);
            data.put("log_type", logType);
            data.put("ck_module",ckModule);
            data.put("functionid", functionid == null ? "" : functionid);
            data.put("contentid", contentid == null ? "" : contentid);

        } catch (JSONException e) {
            e.printStackTrace();
        }



        JSONObject object = BaseInterceptor4Phead.getInstance().getParamJsonObject(data);

        Map<String,String> map = new HashMap<String,String>();
        map.put("phead",object.toString());

//        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), object.toString());
        return apiService.newRequestStatics(map);
    }

    /**
     * OCR请求接口
     * @param data
     * @param mark
     * @param listener
     */
    public void getOCRResultmain(byte[] data,byte[] imageRef,int mark,final ResponseListener listener){
        RetrofitUtils4test.getInstance().getOCRResult(data,imageRef,mark)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<IDCardResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(IDCardResult idCardResult) {
                        if (idCardResult.getResult().getStatus()!= STATUS_SUCCESS) {
                            handleResult(idCardResult.getResult(),listener);
                        }
                        else{
                            listener.onResponse(idCardResult);
                        }
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


    /**
     * 活体请求接口
     * @param map
     * @param name
     * @param number
     * @param delta
     * @param listener
     */
    public void upLoadingLivenessInfo_Newmain(Map<String, byte[]> map
            , String name
            , String number
            , String delta,final ResponseListener listener){
        RetrofitUtils4test.getInstance().upLoadingLivenessInfo_New(map,name,number,delta)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult httpResult) {
                        if (httpResult.getStatus()!= STATUS_SUCCESS) {
                            handleResult(httpResult,listener);
                        }
                        else{
                            listener.onResponse(httpResult);
                        }
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


    /**
     * 银行卡OCR接口
     * @param data
     * @param listener
     */
    public void getBankOCRResultmain(byte[] data,final ResponseListener listener){
        RetrofitUtils4test.getInstance().getBankOCRResult(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BankCardResult>() {


                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BankCardResult bankCardResult) {
                        if (bankCardResult.getResult().getStatus()!= STATUS_SUCCESS) {
                            handleResult(bankCardResult.getResult(),listener);
                        }
                        else{
                            listener.onResponse(bankCardResult);
                        }
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


    /**
     * biztoken
     */
    public void getBizTokenmain(String name,String number,final ResponseListener listener){
        RetrofitUtils4test.getInstance().getBizToken(name,number)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BizTokenResult>() {


                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BizTokenResult bizTokenResult) {
                        if (bizTokenResult.getResult().getStatus()!= STATUS_SUCCESS) {
                            handleResult(bizTokenResult.getResult(),listener);
                        }
                        else{
                            listener.onResponse(bizTokenResult);
                        }
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

    /**
     *
     * @param name
     * @param number
     * @param listener
     */
    public void LivenessVerifymain(String token, byte[] data,final ResponseListener listener){
        RetrofitUtils4test.getInstance().LivenessVerify(token,data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HttpResult>() {


                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HttpResult httpResult) {
                        if (httpResult.getStatus()!= STATUS_SUCCESS) {
                            handleResult(httpResult,listener);
                        }
                        else{
                            listener.onResponse(httpResult);
                        }
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


    /**
     * 魔蝎sdk 请求24接口轮询任务状态
     * @param type
     * @param taskId
     * @param mode
     * @param listener
     */
    public void fetchTypeStatemain(final String type, String taskId, String mode,final ResponseListener listener){
        RetrofitUtils4test.getInstance().fetchTypeState(type,taskId,mode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TypeStateResult>() {


                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(TypeStateResult typeStateResult) {
                        if (typeStateResult.getResult().getStatus()!= STATUS_SUCCESS) {
                            handleResult(typeStateResult.getResult(),listener);
                        }
                        else{
                            listener.onResponse(typeStateResult);
                        }
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


    /**
     * 上传用户信息，魔蝎sdk后请求30接口使用
     * @param account
     * @param taskId
     * @param listener
     */
    public void uploadUserInfomain(final JSONObject account, String taskId,final ResponseListener listener){
        RetrofitUtils4test.getInstance().uploadUserInfo(account,taskId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserInfoResult>() {


                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(UserInfoResult userInfoResult) {
                        if (userInfoResult.getResult().getStatus()!= STATUS_SUCCESS) {
                            handleResult(userInfoResult.getResult(),listener);
                        }
                        else{
                            listener.onResponse(userInfoResult);
                        }
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



    public interface ResponseListener<T> {
        void onResponse(T response);

        void onErrorResponse(int errorcode, String msg);
    }

    public   void handleResult(HttpResult response,ResponseListener listener) {
        int status = response.getStatus();
        int errorCode = response.getErrorcode();
        String msg = response.getMsg();
        if (status == STATUS_OVERDUE || status == STATUS_REGISTER) {
//            AccountContoller.getInstance().gotoLogin();
            if (listener != null) {
                listener.onErrorResponse(errorCode,msg);
            }
        } else if (status == STATUS_SERVER_HANDLE_ERROR || status == STATUS_BUSINESS_HANDLE_ERROR) {
            if (listener != null) {
                listener.onErrorResponse(errorCode,msg);
            }
        }
    }


}
