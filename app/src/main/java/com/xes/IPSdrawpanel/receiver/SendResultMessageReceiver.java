package com.xes.IPSdrawpanel.receiver;

import java.util.List;

import com.xes.IPSdrawpanel.MyApplication;
import com.xes.IPSdrawpanel.api.impl.DrawInterfaceService;
import com.xes.IPSdrawpanel.bean.SubmitCorrectInfo;
import com.xes.IPSdrawpanel.dao.SubmitCorrectInfoDao;
import com.xes.IPSdrawpanel.service.CacheService;
import com.xes.IPSdrawpanel.service.CommitResultService;
import com.xes.IPSdrawpanel.util.Utility;
import com.xes.IPSdrawpanel.util.WifiUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

public class SendResultMessageReceiver extends BroadcastReceiver {

	public static final String ACTION_OK = "com.xes.IPSdrawpanel.activity.ok";
	public static final String ACTION_ERRO = "com.xes.IPSdrawpanel.activity.erro";
	public static final String ACTION_SENDTASK = "com.xes.IPSdrawpanel.activity.sendtask";
	public static final String ACTION_DOWNLOAD = "com.xes.IPSdrawpanel.activity.download";
	public static final String ACTION_AREA = "com.xes.IPSdrawpanel.activity.area";
	public SubmitCorrectInfoDao scinfoDao = new SubmitCorrectInfoDao();

	@Override
	public void onReceive(final Context context, Intent intents) {
		if (intents != null) {
			if (ACTION_OK.equals(intents.getAction())) {
				Utility.showToast(context, "作业提交成功");
				//Intent intentACTION_OK = new Intent(BaseActivity.ACTION_UPDATE_MESSAGE);
				//context.sendBroadcast(intentACTION_OK);
			}
			if (ACTION_ERRO.equals(intents.getAction())) {
				Bundle bdl = intents.getExtras();
				Log.e(Utility.LOG_TAG, "作业提交失败"+bdl.getString("msg"));
			}

			if (ACTION_SENDTASK.equals(intents.getAction())) {
				// 判断网络是否断开
				if (!WifiUtil.isWiFiActive(context)) {
					Log.e(Utility.LOG_TAG, "后台重新发送任务网络已经断开");
					return;
				}
				SubmitCorrectInfo sb = scinfoDao.getSubmitCorrectInfo();
				if (sb != null) {
					Log.e(Utility.LOG_TAG, "后台重新发送任务。。");
					Intent intent = new Intent(context, CommitResultService.class);
					Bundle bd = new Bundle();
					bd.putSerializable("submitCorrectInfo", sb);
					intent.putExtras(bd);
					context.startService(intent);
				} else {
					Log.e(Utility.LOG_TAG, "已没有可提交任务");
				}
				List<SubmitCorrectInfo> sbs = scinfoDao.getSubmitCorrectInfoCacheIsSUC();
				if (sbs != null) {
					Log.e(Utility.LOG_TAG, "未缓存成功的数量" + sbs.size());
					for (SubmitCorrectInfo si : sbs) {
						Intent intent = new Intent(context, CacheService.class);
						intent.putExtra("stuAnswer", si.stuAnswer);
						intent.putExtra("answerId", si.answerId);
						context.startService(intent);
					}
				}
			}
			//同步服务器 登陆地区
			if(ACTION_AREA.equals(intents.getAction())){
				new Thread(new Runnable() {
					@Override
					public void run() {
						DrawInterfaceService.getAreaTask();
					}
				}).start();
			}
			if (ACTION_DOWNLOAD.equals(intents.getAction())) {
				Log.e(Utility.LOG_TAG, "收到缓存通知，开始线程任务 befor Utility.intervalMin()------------------");
				if (Utility.intervalMin()) {
					return;
				}
				Log.e(Utility.LOG_TAG, "收到缓存通知，开始线程任务------------------");
				new Thread(new Runnable() {
					@Override
					public void run() {
						SharedPreferences prefs = MyApplication.getSharedPreferences();
						DrawInterfaceService.getCacheCorrectTask(context, prefs.getString("teaId", ""));
					}
				}).start();
			}
			if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intents.getAction())) {
				ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
				Log.e(Utility.LOG_TAG, "网络发生改变。。。。");
				if (activeNetInfo != null) {
				}
			}
			
		}
	}
}
