package com.loanhome.lib.http;

import android.util.Log;

import com.loanhome.lib.bean.HttpResult;
import com.loanhome.lib.bean.TongDunResult;
import com.loanhome.lib.listener.TongDunResultCallback;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.loanhome.lib.http.HttpGlobal.STATUS_SUCCESS;

/**
 * @Description Created by Don on 2019/7/17
 */
public class AppDataCourceController {
    private static final String TAG = "AppDataCourceController";
    public static AppDataCourceController mInstance;


    public static AppDataCourceController getInstance() {
        if (mInstance == null) {
            synchronized (AppDataCourceController.class) {
                if (mInstance == null) {
                    mInstance = new AppDataCourceController();
                }
            }
        }
        return mInstance;
    }

    public void upLoadFingerprint(String balckBox,final TongDunResultCallback callback){
        RetrofitUtils4test.getInstance().upLoadFingerprint(balckBox)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TongDunResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(TongDunResult tongDunResult) {
                        Log.i(TAG, "onNext: "+tongDunResult.getResult().getStatus());
                        if (tongDunResult.getResult().getStatus()!= STATUS_SUCCESS) {
                            Log.i(TAG, "==新OCR统计接口----上传统计失败 ==");
                            return;
                        }
                        else {
                            Log.i(TAG, "==新OCR统计接口----上传统计成功 ==");
                            if (callback!=null){
                                callback.onSuccess(tongDunResult.getResult().toString());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "== 新OCR统计接口----上传统计失败 ==");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

}
