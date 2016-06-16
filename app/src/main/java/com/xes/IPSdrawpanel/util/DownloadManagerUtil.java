package com.xes.IPSdrawpanel.util;

import java.io.File;
import java.util.ArrayList;

import com.xes.IPSdrawpanel.MyApplication;

import android.app.DownloadManager;
import android.net.Uri;

public class DownloadManagerUtil {

	public static void updateStudentClient(ArrayList<String> list) {
		for (int i = 0; i < list.size(); i++) {
			String downloadUrl = list.get(i);
			String filename = Utility.stringToMd5(downloadUrl) + Utility.getFileExtension(downloadUrl);
			String saveFilepath = MyApplication.filePath + filename;
			File file = new File(saveFilepath);
			if (file != null && !file.exists()) {
				// 创建下载请求
				DownloadManager.Request down = new DownloadManager.Request(Uri.parse(downloadUrl));
				// 设置允许使用的网络类型，这里是wifi
				down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
				// Boolean swichDownload =
				// prefs.getBoolean("watchDownload",false);
				// if(swichDownload){
				//down.setShowRunningNotification(true);
				// down.setVisibleInDownloadsUi(true);
				//down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
				// }else{
				// 禁止发出通知，既后台下载
				down.setShowRunningNotification(false);
				// 不显示下载界面
				down.setVisibleInDownloadsUi(false);
				// }
				// 设置下载后文件存放的位置
				// down.setDestinationInExternalFilesDir(MyApplication.applicationContext,
				// null, saveFilepath);
				String path = MyApplication.filePath.substring(MyApplication.filePath.indexOf("0")+1,MyApplication.filePath.length());
				down.setDestinationInExternalPublicDir(path, filename);
				// 将下载请求放入队列
				MyApplication.getDownloadManager().enqueue(down);
			}
		}
	}
}
