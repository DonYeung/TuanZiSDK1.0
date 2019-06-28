package com.loanhome.lib.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author liaopeijian
 * @Date 2017/12/11
 */

public class VerifyInfo {
    private int type;
    private int cameraType;
    private String startCallbackJs;
    private String failCallbackJs;
    private String waitConfirmCallbackJs;
    private String successCallbackJs;
    private String cancelCallbackJs;
    private String imageUrl;
    private String backImageUrl;
    private String idCardNumber;
    private String idCardName;
    private String action;
    private boolean isNeedCallBackFront;
    private boolean isNeedCallBackBack;
    private String functionId;
    private String contentId;
    private String pPosition;
    private String param1;
    private String param2;
    private String api_id;

    public String getApi_id() {
        return api_id;
    }

    public void setApi_id(String api_id) {
        this.api_id = api_id;
    }

    public String getFunctionId() {
        return functionId;
    }

    public void setFunctionId(String functionId) {
        this.functionId = functionId;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getpPosition() {
        return pPosition;
    }

    public void setpPosition(String pPosition) {
        this.pPosition = pPosition;
    }

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    public boolean getNeedCallBackFront() {
        return isNeedCallBackFront;
    }

    public void setNeedCallBackFront(boolean needCallBackFront) {
        isNeedCallBackFront = needCallBackFront;
    }

    public boolean getNeedCallBackBack() {
        return isNeedCallBackBack;
    }

    public void setNeedCallBackBack(boolean needCallBackBack) {
        isNeedCallBackBack = needCallBackBack;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
    public String getIdCardNumber() {
        return idCardNumber;
    }

    public void setIdCardNumber(String idCardNumber) {
        this.idCardNumber = idCardNumber;
    }

    public String getIdCardName() {
        return idCardName;
    }

    public void setIdCardName(String idCardName) {
        this.idCardName = idCardName;
    }

    public String getBackImageUrl() {
        return backImageUrl;
    }

    public void setBackImageUrl(String backImageUrl) {
        this.backImageUrl = backImageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getCameraType() {
        return cameraType;
    }

    public void setCameraType(int cameraType) {
        this.cameraType = cameraType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStartCallbackJs() {
        return startCallbackJs;
    }

    public void setStartCallbackJs(String startCallbackJs) {
        this.startCallbackJs = startCallbackJs;
    }

    public String getFailCallbackJs() {
        return failCallbackJs;
    }

    public void setFailCallbackJs(String failCallbackJs) {
        this.failCallbackJs = failCallbackJs;
    }

    public String getWaitConfirmCallbackJs() {
        return waitConfirmCallbackJs;
    }

    public void setWaitConfirmCallbackJs(String waitConfirmCallbackJs) {
        this.waitConfirmCallbackJs = waitConfirmCallbackJs;
    }

    public String getSuccessCallbackJs() {
        return successCallbackJs;
    }

    public void setSuccessCallbackJs(String successCallbackJs) {
        this.successCallbackJs = successCallbackJs;
    }

    public String getCancelCallbackJs() {
        return cancelCallbackJs;
    }

    public void setCancelCallbackJs(String cancelCallbackJs) {
        this.cancelCallbackJs = cancelCallbackJs;
    }

    public static VerifyInfo parseJSON(String json){
        VerifyInfo info = new VerifyInfo();
        try {
            JSONObject object = new JSONObject(json);
            info.setType(object.optInt("verifytype"));
            info.setStartCallbackJs(object.optString("start_callback_js"));
            info.setSuccessCallbackJs(object.optString("success_callback_js"));
            info.setFailCallbackJs(object.optString("fail_callback_js"));
            info.setCancelCallbackJs(object.optString("cancel_callback_js"));
            info.setWaitConfirmCallbackJs(object.optString("wait_confirm_callback_js"));
            info.setCameraType(object.optInt("cameratype"));
            info.setImageUrl(object.optString("imageUrl"));
            info.setBackImageUrl(object.optString("back_imageUrl"));
            info.setIdCardNumber(object.optString("id_card_number"));
            info.setIdCardName(object.optString("id_card_name"));
            info.setAction(object.optString("action"));
            info.setNeedCallBackFront(object.optBoolean("isNeedCallBackFront"));
            info.setNeedCallBackBack(object.optBoolean("isNeedCallBackBack"));
            info.setFunctionId(object.optString("functionId"));
            info.setContentId(object.optString("contentId"));
            info.setpPosition(object.optString("pPosition"));
            info.setParam1(object.optString("param1"));
            info.setParam2(object.optString("param2"));
            info.setApi_id(object.optString("api_id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return info;
    }

}
