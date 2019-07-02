package com.loanhome.lib.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @Description Created by Don on 2019/6/28
 */
public class IDCardResult {

    private HttpResult result;

    public HttpResult getResult() {
        return result;
    }

    public void setResult(HttpResult result) {
        this.result = result;
    }

    /**
     * idCardMessage : {"idCardName":"","idCardNumber":"","address":""}
     * flag : true
     */

    /**
     * idCardMessage : {"validDate":"","issuedBy":""}
     */

    private IdCardMessageBean idCardMessage;
    private boolean flag;

    public IdCardMessageBean getIdCardMessage() {
        return idCardMessage;
    }

    public void setIdCardMessage(IdCardMessageBean idCardMessage) {
        this.idCardMessage = idCardMessage;
    }


    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }


    public static class IdCardMessageBean {
        /**
         * idCardName :
         * idCardNumber :
         * address :
         */

        private String idCardName;
        private String idCardNumber;
        private String address;


        /**
         * validDate : 2015.08.19-2025.08.19
         * issuedBy : 揭阳市公安局榕城分局
         */

        private String validDate;
        private String issuedBy;

        public String getValidDate() {
            return validDate;
        }

        public void setValidDate(String validDate) {
            this.validDate = validDate;
        }

        public String getIssuedBy() {
            return issuedBy;
        }

        public void setIssuedBy(String issuedBy) {
            this.issuedBy = issuedBy;
        }

        public String getIdCardName() {
            return idCardName;
        }

        public void setIdCardName(String idCardName) {
            this.idCardName = idCardName;
        }

        public String getIdCardNumber() {
            return idCardNumber;
        }

        public void setIdCardNumber(String idCardNumber) {
            this.idCardNumber = idCardNumber;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }


    /**
     * iconList : [{"icon":"https://img.tuanzidai.cn/fault_tips_1.png","iconMsg":"使用身份证原件"}]
     * iconType : 1
     * errorMsg : 请勿使用手机或电脑上照片
     */

    private int iconType;
    private String errorMsg;
    private List<IconListBean> iconList;

    public int getIconType() {
        return iconType;
    }

    public void setIconType(int iconType) {
        this.iconType = iconType;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public List<IconListBean> getIconList() {
        return iconList;
    }

    public void setIconList(List<IconListBean> iconList) {
        this.iconList = iconList;
    }

    public static class IconListBean {
        /**
         * icon : https://img.tuanzidai.cn/fault_tips_1.png
         * iconMsg : 使用身份证原件
         */

        private String icon;
        private String iconMsg;

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getIconMsg() {
            return iconMsg;
        }

        public void setIconMsg(String iconMsg) {
            this.iconMsg = iconMsg;
        }
    }

}
