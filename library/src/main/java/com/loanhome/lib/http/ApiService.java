package com.loanhome.lib.http;

import com.loanhome.lib.bean.BankCardResult;
import com.loanhome.lib.bean.BizTokenResult;
import com.loanhome.lib.bean.HttpResult;
import com.loanhome.lib.bean.IDCardResult;
import com.loanhome.lib.bean.StatisticResult;
import com.loanhome.lib.bean.TypeStateResult;
import com.loanhome.lib.bean.UserInfoResult;
import com.loanhome.lib.bean.UserType;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import static com.loanhome.lib.http.HttpGlobal.PATH.LOAN_ACCOUNT_TOOL;
import static com.loanhome.lib.http.HttpGlobal.PATH.LOAN_API;
import static com.loanhome.lib.http.HttpGlobal.PATH.LOAN_DATA_SOURCE;
import static com.loanhome.lib.http.HttpGlobal.PATH.LOAN_SERVICE;
import static com.loanhome.lib.http.HttpGlobal.PATH.LOAN_STATISTICS;
import static com.loanhome.lib.http.HttpGlobal.PATH.LOAN_USER_SERVICE;

/**
 * @Description TODO
 * Created by Don on 2019/5/27
 */
public interface ApiService {
    @POST(LOAN_USER_SERVICE + "/active/user-type")
    Observable<UserType> getUserType();

    @POST(LOAN_DATA_SOURCE + "/faceId/ocr/idCard")
    Observable<IDCardResult> getOCRResult(@Body RequestBody requestBody);


    @POST(LOAN_DATA_SOURCE + "/faceId/verifyMeglive")
    Observable<HttpResult> upLoadingLivenessInfo_New(@Body RequestBody requestBody);


    @POST(LOAN_DATA_SOURCE + "/faceId/ocr/bank")
    Observable<BankCardResult> getBankOCRResult(@Body RequestBody requestBody);


    @POST(LOAN_ACCOUNT_TOOL + "/common?funid=24")
    @FormUrlEncoded
    Observable<TypeStateResult> fetchTypeState(@FieldMap Map<String,String> map);


    @POST(LOAN_ACCOUNT_TOOL + "/common?funid=30")
    @FormUrlEncoded
    Observable<UserInfoResult> uploadUserInfo(@FieldMap Map<String,String> map);

    @POST(LOAN_STATISTICS + "/log")
    Observable<StatisticResult> newOCRRequestStatics(@Body RequestBody requestBody);

    @POST(LOAN_SERVICE + "/common?funid=1000")
    @FormUrlEncoded
    Observable<StatisticResult> newRequestStatics(@FieldMap Map<String,String> map);


    @POST(LOAN_API + "/flow/ocr/getBizToken")
    Observable<BizTokenResult> getBizToken(@Body RequestBody requestBody);

    @POST(LOAN_API + "/flow/ocr/verifyMegLive")
    Observable<HttpResult> LivenessVerify(@Body RequestBody requestBody);

}
