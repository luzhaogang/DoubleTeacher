package com.xes.IPSdrawpanel.util;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.xes.IPSdrawpanel.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;

public class Player implements OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener {

	public MediaPlayer mediaPlayer; // ý�岥����
	private SeekBar seekBar; // �϶���
	private Button button;
	private Context mActivity;
	private Timer mTimer = new Timer(); // ��ʱ��

	// ��ʼ��������
	public Player(SeekBar seekBar,Button button,Context mActivity) {
		super();
		this.seekBar = seekBar;
		this.button = button;
		this.mActivity =  mActivity;
		try {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// ����ý��������
			mediaPlayer.setOnBufferingUpdateListener(this);
			mediaPlayer.setOnPreparedListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// ÿһ�봥��һ��
		mTimer.schedule(timerTask, 0, 200);
	}

	// ��ʱ��
	TimerTask timerTask = new TimerTask() {

		@Override
		public void run() {
			if (mediaPlayer == null)
				return;
			if (mediaPlayer.isPlaying() && seekBar.isPressed() == false) {
				handler.sendEmptyMessage(0); // ������Ϣ
			}
		}
	};

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			try {
				int position = mediaPlayer.getCurrentPosition();
				int duration = mediaPlayer.getDuration();
				if (duration > 0) {
					// ������ȣ���ȡ���������̶�?*��ǰ���ֲ���λ�� / ��ǰ����ʱ����
					long pos = seekBar.getMax() * position / duration;
					seekBar.setProgress((int) pos);
					if(pos >= 99){
						seekBar.setProgress(0);
						Drawable ptopDrawable = Utility.getCenterDrawable(R.drawable.correct_icon_voice_pause_default, mActivity);
						button.setBackgroundDrawable(ptopDrawable);
						button.setTag("1");
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

		};
	};

	public void play() {
		mediaPlayer.start();
	}

	/**
	 * 
	 * @param url
	 *            url��ַ
	 */
	public void playUrl(String url) {
		try {
			mediaPlayer.reset();
			mediaPlayer.setDataSource(url); // ��������Դ
			mediaPlayer.prepare(); // prepare�Զ�����
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ��ͣ
	public void pause() {
		mediaPlayer.pause();
	}

	// ֹͣ
	public void stop() {
		if (mTimer != null) {
			mTimer.cancel();
		}

		if (mediaPlayer != null) {
			try {
				mediaPlayer.stop();
				mediaPlayer.release();
				mediaPlayer = null;
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		mp.start();
		Log.e("mediaPlayer", "onPrepared");
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		Log.e("mediaPlayer", "onCompletion");
	}

	/**
	 * �������?
	 */
	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		seekBar.setSecondaryProgress(percent);
		int currentProgress = seekBar.getMax() * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
		Log.e(currentProgress + "% play", percent + " buffer");
	}

}
