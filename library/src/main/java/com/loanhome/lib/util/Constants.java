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
	interface SharedPreferencesKey {
		String GETOCR_LISCENSE = "ocr_liscense";
	}


}
