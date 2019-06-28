package com.loanhome.lib.util;

import android.os.Environment;

import java.io.File;

public interface Constants {

	interface Path {
		// sd卡根目录
		String SDCARD = Environment
				.getExternalStorageDirectory().getPath();
		// 程序根目录
		String STARBABA_PATH = SDCARD + File.separator
				+ "BananaCard";
		// 测试文件路径
		String STARBABA_TEST_FILE_PATH = STARBABA_PATH + File.separator
				+ "test.txt";// 测试文件路径
	}

	interface Net {
		/**
		 * 通用的请求地址
		 */
		String SEVER_ADDRESS = "https://www.tuanzidai.cn/";
		String SEVER_ADDRESS_LOCAL = "https://test.xmiles.cn/";

		// 改成 < 23 就不会收到taobaoId, 值得买的商品就不会跳百川页面而是跳原流程
		// 24 下发首页头条数据
		// 25 添加打开阿里百川页面时的点击统计功能
		// 26 添加办违章服务费优惠信息展示
		// 27 添加违章订单页面上传驾驶证照
		// 28 添加违章主页面办理数据和违章功能
		// 29 财迷之家新版本新手红包
		// 30 车主无忧Icon区域从Native转成H5，财迷之家我的页面改版
		String CLASSIFICATION_INFO_PVERSION = "30";
	}
	
	interface NetKey {
		String KEY_PHEAD = "phead";
		String KEY_DATA = "data";
		String ID = "id";

	}

	interface SharedPreferencesKey {
		String CARLIFE_CITYNAME = "carlife_cityname";
		String CARLIFE_CUR_CITYNAME = "carlife_cur_cityname";
		String CARLIFE_GPS_CITYNAME = "carlife_gps_cityname";
		String CARLIFE_LATLNG = "carlife_latlng";


		String ACCOUNT_DATA = "account_data";
		String DEVICE_USER_TYPE = "device_user_type";
		String ACCOUNT_USERINO = "account_userino";
		String ACCOUNT_TOKEN = "account_token";
		String ACCOUNT_PHONE_NUMBER = "phoneNumer";
		String FROMID = "from_id";

		String ACCOUNT_PRODUCT_INFO = "account_product_info";

		String DEVICE_GLOBAL_NEW_USER = "device_global_new_user";
		String DEVICE_PRODUCT_NEW_USER = "device_product_new_user";

		String BEFORE_CHANNEL="before_channel";
		String ACTIVITY_ID="activity_id";
		String PRODUCT_NEW_USER="account_product_new_user";
		String GLOBAL_NEW_USER="account_global_new_user";

		String CONFIG = "config";
		String CONFIG_CITYCODE = "config_citycode";
		String CONFIG_GPS_CITYCODE = "config_gps_citycode";
		String CONFIG_CUR_VER_CODE = "config_cur_ver_code";
		String CONFIG_LAST_VER_CODE = "config_last_ver_code";
		String CONFIG_START_COUNT = "config_start_count";
		String CONFIG_LAST_SHOW_RATE_TIME = "config_last_show_rate_time";
		String CONFIG_HAS_RATED = "config_has_rated";
		/** 储存推广渠道的偏差渠道**/
		String CONFIG_SURVIVE = "config_survive";

		String CONFIG_LOAN_HOME = "config_loan_home";
		String CONFIG_IS_FIRST = "config_is_first";

		//同盾
		String ACCOUNT_TONGDUN_BALCKBOX = "account_tongdun_back_box";
	}

	interface UmengServiceName {
		String NAME_SHARE = "com.umeng.share";
		String NAME_LOGIN = "com.umeng.login";
	}

}
