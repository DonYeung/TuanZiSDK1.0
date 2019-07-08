package com.loanhome.lib.bean;

/**
 * @Description Created by Don on 2019/7/2
 */
public class TypeStateResult {
    private HttpResult result;

    public HttpResult getResult() {
        return result;
    }

    public void setResult(HttpResult result) {
        this.result = result;
    }

    /**
     * taskStatus : {"eventType":"bank","status":0,"userId":"D5BAAC0368C75DDC87A3C9BA7704BDDD","time":1562051024682}
     * costTime : 268
     */

    private TaskStatusBean taskStatus;
    private int costTime;
    private String action;
    private EmailTaskBean  emailTask;

    public EmailTaskBean getEmailTask() {
        return emailTask;
    }

    public void setEmailTask(EmailTaskBean emailTask) {
        this.emailTask = emailTask;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public TaskStatusBean getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatusBean taskStatus) {
        this.taskStatus = taskStatus;
    }

    public int getCostTime() {
        return costTime;
    }

    public void setCostTime(int costTime) {
        this.costTime = costTime;
    }

    public static class TaskStatusBean {
        /**
         * eventType : bank
         * status : 0
         * userId : D5BAAC0368C75DDC87A3C9BA7704BDDD
         * time : 1562051024682
         */

        private String eventType;
        private int status;
        private String userId;
        private long time;

        public String getEventType() {
            return eventType;
        }

        public void setEventType(String eventType) {
            this.eventType = eventType;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }
    }


    public static class EmailTaskBean{
        private boolean resultValue;

        public boolean isResultValue() {
            return resultValue;
        }

        public void setResultValue(boolean resultValue) {
            this.resultValue = resultValue;
        }
    }
}
