package com.xes.IPSdrawpanel.receiver;

import com.xes.IPSdrawpanel.activity.LoginActivity;
import com.xes.IPSdrawpanel.activity.MainActivity1;
import com.xes.IPSdrawpanel.util.IPSUtility;
import com.xes.IPSdrawpanel.util.Utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (IPSUtility.isAppAlive(context)) {
			 //如果存活的话，就直接启动DetailActivity，但要考虑一种情况，就是app的进程虽然仍然在
            //但Task栈已经空了，比如用户点击Back键退出应用，但进程还没有被系统回收，如果直接启动
            //DetailActivity,再按Back键就不会返回MainActivity了。所以在启动
            //DetailActivity前，要先启动MainActivity。
			Intent mainIntent = new Intent(context, LoginActivity.class);
			// 将MainAtivity的launchMode设置成SingleTask,
			// 或者在下面flag中加上Intent.FLAG_CLEAR_TOP,
			// 如果Task栈中有MainActivity的实例，就会把它移到栈顶，把在它之上的Activity都清理出栈，
			// 如果Task栈不存在MainActivity实例，则在栈顶创建
			mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Log.e(Utility.LOG_TAG, "进程未杀死，APP后台运行。。。。。。。。。");
			Intent detailIntent = new Intent(context, MainActivity1.class);
			Bundle bd = new Bundle();
			bd.putString("detail", "HAVEJOB");
			detailIntent.putExtras(bd);
			Intent[] intents = {mainIntent,detailIntent };
			
			context.startActivities(intents);
		} else {
			Log.e(Utility.LOG_TAG, "进程已杀死，APP重新启动。。。。。。。。。");
			Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.xes.IPSdrawpanel");
			launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			context.startActivity(launchIntent);
		}
	}
}