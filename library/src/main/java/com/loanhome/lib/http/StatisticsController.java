package com.loanhome.lib.http;

import android.content.Context;
import android.util.Log;

import com.loanhome.lib.bean.HttpResult;
import com.loanhome.lib.bean.StatisticResult;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.loanhome.lib.http.HttpGlobal.STATUS_SUCCESS;

/**
 * 统计请求管理类
 * @Description Created by Don on 2019/7/2
 */
public class StatisticsController {
    private static final String TAG = "StatisticsController";
    public static StatisticsController sIns;


    public static StatisticsController getInstance() {
        if (sIns == null) {
            synchronized (StatisticsController.class) {
                if (sIns == null) {
                    sIns = new StatisticsController();
                }
            }
        }
        return sIns;
    }

    /**
     * 新OCR增加的统计接口，增加了api_id
     *
     * @param page
     * @param logType
     * @param ckModule
     * @param index
     * @param functionid
     * @param contentid
     */
    public void newOCRRequestStatics(String page, String logType, String ckModule, String index,
                                     String functionid, String contentid, String api_id,
                                     String pPosition, String param1, String param2) {
        RetrofitUtils4test.getInstance().newOCRRequestStatics(page, logType, ckModule, index, functionid
                , contentid, api_id, pPosition, param1, param2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<StatisticResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(StatisticResult result) {
                        Log.i(TAG, "onNext: "+result.getResult().getStatus());
                        if (result.getResult().getStatus()!= STATUS_SUCCESS) {
                            Log.i(TAG, "==新OCR统计接口----上传统计失败 ==");
                            return;
                        }
                        else {
                            Log.i(TAG, "==新OCR统计接口----上传统计成功 ==");
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

    /**
     * 新的统计接口
     * @param page
     * @param logType
     * @param ckModule
     * @param index
     * @param functionid
     * @param contentid
     */
    public void newRequestStatics(String page,String logType, String ckModule, int index, String functionid, String contentid) {
        RetrofitUtils4test.getInstance().newRequestStatics(page, logType, ckModule, index, functionid, contentid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<StatisticResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(StatisticResult result) {
                        if (result.getResult().getStatus()!= STATUS_SUCCESS) {
                            Log.i(TAG, "== 新的统计接口----上传统计失败 ==");
                            return;
                        }
                        else {
                            Log.i(TAG, "== 新的统计接口----上传统计成功 ==");
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "== 新的统计接口----上传统计失败 ==");

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }




}
