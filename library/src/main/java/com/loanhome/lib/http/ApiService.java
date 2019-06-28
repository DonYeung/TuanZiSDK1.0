package com.loanhome.lib.http;

import com.loanhome.lib.bean.IDCardResult;
import com.loanhome.lib.bean.UserType;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

import static com.loanhome.lib.http.HttpGlobal.PATH.LOAN_DATA_SOURCE;
import static com.loanhome.lib.http.HttpGlobal.PATH.LOAN_USER_SERVICE;

/**
 * @Description TODO
 * Created by Don on 2019/5/27
 */
public interface ApiService {
    @POST(LOAN_USER_SERVICE + "/active/user-type")
    Observable<UserType> getUserType();

    @POST(LOAN_DATA_SOURCE + "/faceId/ocr/idCard")
    @FormUrlEncoded
    Observable<IDCardResult> getOCRResult(@Body RequestBody requestBody);

}
