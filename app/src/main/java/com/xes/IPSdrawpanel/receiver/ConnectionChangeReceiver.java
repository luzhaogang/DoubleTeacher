package com.xes.IPSdrawpanel.receiver;

import com.xes.IPSdrawpanel.MyApplication;
import com.xes.IPSdrawpanel.activity.BaseActivity;
import com.xes.IPSdrawpanel.util.Utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionChangeReceiver extends BroadcastReceiver {
	public static SharedPreferences prefs;

	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null) {
			//Intent intents = new Intent(BaseActivity.ACTION_UPDATE_MESSAGE);
			//context.sendBroadcast(intents);
			Intent intentDown = new Intent(SendResultMessageReceiver.ACTION_DOWNLOAD);
			context.sendBroadcast(intentDown);
			/*// 环信login 线上 xes123ttl 测试 为登陆密码
			Log.e(Utility.LOG_TAG, "ConnectionChangeReceiver ---- 断网重连，环信登陆----------------");
			EMChatManager.getInstance().login(TeacherBean.getInstance().teaId, TeacherBean.getInstance().password, new EMCallBack() {
				@Override
				public void onSuccess() {
					Log.e(Utility.LOG_TAG, "ConnectionChangeReceiver ---- 断网重连，环信登陆登陆成功");
					
					 * runOnUiThread(new Runnable() { public void run() {
					 * EMGroupManager.getInstance().loadAllGroups();
					 * EMChatManager.getInstance().loadAllConversations(); }
					 * });
					 
					Utility.showToastOnUi(MyApplication.getAppContext(), "登陆成功");
				}

				@Override
				public void onProgress(int progress, String status) {
					Log.e(Utility.LOG_TAG, "ConnectionChangeReceiver ---- 断网重连，环信登陆环信登陆中。。。。。。。。。。");
				}

				@Override
				public void onError(int code, String message) {
					Log.e(Utility.LOG_TAG, "ConnectionChangeReceiver ---- 断网重连，环信登陆环信登陆失败。。。。。。。。。。" + "code" + code + "message" + message);
					Utility.showToastOnUi(MyApplication.getAppContext(), "登陆失败");
				}
			});*/
		}else{
			Utility.showToast(MyApplication.getAppContext(), "网络已断开");
		}
	}
}