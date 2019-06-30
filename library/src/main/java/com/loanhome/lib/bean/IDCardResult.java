package com.loanhome.lib.bean;

import java.util.List;

/**
 * @Description Created by Don on 2019/6/28
 */
public class IDCardResult {

    private HttpResult result;



    /**
     * iconList : [{"icon":"https://img.tuanzidai.cn/fault_tips_1.png","iconMsg":"使用身份证原件"}]
     * iconType : 1
     * flag : false
     * errorMsg : 请勿使用手机或电脑上照片
     */

    private int iconType;
    private boolean flag;
    private String errorMsg;
    private List<IconListBean> iconList;
    private List<IdCardMessage> idCardMessages;

    public List<IdCardMessage> getIdCardMessages() {
        return idCardMessages;
    }

    public void setIdCardMessages(List<IdCardMessage> idCardMessages) {
        this.idCardMessages = idCardMessages;
    }

    public HttpResult getResult() {
        return result;
    }

    public void setResult(HttpResult result) {
        this.result = result;
    }

    public int getIconType() {
        return iconType;
    }

    public void setIconType(int iconType) {
        this.iconType = iconType;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
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

    public static class IdCardMessage{
        private String validDate;
        private String issueBy;
        private String idCardName;
        private String idCardNumber;
        private String adress;

        public String getValidDate() {
            return validDate;
        }

        public void setValidDate(String validDate) {
            this.validDate = validDate;
        }

        public String getIssueBy() {
            return issueBy;
        }

        public void setIssueBy(String issueBy) {
            this.issueBy = issueBy;
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

        public String getAdress() {
            return adress;
        }

        public void setAdress(String adress) {
            this.adress = adress;
        }
    }
}
