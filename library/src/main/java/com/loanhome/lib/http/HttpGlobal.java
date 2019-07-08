package com.loanhome.lib.http;

import com.loanhome.lib.util.TestUtil;

/**
 * @Description TODO
 * Created by Don on 2019/5/27
 */
public class HttpGlobal {
    // 服务器处理结果
    /*
     * status=1:处理成功； status=-1:服务器处理出错； status=-2:业务处理异常； status=-3:token过期
     * status=-4:需要注册用户 status=1时客户端才解析数据。
     */
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_SERVER_HANDLE_ERROR = -1;
    public static final int STATUS_BUSINESS_HANDLE_ERROR = -2;
    public static final int STATUS_OVERDUE = -3;
    public static final int STATUS_REGISTER = -4;


    interface PATH{
        static String LOAN_USER_SERVICE = "loan-user";

        static String LOAN_DATA_SOURCE = "loan-datasource";

        static String LOAN_STATISTICS = "loan-statistics";

        static String LOAN_ACCOUNT_TOOL = "loan_account_tool";

        static String LOAN_SERVICE= "loan_service";

        static String LOAN_API= "loan-api";

    }
    interface Net {
        /**
         * 通用的请求地址
         */
        String SEVER_ADDRESS = "https://www.tuanzidai.cn/";
        String SEVER_ADDRESS_LOCAL = "https://test.xmiles.cn/";

        String CLASSIFICATION_INFO_PVERSION = "30";

    }
    /**
     * 获取基础请求地址的方法
     *
     * @return
     */
    public static String getBaseHost() {
        return TestUtil.isTestServer() ? HttpGlobal.Net.SEVER_ADDRESS_LOCAL : HttpGlobal.Net.SEVER_ADDRESS;
    }


}
