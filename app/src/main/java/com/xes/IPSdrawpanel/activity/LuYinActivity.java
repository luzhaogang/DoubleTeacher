package com.xes.IPSdrawpanel.activity;

import java.io.File;
import java.io.IOException;

import com.xes.IPSdrawpanel.MyApplication;
import com.xes.IPSdrawpanel.R;
import com.xes.IPSdrawpanel.util.SoundMeter;
import com.xes.IPSdrawpanel.util.Utility;
import com.xes.IPSdrawpanel.util.ZipFileThread;
import com.xes.IPSdrawpanel.util.ZipTool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class LuYinActivity extends Activity implements OnClickListener {
	private Button record, listen, detele, recordOK, zipsubmit;
	private EditText input_name;
	private TextView show_time;
	private String soundName = "";
	private String soundFileName = "";
	private SoundMeter mSensor;
	private long counttime;
	private long playtime = 0;
	private MediaPlayer player;
	// �洢�ļ�
	private File[] files;
	@SuppressWarnings("unused")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {

			} else if (msg.what == 2) {

			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.luyin);
		record = (Button) findViewById(R.id.record);
		record.setOnClickListener(this);
		record.setTag("1");
		recordOK = (Button) findViewById(R.id.recordOK);
		recordOK.setOnClickListener(this);
		listen = (Button) findViewById(R.id.listen);
		listen.setOnClickListener(this);
		listen.setTag("1");
		detele = (Button) findViewById(R.id.delet);
		detele.setOnClickListener(this);
		zipsubmit = (Button) findViewById(R.id.zipsubmit);
		zipsubmit.setOnClickListener(this);
		input_name = (EditText) findViewById(R.id.input_name);
		show_time = (TextView) findViewById(R.id.show_time);
		mSensor = new SoundMeter();
	}

	private void DoRecord() {
		if (record.getTag().equals("1")) {
			soundName = input_name.getText().toString().trim();
			if (soundName.equals("")) {
				Utility.showToast(this, "请先输入语音文件名称");
				return;
			}
			record.setTag("2");

			soundFileName = soundName + ".amr";
			File file = new File(MyApplication.YuyinFilePath + soundFileName);
			try {
				if (!file.exists()) {
					file.createNewFile();
				} else {
					file.delete();
					file.createNewFile();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (mSensor == null) {
				mSensor = new SoundMeter();
			}else{
				mSensor.stop();
				mSensor = null;
				mSensor = new SoundMeter();
			}
			mSensor.start(soundFileName, "2");
			counttime = 0;
			mHandler.removeCallbacks(mPollTask);
			mHandler.postDelayed(mPollTask, 200);
		} else {
			Utility.showToast(this, "文件录制中");
		}

	}

	private Runnable mPollTask = new Runnable() {
		public void run() {
			double amp = mSensor.getAmplitude();
			// updateDisplay(amp);
			counttime += 200;
			if (counttime > 1000) {
				int time = (int) counttime / 1000;
				show_time.setText("语音时长:" + Utility.secToTime(time) + "''");
			}
			mHandler.postDelayed(mPollTask, 200);
		}
	};

	private void DoRecordOK() {
		mHandler.removeCallbacks(mPollTask);
		mSensor.stop();
		mSensor = null;
		record.setTag("1");
	}

	private void DoListen() {
		if (!record.getTag().equals("1")) {
			Utility.showToast(this, "文件尚未录制完成");
			return;
		}
		if (listen.getTag().equals("1")) {
			listen.setTag("2");
			listen.setText("暂停播放");
			playVoice(soundFileName);
		} else {
			listen.setTag("1");
			listen.setText("语音回放");
			stopVice();
		}
	}

	private void playVoice(String fileName) {
		// 判断sd卡上是否有声音文件，有的话就显示名称并播放
		final String path = MyApplication.YuyinFilePath + fileName;
		File file = new File(path);
		if (file.exists()) {
			player = new MediaPlayer();
			try {
				player.setDataSource(path);
				player.prepare();
				player.start();
				playtime = counttime;
				mHandler.postDelayed(mPlayTask, 200);
			} catch (Exception e) {
				e.printStackTrace();

			}
		}
	}

	public void stopVice() {
		if (player != null) {
			mHandler.removeCallbacks(mPlayTask);
			player.pause();
		}
	}

	private void DoDelete() {
		File file = new File(MyApplication.YuyinFilePath + soundFileName);
		if (file.exists() && !soundFileName.isEmpty() && !soundFileName.equals(" ")) {

			if (player != null) {
				mHandler.removeCallbacks(mPlayTask);
				player.stop();
				player.release();
				player = null;
			}
			if (mSensor != null) {
				mHandler.removeCallbacks(mPollTask);
				mSensor.stop();
			}
			counttime = 0;
			// input_name.setText("");
			show_time.setText("语音时长:" + "00:00");
			listen.setText("语音回放");
			record.setTag("1");
			listen.setTag("1");
			file.delete();
			Utility.showToast(this, "文件已删除");
		} else {
			Utility.showToast(this, "没有可删除的文件");
		}
	}

	// 处理压缩返回信息
	private Handler zipHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			int exitType = bundle.getInt("OPTION_STATUS");
			switch (exitType) {
			case ZipTool.EXIST_UNZIPFILE:

				break;
			case ZipTool.EXIST_ZIPFILE:

				break;
			case ZipTool.NOTEXIST_ZIPFILE:

				break;
			case ZipTool.NULL_ZIPPATH:

				break;
			case ZipTool.ZIPOPTION_FAIL:

				break;
			case ZipTool.ZIPOPTION_SUCCESS:
			//	submitResult();
			default:

				break;
			}

		}
	};
	public void submitResult() {

		new Thread(new Runnable() {
			@Override
			public void run() {
				Intent intent1 = new Intent("com.service.CommitResultServiceTest");
				Bundle bd = new Bundle();
				bd.putString("filename", MyApplication.YuyinFilePath + soundFileName+".zip");
				intent1.putExtras(bd);
				startService(intent1);
			}
		}).start();
	}
	private void Zipsubmit() {
		File file = new File(MyApplication.YuyinFilePath + soundFileName);
		files = new File[] { file };
		ZipFileThread zft = new ZipFileThread();
		zft.setFiles(files);
		zft.setHandlePath(MyApplication.YuyinFilePath + soundFileName);
		zft.setHandler(zipHandler);
		zft.setZip(true);
		zft.start();
	}

	private Runnable mPlayTask = new Runnable() {
		public void run() {
			playtime -= 200;
			if (playtime > 1000) {
				int time = (int) playtime / 1000;
				show_time.setText("语音时长:" + Utility.secToTime(time) + "''");
			} else {
				int time = (int) counttime / 1000;
				show_time.setText("语音时长:" + Utility.secToTime(time) + "''");
				mHandler.removeCallbacks(mPlayTask);
				listen.setText("语音回放");
				return;
			}
			mHandler.postDelayed(mPlayTask, 200);
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.record:
			DoRecord();
			break;
		case R.id.recordOK:
			DoRecordOK();
			break;
		case R.id.listen:
			DoListen();
			break;
		case R.id.delet:
			DoDelete();
			break;
		case R.id.zipsubmit:
			Zipsubmit();
		default:
			break;
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (player != null) {
			mHandler.removeCallbacks(mPlayTask);
			player.stop();
			player.release();
		}
		if (mSensor != null) {
			mHandler.removeCallbacks(mPollTask);
			mSensor.stop();
		}
		record.setTag("1");
		listen.setTag("1");
		counttime = 0;
		this.finish();
	}

}
