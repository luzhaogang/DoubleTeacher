package com.xes.IPSdrawpanel.activity;

import java.util.ArrayList;
import java.util.List;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.EMValueCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.util.EMLog;
import com.xes.IPSdrawpanel.DemoHXSDKHelper;
import com.xes.IPSdrawpanel.MyApplication;
import com.xes.IPSdrawpanel.R;
import com.xes.IPSdrawpanel.adapter.MyFragmentPagerAdapter;
import com.xes.IPSdrawpanel.dao.SubmitCorrectInfoDao;
import com.xes.IPSdrawpanel.fragment.PainterFragment1;
import com.xes.IPSdrawpanel.util.Utility;
import com.xes.IPSdrawpanel.widget.ZoomOutPageTransformer;
import com.xes.huanxin.myapplication.Constant;
import com.xes.huanxin.myapplication.HXSDKHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class MainActivity1 extends BaseActivity implements EMEventListener {
	private ViewPager mTabPager;
	private ArrayList<Fragment> fragmentList;
	private PainterFragment1 painterFragment1;
	private long exitTime = 0;
	private Boolean isRegistReceiver;
	private MainTaskMessageReceiver mainTaskMessageReceiver;
	private MyConnectionListener connectionListener = null;
	private android.app.AlertDialog.Builder conflictBuilder;
	private android.app.AlertDialog.Builder accountRemovedBuilder;
	// 用户被T掉
	private boolean isConflictDialogShow;
	// 用户被删除
	private boolean isAccountRemovedDialogShow;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_main);
		if (getIntent().getBooleanExtra("conflict", false) && !isConflictDialogShow) {
			showConflictDialog();
		} else if (getIntent().getBooleanExtra(Constant.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
			showAccountRemovedDialog();
		}
		initView();
		registMainTaskReceiver(this, mainTaskMessageReceiver);
	}

	private void initView() {
		mTabPager = (ViewPager) findViewById(R.id.tabpager);
		painterFragment1 = PainterFragment1.getPainterFragment1();
		fragmentList = new ArrayList<Fragment>();
		fragmentList.add(painterFragment1);
		MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
		mTabPager.setPageTransformer(true, new ZoomOutPageTransformer());
		mTabPager.setAdapter(myFragmentPagerAdapter);
		mainTaskMessageReceiver = new MainTaskMessageReceiver();
		initchat();
	}

	private void initchat() {
		// 注册一个监听连接状态的listener
		connectionListener = new MyConnectionListener();
		EMChatManager.getInstance().addConnectionListener(connectionListener);
	}

	private void registMainTaskReceiver(Activity mActivity, BroadcastReceiver socketReceiver) {
		if (socketReceiver != null) {
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(ACTION_UPDATE_MESSAGE);
			mActivity.registerReceiver(socketReceiver, intentFilter);
			isRegistReceiver = true;
		}
	}

	private void unMainTaskReceiver(BroadcastReceiver socketReceiver) {
		if (isRegistReceiver && socketReceiver != null) {
			try {
				unregisterReceiver(socketReceiver);
				isRegistReceiver = false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class MainTaskMessageReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (intent != null) {
				if (ACTION_UPDATE_MESSAGE.equals(intent.getAction())) {
					// 通知有新任务了 || 提交成功通知
					Log.e(Utility.LOG_TAG, "通知有新任务了");
					painterFragment1.getUnCorrectNumFromServer();
				}
			}
		}
	}

	/**
	 * 环信连接监听listener
	 * 
	 */
	public class MyConnectionListener implements EMConnectionListener {

		@Override
		public void onConnected() {
			boolean groupSynced = HXSDKHelper.getInstance().isGroupsSyncedWithServer();
			boolean contactSynced = HXSDKHelper.getInstance().isContactsSyncedWithServer();

			// in case group and contact were already synced, we supposed to
			// notify sdk we are ready to receive the events
			if (groupSynced && contactSynced) {
				new Thread() {
					@Override
					public void run() {
						HXSDKHelper.getInstance().notifyForRecevingEvents();
					}
				}.start();
			} else {
				if (!groupSynced) {
					asyncFetchGroupsFromServer();
				}

				if (!contactSynced) {
					asyncFetchContactsFromServer();
				}
				if (!HXSDKHelper.getInstance().isBlackListSyncedWithServer()) {
					asyncFetchBlackListFromServer();
				}
			}

			runOnUiThread(new Runnable() {

				@Override
				public void run() {

				}

			});
		}

		@Override
		public void onDisconnected(final int error) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (error == EMError.USER_REMOVED) {
						// 显示帐号已经被移除
						showAccountRemovedDialog();
					} else if (error == EMError.CONNECTION_CONFLICT) {
						// 显示帐号在其他设备登陆dialog
						showConflictDialog();
					} else {

					}
				}
			});
		}
	}

	/**
	 * 显示帐号在别处登录dialog
	 */
	private void showConflictDialog() {
		isConflictDialogShow = true;
		DemoHXSDKHelper.getInstance().logout(false, null);
		String st = getResources().getString(R.string.Logoff_notification);
		if (!MainActivity1.this.isFinishing()) {
			// clear up global variables
			try {
				if (conflictBuilder == null)
					conflictBuilder = new android.app.AlertDialog.Builder(MainActivity1.this);
				conflictBuilder.setTitle(st);
				conflictBuilder.setMessage(R.string.connect_conflict);
				conflictBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						conflictBuilder = null;
						DemoHXSDKHelper.getInstance().logout(true, null);
						finish();
						Utility.deleteALLDao();
						startActivity(new Intent(MainActivity1.this, LoginActivity.class));
						android.os.Process.killProcess(android.os.Process.myPid());
					}
				});
				conflictBuilder.setCancelable(false);
				conflictBuilder.create().show();
			} catch (Exception e) {
				EMLog.e(Utility.LOG_TAG, "---------color conflictBuilder error" + e.getMessage());
			}

		}

	}

	/**
	 * 帐号被移除的dialog
	 */
	private void showAccountRemovedDialog() {
		isAccountRemovedDialogShow = true;
		DemoHXSDKHelper.getInstance().logout(true, null);
		String st5 = getResources().getString(R.string.Remove_the_notification);
		if (!MainActivity1.this.isFinishing()) {
			// clear up global variables
			try {
				if (accountRemovedBuilder == null)
					accountRemovedBuilder = new android.app.AlertDialog.Builder(MainActivity1.this);
				accountRemovedBuilder.setTitle(st5);
				accountRemovedBuilder.setMessage("此用户已被移除");
				accountRemovedBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						accountRemovedBuilder = null;
						finish();
						startActivity(new Intent(MainActivity1.this, LoginActivity.class));
					}
				});
				accountRemovedBuilder.setCancelable(false);
				accountRemovedBuilder.create().show();
			} catch (Exception e) {
				EMLog.e(Utility.LOG_TAG, "---------color userRemovedBuilder error" + e.getMessage());
			}

		}

	}

	public static void asyncFetchGroupsFromServer() {
		HXSDKHelper.getInstance().asyncFetchGroupsFromServer(new EMCallBack() {

			@Override
			public void onSuccess() {
				HXSDKHelper.getInstance().noitifyGroupSyncListeners(true);

				if (HXSDKHelper.getInstance().isContactsSyncedWithServer()) {
					HXSDKHelper.getInstance().notifyForRecevingEvents();
				}
			}

			@Override
			public void onError(int code, String message) {
				HXSDKHelper.getInstance().noitifyGroupSyncListeners(false);
			}

			@Override
			public void onProgress(int progress, String status) {

			}

		});
	}

	static void asyncFetchContactsFromServer() {
		HXSDKHelper.getInstance().asyncFetchContactsFromServer(new EMValueCallBack<List<String>>() {
			@Override
			public void onSuccess(List<String> usernames) {

			}

			@Override
			public void onError(int error, String errorMsg) {
				HXSDKHelper.getInstance().notifyContactsSyncListener(false);
			}

		});
	}

	static void asyncFetchBlackListFromServer() {
		HXSDKHelper.getInstance().asyncFetchBlackListFromServer(new EMValueCallBack<List<String>>() {

			@Override
			public void onSuccess(List<String> value) {
				EMContactManager.getInstance().saveBlackList(value);
				HXSDKHelper.getInstance().notifyBlackListSyncListener(true);
			}

			@Override
			public void onError(int error, String errorMsg) {
				HXSDKHelper.getInstance().notifyBlackListSyncListener(false);
			}

		});
	}

	// 监听环信消息
	@Override
	public void onEvent(EMNotifierEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), R.string.exist_toast, Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				// android.os.Process.killProcess(android.os.Process.myPid());
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (getIntent().getBooleanExtra("conflict", false) && !isConflictDialogShow) {
			showConflictDialog();
		} else if (getIntent().getBooleanExtra(Constant.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
			showAccountRemovedDialog();
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		MyApplication.hxSDKHelper.pushActivity(this);
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		EMChatManager.getInstance().unregisterEventListener(this);
		MyApplication.hxSDKHelper.popActivity(this);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unMainTaskReceiver(mainTaskMessageReceiver);
		if (conflictBuilder != null) {
			conflictBuilder.create().dismiss();
			conflictBuilder = null;
		}
		if (connectionListener != null) {
			EMChatManager.getInstance().removeConnectionListener(connectionListener);
		}
		super.onDestroy();
	}
}
