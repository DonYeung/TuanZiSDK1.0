package com.loanhome.lib.http;

/**
 * @Description TODO
 * Created by Don on 2019/5/30
 */
public class HttpResult {
        /**
         * status : -1
         * errorcode : -1
         * msg : 系统错误,请重新尝试
         */

        private int status;
        private int errorcode;
        private String msg;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getErrorcode() {
            return errorcode;
        }

        public void setErrorcode(int errorcode) {
            this.errorcode = errorcode;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

}
