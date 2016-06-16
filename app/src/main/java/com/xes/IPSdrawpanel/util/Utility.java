package com.xes.IPSdrawpanel.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import com.xes.IPSdrawpanel.MyApplication;
import com.xes.IPSdrawpanel.R;
import com.xes.IPSdrawpanel.bean.TeacherBean;
import com.xes.IPSdrawpanel.dao.ProblemBankDao;
import com.xes.IPSdrawpanel.dao.SubmitCorrectInfoDao;
import com.xes.IPSdrawpanel.widget.MaterialDialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint({ "ShowToast", "SimpleDateFormat" })
public class Utility {
	public static String LOG_TAG = "IPSdrawpanel-LOG";
	private static long lastClickTime;
	private static long intervalMinTime;
	private static long isonNewMsgTime;

	public static void setTextView(final Activity context, final TextView tvShow, final String html) {
		tvShow.setMovementMethod(ScrollingMovementMethod.getInstance());
		if (TextUtils.isEmpty(html) || "null".equalsIgnoreCase(html)) {
			tvShow.setVisibility(View.GONE);
		} else {
			tvShow.setVisibility(View.VISIBLE);
		}
		new AsyncTask<String, Integer, String>() {
			Spanned spanned;

			@Override
			protected String doInBackground(String... params) {
				spanned = Html.fromHtml(html, new ImageGetter() {
					@Override
					public Drawable getDrawable(String source) {
						BitmapDrawable drawable = null;
						Log.e("试题图片", source);
						Bitmap bitmap = ImageDownLoader.getImageDownLoader().showCacheBitmap(context, source);
						drawable = new BitmapDrawable(context.getResources(), bitmap);
						int scale = 2;
						if (getScreenPixel(context).widthPixels <= 480) {
							scale = 1;
						} else if (getScreenPixel(context).widthPixels <= 720) {
							scale = 2;
						} else if (getScreenPixel(context).widthPixels <= 1080) {
							scale = 3;
						}
						drawable.setBounds(0, 0, drawable.getIntrinsicWidth() * scale, drawable.getIntrinsicHeight() * scale);
						return drawable;

					}
				}, null);
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				tvShow.setText(trimTrailingWhitespace(spanned));
			}
		}.execute();
	}

	// 浠巃ssets璧勬簮涓幏鍙栧浘鐗�
	public static Bitmap getBitmap(Context context, String filename) {

		Bitmap image = null;
		AssetManager am = context.getResources().getAssets();
		try {

			InputStream is = am.open("img/" + filename);
			image = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}

	public static Bitmap getAssetsBitmap(Context context) {

		Bitmap image = null;
		AssetManager am = context.getResources().getAssets();
		try {

			InputStream is = am.open("load_progress.png");
			image = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}

	public static String changeTime1(String time) {
		Date date = new Date(Long.parseLong(time));
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(date);

	}
	public static String changeTImeNow(){
		Date date = new Date(getnowTime());
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(date);
	}
	public static String changeTimeMIN(String time) {
		Date date = new Date(Long.parseLong(time));
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return format.format(date);

	}

	public static String changeTimeToString(long diff) {
		long days = diff / (1000 * 60 * 60 * 24);
		long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
		long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
		if (days > 0) {
			return days + "天" + hours + "小时" + minutes + "分钟";
		} else if (hours > 0) {
			return hours + "小时" + minutes + "分钟";
		} else {
			return minutes + "分钟";
		}

	}

	public static CharSequence trimTrailingWhitespace(CharSequence source) {

		if (source == null)
			return "";

		int i = source.length();

		while (--i >= 0 && Character.isWhitespace(source.charAt(i))) {
		}

		return source.subSequence(0, i + 1);
	}

	public static DisplayMetrics getScreenPixel(Activity context) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm;
	}

	public static Drawable getCenterDrawable(int id, Context context) {
		Drawable centerDrawable = context.getResources().getDrawable(id);
		centerDrawable.setBounds(0, 0, centerDrawable.getIntrinsicHeight(), centerDrawable.getIntrinsicWidth());
		return centerDrawable;
	}

	public static int strNum(String str, String chars) {

		int counter = 0;
		if (str.indexOf(chars) == -1) {
			return 0;
		}
		while (str.indexOf(chars) != -1) {
			counter++;
			str = str.substring(str.indexOf(chars) + chars.length());
		}
		return counter;
	}

	public static String escapeCharacter(String str) {
		if (strNum(str, "<") != strNum(str, ">")) {
			str = str.replace("<br>", "$").replace("</br>", "$");
			str = str.replace("/>", "#");
			str = str.replace(">", "&gt;");
			str = str.replace("#", "/>");
			str = str.replace("<img", "#").replace("< img", "#");
			str = str.replace("<", "&lt;");
			str = str.replace("#", "<img");
			str = str.replace("$", "<br>");
		}

		return str;
	}

	public static void showToastOnUi(Context context, String str) {
		Looper.prepare();
		showToast(context, str);
		Looper.loop();
	}

	public static MaterialDialog showProgressbar(Context context, String str) {
		MaterialDialog mMaterialDialog = new MaterialDialog(context);
		if (mMaterialDialog != null) {
			View view = LayoutInflater.from(context).inflate(R.layout.progressbar_item, null);
			TextView progress_info = (TextView) view.findViewById(R.id.progress_info);
			if (!str.isEmpty()) {
				progress_info.setText(str);
			}
			mMaterialDialog.setBackgroundResource(android.R.color.transparent);
			mMaterialDialog.setView(view).show();
		}
		return mMaterialDialog;
	}

	public static void installApk(Context context, final File apk) {
		try {
			Intent install_intent = new Intent(Intent.ACTION_VIEW);
			install_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Uri uri = Uri.fromFile(apk);
			install_intent.setDataAndType(uri, "application/vnd.android.package-archive");
			context.startActivity(install_intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String stringToMd5(String str) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] resultByte = digest.digest(str.getBytes());
			StringBuffer sb = new StringBuffer();
			for (byte b : resultByte) {
				sb.append(String.format("%02x", b & 0xff));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return str;
	}

	// 隐藏键盘
	public static void hideInput(Context context, View view) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0); // 强制隐藏键盘

	}

	// toast
	public static void showToast(Context context, String str) {
		Toast.makeText(context, str + "", 500).show();
	}

	/**
	 * 判断某个界面是否在前台
	 * 
	 * @param context
	 * @param className
	 *            某个界面名称
	 */
	@SuppressWarnings("deprecation")
	public static boolean isForeground(Context context, String className) {
		if (context == null || TextUtils.isEmpty(className)) {
			return false;
		}

		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(1);
		if (list != null && list.size() > 0) {
			ComponentName cpn = list.get(0).topActivity;
			if (className.equals(cpn.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public static String secToTime(int time) {
		String timeStr = null;
		int hour = 0;
		int minute = 0;
		int second = 0;
		if (time <= 0)
			return "00:00:00";
		else {
			minute = time / 60;
			if (minute < 60) {
				second = time % 60;
				timeStr = "00" + ":" + unitFormat(minute) + ":" + unitFormat(second);
			} else {
				hour = minute / 60;
				if (hour > 99)
					return "99:59:59";
				minute = minute % 60;
				second = time - hour * 3600 - minute * 60;
				timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
			}
		}
		return timeStr;
	}

	public static String secTotime1(long Intime) {
		String timeStr = null;
		int day = 0;
		int hour = 0;
		int minute = 0;
		int second = 0;
		int time = (int) (Intime / 1000);
		if (time <= 0)
			return "00:00:00";
		else {
			minute = time / 60;
			if (minute < 60) {
				second = time % 60;
				if (minute == 0) {
					timeStr = second + "秒";
				} else {
					timeStr = minute + "分" + second + "秒";
				}
			} else {
				hour = minute / 60;
				if (hour >= 24) {
					day = hour / 24;
					hour = hour % 24;
					minute = minute % 60;
					second = time % 60;
					timeStr = day + "天" + hour + "小时" + minute + "分" + second + "秒";
				} else {
					minute = minute % 60;
					second = time % 60;
					timeStr = hour + "小时" + minute + "分" + second + "秒";
				}
			}
		}
		return timeStr;
	}

	public static String secToTimeWithoutHour(int time) {
		String timeStr = null;
		int hour = 0;
		int minute = 0;
		int second = 0;
		if (time <= 0)
			return "00:00";
		else {
			minute = time / 60;
			if (minute < 60) {
				second = time % 60;
				timeStr = unitFormat(minute) + ":" + unitFormat(second);
			} else {
				hour = minute / 60;
				if (hour > 99)
					return "99:59:59";
				minute = minute % 60;
				second = time - hour * 3600 - minute * 60;
				timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
			}
		}
		return timeStr;
	}

	public static String unitFormat(int i) {
		String retStr = null;
		if (i >= 0 && i < 10)
			retStr = "0" + Integer.toString(i);
		else
			retStr = "" + i;
		return retStr;
	}

	public static String getVersionName(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}
	//把图片压缩到100Kb
	public static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		int options = 100;
		while (baos.toByteArray().length / 1024 > 600) {
			if (options > 15) {
				baos.reset();
				options -= 15;
				image.compress(Bitmap.CompressFormat.JPEG, options, baos);
			} else {
				break;
			}
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
		if (image != null && !image.isRecycled()) {
			image.recycle();
			image = null;
		}
		return bitmap;
	}

	public static File createFile(String filename) {
		File file = new File(MyApplication.filePath + filename);
		try {
			if (!file.exists()) {
				file.createNewFile();
			} else {
				file.delete();
				file.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return file;
	}

	public static String md5(String encryptStr) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(encryptStr.getBytes("UTF-8"));
		byte[] digest = md.digest();
		StringBuffer md5 = new StringBuffer();
		for (int i = 0; i < digest.length; i++) {
			md5.append(Character.forDigit((digest[i] & 0xF0) >> 4, 16));
			md5.append(Character.forDigit((digest[i] & 0xF), 16));
		}

		encryptStr = md5.toString();
		return encryptStr;
	}
	//获取图片格式
	public static String getFileExtension(String path) {
		int separatorIndex = path.lastIndexOf(".");
		return (separatorIndex < 0) ? path : path.substring(separatorIndex, path.length());
	}

	public synchronized static boolean isFastClick() {
		long time = System.currentTimeMillis();
		if (time - lastClickTime < 500) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	public synchronized static boolean intervalMin() {
		long time = System.currentTimeMillis();
		if (time - intervalMinTime < 15 * 1000) {
			return true;
		}
		intervalMinTime = time;
		return false;
	}

	public synchronized static boolean isonNewMsg() {
		long time = System.currentTimeMillis();
		if (time - isonNewMsgTime < 6000) {
			return true;
		}
		isonNewMsgTime = time;
		return false;
	}

	// 获取ApiKey
	public static String getMetaValue(Context context, String metaKey) {
		Bundle metaData = null;
		String apiKey = null;
		if (context == null || metaKey == null) {
			return null;
		}
		try {
			ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			if (null != ai) {
				metaData = ai.metaData;
			}
			if (null != metaData) {
				apiKey = metaData.getString(metaKey);
			}
		} catch (NameNotFoundException e) {
			Log.e(Utility.LOG_TAG, "error " + e.getMessage());
		}
		return apiKey;
	}

	public static File getStorageFile(Context context, String fileName) {
		File dir = context.getExternalCacheDir() == null ? context.getFilesDir() : context.getExternalCacheDir();
		File file = new File(dir, fileName);
		return file;
	}

	public static void delete(File file) {
		if (file.isDirectory()) { 
			File[] childs = file.listFiles();
			for (File file2 : childs)
				delete(file2);
		}
		file.delete();
	}

	public static Boolean JsonArrayIsNOTNull(JSONObject json, String data) {
		String str = null;
		try {
			str = json.getString(data);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (!"null".equals(str) && str != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 永远以server为准
	 * 
	 * @return
	 */

	public static long getnowTime() {
		if (MyApplication.timeDifference != 0) {
			if (TeacherBean.getInstance().timeZone.isEmpty()) {
				TeacherBean.getInstance().timeZone = "Asia/Shanghai";
			}
			Date w_ret = DateTimeConvertToServer(new Date(), TeacherBean.getInstance().timeZone);
			w_ret.setTime(w_ret.getTime() + MyApplication.timeDifference);
			return w_ret.getTime();
		} else {
			return System.currentTimeMillis();
		}

	}

	/***
	 * 把当地的时间转为服务器当时的时间，一般递交数据前先把Date一类数据转换
	 * 
	 * @param tm
	 * @return
	 */
	public static Date DateTimeConvertToServer(Date tm, String server_timezone) {
		if (tm == null)
			return null;
		String timezone = TimeZone.getDefault().getID();
		if ("GMT".equals(timezone)) {
			timezone = timezone + "+0";
		}
		tm = new Date(tm.getTime() - getDiffTimeZoneRawOffsetStd(timezone)); // 转成格林威治时间
		Date d = new Date(tm.getTime() + getDiffTimeZoneRawOffsetStd(server_timezone));
		return d;
	}

	/***
	 * 计算出指定时区跟格林威治时间差
	 * 
	 * @param timeZoneId
	 * @return
	 */
	public static int getDiffTimeZoneRawOffsetStd(String timeZoneId) {
		// return TimeZone.getTimeZone(timeZoneId).getRawOffset();
		TimeZone tz = TimeZone.getTimeZone(timeZoneId);
		return tz.getOffset(GregorianCalendar.getInstance(tz).getTimeInMillis());
	}

	public static void deleteFile(String pictureUrl) {
		try {
			File filepic = new File(MyApplication.filePath + Utility.md5(pictureUrl) + Utility.getFileExtension(pictureUrl));
			if (filepic.exists()) {
				filepic.delete();
			}
			File filepic1 = new File(MyApplication.filePath + Utility.stringToMd5(pictureUrl) + ".jpg");
			if (filepic1.exists()) {
				filepic1.delete();
			}
			File filepic2 = new File(MyApplication.filePath + Utility.stringToMd5(pictureUrl) + ".amr");
			if (filepic2.exists()) {
				filepic2.delete();
			}
			File filepic3 = new File(MyApplication.filePath + Utility.stringToMd5(pictureUrl) + ".zip");
			if (filepic3.exists()) {
				filepic3.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//清除所有缓存
	public static void deleteALLDao(){
		SubmitCorrectInfoDao submitDao = new SubmitCorrectInfoDao();
		submitDao.deleteALL();
		ProblemBankDao ProDao = new ProblemBankDao();
		ProDao.deletePBALL();
	}
}
