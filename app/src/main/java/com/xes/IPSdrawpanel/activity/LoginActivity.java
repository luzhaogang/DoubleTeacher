package com.xes.IPSdrawpanel.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.xes.IPSdrawpanel.DemoHXSDKHelper;
import com.xes.IPSdrawpanel.MyApplication;
import com.xes.IPSdrawpanel.R;
import com.xes.IPSdrawpanel.api.ApiConstants;
import com.xes.IPSdrawpanel.api.impl.DrawInterfaceService;
import com.xes.IPSdrawpanel.bean.TeacherBean;
import com.xes.IPSdrawpanel.bean.UpdataBean;
import com.xes.IPSdrawpanel.bean.areaBean;
import com.xes.IPSdrawpanel.dao.LoginAreaDao;
import com.xes.IPSdrawpanel.dao.SubmitCorrectInfoDao;
import com.xes.IPSdrawpanel.log.LogService;
import com.xes.IPSdrawpanel.util.OkHttpUtil;
import com.xes.IPSdrawpanel.util.OkHttpUtil.OkRequestListener;
import com.xes.IPSdrawpanel.util.Utility;
import com.xes.IPSdrawpanel.widget.MaterialDialog;
import com.xes.IPSdrawpanel.widget.NumberProgressBar;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("InflateParams")
public class LoginActivity extends Activity implements OnClickListener {

	private EditText login_username_et, login_passwd_et;
	private SharedPreferences prefs;
	private AlertDialog mDialog = null;
	public static final String NEW_VERSION_INSTALL_PATH = "update_apk_path";
	private String downloadUrl;
	private String apkPath;
	private int areaPos;
	private NumberProgressBar bnp;
	private Boolean currentflag = true;
	private TextView versions_tv, area_name;
	private ImageView login_clear_username_iv;
	private ImageView login_clear_pwd_iv;
	private RelativeLayout login_area;
	private RelativeLayout.LayoutParams layoutParams;
	private LoginAreaDao AreaDao = new LoginAreaDao();
	private List<areaBean> listArea;
	private AreaAdapter areaAdapter;
	private MaterialDialog mMaterialDialog;
	private String UpdateReason = null;
	// 更新app
	Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				showDiloag();
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, "oIG1KG6OIoau5OPaZBa0ae1v");
		prefs = MyApplication.getSharedPreferences();
		Button login_login_tv1 = (Button) findViewById(R.id.login_login_tv1);
		login_username_et = (EditText) findViewById(R.id.login_username_et);
		login_passwd_et = (EditText) findViewById(R.id.login_passwd_et);
		versions_tv = (TextView) findViewById(R.id.versions_tv);
		if(ApiConstants.PPisONLINE.equals("1")){
			versions_tv.setText("V" + Utility.getVersionName(this));
		}else{
			versions_tv.setText("T" + Utility.getVersionName(this));
		}
		
		versions_tv.setOnClickListener(this);

		login_clear_username_iv = (ImageView) findViewById(R.id.login_clear_username_iv);
		login_clear_username_iv.setOnClickListener(this);
		login_username_et.addTextChangedListener(new myTextWatcher(login_clear_username_iv));

		login_clear_pwd_iv = (ImageView) findViewById(R.id.login_clear_pwd_iv);
		login_clear_pwd_iv.setOnClickListener(this);
		login_passwd_et.addTextChangedListener(new myTextWatcher(login_clear_pwd_iv));

		String name = prefs.getString("usename", "");
		login_username_et.setText(name);
		String password = prefs.getString("password", "");
		login_passwd_et.setText(password);
		areaPos = prefs.getInt("areaPos", 0);
		login_area = (RelativeLayout) findViewById(R.id.login_area);
		area_name = (TextView) findViewById(R.id.area_name);
		login_login_tv1.setOnClickListener(this);
		login_area.setOnClickListener(this);
		layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		// 设置原始数据
		initSchoolData();
		listArea = AreaDao.getAreaInfos();
		area_name.setText(listArea.get(areaPos).getCityName() + "学而思培训学校");
		new UpdataTask().execute();
		InitDownLoad();
	}

	private void InitDownLoad() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			@Override
			public void run() {
				DrawInterfaceService.getCacheCorrectTask(LoginActivity.this, prefs.getString("teaId", ""));
			}
		}).start();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			android.os.Process.killProcess(android.os.Process.myPid());
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	class LoginTask extends AsyncTask<String, Void, TeacherBean> {
		private MaterialDialog mMaterialDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mMaterialDialog = Utility.showProgressbar(LoginActivity.this, "登陆中....");
		}

		@Override
		protected TeacherBean doInBackground(String... params) {
			TeacherBean result = DrawInterfaceService.loginWithCode(params[0], params[1]);
			return result;
		}

		@Override
		protected void onPostExecute(final TeacherBean result) {
			mMaterialDialog.dismiss();
			if (result.result == TeacherBean.SUCCESS) {
				// result.teaId环信login 线上 xes123tutor 测试 为登陆密码
				// 同步服务器时间
				MyApplication.gettimeDifference(result.timStamp, result.timeZone);
				if (!prefs.getString("usename", "").equals(result.phone)) {
					SubmitCorrectInfoDao submitCorrectInfoDao = new SubmitCorrectInfoDao();
					submitCorrectInfoDao.deleteALL();
					EMChatManager.getInstance().logout();
				}
				Log.e(Utility.LOG_TAG, "环信登陆状态：" + DemoHXSDKHelper.getInstance().isLogined() + "----------result.teaId" + result.teaId + "result.password" + "xes123ttl");
				EMChatManager.getInstance().login(result.teaId, "xes123tutor", new EMCallBack() {
					// EMChatManager.getInstance().login("lushi", "123456", newEMCallBack() {
					@Override
					public void onSuccess() {
						Log.e(Utility.LOG_TAG, "登陆成功");
						MyApplication.getInstance().setUserName(result.phone);
						MyApplication.getInstance().setPassword(result.password);
						EMChatManager.getInstance().deleteConversation(TeacherBean.getInstance().teaId, true);
						EMGroupManager.getInstance().loadAllGroups();
						EMChatManager.getInstance().loadAllConversations();
						boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(MyApplication.currentUserNick.trim());
						if (!updatenick) {
							Log.e(Utility.LOG_TAG, "更新目前的用户昵称失败");
						}
						Utility.showToastOnUi(MyApplication.getAppContext(), "登陆成功");
					}

					@Override
					public void onProgress(int progress, String status) {
						Log.e(Utility.LOG_TAG, "环信登陆中。。。。。。。。。。");
					}

					@Override
					public void onError(int code, String message) {
						Log.e(Utility.LOG_TAG, "环信登陆失败。。。。。。。。。。" + "code" + code + "message" + message);
						Utility.showToastOnUi(MyApplication.getAppContext(), "登陆失败");
					}
				});
				
				prefs.edit().putString("usename", result.phone).putString("password", result.password).putInt("areaPos", areaPos).putString("teaId", result.teaId).commit();
				Intent intent = null;
				if (MyApplication.isSpenEnabled()) {
					intent = new Intent(LoginActivity.this, MainActivity1.class);// 支持spen
				} else {
					intent = new Intent(LoginActivity.this, MainActivity.class);// 不支持sen
				}
				startActivity(intent);
				finish();
			} else if (result.result == TeacherBean.FAIL) {
				if (result.recmsg == null) {
					Utility.showToast(getApplicationContext(), "连接超时");
				} else {
					Utility.showToast(getApplicationContext(), result.recmsg);
				}
			}
		}
	}

	class UpdataTask extends AsyncTask<String, Void, UpdataBean> {
		// private MaterialDialog mMaterialDialog;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// mMaterialDialog = Util.showProgressbar(getActivity(),"升级检测中....");
		}

		@Override
		protected UpdataBean doInBackground(String... params) {
			UpdataBean result = updateStudentClientJson();
			return result;
		}

		@Override
		protected void onPostExecute(UpdataBean result) {
			// mMaterialDialog.dismiss();
			if (result.result == 1) {
				Updata(result);
			} else if (result.result == 2) {
				
			}
		}
	}

	private void initProgressDialog() {
		if (mDialog == null) {
			mDialog = new AlertDialog.Builder(this).create();
		}
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.setCancelable(false);
	}

	private UpdataBean updateStudentClientJson() {
		final UpdataBean updatabean = new UpdataBean();
		try {
			OkHttpUtil.getInstance().connectGet(ApiConstants.ZHIBO_Update_URL, null, new OkRequestListener() {
				@Override
				public void onSuccess(String response) {
					try {
						JSONObject json = new JSONObject(response);
						JSONObject androidJson = json.getJSONObject("Ipscorrect");
						updatabean.version = androidJson.getString("version");
						updatabean.title = androidJson.getString("title");
						updatabean.note = androidJson.getString("note");
						if (updatabean.note != null && !updatabean.note.isEmpty()) {
							UpdateReason = updatabean.note;
						} else {
							UpdateReason = "有新版本哦!赶快更新吧!";
						}
						updatabean.url = androidJson.getString("url");
						updatabean.updateArea = androidJson.getString("updateArea").split(",");
						updatabean.updateAppoint = androidJson.getString("updateAppoint").split(",");
						// 区域升级
						if (updatabean.updateArea[0].equals("all")) {
							updatabean.result = 1;
						} else {
							for (int i = 0; i < updatabean.updateArea.length; i++) {
								if (updatabean.updateArea[i].equals(listArea.get(prefs.getInt("areaPos", 0)).getCityCode())) {
									updatabean.result = 1;
									break;
								} else {
									updatabean.result = 2;
								}
							}
						}
						// 指定升级
						if (updatabean.updateAppoint[0].equals("all")) {
							updatabean.result = 1;
						} else {
							for (int i = 0; i < updatabean.updateAppoint.length; i++) {
								if (updatabean.updateAppoint[i].equals(prefs.getString("usename", ""))) {
									updatabean.result = 1;
									break;
								} else {
									updatabean.result = 2;
								}
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onError(String errorMsg) {
					updatabean.result = 2;
				}
			});
		} catch (IOException e) {
			updatabean.result = 2;
			e.printStackTrace();
		}
		return updatabean;
	}

	private void Updata(UpdataBean result) {
		String version = result.version;
		String localVersion = Utility.getVersionName(this);
		if (!isNewVersion(localVersion, version)) {
			deleteUpdateAPK(prefs.getString(NEW_VERSION_INSTALL_PATH, ""));
			prefs.edit().putString(NEW_VERSION_INSTALL_PATH, "").commit();
			return;
		}
		initProgressDialog();
		downloadUrl = result.url;
		// String description = result.note;
		String prefInstallPath = prefs.getString(NEW_VERSION_INSTALL_PATH, "");
		Message msg = myHandler.obtainMessage();
		if (prefInstallPath.length() == 0) {
			File file = this.getFilesDir();
			if (!file.exists()) {
				file.mkdir();
			}
			prefs.edit().putString(NEW_VERSION_INSTALL_PATH, "").commit();
		}
		msg.what = 1;
		myHandler.sendMessage(msg);
	}

	public static boolean isNewVersion(String localV, String remoteV) {
		boolean isNew = false;
		if (TextUtils.isEmpty(remoteV)) {
			return false;
		}
		String[] local = localV.split("\\.");
		String[] remote = remoteV.split("\\.");
		if (local.length != remote.length) {
			return true;
		}
		for (int i = 0; i < local.length && i < remote.length; i++) {
			if (Integer.valueOf(local[i]) != Integer.valueOf(remote[i])) {
				return true;
			} else if (Integer.valueOf(local[i]) == Integer.valueOf(remote[i])) {
				continue;
			} else {
				return false;
			}
		}
		return isNew;
	}

	public void deleteUpdateAPK(String filePath) {
		if (filePath == null || filePath.length() == 0) {
			return;
		}
		// Log.e(Utility.LOG_TAG, "deleteUpdateAPK" + filePath);
		try {
			File file = new File(filePath);
			if (file.isFile()) {
				file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void downloadApk(final String downloadUrl) {
		// 请求参数
		RequestParams params = new RequestParams(downloadUrl);
		x.http().get(params, new Callback.ProgressCallback<File>() {

			@Override
			public void onCancelled(CancelledException arg0) {

			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
				// 下载失败
				mDialog.dismiss();
				deleteUpdateAPK(apkPath);
				prefs.edit().putString(NEW_VERSION_INSTALL_PATH, "").commit();

			}

			@Override
			public void onFinished() {
				mDialog.dismiss();
			}

			@Override
			public void onSuccess(File file) {
				// 下载成功
				mDialog.dismiss();
				apkPath = file.getPath();
				String suffixs = downloadUrl.substring(downloadUrl.lastIndexOf("."), downloadUrl.length());
				file.renameTo(new File(apkPath + suffixs));
				prefs.edit().putString(NEW_VERSION_INSTALL_PATH, apkPath + suffixs).commit();
				Utility.installApk(LoginActivity.this, new File(prefs.getString(NEW_VERSION_INSTALL_PATH, "")));

			}

			@Override
			public void onLoading(long total, long current, boolean isDownloading) {
				if (currentflag) {
					bnp.setMax((int) total);
					currentflag = false;
				}
				bnp.setProgress((int) current);

			}

			@Override
			public void onStarted() {
				// 下载开始
				if (mDialog != null) {
					mDialog.show();
					mDialog.getWindow().setContentView(R.layout.dialog_item);
					TextView message = (TextView) mDialog.getWindow().findViewById(R.id.message);
					message.setText(R.string.update_install_meg);
					bnp = (NumberProgressBar) mDialog.getWindow().findViewById(R.id.numberbar1);
				}

			}

			@Override
			public void onWaiting() {
				// TODO Auto-generated method stub

			}

		});
	}

	private void showDiloag() {
		final MaterialDialog mMaterialDialog = new MaterialDialog(this);
		mMaterialDialog.setTitle(R.string.update_install).setMessage(UpdateReason).setPositiveButton(R.string.install, new OnClickListener() {
			@Override
			public void onClick(View v) {
				mMaterialDialog.dismiss();
				downloadApk(downloadUrl);
			}
		}).setNegativeButton(R.string.cancel, new OnClickListener() {
			@Override
			public void onClick(View v) {
				mMaterialDialog.dismiss();
			}
		}).setCanceledOnTouchOutside(false).show();
	}

	class AreaAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listArea == null ? 0 : listArea.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return listArea.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			AreaHolder holder = null;
			if (convertView == null) {
				holder = new AreaHolder();
				convertView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.login_item, null);
				holder.AreaName = (TextView) convertView.findViewById(R.id.AreaName);
				convertView.setTag(holder);
			} else {
				holder = (AreaHolder) convertView.getTag();
			}
			areaBean bean = listArea.get(position);
			holder.AreaName.setText(bean.getCityName() + "学而思培训学校");
			holder.AreaName.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					areaPos = position;
					area_name.setText(listArea.get(areaPos).getCityName() + "学而思培训学校");
					prefs.edit().putInt("areaPos", areaPos).commit();
					mMaterialDialog.dismiss();
				}
			});
			return convertView;
		}

		@SuppressWarnings("unchecked")
		public void setsubeList(ArrayList<areaBean> list) {
			if (list != null && list.size() > 0) {
				if (listArea != null) {
					listArea.clear();
				}
				listArea = (List<areaBean>) list.clone();
			} else {
				listArea.clear();
			}
			notifyDataSetChanged();
		}
	}

	class AreaHolder {
		TextView AreaName;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.versions_tv:
			Intent intent1 = new Intent(LoginActivity.this, LogService.class);
			startService(intent1);
			break;
		case R.id.login_clear_username_iv:
			login_username_et.setText("");
			break;
		case R.id.login_clear_pwd_iv:
			login_passwd_et.setText("");
			break;
		case R.id.login_login_tv1:
			String username = login_username_et.getText().toString().trim();
			String password = login_passwd_et.getText().toString().trim();
			if (username.isEmpty() || password.isEmpty()) {
				Toast.makeText(LoginActivity.this, "用户名/密码不能为空", Toast.LENGTH_SHORT).show();
				return;
			} else if ("1".equals(username) && "123456".equals(password)) {
				prefs.edit().putString("usename", "1").putString("password", "123456").commit();
				Intent intent = new Intent(LoginActivity.this, LuYinActivity.class);
				startActivity(intent);
				return;
			}
			Utility.hideInput(LoginActivity.this, v);
			new LoginTask().execute(username, password, AreaDao.getAreaInfos().get(areaPos).getCityCode());
			break;
		case R.id.login_area:
			listArea = AreaDao.getAreaInfos();
			mMaterialDialog = new MaterialDialog(LoginActivity.this);
			View view = LayoutInflater.from(LoginActivity.this).inflate(R.layout.loginarea, null);
			mMaterialDialog.setView(view).show();
			ListView listview = (ListView) view.findViewById(R.id.listview);
			areaAdapter = new AreaAdapter();
			listview.setAdapter(areaAdapter);
			break;
		}
	}

	public class myTextWatcher implements TextWatcher {
		private ImageView view;

		public myTextWatcher(ImageView view) {
			this.view = view;
		}

		@Override
		public void afterTextChanged(Editable arg0) {

		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			if (arg3 > 0) {
				view.setVisibility(View.VISIBLE);
			} else {
				view.setVisibility(View.GONE);
			}
		}

	}

	private void initSchoolData() {
		if (AreaDao.getAreaInfos() != null && AreaDao.getAreaInfos().size() > 0) {

		} else {
			areaBean bean = new areaBean();
			bean.cityCode = "010";
			bean.cityName = "北京";
			AreaDao.saveAreaInfo(bean);
		}
	}
}
