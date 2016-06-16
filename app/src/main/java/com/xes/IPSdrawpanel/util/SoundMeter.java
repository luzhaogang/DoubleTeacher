package com.xes.IPSdrawpanel.util;

import java.io.IOException;

import com.xes.IPSdrawpanel.MyApplication;

import android.media.MediaRecorder;
import android.os.Environment;

public class SoundMeter {
	static final private double EMA_FILTER = 0.6;

	private MediaRecorder mRecorder = null;
	private double mEMA = 0.0;

	public void start(String name, String temp) {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return;
		}
		
		 if (mRecorder != null) {
			 mRecorder.release();
			 mRecorder = null;
         }else{
			mRecorder = new MediaRecorder();
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mRecorder.setAudioChannels(1); // MONO
			mRecorder.setAudioSamplingRate(8000); // 8000Hz
			mRecorder.setAudioEncodingBitRate(64); // seems if change this to
			if (temp.equals("1")) {
				mRecorder.setOutputFile(MyApplication.filePath + name);
			} else if (temp.equals("2")) {
				mRecorder.setOutputFile(MyApplication.YuyinFilePath + name);
			}

			try {
				mRecorder.prepare();
				mRecorder.start();
				mEMA = 0.0;
			} catch (IllegalStateException e) {
				System.out.print(e.getMessage());
			} catch (IOException e) {
				System.out.print(e.getMessage());
			}
		}
	}

	public void stop() {
		if (mRecorder != null) {
			mRecorder.setOnErrorListener(null);
			mRecorder.setPreviewDisplay(null);
			try {
				mRecorder.stop();
				mRecorder.release();
				mRecorder = null;
			} catch (IllegalStateException e) {
				//Log.e(Util.LOG_TAG, "stopRecord", e);
			} catch (RuntimeException e) {
				//Log.e(Util.LOG_TAG, "stopRecord", e);
			} catch (Exception e) {
				//Log.e(Util.LOG_TAG, "stopRecord", e);
			}
		}
	}

	public void pause() {
		if (mRecorder != null) {
			mRecorder.stop();
		}
	}

	public void start() {
		if (mRecorder != null) {
			mRecorder.start();
		}
	}

	public double getAmplitude() {
		if (mRecorder != null)
			return (mRecorder.getMaxAmplitude() / 2700.0);
		else
			return 0;

	}

	public double getAmplitudeEMA() {
		double amp = getAmplitude();
		mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
		return mEMA;
	}
}
