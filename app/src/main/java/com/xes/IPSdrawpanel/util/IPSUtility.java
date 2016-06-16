package com.xes.IPSdrawpanel.util;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;

public class IPSUtility {
    @SuppressWarnings("deprecation")
	public static boolean isRunningForeground (Context context)  
    {  
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);  
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;  
        String currentPackageName = cn.getPackageName();  
        if(!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals("com.xes.IPSdrawpanel"))  
        {  
            return true ;  
        }  
       
        return false ;  
    }  
    public static boolean isTopActivity(Activity activity) {
		String packageName = "xxxxx";
		ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
		if (tasksInfo.size() > 0) {
			System.out.println("---------------包名-----------" + tasksInfo.get(0).topActivity.getPackageName());
			// 应用程序位于堆栈的顶层
			if (packageName.equals(tasksInfo.get(0).topActivity.getPackageName())) {
				return true;
			}
		}
		return false;
	}
    //判断APP是否被杀死
    public static boolean isAppAlive(Context context){
    	 ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
    	    @SuppressWarnings("deprecation")
			List<RunningTaskInfo> list = am.getRunningTasks(100);
    	    for (RunningTaskInfo info : list) {
    	        if (info.topActivity.getPackageName().equals("com.xes.IPSdrawpanel") && info.baseActivity.getPackageName().equals("com.xes.IPSdrawpanel")) {
    	            return true;
    	        }
    	    }
    	    return false;
    }
}
