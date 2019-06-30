package com.loanhome.lib.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.util.Locale;

/**
 * 
 *
 */
// CHECKSTYLE:OFF
public class Machine {
	private static boolean sCheckTablet = false;
	private static boolean sIsTablet = false;

	public static final boolean IS_SDK_ABOVE_KITKAT = Build.VERSION.SDK_INT >= 19; //sdk是否4.4或以上


	// 判断当前设备是否为平板
	private static boolean isPad(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	public static boolean isTablet(Context context) {
		if (sCheckTablet) {
			return sIsTablet;
		}
		sCheckTablet = true;
		sIsTablet = isPad(context);
		return sIsTablet;
	}

	/**
	 * 判断当前网络是否可以使用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkOK(Context context) {
		boolean result = false;
		if (context != null) {
			try {
				ConnectivityManager cm = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				if (cm != null) {
					NetworkInfo networkInfo = cm.getActiveNetworkInfo();
					if (networkInfo != null && networkInfo.isConnected()) {
						result = true;
					}
				}
			} catch (NoSuchFieldError e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	/**
	 * @return
	 */
	public static String getIMEI(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}
	
	
	/**
	 * <br>功能简述:获取Android ID的方法
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public static String getAndroidId(Context context) {
		String androidId = null;
		if (context != null) {
			androidId = Settings.Secure.getString(context.getContentResolver(),
					Settings.Secure.ANDROID_ID);
		}
		return androidId;
	}


	/**
	 * 检测手机WIFI有没有打开的方法
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifiEnable(Context context) {
		boolean result = false;
		try {
			if (context != null) {
				ConnectivityManager connectivityManager = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				if (connectivityManager != null) {
					NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
					if (networkInfo != null && networkInfo.isConnected()
							&& networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
						result = true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获取用户运营商代码
	 * 
	 * @return
	 */
	public static String getCnUser(Context context) {
		String simOperator = "000";
		try {
			if (context != null) {
				// 从系统服务上获取了当前网络的MCC(移动国家号)，进而确定所处的国家和地区
				TelephonyManager manager = (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE);
				simOperator = manager.getSimOperator();
			}
		} catch (Throwable ignored) {
		}

		return simOperator;
	}

	/**
	 * 获取SIM卡所在的国家
	 * 
	 * @param context
	 * @return 当前手机sim卡所在的国家，如果没有sim卡，取本地语言代表的国家
	 */
	public static String local(Context context) {
		String ret = null;
//		// 根据桌面语言设置请求的语言信息
//		DeskResourcesConfiguration dc = DeskResourcesConfiguration.getInstance();
		try {
			TelephonyManager telManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			if (telManager != null) {
				ret = telManager.getSimCountryIso();
			}
		} catch (Throwable e) {
			// e.printStackTrace();
		}

		if (ret == null || ret.equals("")) {
			ret = Locale.getDefault().getCountry().toLowerCase();
		}
		return ret;
	}

	/**
	 * 获取语言和国家地区的方法 格式: SIM卡方式：cn 系统语言方式：zh-CN
	 * 
	 * @return
	 */
	public static String language(Context context) {

		String ret = null;
		// 根据桌面语言设置请求的语言信息
		Locale locale = Locale.getDefault();
		try {
			TelephonyManager telManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			if (telManager != null) {
				ret = telManager.getSimCountryIso();
				if (ret != null && !ret.equals("")) {
					ret = String.format("%s_%s", locale.getLanguage().toLowerCase(),
							ret.toLowerCase());
				}
			}
		} catch (Throwable ignored) {
		}

		if (ret == null || ret.equals("")) {
			ret = String.format("%s_%s", locale.getLanguage().toLowerCase(), locale.getCountry()
					.toLowerCase());
		}
		return null == ret ? "error" : ret;
	}

	/**
	 * 获取当前网络状态，wifi，GPRS，3G，4G
	 * 
	 * @param context
	 * @return
	 */
	public static String buildNetworkState(Context context) {
		// build Network conditions
		String ret = "";
		try {
			ConnectivityManager manager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkinfo = manager.getActiveNetworkInfo();
			
			if (networkinfo == null) {
				return ret;
			}
			
			if (networkinfo.getType() == ConnectivityManager.TYPE_WIFI) {
				ret = "WIFI";
			} else if (networkinfo.getType() == ConnectivityManager.TYPE_MOBILE) {
				int subtype = networkinfo.getSubtype();
				switch (subtype) {
					case TelephonyManager.NETWORK_TYPE_1xRTT :
					case TelephonyManager.NETWORK_TYPE_CDMA :
					case TelephonyManager.NETWORK_TYPE_EDGE :
					case TelephonyManager.NETWORK_TYPE_GPRS :
					case TelephonyManager.NETWORK_TYPE_IDEN :
						// 2G
						ret = "2G" /*+ "(typeid = " + networkinfo.getType() + "  typename = "
									+ networkinfo.getTypeName() + "  subtypeid = "
									+ networkinfo.getSubtype() + "  subtypename = "
									+ networkinfo.getSubtypeName() + ")"*/;
						break;
					case TelephonyManager.NETWORK_TYPE_EVDO_0 :
					case TelephonyManager.NETWORK_TYPE_EVDO_A :
					case TelephonyManager.NETWORK_TYPE_HSDPA :
					case TelephonyManager.NETWORK_TYPE_HSPA :
					case TelephonyManager.NETWORK_TYPE_HSUPA :
					case TelephonyManager.NETWORK_TYPE_UMTS :
						// 3G,4G
						ret = "3G/4G" /*+ "(typeid = " + networkinfo.getType() + "  typename = "
										+ networkinfo.getTypeName() + "  subtypeid = "
										+ networkinfo.getSubtype() + "  subtypename = "
										+ networkinfo.getSubtypeName() + ")"*/;
						break;
					case TelephonyManager.NETWORK_TYPE_UNKNOWN :
					default :
						// unknow
						ret = "UNKNOW" /*+ "(typeid = " + networkinfo.getType() + "  typename = "
										+ networkinfo.getTypeName() + "  subtypeid = "
										+ networkinfo.getSubtype() + "  subtypename = "
										+ networkinfo.getSubtypeName() + ")"*/;
						break;
				}
			} else {
				ret = "UNKNOW" /*+ "(typeid = " + networkinfo.getType() + "  typename = "
								+ networkinfo.getTypeName() + ")"*/;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * 获取版本号
	 * 
	 * @param context
	 * @return
	 */
	public static String buildVersion(Context context) {
		String ret = "";
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			ret = pi.versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public static int buildVersionCode(Context context) {
		int ret = 0;
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			ret = pi.versionCode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}


	/**
	 * 获取设备分辨率
	 * 
	 * @param context
	 * @return
	 */
	public static String getDisplay(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wMgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wMgr.getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		return width + "*" + height;
	}


	/**
	 * <br>
	 * 功能简述: 获取手机号码 <br>
	 * 功能详细描述: <br>
	 * 注意: 不是所有的手机都可以获取手机号码
	 * 
	 * @param context
	 * @return
	 */
	public static String getPhoneNumber(Context context) {
		if (context != null) {
			// 创建电话管理
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			// 获取手机号码
			String phoneNumber = tm.getLine1Number();
			return phoneNumber;
		}
		return "";
	}


	/**获取apk的VersionCode
	 * @param absPath
	 * @param context
	 * @return
	 */
	public static int getAPKVersionCode(String absPath, Context context){
		PackageManager pm = context.getPackageManager();
		if (pm == null ||absPath == null){
			return -1;
		}
		PackageInfo pkgInfo = pm.getPackageArchiveInfo(absPath, PackageManager.GET_ACTIVITIES);
		if (pkgInfo != null){
			return pkgInfo.versionCode;
		}
		return -1;
	}


	/**对比apk的VersionCode返回是否要更新应用
	 * @param context
	 * @param apkVersionCode
	 * @return
	 */
	public static boolean needUpdate(Context context, int apkVersionCode){
		int code = buildVersionCode(context);
		return apkVersionCode != -1 && apkVersionCode > code && code != 0;
	}

}
