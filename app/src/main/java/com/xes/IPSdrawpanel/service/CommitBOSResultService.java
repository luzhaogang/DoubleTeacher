package com.xes.IPSdrawpanel.service;

import com.xes.IPSdrawpanel.api.impl.DrawInterfaceService;
import com.xes.IPSdrawpanel.bean.SubmitCorrectInfo;
import com.xes.IPSdrawpanel.dao.SubmitCorrectInfoDao;
import com.xes.IPSdrawpanel.util.Utility;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class CommitBOSResultService extends IntentService{
	private SubmitCorrectInfoDao submitCorrectDao;
	public CommitBOSResultService() {
		super("CommitBOSResultService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		if (intent != null) {
			Bundle bdl = intent.getExtras();
			Log.e(Utility.LOG_TAG, "BOSservice提交中。。。。。");
			SubmitCorrectInfo submitCorrectInfo = (SubmitCorrectInfo) bdl.getSerializable("submitCorrectInfo");
			submitCorrectDao = new SubmitCorrectInfoDao();
			if(submitCorrectInfo.pictureUrl == null || submitCorrectInfo.pictureUrl.isEmpty()){
				submitCorrectDao.deleteSubmitCorrectInfo(submitCorrectInfo);
				Log.e(Utility.LOG_TAG, "PICture为null 删除。。。。。");
				return ;
			}
 			if(DrawInterfaceService.uploadToBOS(this,Utility.stringToMd5(submitCorrectInfo.pictureUrl) + ".jpg",submitCorrectInfo)){
 				Intent Upintent = new Intent(this, CommitResultService.class);
				Bundle bd = new Bundle();
				bd.putSerializable("submitCorrectInfo", submitCorrectInfo);
				Upintent.putExtras(bd);
				this.startService(Upintent);
 			}else{
 				//uploadToBOS失败
 				SubmitCorrectInfo submitCorrectInfo1 = submitCorrectDao.getSubmitCorrectInfo(submitCorrectInfo.answerId);
 				if(submitCorrectInfo1 != null)
 				{
 					Intent Upintent = new Intent(this, CommitResultService.class);
 					Bundle bd = new Bundle();
 					bd.putSerializable("submitCorrectInfo", submitCorrectInfo1);
 					Upintent.putExtras(bd);
 					this.startService(Upintent);
 				}else{
 					//获取失败
 				} 				
 			}
		}
	}
	
}
