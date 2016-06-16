package com.xes.IPSdrawpanel.widget;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.xes.IPSdrawpanel.activity.BaseActivity;
import com.xes.IPSdrawpanel.util.Utility;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class PainterCanvas extends SurfaceView implements Callback {

	private PainterThread mThread;
	private Bitmap mBitmap;
	private Bitmap bgBitmap;
	private Canvas mCanvas;
	//private StringBuffer sb;
	public Context mcontext;

	public PainterCanvas(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mcontext = context;
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		setFocusable(true);
		Log.e("xx", "PainterCanvas");
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if (mBitmap == null) {
			Log.e("xx", "surfaceChanged1");
			mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			bgBitmap = Bitmap.createBitmap(mBitmap);//背景图
			bgBitmap.eraseColor(Color.WHITE);
			mCanvas = new Canvas(bgBitmap);
			getThread().setBitmap(mBitmap, true);
			getThread().drawBitmap(bgBitmap,true);
		} else {
			Log.e("xx", "surfaceChanged2");
			getThread().setBitmap(mBitmap, false);
			getThread().drawBitmap(bgBitmap,false);
		}
	}

	public void surfaceCreated(SurfaceHolder holder) {
		getThread().on();
		getThread().start();
	}

	public void PaintClean() {
		getThread().setBitmap(mBitmap, true);
		getThread().drawBitmap(bgBitmap,true);
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		getThread().off();
		while (retry) {
			try {
				getThread().join();
				retry = false;
			} catch (InterruptedException e) {

			}
		}

		mThread = null;
	}

	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			//Log.e("xx", "开始。。。。");
			//sb = new StringBuffer();
			getThread().drawBegin();
			break;
		case MotionEvent.ACTION_MOVE:
			//Log.e("xx", "进行中。。。。");
			getThread().draw((int) event.getX(), (int) event.getY());
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			//Log.e("xx", "结束。。。。");
			getThread().drawEnd();
			break;
		}
		return true;
	}

	public PainterThread getThread() {
		if (mThread == null) {
			mThread = new PainterThread(getHolder());
		}
		return mThread;
	}

	public void savesImge(File file,Handler handler) {
		Bitmap imgBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(imgBitmap);
		canvas.drawColor(Color.WHITE);
		canvas.drawBitmap(getThread().getbgBitmap(), 0, 0, null);
		canvas.drawBitmap(getThread().getmBitmap(), 0, 0, null);	
		handler.sendEmptyMessage(1);//通知获取下一个任务
		Intent intents = new Intent(BaseActivity.ACTION_submit);
		mcontext.sendBroadcast(intents);//更新批改任务数量
		Bitmap bitmap =Utility.compressImage(imgBitmap);		
		OutputStream stream;
		try {
			stream = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (bitmap != null && !bitmap.isRecycled()) {
				bitmap.isRecycled();
				bitmap = null;
			}
			canvas =null;
		}

	}

	public void drawBitmap(Bitmap bitmap) {
		Matrix matrix = center(bitmap, true, true);
		//Matrix matrix  =new Matrix();
		mCanvas.drawBitmap(bitmap, matrix, null);
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.isRecycled();
			bitmap = null;
		}
		getThread().drawBitmap(bgBitmap,false);
	}

	/**
	 * 横向、纵向居中
	 */
	protected Matrix center(Bitmap bitmap, boolean horizontal, boolean vertical) {
		Matrix matrix = new Matrix();
		RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());

		float height = rect.height();
		float width = rect.width();
		int screenHeight = mBitmap.getHeight();
		int screenWidth = mBitmap.getWidth();

		float deltaX = 0, deltaY = 0, scale = 0;

		if (height >= screenHeight && width <= screenWidth) {
			scale = screenHeight / height;
		} else if (height <= screenHeight && width >= screenWidth) {
			scale = screenWidth / width;
		} else if (height >= screenHeight && width >= screenWidth) {
			float scaleh = screenHeight / height;
			float scalew = screenWidth / width;
			scale = scaleh > scalew ? scalew : scaleh;
		} else if (height <= screenHeight && width <= screenWidth) {
			float scaleh = screenHeight / height;
			float scalew = screenWidth / width;
			scale = scaleh > scalew ? scalew : scaleh;
		}
		matrix.postScale(scale, scale);
		if (vertical) {
			// 图片小于屏幕大小，则居中显示。大于屏幕，上方留空则往上移，下方留空则往下移
			if (height*scale <= screenHeight+1) {
				deltaY = (screenHeight - height*scale)/2 - rect.top;
			} else if (rect.top > 0) {
				deltaY = -rect.top;
			} else if (rect.bottom < screenHeight) {
				deltaY = screenHeight - rect.bottom;
			}
		}

		if (horizontal) {
			if (width*scale <= screenWidth+1) {
				deltaX = (screenWidth - width*scale) / 2 - rect.left;
			} else if (rect.left > 0) {
				deltaX = -rect.left;
			} else if (rect.right < screenWidth) {
				deltaX = screenWidth - rect.right;
			}
		}
		matrix.postTranslate(deltaX, deltaY);
		return matrix;
	}

	public void setpainteaser(Boolean Eraseflag) {
		
		getThread().setpainteaser(Eraseflag);
	}

	public void destroy() {
		Log.e(Utility.LOG_TAG, "getThread().off();");
		getThread().off();
		mCanvas = null;
	}
}
