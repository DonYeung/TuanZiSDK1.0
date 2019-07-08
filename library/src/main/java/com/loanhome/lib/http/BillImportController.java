package com.loanhome.lib.http;

import com.loanhome.lib.bean.TypeStateResult;
import com.loanhome.lib.bean.UserInfoResult;
import com.moxie.client.model.MxParam;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Description Created by Don on 2019/7/2
 */
public class BillImportController {
    private static BillImportController mInstance;

    public static BillImportController getInstance(){
        if (mInstance==null){
            mInstance = new BillImportController();
        }
        return mInstance;
    }

    public void fetchTypeState (final String type, String taksId,String mode,final OnCallbackListener listener){
        RetrofitUtils4test.getInstance().fetchTypeStatemain(type, taksId, mode, new RetrofitUtils4test.ResponseListener<TypeStateResult>() {
            @Override
            public void onResponse(TypeStateResult response) {
                    if (listener != null){
                        if (type.equals(MxParam.PARAM_TASK_EMAIL)){
                            TypeStateResult.TaskStatusBean taskStatus = response.getTaskStatus();
                            if (taskStatus != null){
                                int status = taskStatus.getStatus();
                                if (status == -1){
                                    listener.onFailed();
                                }else if (status == 0){
                                    listener.onProgress();
                                }

                            }else {
                                TypeStateResult.EmailTaskBean emailTask = response.getEmailTask();
                                boolean hasBriedBill = emailTask.isResultValue();
                                if (hasBriedBill){
                                    String action = response.getAction();
                                    listener.onSuccess(action);
                                }else {
                                    listener.onFailed();
                                }
                            }


                        }else {
                            TypeStateResult.TaskStatusBean taskStatus = response.getTaskStatus();
                            int status = taskStatus.getStatus();
                            if (status == 1){
                                String action = response.getAction();
                                listener.onSuccess(action);
                            }else if (status == -1){
                                listener.onFailed();
                            }else {
                                listener.onProgress();
                            }
                        }



                    }

            }

            @Override
            public void onErrorResponse(int errorcode, String msg) {
                if (listener != null){
                    listener.onFailed();
                }
            }
        });

    }


    public void uploadUserInfo(final JSONObject account,String taskId,final OnCallbackListener listener){
        RetrofitUtils4test.getInstance().uploadUserInfomain(account, taskId, new RetrofitUtils4test.ResponseListener<UserInfoResult>() {

            @Override
            public void onResponse(UserInfoResult response) {
                if (listener != null){
                    int briefBill = response.getBrief_bill();
                    int detailed_bill = response.getDetailed_bill();
                    listener.onSuccess();

                }
            }

            @Override
            public void onErrorResponse(int errorcode, String msg) {
                if (listener != null){
                    listener.onFailed();
                }
            }
        });
    }

    public interface OnCallbackListener{
        void onSuccess(String action);
        void onSuccess();
        void onFailed();
        void onProgress();
    }
}
