package com.xes.IPSdrawpanel.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.samsung.android.sdk.pen.SpenSettingEraserInfo;
import com.samsung.android.sdk.pen.SpenSettingPenInfo;
import com.samsung.android.sdk.pen.document.SpenNoteDoc;
import com.samsung.android.sdk.pen.document.SpenPageDoc;
import com.samsung.android.sdk.pen.engine.SpenSimpleSurfaceView;
import com.xes.IPSdrawpanel.MyApplication;
import com.xes.IPSdrawpanel.R;
import com.xes.IPSdrawpanel.api.impl.DrawInterfaceService;
import com.xes.IPSdrawpanel.bean.NewsLabelBean;
import com.xes.IPSdrawpanel.bean.ProblemBank;
import com.xes.IPSdrawpanel.bean.SubmitCorrectInfo;
import com.xes.IPSdrawpanel.bean.TeacherBean;
import com.xes.IPSdrawpanel.dao.NewsLabelBeanDao;
import com.xes.IPSdrawpanel.dao.ProblemBankDao;
import com.xes.IPSdrawpanel.dao.SubmitCorrectInfoDao;
import com.xes.IPSdrawpanel.util.BitmapUtil;
import com.xes.IPSdrawpanel.util.OkHttpUtil;
import com.xes.IPSdrawpanel.util.Utility;
import com.xes.IPSdrawpanel.util.WifiUtil;
import com.xes.IPSdrawpanel.util.ZipFileThread1;
import com.xes.IPSdrawpanel.widget.DrawableCenterTextView;
import com.xes.IPSdrawpanel.widget.LookAnswerBuilder;
import com.xes.IPSdrawpanel.widget.MaterialDialog;
import com.xes.IPSdrawpanel.widget.NumberProgressBar;
import com.xes.IPSdrawpanel.widget.discreteseekbar.DiscreteSeekBar;
import com.xes.IPSdrawpanel.widget.discreteseekbar.DiscreteSeekBar.OnProgressChangeListener;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;

public class PainterFragment1 extends Fragment implements OnClickListener, OnLongClickListener {
	private static PainterFragment1 painterFragment;
	private View mainView;
	private SpenNoteDoc mSpenNoteDoc;
	private SpenPageDoc mSpenPageDoc;
	private SpenSimpleSurfaceView mSpenSimpleSurfaceView;
	private FrameLayout spenViewLayout;
	private int mFINGERToolType = SpenSimpleSurfaceView.TOOL_FINGER;
	private int mSPENToolType = SpenSimpleSurfaceView.TOOL_SPEN;
	private Button re_getimage;
	private ImageView main_right, main_wrong;
	private RelativeLayout main_message;
	private NumberProgressBar progress_wheel;
	public DrawableCenterTextView main_questionAnswer;
	private DrawableCenterTextView main_rubber;
	private TextView tv_rubber;
	private TextView studentName, waitpigaiNUM, AlreadywaitpigaiNUM;
	private TextView subject;
	private String Pigairesult;
	private ProgressBar main_seekbar;
	// 提交文件
	private String zipName = "";
	// 提交数据结构
	private SubmitCorrectInfo submitCorrectInfo;
	// 记录是否获取任务
	public Boolean getPaiGeting = false;
	// 记录本次图片
	public String countentImageUrl = "";
	public static final int GETWORK = 1;
	public static final int GETUNCORRECTNUMOK = 2;
	public static final int GETUNCORRECTNUMERROR = 3;
	public int waitPigaiNum;
	private SubmitCorrectInfoDao submitCorrectInfoDao;
	private Boolean currentflag = true;
	private int eraserInfoSize = (int) MyApplication.getAppContext().getResources().getDimension(R.dimen.mBrushSize_TRANSPARENT);
	private int dynamicEraserInfoSize;
	private String contentFilePath;
	private int mScreenRectWidth;
	private int mScreenRectHeight;
	private int spenHeight,spenWidth;
	private File files = null;
	private int isSuccess = -1;// 0获取0K图片1获取成功2获取失败404//3.网路异常
	private String LogClassName = "";
	private NewsLabelBeanDao NewslableBeandao;
	private ProblemBankDao PbDao;
	private NewsLabelBean NewsLabelBean;
	private int CorrectNum;
	@SuppressLint("HandlerLeak")
	public final Handler PaintHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GETWORK:
				Log.e(Utility.LOG_TAG, "执行GETWORK --------------------------------- ");
				NewsLabelBean = NewslableBeandao.getNewsLabelBean(TeacherBean.getInstance().teaId);
				CorrectNum = NewsLabelBean.toBeCorrectNum ;
				if (CorrectNum > 0) {
					NewsLabelBean.toBeCorrectNum --;
					NewsLabelBean.correctedNum++;
					NewslableBeandao.updateNewsLabelBean(NewsLabelBean);
				}
				waitpigaiNUM.setText(NewsLabelBean.toBeCorrectNum+"");
				AlreadywaitpigaiNUM.setText(NewsLabelBean.correctedNum + "");
				getInitPaintWork();
				getTaskData();
				break;
			case GETUNCORRECTNUMOK:
				NewsLabelBean = NewslableBeandao.getNewsLabelBean(TeacherBean.getInstance().teaId);
				waitpigaiNUM.setText(NewsLabelBean.toBeCorrectNum +"");
				AlreadywaitpigaiNUM.setText(NewsLabelBean.correctedNum + "");
				if (!getPaiGeting && NewsLabelBean.toBeCorrectNum > 0 && countentImageUrl.equals("")) {
					getInitPaintWork();
					getTaskData();
				}
				break;
			case GETUNCORRECTNUMERROR:
				if (!getPaiGeting && countentImageUrl.equals("")) {
					getInitPaintWork();
					getTaskData();
				}
				break;
			}
		}
	};

	public static PainterFragment1 getPainterFragment1() {
		if (painterFragment == null) {
			painterFragment = new PainterFragment1();
		}
		return painterFragment;
	}

	public void setWaitPigaiNum(int pigaiNum) {
		this.waitPigaiNum = pigaiNum;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mainView = inflater.inflate(R.layout.main1, container, false);
		initView();
		return mainView;
	}

	private void initView() {
		spenViewLayout = (FrameLayout) mainView.findViewById(R.id.spenViewLayout);
		mSpenSimpleSurfaceView = new SpenSimpleSurfaceView(MyApplication.applicationContext);
		if (mSpenSimpleSurfaceView == null) {
			Toast.makeText(MyApplication.applicationContext, "Cannot create new SpenView.", Toast.LENGTH_SHORT).show();
		}
		spenViewLayout.addView(mSpenSimpleSurfaceView, 0);
		ViewTreeObserver vto = spenViewLayout.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				spenViewLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				mScreenRectWidth = spenViewLayout.getWidth();
				mScreenRectHeight = spenViewLayout.getHeight();
				try {
					mSpenNoteDoc = new SpenNoteDoc(MyApplication.applicationContext, mScreenRectWidth, mScreenRectHeight);
				} catch (IOException e) {
					e.printStackTrace();
				}
				mSpenPageDoc = mSpenNoteDoc.appendPage();
				mSpenPageDoc.setBackgroundColor(0xFFf0f0f0);
				mSpenPageDoc.clearHistory();
				mSpenSimpleSurfaceView.setPageDoc(mSpenPageDoc, true);
				mSpenSimpleSurfaceView.setBlankColor(getResources().getColor(android.R.color.white));
				mSpenSimpleSurfaceView.setToolTypeAction(mFINGERToolType, SpenSimpleSurfaceView.ACTION_STROKE);
				mSpenSimpleSurfaceView.setToolTypeAction(mSPENToolType, SpenSimpleSurfaceView.ACTION_STROKE);
				SpenSettingPenInfo penInfo = new SpenSettingPenInfo();
				penInfo.color = Color.RED;
				penInfo.size = (int) MyApplication.getAppContext().getResources().getDimension(R.dimen.mBrushSize_red);
				mSpenSimpleSurfaceView.setPenSettingInfo(penInfo);
				// 初始化任务
				getUnCorrectNumFromServer();
				getTaskData();
			}
		});
		countentImageUrl = "";
		progress_wheel = (NumberProgressBar) mainView.findViewById(R.id.progress_wheel);
		main_questionAnswer = (DrawableCenterTextView) mainView.findViewById(R.id.main_questionAnswer);
		main_questionAnswer.setOnClickListener(this);
		main_rubber = (DrawableCenterTextView) mainView.findViewById(R.id.main_rubber);
		main_rubber.setTag("1");
		main_rubber.setOnClickListener(this);
		main_rubber.setOnLongClickListener(this);
		main_seekbar = (ProgressBar) mainView.findViewById(R.id.main_seekbar);
		main_seekbar.setIndeterminate(false);
		main_right = (ImageView) mainView.findViewById(R.id.main_right);
		main_right.setOnClickListener(this);
		main_wrong = (ImageView) mainView.findViewById(R.id.main_wrong);
		main_wrong.setOnClickListener(this);
		studentName = (TextView) mainView.findViewById(R.id.studentName);
		subject = (TextView) mainView.findViewById(R.id.subject);
		re_getimage = (Button) mainView.findViewById(R.id.re_getimage);
		re_getimage.setOnClickListener(this);
		main_message = (RelativeLayout) mainView.findViewById(R.id.main_message);
		main_message.setVisibility(View.INVISIBLE);
		submitCorrectInfoDao = new SubmitCorrectInfoDao();
		waitpigaiNUM = (TextView) mainView.findViewById(R.id.waitpigaiNUM);
		AlreadywaitpigaiNUM = (TextView) mainView.findViewById(R.id.AlreadywaitpigaiNUM);
		NewslableBeandao = new NewsLabelBeanDao();
		PbDao = new ProblemBankDao();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.re_getimage:
			if (!WifiUtil.isWiFiActive(getActivity())) {
				Utility.showToast(getContext(), "网络已断开");
				return;
			}
			progress_wheel.setVisibility(View.VISIBLE);
			if ("".equals(countentImageUrl)) {
				//Log.e(Utility.LOG_TAG ,  LogClassName+"重新获取任务。。。。。。");
				getInitPaintWork();
				getTaskData();
			} else {
				//Log.e(Utility.LOG_TAG ,LogClassName+ "重新获取图片。。。。。");
				getInteImage(countentImageUrl);
			}
			break;
		case R.id.main_right:
			if (Utility.isFastClick()) {
				return;
			}
			if ("".equals(countentImageUrl)) {
				Utility.showToast(MyApplication.applicationContext, "没有任务");
				return;
			}
			main_right.setEnabled(false);
			main_wrong.setEnabled(false);
			Pigairesult = "1";
			saveImage();
			break;
		case R.id.main_wrong:
			if (Utility.isFastClick()) {
				return;
			}
			if ("".equals(countentImageUrl)) {
				Utility.showToast(MyApplication.applicationContext, "没有任务");
				return;
			}
			main_right.setEnabled(false);
			main_wrong.setEnabled(false);
			Pigairesult = "2";
			saveImage();
			break;
		case R.id.main_questionAnswer:
			if (Utility.isFastClick()) {
				return;
			}
			if ("".equals(countentImageUrl)) {
				Utility.showToast(MyApplication.applicationContext, "没有任务");
				return;
			}
			ProblemBank pb = PbDao.getProblemBean(submitCorrectInfo.quesId);
			if (pb != null) {
				((TextView) v).setCompoundDrawablesWithIntrinsicBounds(R.drawable.correct_icon_answer_pressed, 0, 0, 0);
				((TextView) v).setTextColor(MyApplication.applicationContext.getResources().getColor(R.color.dark_blue));
				LookAnswerBuilder lookAnswerBuilder = new LookAnswerBuilder(getActivity(), pb, main_questionAnswer);
				lookAnswerBuilder.seeAnswer();
			} else {
				Utility.showToast(MyApplication.applicationContext, "获取试题失败");
			}
			break;
		case R.id.main_rubber:
			if ("".equals(countentImageUrl)) {
				Utility.showToast(MyApplication.applicationContext, "没有任务");
				return;
			}
			mSpenSimpleSurfaceView.closeControl();
			if ("1".equals(v.getTag())) {
				setpentype(2);
				v.setTag("2");
				((TextView) v).setText("橡皮");
				((TextView) v).setCompoundDrawablesWithIntrinsicBounds(R.drawable.correct_icon_eraser, 0, 0, 0);
			} else if ("2".equals(v.getTag())) {
				setpentype(1);
				v.setTag("1");
				((TextView) v).setText("画笔");
				((TextView) v).setCompoundDrawablesWithIntrinsicBounds(R.drawable.correct_icon_pen_pressed, 0, 0, 0);
			}
			break;
		}
	}
	
	/********************************** 获取任务数量 *************************************/
	public void getUnCorrectNumFromServer() {
		new Thread(new Runnable() {
			public void run() {
				DrawInterfaceService.takeWorkNum(PaintHandler);
			}
		}).start();
	}

	/********************************** 获取任务开始 *************************************/
	private void getInitPaintWork() {
		getPaiGeting = false;
		Log.e(Utility.LOG_TAG ,LogClassName+ "初始化批改页面");
		countentImageUrl = "";
		contentFilePath = "";
		isSuccess = -1;
		setpentype(1);
		re_getimage.setVisibility(View.GONE);
		main_message.setVisibility(View.INVISIBLE);
		Pigairesult = "";
		zipName = "";
		submitCorrectInfo = null;
		main_seekbar.setProgress(0);
		main_rubber.setTag("1");
		main_rubber.setText("画笔");
		main_rubber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.correct_icon_pen_pressed, 0, 0, 0);
		mSpenSimpleSurfaceView.closeControl();
		Log.e(Utility.LOG_TAG ,LogClassName + "getTaskData"+ Utility.changeTimeMIN(System.currentTimeMillis() + ""));
	}

	private void getTaskData() {
		Log.e(Utility.LOG_TAG ,LogClassName+ "获取任务");
		getPaiGeting = true;// 正在获取任务
		submitCorrectInfo = submitCorrectInfoDao.getSubmitCorrectInfoFirst();
		if (submitCorrectInfo != null) {
			//Log.e(Utility.LOG_TAG ,"本地获取URl ---------- " + submitCorrectInfo.stuAnswer);
			if (submitCorrectInfo.stuAnswer != null && !"".equals(submitCorrectInfo.stuAnswer)) {
				String[] str = submitCorrectInfo.stuAnswer.split(",");
				if (str.length > 0) {
					// 通知服务器取哪个批改任务
					modifyGetCorrectTask(submitCorrectInfo.answerId, "1");
					getInteImage(str[0]);
				} else {
					getPaiGeting = false;
					progress_wheel.setVisibility(View.GONE);
				}
			} else {
				getPaiGeting = false;
				Utility.showToast(MyApplication.applicationContext, "该学生好像忘了上传该题的答题结果哦！");
				progress_wheel.setVisibility(View.GONE);
				// 该任务批改完成
				modifyGetCorrectTask(submitCorrectInfo.answerId, "2");
				submitCorrectInfoDao.deleteSubmitCorrectInfoById(submitCorrectInfo.answerId);
				getInitPaintWork();
				getTaskData();
			}
		} else {
			OkHttpUtil.getInstance().mOkHttpClient.cancel("getCorrectTask");
			new StuAnswerInfoTask().execute();
		}

	}

	class StuAnswerInfoTask extends AsyncTask<String, Void, SubmitCorrectInfo> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress_wheel.setVisibility(View.VISIBLE);
		}

		@Override
		protected SubmitCorrectInfo doInBackground(String... params) {
			SubmitCorrectInfo result = DrawInterfaceService.getCorrectTask(TeacherBean.getInstance().teaId);
			//result.stuAnswer = "http://tutor-server.bj.bcebos.com/tutor/picture/20160530151147.png";
			//result.result = "1";
			//result.quesId = "fdfsd";
			//result.answerId = "asdfsadf";
			return result;
		}

		@Override
		protected void onPostExecute(SubmitCorrectInfo si) {
			submitCorrectInfo = si;
			if ("1".equals(si.result)) {
				Log.e(Utility.LOG_TAG , LogClassName+"si.StuAnswer:" + si.stuAnswer);
				if (submitCorrectInfoDao.getTaskIsUping(si.answerId) != null) {
					PaintHandler.sendEmptyMessage(GETWORK);
					Log.e(Utility.LOG_TAG, "该任务本地正在提交，执行重新获取任务");
				} else {
					// 保存数据库
					submitCorrectInfoDao.saveSubmitCorrectInfo(si);
					if (si.stuAnswer != null && !"".equals(si.stuAnswer)) {
						String[] str = si.stuAnswer.split(",");
						if (str.length > 0) {
							getInteImage(str[0]);
						} else {
							getPaiGeting = false;
							progress_wheel.setVisibility(View.GONE);
						}
					} else {
						getPaiGeting = false;
						Utility.showToast(MyApplication.applicationContext, "该学生好像忘了上传该题的答题结果哦！");
						progress_wheel.setVisibility(View.GONE);
						// 该任务批改完成
						modifyGetCorrectTask(submitCorrectInfo.answerId, "2");
						submitCorrectInfoDao.deleteSubmitCorrectInfoById(submitCorrectInfo.answerId);
						getInitPaintWork();
						getTaskData();
					}
				}
			} else if ("3".equals(si.result)) {
				getPaiGeting = false;
				re_getimage.setVisibility(View.VISIBLE);
				Log.e(Utility.LOG_TAG, "网络不给力,请检查网络。。");
				progress_wheel.setVisibility(View.GONE);
				clearView();
				main_right.setEnabled(true);
				main_wrong.setEnabled(true);
			} else if ("2".equals(si.result)) {
				getPaiGeting = false;
				Utility.showToast(MyApplication.applicationContext, si.msg);
				progress_wheel.setVisibility(View.GONE);
				clearView();
				main_right.setEnabled(true);
				main_wrong.setEnabled(true);
				//暴力更正待批改任务为零
				waitpigaiNUM.setText("0");
			}
		}
	}

	private void getInteImage(String imageUrl) {
		if (!imageUrl.trim().substring(0, 4).equals("http")) {
			Log.e(Utility.LOG_TAG, "图片地址错误");
			progress_wheel.setVisibility(View.GONE);
			getPaiGeting = false;
			submitCorrectInfoDao.deleteSubmitCorrectInfo(submitCorrectInfo);
			getTaskData();
			return;
		}
		try {
			String filepath = MyApplication.filePath + Utility.md5(imageUrl) + Utility.getFileExtension(imageUrl);
			File file = new File(filepath);
			//判断文件已存在，是空的
			Bitmap bitmap = BitmapUtil.getpathBitmaps(filepath);
			if (file.exists() && bitmap != null) {
				bitmap.recycle();
				Log.e(Utility.LOG_TAG, LogClassName + "本地存在该图片"+"--------------" + submitCorrectInfo.stuAnswer);
				loadImageToSpenView(filepath, imageUrl);
			} else {
				Log.e(Utility.LOG_TAG ,LogClassName + "网络获取图片"+ "si.StuAnswer:" + submitCorrectInfo.stuAnswer);
				getHttpImageView(imageUrl);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getHttpImageView(final String imageUrl) {
		RequestParams params = new RequestParams(imageUrl);
		Log.e(Utility.LOG_TAG ,LogClassName + "getHttpImageViewstart"+ Utility.changeTimeMIN(System.currentTimeMillis() + ""));
		x.http().get(params, new Callback.ProgressCallback<File>() {

			@Override
			public void onCancelled(CancelledException arg0) {

			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
				// 下载失败
				if (arg0.getMessage() != null) {
					Log.e(Utility.LOG_TAG , LogClassName+ "请求图片失败" + arg0.getMessage());
					if (arg0.getMessage().toString().length() > 0) {
						if (arg0.getMessage().equals("Not Found") || arg0.getMessage().indexOf("not found") != -1) {// 图片是0K和404
							/*
							 * new Thread(new Runnable() {
							 * 
							 * @Override public void run() {
							 * DrawInterfaceService
							 * .errTaskHandler(submitCorrectInfo, "404"); }
							 * }).start();
							 */
							// submitCorrectInfoDao.deleteSubmitCorrectInfo(submitCorrectInfo);
							isSuccess = 0;
							Log.e(Utility.LOG_TAG, "未找到图片。。。");
						} else if (arg0.getMessage().indexOf("stream closed") != -1 || arg0.getMessage().indexOf("Unable to resolve host") != -1) {// 网络异常
							progress_wheel.setVisibility(View.INVISIBLE);
							re_getimage.setVisibility(View.VISIBLE);
							isSuccess = 3;
							Log.e(Utility.LOG_TAG, "网络不给力。。。");
						} else {
							Log.e(Utility.LOG_TAG, "学生提交图片错误。。。");
							isSuccess = 0;
						}
					}
				} else if (arg0.toString() != null && arg0.toString().indexOf("SocketTimeoutException") != -1) {
					progress_wheel.setVisibility(View.INVISIBLE);
					re_getimage.setVisibility(View.VISIBLE);
					Log.e(Utility.LOG_TAG, "资源服务器出错了。。。");
					isSuccess = 3;
				} else {
					Log.e(Utility.LOG_TAG, "未知错误。。。");
					isSuccess = 0;
				}
			}

			@Override
			public void onFinished() {
				loadViewPen(isSuccess, imageUrl);
				currentflag = true;
			}

			@Override
			public void onSuccess(File file) {
				isSuccess = 1;
				files = file;
			}

			@Override
			public void onLoading(long total, long current, boolean isDownloading) {
				if (currentflag) {
					progress_wheel.setMax((int) total);
					currentflag = false;
				}
				progress_wheel.setProgress((int) current);
			}

			@Override
			public void onStarted() {
				progress_wheel.setVisibility(View.VISIBLE);
				// mSpenSurfaceView.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onWaiting() {

			}
		});
	}

	public void loadViewPen(int isSuccess, String imageUrl) {
		Log.e(Utility.LOG_TAG, LogClassName+"请求图片《《《《《《《《《《《《《《" + isSuccess);
		if (isSuccess == 1) {
			String filePath = files.getPath();
			String suffixs = imageUrl.substring(imageUrl.lastIndexOf("."), imageUrl.length());
			files.renameTo(new File(filePath + suffixs));
			Log.e(Utility.LOG_TAG , LogClassName + "getHttpImageViewsuccess"+Utility.changeTimeMIN(System.currentTimeMillis() + ""));
			loadImageToSpenView(filePath + suffixs, imageUrl);
		} else if (isSuccess == 0 || isSuccess == -1) {
			// 注意 图片大小为0K时执行onError后再执行onFinished()
			// 0k的图片也可能直接执行onFinished()取决于资源服务器
			loadImageToSpenView("pictureerro", imageUrl);
		}
		// else if (isSuccess == 2) {// 获取失败的情况404
		// PaintHandler.sendEmptyMessage(ONWORK);// 继续获取下一个任务
		// }
	}

	public void loadImageToSpenView(String strFilePath, String imageUrl) {
		try {
			Log.e(Utility.LOG_TAG,LogClassName+ "请求图片成功。。。。。");
			progress_wheel.setVisibility(View.GONE);
			progress_wheel.setProgress(0);
			re_getimage.setVisibility(View.GONE);
			if ("pictureerro".equals(strFilePath)) {
				loadFileView(strFilePath);
			} else {
				String filepath = MyApplication.filePath + Utility.stringToMd5(imageUrl) + ".jpg";
				File filepic = new File(filepath);
				if (filepic.exists()) {
					contentFilePath = filepath;// 已批改的图片
					loadFileView(filepath);
				} else {
					contentFilePath = strFilePath;// 未批改的图片
					File strFilePaths = new File(strFilePath);
					if (strFilePaths.exists()) {
						loadFileView(strFilePath);
					} else {
						clearView();
					}
				}
			}
			zipName = Utility.stringToMd5(imageUrl);
			getPaiGeting = false;
			countentImageUrl = imageUrl;
			main_message.setVisibility(View.VISIBLE);
			studentName.setText(submitCorrectInfo.stuName);
			subject.setText(submitCorrectInfo.paperName);
			submitCorrectInfo.startTime = Utility.getnowTime();
			submitCorrectInfo.pictureUrl = imageUrl;
			// 解除禁止
			main_right.setEnabled(true);
			main_wrong.setEnabled(true);
			// 缓存试题
			if (PbDao.getProblemBean(submitCorrectInfo.quesId) == null) {
				getQuestionAnswer();
			}
		} catch (Exception e) {
			// 重启任务
			String filepath = MyApplication.filePath + Utility.stringToMd5(imageUrl) + ".jpg";
			File filepic = new File(filepath);
			if (filepic.exists()) {
				filepic.delete();
			}
			getInitPaintWork();
			getTaskData();
			Log.e(Utility.LOG_TAG, "Failed to load noteDoc.");
		}
	}

	/**********************************
	 * 获取任务 结束
	 *************************************/

	/************************ 保存任务开始 ******************************/
	private void saveImage() {
		Log.e(Utility.LOG_TAG ,LogClassName + "saveImage"+ Utility.changeTimeMIN(System.currentTimeMillis() + ""));
		submitCorrectInfo.pigairesult = Pigairesult;// 批改结果
		submitCorrectInfo.endTime = Utility.getnowTime();
		submitCorrectInfo.upLoadstate = 4;
		submitCorrectInfoDao.updateSubmitCorrectInfo(submitCorrectInfo);
		File Picfile = Utility.createFile(zipName + ".jpg");
		zipFile(Picfile);
	}

	// 压缩文件
	private void zipFile(File Picfile) {
		File[] files;
		ZipFileThread1 zft = new ZipFileThread1();
		files = new File[] { Picfile };
		zft.setFiles(files);
		zft.setHandlePath(MyApplication.filePath + zipName);
		zft.setHandler(PaintHandler);
		zft.setZip(true);
		zft.setmCanvas(mSpenSimpleSurfaceView, mSpenNoteDoc, Picfile,spenHeight,spenWidth);
		zft.setSubmitCorrectInfo(submitCorrectInfo);
		zft.setContext(MyApplication.applicationContext);
		zft.start();
	}

	/************************* 保存任务结束 *****************************/

	// 通知服务器端正在批改哪个任务
	private void modifyGetCorrectTask(final String answerId, final String state) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				DrawInterfaceService.modifyGetCorrectTask(TeacherBean.getInstance().teaId, answerId, state);
			}
		}).start();
	}

	// 获取试题
	private void getQuestionAnswer() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				DrawInterfaceService.checkQues(submitCorrectInfo.quesId);
			}
		}).start();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mSpenSimpleSurfaceView != null) {
			mSpenSimpleSurfaceView.closeControl();
			mSpenSimpleSurfaceView.close();
			mSpenSimpleSurfaceView = null;
		}

		if (mSpenNoteDoc != null) {
			try {
				mSpenPageDoc.save();
				mSpenNoteDoc.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			mSpenNoteDoc = null;
		}
	};

	public void setpentype(int flag) {
		if (flag == 1) {// 笔
			SpenSettingPenInfo penInfo = new SpenSettingPenInfo();
			penInfo.color = Color.RED;
			penInfo.size = (int) MyApplication.getAppContext().getResources().getDimension(R.dimen.mBrushSize_red);
			mSpenSimpleSurfaceView.setPenSettingInfo(penInfo);
			mSpenSimpleSurfaceView.setToolTypeAction(mFINGERToolType, SpenSimpleSurfaceView.ACTION_STROKE);
			mSpenSimpleSurfaceView.setToolTypeAction(mSPENToolType, SpenSimpleSurfaceView.ACTION_STROKE);

		} else if (flag == 2) {// 橡皮擦
			SpenSettingEraserInfo eraserInfo = new SpenSettingEraserInfo();
			eraserInfo.size = eraserInfoSize;
			mSpenSimpleSurfaceView.setEraserSettingInfo(eraserInfo);
			mSpenSimpleSurfaceView.setToolTypeAction(mFINGERToolType, SpenSimpleSurfaceView.ACTION_ERASER);
			mSpenSimpleSurfaceView.setToolTypeAction(mSPENToolType, SpenSimpleSurfaceView.ACTION_ERASER);
		}
	}

	@SuppressLint("InflateParams")
	public MaterialDialog showSeekbar(Context context) {
		final MaterialDialog mMaterialDialog = new MaterialDialog(context);
		if (mMaterialDialog != null) {
			View view = LayoutInflater.from(context).inflate(R.layout.seekbar_item, null);
			DiscreteSeekBar discrete = (DiscreteSeekBar) view.findViewById(R.id.discrete);
			Button start_btn = (Button) view.findViewById(R.id.start_btn);
			start_btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!"".equals(contentFilePath)) {
						loadFileView(contentFilePath);
					}
					mMaterialDialog.dismiss();
				}
			});
			Button dissmiss = (Button) view.findViewById(R.id.dissmiss);
			dissmiss.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mMaterialDialog.dismiss();
				}
			});
			Button submit_btn = (Button) view.findViewById(R.id.submit_btn);
			submit_btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mMaterialDialog.dismiss();
					eraserInfoSize = dynamicEraserInfoSize;
					setpentype(2);
				}
			});

			tv_rubber = (TextView) view.findViewById(R.id.tv_rubber);
			tv_rubber.setText("橡皮擦大小为" + eraserInfoSize);
			discrete.setProgress(eraserInfoSize);
			mMaterialDialog.setBackgroundResource(android.R.color.white);
			mMaterialDialog.setView(view).show();
			discrete.setOnProgressChangeListener(new myOnProgressChangeListener());
		}
		return mMaterialDialog;
	}

	class myOnProgressChangeListener implements OnProgressChangeListener {

		@Override
		public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
			dynamicEraserInfoSize = value;
			tv_rubber.setText("橡皮擦大小为" + value);
		}

		@Override
		public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

		}

	}

	@Override
	public boolean onLongClick(View v) {
		if ("2".equals(v.getTag())) {
			MaterialDialog mMaterialDialog = showSeekbar(getActivity());
			mMaterialDialog.show();
		}
		return false;
	}

	public void clearView() {
		try {
			mSpenSimpleSurfaceView.closeControl();
			SpenNoteDoc tmpSpenNoteDoc = new SpenNoteDoc(MyApplication.applicationContext, mScreenRectWidth, mScreenRectHeight);
			mSpenNoteDoc.close();
			mSpenNoteDoc = tmpSpenNoteDoc;
			if (mSpenNoteDoc.getPageCount() == 0) {
				mSpenPageDoc = mSpenNoteDoc.appendPage();
			} else {
				mSpenPageDoc = mSpenNoteDoc.getPage(mSpenNoteDoc.getLastEditedPageIndex());
			}
			mSpenPageDoc.setBackgroundColor(0xFFf0f0f0);
			mSpenPageDoc.clearHistory();
			mSpenSimpleSurfaceView.setPageDoc(mSpenPageDoc, true);
			mSpenSimpleSurfaceView.update();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadFileView(String filepath) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options(); 
		options.inJustDecodeBounds = true;
		if ("pictureerro".equals(filepath)) {
			bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.picture_erro);
			BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.picture_erro,options);
		} else {
			bitmap = BitmapUtil.getpathBitmaps(filepath);
			BitmapFactory.decodeFile(filepath, options); 
		}
		if (bitmap != null) {
			try {
				float wide = options.outWidth;
				float height = options.outHeight;
				Log.e(Utility.LOG_TAG, "获取图片的长宽"+height+wide);
				float proportionW = mScreenRectWidth / wide;
				float proportionH = mScreenRectHeight / height;
				float proportion = proportionW;
				if (proportionW > proportionH) {
					proportion = proportionH;
				}
				spenWidth = (int) (wide * proportion);
				spenHeight = (int) (height * proportion);
				SpenNoteDoc tmpSpenNoteDoc = new SpenNoteDoc(MyApplication.applicationContext, spenWidth, spenHeight);
				mSpenSimpleSurfaceView.closeControl();
				mSpenNoteDoc.close();
				mSpenNoteDoc = tmpSpenNoteDoc;
				if (mSpenNoteDoc.getPageCount() == 0) {
					mSpenPageDoc = mSpenNoteDoc.appendPage();
				} else {
					mSpenPageDoc = mSpenNoteDoc.getPage(mSpenNoteDoc.getLastEditedPageIndex());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			mSpenPageDoc.setBackgroundColor(0xFFf0f0f0);
			mSpenPageDoc.clearHistory();
			mSpenPageDoc.setBackgroundImageMode(SpenPageDoc.BACKGROUND_IMAGE_MODE_FIT);
			mSpenPageDoc.setVolatileBackgroundImage(bitmap);
			bitmap.recycle();
		}
		mSpenSimpleSurfaceView.setPageDoc(mSpenPageDoc, true);
		mSpenSimpleSurfaceView.update();
	}
}
