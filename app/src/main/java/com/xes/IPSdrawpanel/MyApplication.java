package com.xes.IPSdrawpanel;

import android.app.AlarmManager;
import android.app.Application;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.samsung.android.sdk.pen.Spen;
import com.xes.IPSdrawpanel.receiver.SendResultMessageReceiver;
import com.xes.IPSdrawpanel.util.NetworkImageCache;
import com.xes.IPSdrawpanel.util.Utility;

import org.xutils.DbManager;
import org.xutils.DbManager.DaoConfig;
import org.xutils.DbManager.DbUpgradeListener;
import org.xutils.common.util.LogUtil;
import org.xutils.x;

import java.io.File;
import java.util.Date;

public class MyApplication extends Application {
	public static Context applicationContext;
	private static MyApplication instance;
	private static ImageLoader sImageLoader = null;
	public static RequestQueue mRequestQueue;
	private final NetworkImageCache imageCacheMap = new NetworkImageCache();
	public static String filePath;
	public static String YuyinFilePath;
	private final String dir = "IPSXESlive" + File.separator;
	private final String LuYinDir = "IPSXESliveLuYin" + File.separator;
	public static String currentUserNick = "";
	private static SharedPreferences prefs;
	private static DaoConfig daoConfig;
	public static AlarmManager am;
	public static PendingIntent pi;
	public static Boolean alarmManagerIson = false;
	private static Boolean isSpenFeatureEnabled;
	private static DownloadManager downloadManager;
	public static long timeDifference;
	public static DemoHXSDKHelper hxSDKHelper = new DemoHXSDKHelper();

	public static ImageLoader getImageLoader() {
		return sImageLoader;
	}

	public static SharedPreferences getSharedPreferences() {
		return prefs;
	}

	public static DaoConfig getDaoConfig() {
		return daoConfig;
	}

	public static Context getAppContext() {
		return applicationContext;
	}

	public static MyApplication getInstance() {
		return instance;
	}

	public static Resources getAppResources() {
		return getAppResources();
	}

	public static Boolean isSpenEnabled() {
		return isSpenFeatureEnabled;
	}

	public static DownloadManager getDownloadManager() {
		return downloadManager;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		applicationContext = this;
		instance = this;
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		mRequestQueue = Volley.newRequestQueue(this);
		sImageLoader = new ImageLoader(mRequestQueue, imageCacheMap);
		x.Ext.init(this);
		x.Ext.setDebug(true);
		EMChat.getInstance().setAutoLogin(false);
		hxSDKHelper.onInit(this);
		createDir();
		initTask();
		daoConfig = new DaoConfig()
				// 数据库的名字
				.setDbName("IPSdrawpanel")
				// 保存到指定路径
				// .setDbDir(new
				// File(Environment.getExternalStorageDirectory().getAbsolutePath()))
				// 数据库的版本号
				.setDbVersion(1)
				// 数据库版本更新监听
				.setDbUpgradeListener(new DbUpgradeListener() {
					@Override
					public void onUpgrade(DbManager arg0, int arg1, int arg2) {
						LogUtil.e("数据库版本更新了！");
					}
				});

		// 初始化手写笔
		Spen spenPackage = new Spen();
		try {
			spenPackage.initialize(this, 5, Spen.SPEN_STATIC_LIB_MODE);
			isSpenFeatureEnabled = spenPackage.isFeatureEnabled(Spen.DEVICE_PEN);
		} catch (Exception e1) {
			isSpenFeatureEnabled = false;
			Log.e(Utility.LOG_TAG, "Cannot initialize Spen.");
		}
		// EMChat.getInstance().setDebugMode(true);
		downloadManager = (DownloadManager) this.getSystemService(this.DOWNLOAD_SERVICE);
	}

	public void createDir() {
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			File externalpath = applicationContext.getExternalCacheDir() == null ? applicationContext.getFilesDir()
					: applicationContext.getExternalCacheDir();
			filePath = externalpath.getPath() + File.separator + dir;
			File file = new File(filePath);
			if (!file.exists())
				file.mkdir();

			YuyinFilePath = Environment.getExternalStorageDirectory().getPath() + File.separator + LuYinDir;
			File fileLuYin = new File(YuyinFilePath);
			if (!fileLuYin.exists())
				fileLuYin.mkdir();
			File fileimage = Utility.getStorageFile(applicationContext, "xUtils_cache");
			Utility.delete(fileimage);// 清除在线获取的图片
		}
	}

	private void initTask() {
		Intent intent = new Intent();
		String action = SendResultMessageReceiver.ACTION_SENDTASK;
		intent.setAction(action);
		pi = PendingIntent.getBroadcast(this, 0, intent, 0);
		am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		startSendTaskAlarm();
		// 发送获取地区通知
		Intent intentArea = new Intent();
		String Areaaction = SendResultMessageReceiver.ACTION_AREA;
		intentArea.setAction(Areaaction);
		applicationContext.sendBroadcast(intentArea);
	}

	public static void startSendTaskAlarm() {
		if (am != null) {
			am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 30 * 1000, pi); // 开启本地提交
		}
	}

	public static void stopAlarmManager() {
		am.cancel(pi);
	}

	/**
	 * 获取当前登陆用户名
	 *
	 * @return
	 */
	public String getUserName() {
		currentUserNick = hxSDKHelper.getHXId();
		return hxSDKHelper.getHXId();
	}

	/**
	 * 获取密码
	 *
	 * @return
	 */
	public String getPassword() {
		return hxSDKHelper.getPassword();
	}

	/**
	 * 设置用户名
	 *
	 * @param
	 */
	public void setUserName(String username) {
		hxSDKHelper.setHXId(username);
	}

	/**
	 * 设置密码 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference 环信sdk
	 * 内部的自动登录需要的密码，已经加密存储了
	 *
	 * @param pwd
	 */
	public void setPassword(String pwd) {
		hxSDKHelper.setPassword(pwd);
	}

	/**
	 * 退出登录,清空数据
	 */
	public void logout(final boolean isGCM, final EMCallBack emCallBack) {
		// 先调用sdk logout，在清理app中自己的数据
		hxSDKHelper.logout(isGCM, emCallBack);
	}

	public static void gettimeDifference(long org_server_tm, String server_timezone) {
		Date w_ret = Utility.DateTimeConvertToServer(new Date(), server_timezone);
		timeDifference = org_server_tm - w_ret.getTime();
	}
}
