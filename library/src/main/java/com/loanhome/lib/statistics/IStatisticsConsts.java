package com.loanhome.lib.statistics;

/**
 * 统计相关的常量
 * 
 * @author wangzhuobin
 *
 */
public interface IStatisticsConsts {

	/**
	 * 友盟自定义统计事件的id常量
	 * 
	 * @author wangzhuobin
	 *
	 */
	interface UmengEventId {

		/**
		 * 分享模块的自定义统计事件id
		 * 
		 * @author wangzhuobin
		 *
		 */
		interface Share {
			/**
			 * 进行分享的自定义事件
			 */
			String EVENTID_SHARE = "share";
			/**
			 * 进行微信分享的自定义事件
			 */
			String EVENTID_SHARE_WEIXIN = "share_2";
			/**
			 * 进行微信朋友圈分享的自定义事件
			 */
			String EVENTID_SHARE_WEIXIN_CIRCLE = "share_1";
			/**
			 * 进行新浪微博分享的自定义事件
			 */
			String EVENTID_SHARE_SINA = "share_3";
			/**
			 * 进行qq分享的自定义事件
			 */
			String EVENTID_SHARE_QQ = "share_5";
			/**
			 * 进行qq空间分享的自定义事件
			 */
			String EVENTID_SHARE_QZONE = "share_4";
			/**
			 * 分享成功的自定义事件
			 */
			String EVENTID_SHARE_SUCCESS = "share_success";
			/**
			 * 分享微信成功的自定义事件
			 */
			String EVENTID_SHARE_WEIXIN_SUCCESS = "share_success_2";
			/**
			 * 分享微信朋友圈成功的自定义事件
			 */
			String EVENTID_SHARE_WEIXIN_CIRCL_SUCCESS = "share_success_1";
			/**
			 * 分享新浪微博成功的自定义事件
			 */
			String EVENTID_SHARE_SINA_SUCCESS = "share_success_3";
			/**
			 * 分享qq成功的自定义事件
			 */
			String EVENTID_SHARE_QQ_SUCCESS = "share_success_5";
			/**
			 * 分享qq空间成功的自定义事件
			 */
			String EVENTID_SHARE_QZONE_SUCCESS = "share_success_4";
		}

		interface Mine {
			String MINE_NEWS_CLICK = "mine_news_click";
			String MINE_INFO_CLICK = "mine_info_click";
			String MINE_MONEY_CLICK = "mine_money_click";
			String MINE_LIST_CLICK = "mine_list_click";

		}

		interface LogType{
			String LOG_TYPE_CLICK = "click";
			String LOG_TYPE_VIEW = "view";
		}

		interface CkModule{
			String CK_MODULE_AD = "ad";
			String CK_MODULE_TAB_ICON = "click_tab_icon";
			String CK_MODULE_ADD_CARD = "click_add_card";
			String CK_MODULE_CLICK_LIST_CARD = "click_card_list";
			String CK_MODULE_TOP_ICON = "click_topicon";
			String CK_MODULE_RECENTPAY_DATE = "click_recentpaydate";
			String CK_MODULE_APPLY_CARD = "click_apply_card";
			String CK_MODULE_MY_NEWS = "click_mynews";
			String CK_MODULE_USERS = "click_users";
			String CK_MODULE_REPAYMENT = "click_repayment";
			String CK_MODULE_CARDLIST = "click_card_list";
			String CK_MODULE_REPAYMENT_REMIND = "click_repayment_remind";
			String CK_MODULE_CLICK_CALENDAR_REPAYMENT = "click_calendar_repayment";
			String CK_MODULE_VIEW_CALENDAR_REPAYMENT = "view_calendar_repayment";
			String CK_MODULE_CLICK_SETTING_FUNCTION = "click_setting_function";
			String CK_MODULE_CLICK_USERS_FUNCTION = "click_users_function";
			String CK_MODULE_VIEW_USERS = "view_users";
			String CK_MODULE_VIEW_LAND_LOADING_END = "view_land_loading_end";
			String CK_MODULE_CLICK_CARD_SETTING = "click_card_setting";
			String CK_MODULE_CLICK_SLIDE_CARD = "click_slide_card";
			String CK_MODULE_CLICK_UPDATE_BILL = "click_update_bill";
			String CK_MODULE_CLICK_REPAYMENT = "click_repayment";
			String CK_MODULE_CLICK_BILL_SETTING = "click_bill_setting";
			String CK_MODULE_CLICK_BILL_DEDETAILS = "click_bill_dedetails";
			String CK_MODULE_VIEW_BILL_GRAPH = "view_bill_graph";
			String CK_MODULE_CLICK_BILL_GRAPH = "click_bill_graph";
			String CK_MODULE_CLICK_BILL_LIST = "click_bill_list";
			String CK_MODULE_CLICK_BANK_IMPORT = "click_bank_import";
			String CK_MODULE_CLICK_BILL_FORM = "click_bill_form";
			String CK_MODULE_CLICK_TIME_SELECT= "click_time_select";
			String CK_MODULE_VIEW_TRANSACTION_TYPE = "view_transaction_Type";
			String CK_MODULE_CLICK_TRANSACTION_TYPE = "click_transaction_Type";
			String CK_MODULE_CLICK_SORT_LIST = "click_sort_list";
			String CK_MODULE_VIEW_BANK_SERVICE = "view_bank_service";
			String CK_MODULE_CLICK_SET_RETURNED = "click_set_returned";
			String CK_MODULE_CLICK_SET_NORETURNED = "click_set_noreturned";
			String CK_MODULE_CLICK_COPY = "click_copy";
			String CK_MODULE_VIEW_ADD_CARD_PAGE = "view_add_card_page";
			String CK_MODULE_CLICK_ADD_CARD_PAGE = "click_add_card_page";
			String CK_MODULE_VIEW_BILLDETAILS_PAGE = "view_billdetails_page";
			String CK_MODULE_CLICK_INQUIRY = "click_card_inquiry";
			String CK_MODULE_CLICK_CONTACT = "click_contact";
			String CK_MODULE_VIEW_TIE_CARD = "view_tie_card";
			String CK_MODULE_VIEW_LOGIN_FRAME = "view_login_frame";
			String CK_MODULE_CLICK_IMPUT_PHONENUM = "click_imput_phonenum";
			String CK_MODULE_CLICK_IMPUT_REQUESTCODE = "click_imput_requestcode";
			String CK_MODULE_CLICK_IMPUT_CODE = "click_imput_code";
			String CK_MODULE_CLICK_LOGIN = "click_login";

			String CK_MODULE_OCR_START = "ocr_start";
			String CK_MODULE_OCR_SHOT_DONE = "ocr_shot_done";
			String CK_MODULE_OCR_SHOT_FAIL = "ocr_shot_fail";
//			String CK_MODULE_OCR_ID_DONE = "ocr_id_done";
			String CK_MODULE_OCR_ID_FAIL = "ocr_id_fail";
			String CK_MODULE_OCR_ID_COMPARE_DONE = "ocr_id_compare_done";
			String CK_MODULE_OCR_ID_COMPARE_FAIL = "ocr_id_compare_fail";
			String CK_MODULE_CLOSE_CAM = "close_cam";
			String CK_MODULE_ID_CHECK_CONFIRMED = "ID_check_confirmed";
			String CK_MODULE_OCR_RETAKE = "ocr_retake";
			String CK_MODULE_OCR_ID_REQUEST = "ocr_id_request";

			String CK_MODULE_OCR_ASKCAMERA = "ocr_ask_camera";
			String CK_MODULE_OCR_FILP = "ocr_flip";
			String CK_MODULE_OCR_FILP_OK = "ocr_flip_ok";
			String CK_MODULE_OCR_CLOSE = "click_close";
			String CK_MODULE_OCR_SHOT = "ocr_shot";
			String CK_MODULE_OCR_POPUP = "ocr_popup";
			String CK_MODULE_OCR_CONFIRM = "ocr_confirm";
			String CK_MODULE_OCR_RETRY = "ocr_retry";


			String CK_MODULLE_HUMAN_DETECT_PIC = "human_detect_pic";
			String CK_MODULE_HUMAN_DETECT_FAIL = "human_detect_fail";
			String CK_MODULE_HUMAN_DETECT_SUCCESS = "human_detect_success";

			String CK_MODULE_LIVENESS_ASK_CAMERA = "liveness_ask_camera";
			String CK_MODULE_LIVENESS_SHOT = "liveness_shot";

			String CK_MODULE_VIEW_LAUNCHSCREEN_PAGE = "launchscreen";
			String CK_MODULE_VIEW_START_CLIENT = "start_client";

			String CK_MODULE_VIEW_OCR_CARD_PAGE = "ocr_card_page";
			String CK_MODULE_CLICK_CLICK_TIPS = "click_tips";
			String CK_MODULE_VIEW_CARDNUM_POP = "cardnum_pop";
			String CK_MODULE_CLICK_CLICK_CHECK_CONFIRM = "click_check_confirm";
			String CK_MODULE_CLICK_CLICK_RETAKE = "click_retake";
			String CK_MODULE_VIEW_OCR_CARD_FAIL = "ocr_card_fail";
		}

		interface Page{
			String PAGE_DEFAULT = "default";
			String PAGE_ADD_CARD = "add_card";
			String PAGE_SETTING = "setting";
			String PAGE_MINE = "mine";
			String PAGE_TAB = "tab";
			String PAGE_MAIN = "main_page";
			String PAGE_BILLDETAILS = "billdetails_page";
			String PAGE_REPAYMENT_CALENDAR = "repayment_calendar";
			String PAGE_SELECT_MAIL = "select_mail";
			String PAGE_SELECT_BANK = "select_bank";
			String PAGE_ADD_CARD_PAGE = "add_card_page";
			String PAGE_REPAYMENT_FRAME = "repayment_frame";
			String PAGE_BILL_SETTING_FRAME = "bill_setting_frame";
			String PAGE_CARD_CONTACT_PAGE = "card_contact_page";
			String PAGE_CARD_INQUIRY_PAGE = "card_inquiry_page";
			String PAGE_LAND_LOADING_END = "land_loading_end";
			String PAGE_LOGIN_FRAME = "login_frame";

			String PAGE_LAUNCHSCREEN_PAGE = "launchscreen_page";
			String PAGE_START_CLIENT = "start_client";

			String PAGE_OCR_START = "ocr_start";

			String PAGE_OCR_SHOT = "ocr_shot";
			String PAGE_OCR_FILP = "ocr_flip";
			String PAGE_OCR_POPUP = "ocr_popup";
			String PAGE_OCR_CONFIRM = "ocr_confirm";
			String PAGE_OCR_RETRY = "ocr_retry";

			String PAGE_OCR_ID = "ocr_id";
			String PAGE_OCR_ID_COMPARE = "ocr_id_compare";
			String PAGE_ID_CHECK = "ID_check";
			String PAGE_OCR_RETAKE = "ocr_retake";
			String PAGE_CLOSE_CAM = "close_cam";
			String PAGE_OCR_ID_REQUEST = "ocr_id_request";
			String PAGE_HUMAN_DETECT_PIC = "human_detect_pic";
			String PAGE_HUMAN_DETECT = "human_detect";

			String PAGE_LIVENESS_SHOT = "liveness_shot";

			String PAGE_CARD_PAGE = "ocr_card_page";
			String PAGE_CARDNUM_POP = "cardnum_pop";
			String PAGE_CARD_FAIL = "ocr_card_fail";

		}








		interface LocationError {
			String LOCATION_FAILED = "location_failed";
		}

	}

	/**
	 * 统计的key
	 * 
	 * @author wangzhuobin
	 *
	 */
	interface Key {
		/**
		 * 分享模块的key
		 * 
		 * @author wangzhuobin
		 *
		 */
		interface Share {
			/**
			 * 分享标题的key
			 */
			String KEY_SHARE_TITLE = "title";
			/**
			 * 分享平台的key
			 */
			String KEY_SHARE_MEDIA = "media";
			/**
			 * 分享用户名的key
			 */
			String KEY_SHARE_USERNAME = "username";
		}

	}

}
