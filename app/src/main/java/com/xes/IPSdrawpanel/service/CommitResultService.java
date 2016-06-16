package com.xes.IPSdrawpanel.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.xes.IPSdrawpanel.MyApplication;
import com.xes.IPSdrawpanel.api.impl.DrawInterfaceService;
import com.xes.IPSdrawpanel.bean.SubmitCorrectInfo;
import com.xes.IPSdrawpanel.util.Utility;

public class CommitResultService extends IntentService {
	
	public CommitResultService() {
		super("CommitResultService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent != null) {
			Bundle bdl = intent.getExtras();
			Log.e(Utility.LOG_TAG, "service提交中。。。。。");
			SubmitCorrectInfo submitCorrectInfo = (SubmitCorrectInfo) bdl.getSerializable("submitCorrectInfo");
			if(submitCorrectInfo.isBOSOK == 1){
				DrawInterfaceService.submitCorrectTask(this,submitCorrectInfo,MyApplication.filePath + Utility.stringToMd5(submitCorrectInfo.pictureUrl) + ".zip");
			}else{
				//BOS 提交失败,将文件提交给后台
				DrawInterfaceService.submitTaskFile(this,submitCorrectInfo,MyApplication.filePath + Utility.stringToMd5(submitCorrectInfo.pictureUrl) + ".zip");
			}
 			
		}
	}
}
