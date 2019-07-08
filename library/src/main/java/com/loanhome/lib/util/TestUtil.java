package com.loanhome.lib.util;

public class TestUtil {

	private final static boolean DEBUG = true;

	
	public static boolean isDebug() {
		return DEBUG;
	}

	public static boolean isTestServer() {
		return TestUtil.isDebug() && FileUtil.isFileExist(Constants.Path.STARBABA_TEST_FILE_PATH);
	}


}
