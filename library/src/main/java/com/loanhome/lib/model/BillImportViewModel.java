package com.loanhome.lib.model;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import com.loanhome.lib.bean.ImportStateInfo;

import org.json.JSONObject;

/**
 * @author liaopeijian
 * @Date 2018/7/9
 */
public class BillImportViewModel extends ViewModel {
    private MutableLiveData<ImportStateInfo> stateLiveData;
    private MutableLiveData<Integer> upLoadingLiveData;
    public static final int FETCH_SUCCESS = 0;
    public static final int FETCH_FAIL = 1;
    public static final int FETCH_PROGRESS = 2;


    public MutableLiveData<ImportStateInfo> getTaskState(String type, String taskId, String mode) {
        if (stateLiveData == null){
            stateLiveData = new MutableLiveData<>();
        }
        fetchTaskState(type,taskId,mode);
        return stateLiveData;
    }

    public void fetchTaskState(String type, String taskId, String mode){
        BillImportController
                .getInstance()
                .fetchTypeState(type, taskId, mode, new BillImportController.OnCallbackListener() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onSuccess(String action) {
                        ImportStateInfo info = new ImportStateInfo();
                        info.setState(FETCH_SUCCESS);
                        info.setAction(action);
                        stateLiveData.setValue(info);
                    }

                    @Override
                    public void onProgress() {
                        ImportStateInfo info = new ImportStateInfo();
                        info.setState(FETCH_PROGRESS);
                        stateLiveData.setValue(info);
                    }

                    @Override
                    public void onFailed() {
                        ImportStateInfo info = new ImportStateInfo();
                        info.setState(FETCH_FAIL);
                        stateLiveData.setValue(info);
                    }
                });
    }

    public MutableLiveData<Integer> upLoadingAccount(JSONObject info, String taskId) {
        if (upLoadingLiveData == null){
            upLoadingLiveData = new MutableLiveData<>();
        }
        upLoadingAccountInfo(info,taskId);
        return upLoadingLiveData;
    }

    private void upLoadingAccountInfo(JSONObject account, String taskId){
        BillImportController
                .getInstance()
                .uploadUserInfo(account, taskId, new BillImportController.OnCallbackListener() {
                    @Override
                    public void onSuccess() {
                        upLoadingLiveData.setValue(FETCH_SUCCESS);
                    }

                    @Override
                    public void onFailed() {
                        upLoadingLiveData.setValue(FETCH_FAIL);
                    }

                    @Override
                    public void onSuccess(String action) {

                    }

                    @Override
                    public void onProgress() {
                        upLoadingLiveData.setValue(FETCH_PROGRESS);
                    }
                });
    }


}
