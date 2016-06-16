package com.xes.IPSdrawpanel.fragment;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xes.IPSdrawpanel.MyApplication;
import com.xes.IPSdrawpanel.R;
import com.xes.IPSdrawpanel.api.impl.DrawInterfaceService;
import com.xes.IPSdrawpanel.bean.ProblemBank;
import com.xes.IPSdrawpanel.bean.SubmitCorrectInfo;
import com.xes.IPSdrawpanel.bean.TeacherBean;
import com.xes.IPSdrawpanel.dao.SubmitCorrectInfoDao;
import com.xes.IPSdrawpanel.util.BitmapUtil;
import com.xes.IPSdrawpanel.util.SoundMeter;
import com.xes.IPSdrawpanel.util.Utility;
import com.xes.IPSdrawpanel.util.ZipFileThread;
import com.xes.IPSdrawpanel.widget.ContactDialog;
import com.xes.IPSdrawpanel.widget.ContactDialog.OnContactSelectedListener;
import com.xes.IPSdrawpanel.widget.DrawableCenterTextView;
import com.xes.IPSdrawpanel.widget.LookAnswerBuilder;
import com.xes.IPSdrawpanel.widget.NumberProgressBar;
import com.xes.IPSdrawpanel.widget.PainterCanvas;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

public class PainterFragment extends Fragment implements OnClickListener {
	private View mainView;
	public PainterCanvas mCanvas;
	private Button main_listen_record, reason1, reason2, reason3, re_getimage;
	private ImageView main_right, main_rightWrong, main_wrong, main_startrecord;
	private RelativeLayout main_opition, main_reason, main_message;
	private NumberProgressBar progress_wheel;
	// 显示录音时间
	private long recordcounttime = 0;
	private long processTime = 0;
	public DrawableCenterTextView main_questionAnswer;
	private DrawableCenterTextView main_rubber;
	private TextView main_recordTime;
	private TextView studentName;
	private TextView subject;
	private TextView student_time;
	private String Correctresult, reasonResult;
	private SoundMeter mSensor;
	private ProgressBar main_seekbar;
	private MediaPlayer player;
	// 提交文件
	private String zipName = "";
	// 提交数据结构
	private SubmitCorrectInfo submitCorrectInfo;
	// 记录本次图片
	public String countentImageUrl = "";
	private ProblemBank pb = new ProblemBank();
	public static final int ONTWORK = 1;
	public static final int GETQUESOK = 2;
	public static final int GETQUESERROR = 3;
	public static final int RECORDTIMEUP = 4;
	public static final int OUTWORK = 5;
	public static final int ONLINE = 6;
	private static PainterFragment painterFragment;
	private Boolean isOnline = false;
	private int waitPigaiNum;
	public Boolean getPaiGeting = false;
	private SubmitCorrectInfoDao submitCorrectInfoDao;
	private Boolean currentflag = true;

	public static PainterFragment getPainterFragment() {
		if (painterFragment == null) {
			painterFragment = new PainterFragment();
		}
		return painterFragment;
	}

	public final Handler PaintHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case ONTWORK:
				if (isOnline) {
					Log.e(Utility.LOG_TAG, "在线状态获取图片");
					getPaintWork();
					getTaskData();
				} else {
					Log.e(Utility.LOG_TAG, "离线状态获取图片数量" + waitPigaiNum);
					if (waitPigaiNum > 0) {
						waitPigaiNum -= 1;
						getPaintWork();
						getTaskData();
					} else {
						getPaintWork();
						Utility.showToast(getActivity(), "恭喜你老师可以休息了");
					}
				}
				break;
			case ONLINE:
				isOnline = true;
				onORoutwork("1");
				Log.e(Utility.LOG_TAG, "在线获取countentImageUrl" + countentImageUrl);
				if ("".equals(countentImageUrl) || countentImageUrl != null) {
					getPaintWork();
					getTaskData();
				}
				break;
			case GETQUESOK:
				LookAnswerBuilder lookAnswerBuilder = new LookAnswerBuilder(getActivity(), pb, main_questionAnswer);
				lookAnswerBuilder.seeAnswer();
				break;
			case GETQUESERROR:
				Utility.showToast(getActivity(), "获取数据错误");
				main_questionAnswer.setCompoundDrawablesWithIntrinsicBounds(R.drawable.correct_icon_answer, 0, 0, 0);
				main_questionAnswer.setTextColor(getActivity().getResources().getColor(R.color.dark_grey_text));
				break;
			case RECORDTIMEUP:
				stopRecord();
				stopVice();
				break;
			case OUTWORK:
				isOnline = false;
				onORoutwork("0");
				break;
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mainView = inflater.inflate(R.layout.main, container, false);
		initView();
		return mainView;
	}

	private void initView() {
		mSensor = new SoundMeter();
		progress_wheel = (NumberProgressBar) mainView.findViewById(R.id.progress_wheel);
		mCanvas = (PainterCanvas) mainView.findViewById(R.id.canvas);
		main_questionAnswer = (DrawableCenterTextView) mainView.findViewById(R.id.main_questionAnswer);
		main_questionAnswer.setOnClickListener(this);
		main_rubber = (DrawableCenterTextView) mainView.findViewById(R.id.main_rubber);
		main_rubber.setTag("1");
		main_rubber.setOnClickListener(this);
		main_startrecord = (ImageView) mainView.findViewById(R.id.main_startrecord);
		main_startrecord.setOnClickListener(this);
		main_startrecord.setTag("1");
		main_recordTime = (TextView) mainView.findViewById(R.id.main_recordTime);
		main_listen_record = (Button) mainView.findViewById(R.id.main_listen_record);
		main_listen_record.setOnClickListener(this);
		main_listen_record.setTag("1");
		main_seekbar = (ProgressBar) mainView.findViewById(R.id.main_seekbar);
		main_seekbar.setIndeterminate(false);
		main_right = (ImageView) mainView.findViewById(R.id.main_right);
		main_right.setOnClickListener(this);
		main_rightWrong = (ImageView) mainView.findViewById(R.id.main_rightWrong);
		main_rightWrong.setOnClickListener(this);
		main_wrong = (ImageView) mainView.findViewById(R.id.main_wrong);
		main_wrong.setOnClickListener(this);
		main_opition = (RelativeLayout) mainView.findViewById(R.id.main_opition);
		main_reason = (RelativeLayout) mainView.findViewById(R.id.main_reason);
		reason1 = (Button) mainView.findViewById(R.id.reason1);
		reason1.setOnClickListener(this);
		reason2 = (Button) mainView.findViewById(R.id.reason2);
		reason2.setOnClickListener(this);
		reason3 = (Button) mainView.findViewById(R.id.reason3);
		reason3.setOnClickListener(this);
		studentName = (TextView) mainView.findViewById(R.id.studentName);
		subject = (TextView) mainView.findViewById(R.id.subject);
		student_time = (TextView) mainView.findViewById(R.id.student_time);
		re_getimage = (Button) mainView.findViewById(R.id.re_getimage);
		re_getimage.setOnClickListener(this);
		main_message = (RelativeLayout) mainView.findViewById(R.id.main_message);
		main_message.setVisibility(View.INVISIBLE);
		if (submitCorrectInfoDao == null) {
			submitCorrectInfoDao = new SubmitCorrectInfoDao();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.re_getimage:
			progress_wheel.setVisibility(View.VISIBLE);
			if ("".equals(countentImageUrl)) {
				Log.e(Utility.LOG_TAG, "重新获取任务。。。。。。");
				getPaintWork();
				getTaskData();
			} else {
				Log.e(Utility.LOG_TAG, "重新获取图片。。。。。");
				getInteImage(countentImageUrl);
			}

			break;
		case R.id.reason1:
			if (Utility.isFastClick()) {
				return;
			}
			main_opition.setVisibility(View.VISIBLE);
			main_reason.setVisibility(View.GONE);
			reasonResult = "1";
			saveImage();
			break;
		case R.id.reason2:
			if (Utility.isFastClick()) {
				return;
			}
			main_opition.setVisibility(View.VISIBLE);
			main_reason.setVisibility(View.GONE);
			reasonResult = "2";
			saveImage();
			break;
		case R.id.reason3:
			if (Utility.isFastClick()) {
				return;
			}
			main_opition.setVisibility(View.VISIBLE);
			main_reason.setVisibility(View.GONE);
			reasonResult = "3";
			saveImage();
			break;
		case R.id.main_right:
			if (Utility.isFastClick()) {
				return;
			}
			if ("".equals(countentImageUrl)) {
				Utility.showToast(getActivity(), "没有任务");
				return;
			}
			stopRecord();
			stopVice();
			if (recordcounttime < 1000) {
				Utility.showToast(getActivity(), "请录制批改语音");
				return;
			}
			Correctresult = "1";
			reasonResult = "0";
			saveImage();
			break;
		case R.id.main_rightWrong:
			if ("".equals(countentImageUrl)) {
				Utility.showToast(getActivity(), "没有任务");
				return;
			}
			stopRecord();
			stopVice();
			if (recordcounttime < 1000) {
				Utility.showToast(getActivity(), "请录制批改语音");
				return;
			}
			main_opition.setVisibility(View.GONE);
			main_reason.setVisibility(View.VISIBLE);
			Correctresult = "3";
			break;
		case R.id.main_wrong:
			if ("".equals(countentImageUrl)) {
				Utility.showToast(getActivity(), "没有任务");
				return;
			}
			stopRecord();
			stopVice();
			if (recordcounttime < 1000) {
				Utility.showToast(getActivity(), "请录制批改语音");
				return;
			}
			main_opition.setVisibility(View.GONE);
			main_reason.setVisibility(View.VISIBLE);
			Correctresult = "2";
			break;
		case R.id.main_listen_record:
			if ("".equals(countentImageUrl)) {
				Utility.showToast(getActivity(), "没有任务");
				return;
			}
			if ("1".equals(main_listen_record.getTag())) {
				playVoice();
			} else {
				stopVice();
			}
			break;
		case R.id.main_questionAnswer:
			if (Utility.isFastClick()) {
				return;
			}
			if ("".equals(countentImageUrl)) {
				Utility.showToast(getActivity(), "没有任务");
				return;
			}
			((TextView) v).setCompoundDrawablesWithIntrinsicBounds(R.drawable.correct_icon_answer_pressed, 0, 0, 0);
			((TextView) v).setTextColor(getActivity().getResources().getColor(R.color.dark_blue));

			getQuestionAnswer();
			break;
		case R.id.main_rubber:
			if ("".equals(countentImageUrl)) {
				Utility.showToast(getActivity(), "没有任务");
				return;
			}
			v.setBackgroundColor(getActivity().getResources().getColor(R.color.dark_gray));
			main_questionAnswer.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
			if ("1".equals(v.getTag())) {
				mCanvas.setpainteaser(true);
				v.setTag("2");
				((TextView) v).setText("橡皮");
				((TextView) v).setCompoundDrawablesWithIntrinsicBounds(R.drawable.correct_icon_eraser, 0, 0, 0);
			} else if ("2".equals(v.getTag())) {
				mCanvas.setpainteaser(false);
				v.setTag("1");
				((TextView) v).setText("画笔");
				((TextView) v).setCompoundDrawablesWithIntrinsicBounds(R.drawable.correct_icon_pen_pressed, 0, 0, 0);
			}
			break;
		case R.id.main_startrecord:
			if ("".equals(countentImageUrl)) {
				Utility.showToast(getActivity(), "没有任务");
				return;
			}
			if (main_startrecord.getTag().equals("1")) {
				if (recordcounttime > 0) {
					showIsDelete();
				} else {
					startRecord();
				}
			} else {
				stopRecord();
			}
			break;
		}
	}

	// 初始化批改页面
	private void getPaintWork() {
		getPaiGeting = false;
		Log.e(Utility.LOG_TAG, "初始化批改页面");
		countentImageUrl = "";
		mCanvas.PaintClean();
		mCanvas.setpainteaser(false);
		re_getimage.setVisibility(View.GONE);
		main_message.setVisibility(View.INVISIBLE);
		submitCorrectInfo = null;
		Correctresult = "";
		reasonResult = "";
		recordcounttime = 0;
		processTime = 0;
		zipName = "";
		main_seekbar.setProgress(0);
		main_rubber.setTag("1");
		main_rubber.setText("画笔");
		main_rubber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.correct_icon_pen_default, 0, 0, 0);
		main_listen_record.setVisibility(View.GONE);
		main_startrecord.setImageResource(R.drawable.startrecord_button);
		main_startrecord.setTag("1");
		main_recordTime.setText("00:00");
		main_opition.setVisibility(View.VISIBLE);
		main_reason.setVisibility(View.GONE);
	}

	private void getTaskData() {
		Log.e(Utility.LOG_TAG, "获取任务");
		getPaiGeting = true;// 正在获取任务
		submitCorrectInfo = submitCorrectInfoDao.getSubmitCorrectInfoFirst();
		if (submitCorrectInfo != null) {
			Log.e(Utility.LOG_TAG, "si.StuAnswer:" + submitCorrectInfo.stuAnswer);
			if (submitCorrectInfo.stuAnswer != null && !"".equals(submitCorrectInfo.stuAnswer)) {
				String[] str = submitCorrectInfo.stuAnswer.split(",");
				if (str.length > 0) {
					//modifyGetCorrectTask(submitCorrectInfo.workTaskId);
					getInteImage(str[0]);
				} else {
					getPaiGeting = false;
					progress_wheel.setVisibility(View.GONE);
				}
			} else {
				getPaiGeting = false;
				Utility.showToast(getActivity(), "该学生好像忘了上传该题的答题结果哦！");
				progress_wheel.setVisibility(View.GONE);
			}
		} else {
			new StuAnswerInfoTask().execute();
		}
	}

	private void getInteImage(String imageUrl) {
		Log.e(Utility.LOG_TAG, "正在请求图片。。。。。。。。。。" + imageUrl);
		if (!imageUrl.trim().substring(0, 4).equals("http")) {
			Utility.showToast(getActivity(), "图片地址错误");
			progress_wheel.setVisibility(View.GONE);
			getPaiGeting = false;
			return;
		}
		try {
			String filepath = MyApplication.filePath + Utility.md5(imageUrl) + Utility.getFileExtension(imageUrl);
			File file = new File(filepath);
			if (file.exists()) {
				loadImageToSpenView(filepath, imageUrl);
			} else {
				getHttpImageView(imageUrl);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getHttpImageView(final String imageUrl) {
		RequestParams params = new RequestParams(imageUrl);
		x.http().get(params, new Callback.ProgressCallback<File>() {
			@Override
			public void onCancelled(CancelledException arg0) {
			}

			@Override
			public void onError(Throwable arg0, boolean arg1) {
				// 下载失败
				Log.e(Utility.LOG_TAG, "请求图片失败");
				progress_wheel.setVisibility(View.INVISIBLE);
				mCanvas.setVisibility(View.INVISIBLE);
				re_getimage.setVisibility(View.VISIBLE);
			}

			@Override
			public void onFinished() {
			}

			@Override
			public void onSuccess(File file) {
				progress_wheel.setVisibility(View.INVISIBLE);
				mCanvas.setVisibility(View.VISIBLE);
				String filePath = file.getPath();
				String suffixs = imageUrl.substring(imageUrl.lastIndexOf("."), imageUrl.length());
				file.renameTo(new File(filePath + suffixs));
				loadImageToSpenView(filePath + suffixs, imageUrl);
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
				mCanvas.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onWaiting() {
			}

		});
	}

	public void loadImageToSpenView(String strFilePath, String imageUrl) {
		try {
			Bitmap bitmap = null;
			String filepath = MyApplication.filePath + Utility.stringToMd5(imageUrl) + ".jpg";
			File filepic = new File(filepath);
			if (filepic.exists()) {
				bitmap = BitmapUtil.getpathBitmap(filepath);// 批改过得图片
			} else {
				bitmap = BitmapUtil.getpathBitmap(strFilePath);// 未批改的图片
			}
			///storage/sdcard0/Android/data/com.xes.drawpanel/cache/xUtils_cache/1364ec6f17c0e6723dbef40f6b0df308.jpg
			Log.e(Utility.LOG_TAG, "请求图片成功。。。。。");
			re_getimage.setVisibility(View.GONE);
			zipName = Utility.stringToMd5(imageUrl);
			getPaiGeting = false;
			countentImageUrl = imageUrl;
			mCanvas.drawBitmap(bitmap);
			/*if (submitCorrectInfo.audioTime > 0) {
				recordcounttime = submitCorrectInfo.audioTime;
			}*/
			main_message.setVisibility(View.VISIBLE);
			studentName.setText(submitCorrectInfo.stuName);
			subject.setText(submitCorrectInfo.subject + " : " + submitCorrectInfo.paperName);
			//student_time.setText(submitCorrectInfo.submittime);
			submitCorrectInfo = new SubmitCorrectInfo();
			submitCorrectInfo.startTime = System.currentTimeMillis();
			submitCorrectInfo.pictureUrl = imageUrl;
		} catch (Exception e) {

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
			return result;
		}

		@Override
		protected void onPostExecute(SubmitCorrectInfo si) {
			submitCorrectInfo = si;
			if ("1".equals(si.result)) {
				Log.e(Utility.LOG_TAG, "si.StuAnswer:" + si.stuAnswer);
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
					Utility.showToast(getActivity(), "该学生好像忘了上传该题的答题结果哦！");
					progress_wheel.setVisibility(View.GONE);
				}
			} else if ("3".equals(si.result)) {
				getPaiGeting = false;
				re_getimage.setVisibility(View.VISIBLE);
				Utility.showToast(getActivity(), "网络不给力,请检查网络。。");
				progress_wheel.setVisibility(View.GONE);
			} else if ("2".equals(si.result)) {
				getPaiGeting = false;
				Utility.showToast(getActivity(), si.msg);
				mCanvas.setVisibility(View.INVISIBLE);
				progress_wheel.setVisibility(View.GONE);
			}

		}
	}

	private void saveImage() {
		/*if (recordcounttime > 1000) {
			submitCorrectInfo.audioTime = recordcounttime;
		} else {
			submitCorrectInfo.audioTime = 0;
		}
		submitCorrectInfo.pigairesult = Correctresult;// 批改结果
		submitCorrectInfo.evaluation = reasonResult;// 文字评价
		submitCorrectInfo.endTime = System.currentTimeMillis();
		submitCorrectInfo.upLoadstate = 4;*/
		submitCorrectInfoDao.updateSubmitCorrectInfo(submitCorrectInfo);
		File Picfile = Utility.createFile(zipName + ".jpg");
		zipFile(Picfile);
	}

	// 压缩文件
	private void zipFile(File Picfile) {
		File[] files;
		ZipFileThread zft = new ZipFileThread();
		if (recordcounttime > 1000) {
			files = new File[] { new File(MyApplication.filePath + zipName + ".amr"), Picfile };
		} else {
			files = new File[] { Picfile };
		}
		zft.setFiles(files);
		zft.setHandlePath(MyApplication.filePath + zipName);
		zft.setHandler(PaintHandler);
		zft.setZip(true);
		zft.setmCanvas(mCanvas, Picfile);
		zft.setSubmitCorrectInfo(submitCorrectInfo);
		zft.setContext(getActivity());
		zft.start();
	}

	private void playVoice() {
		processTime = 0;
		final String path = MyApplication.filePath + zipName + ".amr";
		File file = new File(path);
		if (file.exists()) {
			player = new MediaPlayer();
			try {
				main_listen_record.setTag("2");
				main_listen_record.setBackgroundResource(R.drawable.pause_listen);
				PaintHandler.postDelayed(startTimeTask, 200);
				player.setDataSource(path);
				player.prepare();
				player.start();
			} catch (Exception e) {
				e.printStackTrace();

			}
		}
	}

	private void startRecord() {
		recordcounttime = 0;
		processTime = 0;
		stopVice();
		stopRecord();
		String audioName = zipName + ".amr";
		main_listen_record.setVisibility(View.GONE);
		main_startrecord.setImageResource(R.drawable.pause_record);
		main_startrecord.setTag("2");
		int time = (int) recordcounttime / 1000;
		main_recordTime.setText(Utility.secToTimeWithoutHour(time));
		Utility.createFile(audioName);
		mSensor.start(audioName, "1");
		// runTime = new MyTimeRunnable(recordcounttime);
		PaintHandler.postDelayed(startTimeTask, 200);
	}

	private Runnable startTimeTask = new Runnable() {

		@Override
		public void run() {
			processTime += 200;
			int time = (int) processTime / 1000;
			main_recordTime.setText(Utility.secToTimeWithoutHour(time));
			main_seekbar.setProgress(time);
			if (recordcounttime == 0) {// 录音
				if (time >= 240) {
					stopRecord();
					return;
				}
			} else {// 播放录音
				if (processTime >= recordcounttime) {
					stopVice();
					return;
				}
			}
			PaintHandler.postDelayed(startTimeTask, 200);
		}
	};

	private void stopVice() {
		PaintHandler.removeCallbacks(startTimeTask);
		if (player != null) {
			main_recordTime.setText(Utility.secToTimeWithoutHour((int) recordcounttime / 1000));
			main_seekbar.setProgress((int) recordcounttime / 1000);
			main_listen_record.setTag("1");
			main_listen_record.setBackgroundResource(R.drawable.listen_record_button);
			player.stop();
			player.release();
			player = null;
		}
	}

	private void stopRecord() {
		PaintHandler.removeCallbacks(startTimeTask);
		if (mSensor != null) {
			recordcounttime = processTime;
			main_listen_record.setVisibility(View.VISIBLE);
			main_startrecord.setImageResource(R.drawable.startrecord_button);
			main_startrecord.setTag("1");
			mSensor.stop();
		}
	}

	@Override
	public void onStop() {
		stopRecord();
		stopVice();
		super.onStop();
	}

	@Override
	public void onDestroy() {
		mCanvas.destroy();
		super.onDestroy();
	}

	private void onORoutwork(final String flag) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				DrawInterfaceService.modifyStatus(flag);
			}
		}).start();

	}

	// 通知服务器端正在批改哪个任务
	private void modifyGetCorrectTask(final String workTaskId) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				//DrawInterfaceService.modifyGetCorrectTask(workTaskId);
			}
		}).start();
	}

	private void getQuestionAnswer() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				//DrawInterfaceService.checkQues(submitCorrectInfo.quesId, PaintHandler, pb);
			}
		}).start();
	}

	private void showIsDelete() {
		// View showview =
		// getActivity().getWindow().findViewById(Window.ID_ANDROID_CONTENT);
		// int width = showview.getWidth();
		// int height = showview.getHeight();
		int height = getActivity().getResources().getDimensionPixelSize(R.dimen.contact_height);
		int width = getActivity().getResources().getDimensionPixelSize(R.dimen.contact_width);
		ContactDialog contactDialog = new ContactDialog(getActivity(), new OnContactSelectedListener() {
			@Override
			public void OnContactSelected(int itemId, CharSequence name, CharSequence number) {
				if (itemId == 2) {
					startRecord();
				}

			}
		});
		contactDialog.show(width, height);
	}

	public void setWaitPigaiNum(int waitPigaiNum) {
		this.waitPigaiNum = waitPigaiNum;
	}
}
