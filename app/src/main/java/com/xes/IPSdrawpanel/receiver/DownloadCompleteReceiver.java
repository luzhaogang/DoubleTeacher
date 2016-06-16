package com.xes.IPSdrawpanel.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.xes.IPSdrawpanel.api.impl.DrawInterfaceService;
import com.xes.IPSdrawpanel.bean.SubmitCorrectInfo;
import com.xes.IPSdrawpanel.dao.SubmitCorrectInfoDao;
import com.xes.IPSdrawpanel.util.Utility;

import java.io.File;

public class DownloadCompleteReceiver extends BroadcastReceiver {
	private DownloadManager downloadManager;
	private SubmitCorrectInfoDao scinfodao = new SubmitCorrectInfoDao();

	@Override
	public void onReceive(Context context, Intent intent) {
		downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
		if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
			long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
			//Log.e(Utility.LOG_TAG, "id : " + downId + "下载成功");
			queryDownloadStatus(downId);
		}
	}

	private void queryDownloadStatus(Long downId) {
		DownloadManager.Query query = new DownloadManager.Query();
		query.setFilterById(downId);
		Cursor c = downloadManager.query(query);
		if (c.moveToFirst()) {
			int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
			String path = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
			int reasonIdx = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON));
			String urlMd5;
			SubmitCorrectInfo scinfo = null;
			if (path != null) {
				urlMd5 = path.substring(path.lastIndexOf(File.separator) + 1, path.lastIndexOf("."));
				scinfo = scinfodao.getSubmitCorrectInfopictureUrlMD5(urlMd5);
			}
			switch (status) {
			case DownloadManager.STATUS_SUCCESSFUL:
				// 完成
				if (scinfo != null) {
					scinfo.cacheIsSUC = 1;
					scinfodao.updateSubmitCorrectInfo(scinfo);
				}
				//Log.e(Utility.LOG_TAG, " download complete! id : " + downId + "下载成功");
				//Log.e(Utility.LOG_TAG, " download complete! path : " + path);
				break;
			case DownloadManager.STATUS_FAILED:
				// 清除已下载的内容，重新下载
				Log.e(Utility.LOG_TAG, " download complete! reasonIdx : " + reasonIdx);
				Log.e(Utility.LOG_TAG, " download complete! path : " + path);
				if (reasonIdx != 404) {
					Log.e(Utility.LOG_TAG, "service缓存失败");
					if (scinfo != null) {
						scinfo.cacheIsSUC = 2;
						scinfodao.updateSubmitCorrectInfo(scinfo);
					}
				} else if (reasonIdx == 404) {
					Log.e(Utility.LOG_TAG, "找不到图片");
					if(scinfo != null){
						DrawInterfaceService.errTaskHandler(scinfo, "404");
					}
				}
				Log.e(Utility.LOG_TAG, downId + "STATUS_FAILED下载失败");
				removeDownload(downId);
				break;
			}
		}
		c.close();
	}

	// 移除停止下载
	private int removeDownload(long downloadId) {
		return downloadManager.remove(downloadId);
	}

	/*
	 * //删除文件 private void deleteDownloadFile(String path) { File file = new
	 * File(path); DownloadUtil.deleteOldFile(file);
	 * 
	 * }
	 */

}
