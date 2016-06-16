package com.xes.IPSdrawpanel.service;

import java.io.File;
import java.io.IOException;

import com.xes.IPSdrawpanel.MyApplication;
import com.xes.IPSdrawpanel.bean.SubmitCorrectInfo;
import com.xes.IPSdrawpanel.dao.SubmitCorrectInfoDao;
import com.xes.IPSdrawpanel.util.OkHttpUtil;
import com.xes.IPSdrawpanel.util.OkHttpUtil.OkDownloadRequestListener;
import com.xes.IPSdrawpanel.util.Utility;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class CacheService extends IntentService {

	private SubmitCorrectInfoDao scid;

	public CacheService() {
		super("CacheService");
	}

	@Override
	public void onCreate() {
		scid = new SubmitCorrectInfoDao();
		super.onCreate();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent != null) {
			String stuAnswer = intent.getStringExtra("stuAnswer");
			final String answerId = intent.getStringExtra("answerId");
			Log.e(Utility.LOG_TAG, "service缓存中。。。。。");
			String[] str = stuAnswer.split(",");
			File file = null;
			if (str.length > 0) {
				file = new File(MyApplication.filePath + Utility.stringToMd5(str[0]) + Utility.getFileExtension(str[0]));
			}
			if (file != null && !file.exists()) {
				try {
					OkHttpUtil.getInstance().downloadFile(stuAnswer, new OkDownloadRequestListener() {
						@Override
						public void onSuccess(String response) {
							SubmitCorrectInfo scifo = scid.getSubmitCorrectInfo(answerId);
							scifo.cacheIsSUC = 1;
							scid.updateSubmitCorrectInfo(scifo);
							Log.e(Utility.LOG_TAG, "service缓存成功。。。。。未批改数量" + scid.getSubmitCorrectInfoNum());
							// DrawInterfaceService.modifyCacheStatus(workTaskId);
						}

						@Override
						public void onErrorCode(int code, String errorMsg) {
							if (code != 404) {
								Log.e(Utility.LOG_TAG, "service缓存失败");
								SubmitCorrectInfo scifo = scid.getSubmitCorrectInfo(answerId);
								scifo.cacheIsSUC = 2;
								scid.updateSubmitCorrectInfo(scifo);
							} else if (code == 404) {
								Log.e(Utility.LOG_TAG, "找不到图片");
							}
							// scid.deleteSubmitCorrectInfoById(workTaskId);
						}
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}
}
