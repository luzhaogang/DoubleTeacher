package com.xes.IPSdrawpanel.activity;

import android.os.Bundle;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.exceptions.EaseMobException;
import com.xes.IPSdrawpanel.MyApplication;
import com.xes.IPSdrawpanel.R;
import com.xes.IPSdrawpanel.fragmentStackapi.RootActivity;
import com.xes.IPSdrawpanel.util.Utility;
import com.xes.huanxin.myapplication.HXSDKHelper;


public class ContentActivity extends RootActivity implements EMEventListener {

	
	   @Override
    public void onCreateNow(Bundle savedInstanceState) {
	        setAnim(R.anim.next_in, R.anim.next_out, R.anim.quit_in, R.anim.quit_out);
	}
	  
		@Override
		protected void onResume() {
			EMChatManager.getInstance().registerEventListener(this,
					new EMNotifierEvent.Event[] { EMNotifierEvent.Event.EventNewMessage});
			super.onResume();
		}
	
	@Override
	protected void onStart() {
		MyApplication.hxSDKHelper.pushActivity(this);
		super.onStart();
	}
	@Override
	protected void onStop() {
		EMChatManager.getInstance().unregisterEventListener(this);
		MyApplication.hxSDKHelper.popActivity(this);
		HXSDKHelper.getInstance().getNotifier().reset();
		super.onStop();
	}

	@Override
	public void onEvent(EMNotifierEvent event) {
		switch (event.getEvent()) {
		case EventNewMessage: // 普通消息
		{	
			EMMessage message = (EMMessage) event.getData();
			if(Utility.isonNewMsg()){
				return;
			}
			try {
				final String userNick = message.getStringAttribute(BaseActivity.CUSTOM_KEY_USERNAME);
				if(message.getChatType()==ChatType.GroupChat){
					  if(!MyApplication.getSharedPreferences().getBoolean("isblockGroupMessage", false)){
						  runOnUiThread(new Runnable() {
								public void run() {
									Utility.showToast(getApplicationContext(), userNick+"来消息了");		
								}
							});
						}
					}else{
						runOnUiThread(new Runnable() {
							public void run() {
								Utility.showToast(getApplicationContext(), userNick+"来消息了");		
							}
						});
					}	
			} catch (EaseMobException e) {
				e.printStackTrace();
			}
			//HXSDKHelper.getInstance().getNotifier().onNewMsg(message);	
			}
			break;
		}	
		
	}

	
}
