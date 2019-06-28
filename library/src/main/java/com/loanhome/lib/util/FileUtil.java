package com.loanhome.lib.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 
 * @author liuheng
 *
 */
public class FileUtil {
	/**
	 * sdcard head
	 */
	public final static String SDCARD = Environment
			.getExternalStorageDirectory().getPath();

	private static String sANDROID_SECURE = "/mnt/sdcard/.android_secure";
	public static final String ROOT_PATH = "/";

	public static final String SDCARD_PATH = ROOT_PATH + "sdcard";

	/**
	 * 保存位图到sd卡目录下
	 * 
	 * @author huyong
	 * @param bitmap
	 *            ：位图资源
	 * @param filePathName
	 *            ：待保存的文件完整路径名
	 * @param iconFormat
	 *            ：图片格式
	 * @return true for 保存成功，false for 保存失败。
	 */
	public static boolean saveBitmapToSDFile(final Bitmap bitmap,
                                             final String filePathName, CompressFormat iconFormat) {
		boolean result = false;
		if (bitmap == null || bitmap.isRecycled()) {
			return result;
		}
		try {
			createNewFile(filePathName, false);
			OutputStream outputStream = new FileOutputStream(filePathName);
			result = bitmap.compress(iconFormat, 100, outputStream);
			outputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return result;
	}



	/**
	 * 保存数据到指定文件
	 * 
	 * @author huyong
	 * @param byteData
	 * @param filePathName
	 * @return true for save successful, false for save failed.
	 */
	public static boolean saveByteToSDFile(final byte[] byteData,
			final String filePathName) {
		boolean result = false;
		try {
			File newFile = createNewFile(filePathName, false);
			FileOutputStream fileOutputStream = new FileOutputStream(newFile);
			fileOutputStream.write(byteData);
			fileOutputStream.flush();
			fileOutputStream.close();
			result = true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * @author huyong
	 * @param path
	 *            ：文件路径
	 * @param append
	 *            ：若存在是否插入原文件
	 * @return
	 */
	public static File createNewFile(String path, boolean append) {
		File newFile = new File(path);
		if (!append) {
			if (newFile.exists()) {
				newFile.delete();
			} else {
				// 不存在，则删除带png后缀名的文件
				File prePngFile = new File(path + ".png");
				if (prePngFile != null && prePngFile.exists()) {
					prePngFile.delete();
				}
			}
		}
		if (!newFile.exists()) {
			try {
				File parent = newFile.getParentFile();
				if (parent != null && !parent.exists()) {
					parent.mkdirs();
				}
				newFile.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return newFile;
	}

	/**
	 * <br>
	 * 功能简述:创建文件 <br>
	 * 功能详细描述: <br>
	 * 注意:1：如果不存在父文件夹，则新建文件夹；2：如果文件已存在，则直接返回
	 * 
	 * @param destFileName
	 * @param replace
	 *            是否删除旧文件，生成新文件
	 * @return
	 */
	public static boolean createFile(String destFileName, boolean replace) {
		File file = new File(destFileName);
		if (file.exists()) {
			if (replace) {
				file.delete();
			} else {
				System.out.println("创建单个文件" + destFileName + "失败，目标文件已存在！");
				return false;
			}
		}
		if (destFileName.endsWith(File.separator)) {
			System.out.println("创建单个文件" + destFileName + "失败，目标不能是目录！");
			return false;
		}
		if (!file.getParentFile().exists()) {
			System.out.println("目标文件所在路径不存在，准备创建。。。");
			if (!file.getParentFile().mkdirs()) {
				System.out.println("创建目录文件所在的目录失败！");
				return false;
			}
		}
		// 创建目标文件
		try {
			if (file.createNewFile()) {
				System.out.println("创建单个文件" + destFileName + "成功！");
				return true;
			} else {
				System.out.println("创建单个文件" + destFileName + "失败！");
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("创建单个文件" + destFileName + "失败！");
			return false;
		}
	}

	/**
	 * sd卡是否可读写
	 * 
	 * @author huyong
	 * @return
	 */
	public static boolean isSDCardAvaiable() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}

	/**
	 * 指定路径文件是否存在
	 * 
	 * @author huyong
	 * @param filePath
	 * @return
	 */
	public static boolean isFileExist(String filePath) {
		boolean result = false;
		try {
			File file = new File(filePath);
			result = file.exists();
			file = null;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}

	/**
	 * 在媒体库中隐藏文件夹内的媒体文件 1. 加入.nomedia文件，使媒体功能扫描不到，用户可以通过文件浏览器方便看到 2.
	 * 在文件夹前面加点，隐藏整个文件夹，用户需要对文件浏览器设置显示点文件才能看到
	 * 
	 * @param folder
	 *            文件夹
	 */
	public static void hideMedia(final String folder) {
		File file = new File(folder);
		if (!file.exists()) {
			file.mkdirs();
		}

		file = new File(folder, ".nomedia");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		file = null;
	}

	/**
	 * 创建文件夹（如果不存在）
	 * 
	 * @param dir
	 */
	public static void mkDir(final String dir) {
		File file = new File(dir);
		if (!file.exists()) {
			try {
				file.mkdirs();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		file = null;
	}

	/**
	 * 在媒体库中显示文件夹内的媒体文件
	 * 
	 * @param folder
	 *            文件夹
	 */
	public static void showMediaInFolder(final String folder) {
		File file = new File(folder, ".nomedia");
		if (file.exists()) {
			try {
				file.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void copyFile(String srcStr, String decStr) {
		// 前提
		File srcFile = new File(srcStr);
		if (!srcFile.exists()) {
			return;
		}
		File decFile = new File(decStr);
		if (!decFile.exists()) {
			File parent = decFile.getParentFile();
			parent.mkdirs();

			try {
				decFile.createNewFile();

			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream(srcFile);
			output = new FileOutputStream(decFile);
			byte[] data = new byte[4 * 1024]; // 4k
			while (true) {
				int len = input.read(data);
				if (len <= 0) {
					break;
				}
				output.write(data, 0, len);
			}
		} catch (Exception e) {
		} finally {
			if (null != input) {
				try {
					input.close();
				} catch (Exception e2) {
				}
			}
			if (null != output) {
				try {
					output.flush();
					output.close();
				} catch (Exception e2) {
				}
			}
		}
	}
	
    // copy a file from srcFile to destFile, return true if succeed, return
    // false if fail
    public static boolean copyFile(File srcFile, File destFile) {
        boolean result = false;
        try {
            InputStream in = new FileInputStream(srcFile);
            try {
                result = copyToFile(in, destFile);
            } finally  {
                in.close();
            }
        } catch (IOException e) {
            result = false;
        }
        return result;
    }
    
    /**
     * Copy data from a source stream to destFile.
     * Return true if succeed, return false if failed.
     */
    public static boolean copyToFile(InputStream inputStream, File destFile) {
        try {
            if (destFile.exists()) {
                destFile.delete();
            }
            FileOutputStream out = new FileOutputStream(destFile);
            try {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                out.flush();
                try {
                    out.getFD().sync();
                } catch (IOException e) {
                }
                out.close();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

	/**
	 * 根据给定路径参数删除单个文件的方法 私有方法，供内部其它方法调用
	 * 
	 * @param filePath
	 *            要删除的文件路径
	 * @return 成功返回true,失败返回false
	 */
	public static boolean deleteFile(String filePath) {
		// 定义返回结果
		boolean result = false;
		// //判断路径参数是否为空
		// if(filePath == null || "".equals(filePath)) {
		// //如果路径参数为空
		// System.out.println("文件路径不能为空~！");
		// } else {
		// //如果路径参数不为空
		// File file = new File(filePath);
		// //判断给定路径下要删除的文件是否存在
		// if( !file.exists() ) {
		// //如果文件不存在
		// System.out.println("指定路径下要删除的文件不存在~！");
		// } else {
		// //如果文件存在，就调用方法删除
		// result = file.delete();
		// }
		// }

		if (filePath != null && !"".equals(filePath.trim())) {
			File file = new File(filePath);
			if (file.exists()) {
				result = file.delete();
			}
		}
		return result;
	}

	/*
	 * @param path 要删除的文件夹路径
	 * 
	 * @return 是否成功
	 */
	public static boolean deleteCategory(String path) {
		if (path == null || "".equals(path)) {
			return false;
		}

		File file = new File(path);
		if (!file.exists()) {
			return false;
		}

		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				deleteFile(f.getAbsolutePath());
			}
		}

		return file.delete();
	}

	public static boolean isNormalFile(String fullName) {
		return !fullName.equals(sANDROID_SECURE);
	}

	public static String getSdDirectory() {
		return Environment.getExternalStorageDirectory().getPath();
	}

	/*
	 * 采用了新的办法获取APK图标，之前的失败是因为android中存在的一个BUG,通过 appInfo.publicSourceDir =
	 * apkPath;来修正这个问题，详情参见:
	 * http://code.google.com/p/android/issues/detail?id=9151
	 */
	public static Drawable getApkIcon(Context context, String apkPath) {
		PackageManager pm = context.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(apkPath,
				PackageManager.GET_ACTIVITIES);
		if (info != null) {
			ApplicationInfo appInfo = info.applicationInfo;
			appInfo.sourceDir = apkPath;
			appInfo.publicSourceDir = apkPath;
			try {
				return appInfo.loadIcon(pm);
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String getExtFromFilename(String filename) {
		int dotPosition = filename.lastIndexOf('.');
		if (dotPosition != -1) {
			return filename.substring(dotPosition + 1, filename.length());
		}
		return "";
	}

	public static String getNameFromFilename(String filename) {
		int dotPosition = filename.lastIndexOf('.');
		if (dotPosition != -1) {
			return filename.substring(0, dotPosition);
		}
		return "";
	}

	public static String getPathFromFilepath(String filepath) {
		int pos = filepath.lastIndexOf('/');
		if (pos != -1) {
			return filepath.substring(0, pos);
		}
		return "";
	}

	public static String getNameFromFilepath(String filepath) {
		int pos = filepath.lastIndexOf('/');
		if (pos != -1) {
			return filepath.substring(pos + 1);
		}
		return "";
	}

	// storage, G M K B
	public static String convertStorage(long size) {
		long kb = 1024;
		long mb = kb * 1024;
		long gb = mb * 1024;

		if (size >= gb) {
			return String.format("%.1f GB", (float) size / gb);
		} else if (size >= mb) {
			float f = (float) size / mb;
			return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
		} else if (size >= kb) {
			float f = (float) size / kb;
			return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
		} else {
			return String.format("%d B", size);
		}
	}

	/**
	 * 
	 * <br>
	 * 类描述: <br>
	 * 功能详细描述:
	 * 
	 * @author Administrator
	 * @date [2012-12-20]
	 */
	/**
	 * 
	 * <br>
	 * 类描述: <br>
	 * 功能详细描述:
	 * 
	 */
	public static class SDCardInfo {
		public long total;

		public long free;
	}

	public static SDCardInfo getSDCardInfo() {
		String sDcString = android.os.Environment.getExternalStorageState();

		if (sDcString.equals(android.os.Environment.MEDIA_MOUNTED)) {
			File pathFile = android.os.Environment
					.getExternalStorageDirectory();

			try {
				android.os.StatFs statfs = new android.os.StatFs(
						pathFile.getPath());

				// 获取SDCard上BLOCK总数
				long nTotalBlocks = statfs.getBlockCount();

				// 获取SDCard上每个block的SIZE
				long nBlocSize = statfs.getBlockSize();

				// 获取可供程序使用的Block的数量
				long nAvailaBlock = statfs.getAvailableBlocks();

				// 获取剩下的所有Block的数量(包括预留的一般程序无法使用的块)
				long nFreeBlock = statfs.getFreeBlocks();

				SDCardInfo info = new SDCardInfo();
				// 计算SDCard 总容量大小MB
				info.total = nTotalBlocks * nBlocSize;

				// 计算 SDCard 剩余大小MB
				info.free = nAvailaBlock * nBlocSize;

				return info;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public static String formatDateString(Context context, long time) {
		DateFormat dateFormat = android.text.format.DateFormat
				.getDateFormat(context);
		DateFormat timeFormat = android.text.format.DateFormat
				.getTimeFormat(context);
		Date date = new Date(time);
		return dateFormat.format(date) + " " + timeFormat.format(date);
	}

	public static long getFileSize(File file) {
		return getFileSize(file, null);
	}

	public static long getFileSize(File file, FilenameFilter filter) {
		long size = 0;
		if (file != null && file.exists()) {
			if (!file.isDirectory()) {
				size = file.length();
			} else {
				File[] childFiles = file.listFiles(filter);
				if (childFiles != null) {
					int count = childFiles.length;
					for (int i = 0; i < count; i++) {
						size += getFileSize(childFiles[i]);
					}
				}
			}
		}
		return size;
	}

	public static byte[] getByteFromSDFile(final String filePathName) {
		byte[] bs = null;
		try {
			File newFile = new File(filePathName);
			FileInputStream fileInputStream = new FileInputStream(newFile);
			DataInputStream dataInputStream = new DataInputStream(
					fileInputStream);
			BufferedInputStream inPutStream = new BufferedInputStream(
					dataInputStream);
			bs = new byte[(int) newFile.length()];
			inPutStream.read(bs);
			fileInputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return bs;
	}

	/**
	 * <br>
	 * 功能简述:删除文件夹 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param sPath
	 * @return
	 */
	public static boolean deleteDirectory(String sPath) {
		// 如果sPath不以文件分隔符结尾，自动添加文件分隔符
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag) {
					break;
				}
			} // 删除子目录
			else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag) {
					break;
				}
			}
		}
		if (!flag) {
			return false;
		}
		// 删除当前目录
		return dirFile.delete();
	}

	public static String copyFileToApkCacheFromAssetOut(Context context,
                                                        String fileName) {
		InputStream is = null;
		FileOutputStream fos = null;
		// String apkFile = null;
		try {
			is = context.getAssets().open(fileName);
			// apkFile = createApkCachePath_out(context, fileName);
			// fos = new FileOutputStream(new File(apkFile));
			fos = context.openFileOutput(fileName, Context.MODE_WORLD_READABLE
					| Context.MODE_WORLD_WRITEABLE);
			byte[] buffer = new byte[1024];
			while (true) {
				int len = is.read(buffer);
				if (len == -1) {
					break;
				}
				fos.write(buffer, 0, len);
				fos.flush();
			}
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		File fileLocation = new File(context.getFilesDir(), fileName);

		return fileLocation.getPath();
	}

	private static String createApkCachePath(Context context, String fileName) {
		// 存放临时从assets目录中读取出来的dex文件的缓存目录
		final String apks_cache = "apks";
		String parentDir = context.getDir(apks_cache, Context.MODE_PRIVATE)
				.getAbsolutePath();
		String cacheName = null;
		File file = new File(parentDir);
		try {
			if (!file.exists()) {
				file.mkdirs();
			}
			file = new File(parentDir + File.separator + fileName);
			if (file.exists()) {
				file.delete();
				file.createNewFile();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (file != null) {
			cacheName = file.getAbsolutePath();
			Log.i("createPath", "============apk = " + cacheName);
		}
		return cacheName;
	}

	public static String copyFileToApkCacheFromAsset(Context context,
                                                     String fileName) {
		InputStream is = null;
		FileOutputStream fos = null;
		String apkFile = null;
		try {
			is = context.getAssets().open(fileName);
			apkFile = createApkCachePath(context, fileName);
			fos = new FileOutputStream(new File(apkFile));
			byte[] buffer = new byte[1024];
			while (true) {
				int len = is.read(buffer);
				if (len == -1) {
					break;
				}
				fos.write(buffer, 0, len);
				fos.flush();
			}
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return apkFile;
	}

	public static boolean checkFileInAssets(Context context, String toCheckFile) {
		AssetManager assetMgr = null;
		boolean isContainShellEngine = false;
		InputStream input = null;
		try {
			assetMgr = context.getAssets();
			if (assetMgr != null) {
				input = assetMgr.open(toCheckFile);
			}
			isContainShellEngine = true;
		} catch (Exception e) {
			e.printStackTrace();
			isContainShellEngine = false;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		Log.i("shellfactory", "=====sContainEngine = " + toCheckFile);
		return isContainShellEngine;
	}

	/**
	 * 统一获取raw文件流中数据
	 * 
	 * @param context
	 * @param rawId
	 * @return
	 */
	public static String getShortStrDataFromRaw(Context context, int rawId) {
		String strData = null;
		if (context == null) {
			return strData;
		}
		// 从资源获取流
		InputStream is = null;
		try {
			is = context.getResources().openRawResource(rawId);
			if (is != null) {
				byte[] buffer = new byte[128];
				int len = is.read(buffer); // 读取流内容
				if (len > 0) {
					strData = new String(buffer, 0, len).trim(); // 生成字符串
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return strData;
	}

	/**
	 *
	 * @param strPath
	 *            获取文件夹下文件的路径
	 * @return 文件夹下的文件列表
	 * @author zhangxi
	 * @date 2013-09-22
	 */
	public static ArrayList<String> getDirFiles(String strPath) {
		ArrayList<String> strFileList = new ArrayList<String>();
		try {
			File dirFile = new File(strPath);
			// 如果dir对应的文件不存在，或者不是一个目录，则退出
			if (!dirFile.exists() || !dirFile.isDirectory()) {
				return null;
			}

			File[] files = dirFile.listFiles();
			for (int i = 0; i < files.length; i++) {
				// 将文件夹下的文件返回，排除子文件夹
				if (files[i].isFile()) {
					strFileList.add(files[i].getAbsolutePath());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strFileList;
	}

	public static String readFileToString(String filePath) {
		if (filePath == null || "".equals(filePath)) {
			return null;
		}
		File file = new File(filePath);
		if (!file.exists()) {
			return null;
		}

		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			return readToString(inputStream, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 
	 * @param inputStream
	 * @param encoding
	 * @return
	 */
	public static String readToString(InputStream inputStream, String encoding) {

		InputStreamReader in = null;
		try {
			StringWriter sw = new StringWriter();
			in = new InputStreamReader(inputStream, encoding);
			copy(in, sw);
			return sw.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		return null;
	}

	private static int copy(Reader input, Writer output) throws IOException {
		char[] buffer = new char[1024 * 4];
		int count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	public static int copy(InputStream input, OutputStream output)
			throws IOException {
		byte[] buffer = new byte[1024 * 4];
		int count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	public static int getShort(byte[] data) {
		return (data[0] << 8) | data[1] & 0xFF;
	}

	public static String getUnZipString(byte[] data) {
		byte[] h = new byte[2];
		h[0] = (data)[0];
		h[1] = (data)[1];
		int head = getShort(h);
		boolean t = head == 0x1f8b;
		InputStream in;
		StringBuilder sb = new StringBuilder();
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			if (t) {
				in = new GZIPInputStream(bis);
			} else {
				in = bis;
			}
			BufferedReader r = new BufferedReader(new InputStreamReader(in,
					"utf-8"), 1000);
			for (String line = r.readLine(); line != null; line = r.readLine()) {
				sb.append(line);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static void unZipFile(String archive, String decompressDir)
			throws Exception {
		BufferedInputStream bufferedInputStream;
		ZipFile zf = new ZipFile(archive);
		Enumeration e = zf.entries();
		while (e.hasMoreElements()) {
			ZipEntry ze2 = (ZipEntry) e.nextElement();
			String entryName = ze2.getName();
			String path = decompressDir + File.separator + entryName;
			if (ze2.isDirectory()) {
				File decompressDirFile = new File(path);
				if (!decompressDirFile.exists()) {
					decompressDirFile.mkdirs();
				}
			} else {
				String fileDir = path.substring(0,
						path.lastIndexOf(File.separator));
				File fileDirFile = new File(fileDir);
				if (!fileDirFile.exists()) {
					fileDirFile.mkdirs();
				}
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(decompressDir + File.separator
								+ entryName));
				bufferedInputStream = new BufferedInputStream(
						zf.getInputStream(ze2));
				byte[] readContent = new byte[1024];
				int readCount = bufferedInputStream.read(readContent);
				while (readCount != -1) {
					bos.write(readContent, 0, readCount);
					readCount = bufferedInputStream.read(readContent);
				}
				bos.close();
			}
		}
		zf.close();
	}
	
	public static String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return filename;
	}

	public static String getFileNameNoEx(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return filename;
	}
	
	
	public static void writeFileToSD(String fileName, String src) {
		try {
			FileOutputStream fout = new FileOutputStream(fileName);
			byte[] bytes = src.getBytes();
			fout.write(bytes);
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读文件
	 * @param fileName
	 * @return
	 */
	public static String readTextFromFile(String fileName) {
		FileInputStream fis = null;
		String ret = "";
		try {
			fis = new FileInputStream(fileName);
			ret = convertCodeAndGetText(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}



	/**
	 * 根据不同编码转换流
	 * @param is
	 * @return
	 */
	public static String convertCodeAndGetText(InputStream is) {
		BufferedReader reader = null;
		String text = "";
		try {
			BufferedInputStream in = new BufferedInputStream(is);
			in.mark(4);
			byte[] first3bytes = new byte[3];
			in.read(first3bytes);
			in.reset();
			if (first3bytes[0] == (byte) 0xEF && first3bytes[1] == (byte) 0xBB
					&& first3bytes[2] == (byte) 0xBF) {
				reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
			} else if (first3bytes[0] == (byte) 0xFF
					&& first3bytes[1] == (byte) 0xFE) {
				reader = new BufferedReader(
						new InputStreamReader(in, "unicode"));
			} else if (first3bytes[0] == (byte) 0xFE
					&& first3bytes[1] == (byte) 0xFF) {
				reader = new BufferedReader(new InputStreamReader(in,
						"utf-16be"));
			} else if (first3bytes[0] == (byte) 0xFF
					&& first3bytes[1] == (byte) 0xFF) {
				reader = new BufferedReader(new InputStreamReader(in,
						"utf-16le"));
			} else {
				// TODO GBK处理
				reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
			}
			String str = reader.readLine();
			while (str != null) {
				text = text + str + "\n";
				str = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return text;
	}

	public static void writeStringToSD(String fileName, String content) {
		try {
			File file = new File(fileName);
			File parent = file.getParentFile();
			if (parent != null) {
				parent.mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			FileWriter writer = new FileWriter(fileName, true);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
