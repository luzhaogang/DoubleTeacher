package com.xes.IPSdrawpanel.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.xes.IPSdrawpanel.R;
import com.xes.IPSdrawpanel.activity.BaseActivity;
import com.xes.IPSdrawpanel.util.Utility;

public class GuideService extends Service {
	private FrameLayout guide;
	private ImageView guide_class,guide_correct,guide_online,guide_sendActivity,guide_fragment,guide_selectClass,guide_diss;
	private WindowManager wm;
	private LayoutParams params;
	
	@Override
	public void onCreate() {
		createSystemWindow();
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
		String	GuideName = intent.getStringExtra("GuideName");	
		changeView(GuideName);
		}
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		 removeSystemWindow();
		super.onDestroy();
	}

	private void removeSystemWindow() {
		wm.removeViewImmediate(guide);
	}
	
	
	private void createSystemWindow() {
		final LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		guide = (FrameLayout) inflator.inflate(R.layout.guide, null);
		guide_class = (ImageView) guide.findViewById(R.id.guide_class);
		guide_correct = (ImageView) guide.findViewById(R.id.guide_correct);
		guide_online = (ImageView) guide.findViewById(R.id.guide_online);
		guide_sendActivity = (ImageView) guide.findViewById(R.id.guide_sendActivity);
		guide_fragment = (ImageView) guide.findViewById(R.id.guide_sendFragment);
		guide_selectClass = (ImageView) guide.findViewById(R.id.guide_selectClass);
		guide_diss = (ImageView) guide.findViewById(R.id.guide_diss);
		// 设置LayoutParams参数
		params = new LayoutParams();
		// 设置显示的类型，TYPE_PHONE指的是来电话的时候会被覆盖，其他时候会在最前端，显示位置在stateBar下面，其他更多的值请查阅文档
		params.type = LayoutParams.TYPE_PHONE;
		// 设置显示格式
		// params.format = PixelFormat.RGBA_8888;
		params.alpha = 140;
		// 设置对齐方式
		// params.gravity = Gravity.LEFT | Gravity.TOP;
		// 设置宽高
		// params.width = ScreenUtils.getScreenWidth(this);
		// params.height = ScreenUtils.getScreenHeight(this);
		params.width = LayoutParams.MATCH_PARENT;
		params.height = LayoutParams.MATCH_PARENT;
		wm.addView(guide, params);
	}
	
	
	private  void changeView(String guideName){
		guide_class.setVisibility(View.GONE);
		guide_correct.setVisibility(View.GONE);
		guide_selectClass.setVisibility(View.GONE);
		guide_online.setVisibility(View.GONE);
		guide_sendActivity.setVisibility(View.GONE);
		guide_fragment.setVisibility(View.GONE);
		guide_diss.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intents = new Intent(BaseActivity.ACTION_GUIDE_DISSMISS);
				sendBroadcast(intents);
			}
		});
		if (("Activity").equals(guideName)) {
			Log.e(Utility.LOG_TAG, "Activity<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			guide_class.setVisibility(View.VISIBLE);
			//wm.addView(guide, params);
			guide_class.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					//wm.removeView(guide);
					Intent intents = new Intent(BaseActivity.ACTION_GUIDE_CLASS);
					intents.putExtra("name", "Activity");
					sendBroadcast(intents);
				}
			});
		} else if (("pigai").equals(guideName)) {
			//wm.addView(guide, params);
			guide_correct.setVisibility(View.VISIBLE);
			guide_correct.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					//wm.removeView(guide);
					Intent intents = new Intent(BaseActivity.ACTION_GUIDE_CLASS);
					intents.putExtra("name", "pigai");
					sendBroadcast(intents);
				}
			});
		} else if (("online").equals(guideName)) {
			//wm.addView(guide, params);
			guide_online.setVisibility(View.VISIBLE);
			guide_online.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					//wm.removeView(guide);
					Intent intents = new Intent(BaseActivity.ACTION_GUIDE_CLASS);
					intents.putExtra("name", "online");
					sendBroadcast(intents);
				}
			});
		}else if(("class").equals(guideName)){
			guide_selectClass.setVisibility(View.VISIBLE);
			//wm.addView(guide, params);
			guide_selectClass.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					//wm.removeView(guide);
					Intent intents = new Intent(BaseActivity.ACTION_GUIDE_CLASS);
					intents.putExtra("name", "class");
					sendBroadcast(intents);
				}
			});
		}else if(("selectclass").equals(guideName)){
			//wm.addView(guide, params);
			guide_sendActivity.setVisibility(View.VISIBLE);
			guide_sendActivity.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					//wm.removeView(guide);
					Intent intents = new Intent(BaseActivity.ACTION_GUIDE_CLASS);
					intents.putExtra("name", "selectclass");
					sendBroadcast(intents);
				}
			});
		}else if(("sendclass").equals(guideName)){
			guide_fragment.setVisibility(View.VISIBLE);
			wm.removeView(guide);
			wm.addView(guide, params);
			Log.e(Utility.LOG_TAG, "sendclass<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			guide_fragment.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					//wm.removeView(guide);
					/*Intent intents = new Intent(MainTaskFragment.ACTION_GUIDE_CLASS);
					intents.putExtra("name", "sendclass");
					sendBroadcast(intents);*/
				}
			});
		}
	}

	
}
