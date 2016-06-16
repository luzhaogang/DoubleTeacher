package com.xes.IPSdrawpanel.widget;

import com.xes.IPSdrawpanel.MyApplication;
import com.xes.IPSdrawpanel.R;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.Log;
import android.view.SurfaceHolder;

@SuppressLint("NewApi")
public class PainterThread extends Thread {
	private SurfaceHolder mHolder;
	private Paint mBrush;
	private float mBrushSize;
	private int mLastBrushPointX;
	private int mLastBrushPointY;
	private Canvas mCanvas;
	private Bitmap mBitmap;
	private Bitmap bgBitmap;
	private Boolean mIsActive;
	private Canvas canvas;
	//public List<String> xy = new ArrayList<String>();
	//public List<Integer> xs = new ArrayList<Integer>();
	//public List<Integer> ys = new ArrayList<Integer>();
	//private Boolean Eraseflag = false;

	public PainterThread(SurfaceHolder surfaceHolder) {
		mHolder = surfaceHolder;
		mBrushSize = 5;
		mBrush = new Paint();
		mBrush.setAntiAlias(true);
		mBrush.setColor(Color.RED);
		mBrush.setStrokeWidth(mBrushSize);
		mBrush.setStrokeCap(Cap.ROUND);
		// 重置坐标点
		mLastBrushPointX = -1;
		mLastBrushPointY = -1;
	}

	@Override
	public void run() {
		waitForBitmap();
		while (isRun()) {
			canvas = null;
			try {
				canvas = mHolder.lockCanvas();
				synchronized (mHolder) {
					if (canvas != null) {
						//if (bgBitmap != null) {
							canvas.drawBitmap(bgBitmap, 0, 0, null);
						//}
						canvas.drawBitmap(mBitmap, 0, 0, null);
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("paiters", "e.printStackTrace()");
			} finally {
				if (canvas != null) {
					mHolder.unlockCanvasAndPost(canvas);
				}

			}
		}
	}
	
	public void drawBegin() {
		mLastBrushPointX = -1;
		mLastBrushPointY = -1;
	}

	public void drawEnd() {
		mLastBrushPointX = -1;
		mLastBrushPointY = -1;
	}

	public void draw(int x, int y) {
		if (mLastBrushPointX > 0) {
			if (mLastBrushPointX - x == 0 && mLastBrushPointY - y == 0) {
				return;
			}
			mCanvas.drawLine(x, y, mLastBrushPointX, mLastBrushPointY, mBrush);
		} else {
			mCanvas.drawCircle(x, y, mBrushSize * .5f, mBrush);
		}
		mLastBrushPointX = x;
		mLastBrushPointY = y;

	}

	/*private void findBrushPoint(int x, int y) {
		for (int i = 0; i < xy.size(); i++) {
			String xystr = xy.get(i);
			String[] str = xystr.split(",");
			for (int j = 0; j < str.length; j++) {
				if (j % 2 == 0) {
					xs.add(Integer.parseInt(str[j]));
				} else {
					ys.add(Integer.parseInt(str[j]));
				}
			}
			for (int h = 0; h < xs.size(); h++) {
				if ((xs.get(h) - 20 < x && x < xs.get(h) + 20) || (xs.get(h) - 20 > x && x > xs.get(h) + 20)) {
					if ((ys.get(h) - 20 < y && y < ys.get(h) + 20) || (ys.get(h) - 20 > y && y > ys.get(h) + 20)) {
						drawerase(xystr);
						xs.clear();
						ys.clear();
					}
				}
			}
		}
	}*/

	/*private void drawerase(String xystr) {
		String[] str = xystr.split(",");
		mCanvas.drawCircle(Float.valueOf(str[0]), Float.valueOf(str[1]), mBrushSize * .5f, mBrush);
		for (int j = 2; j < str.length; j++) {
			float x1 = Float.valueOf(str[j]);
			float y1 = Float.valueOf(str[j += 1]);
			float x2 = Float.valueOf(str[j += 1]);
			float y2 = Float.valueOf(str[j += 1]);
			mCanvas.drawLine(x1, y1, x2, y2, mBrush);
		}
	}*/

	public void drawBitmap(Bitmap bitmap,boolean clear) {
		bgBitmap = bitmap;
		if (clear) {
		bgBitmap.eraseColor(Color.WHITE);
		}
	}

	public void setBitmap(Bitmap bitmap, boolean clear) {
		mBitmap = bitmap;
		if (clear) {
			mBitmap.eraseColor(Color.TRANSPARENT);
		}
		mCanvas =null;
		mCanvas = new Canvas(mBitmap);
	}
	

	public Bitmap getmBitmap() {
		return mBitmap;
	}

	public Bitmap getbgBitmap() {
		return bgBitmap;
	}

	public void on() {
		mIsActive = true;
	}

	public void off() {
		mIsActive = false;
		canvas = null;
		//xs.clear();
		//ys.clear();
		if (mBitmap != null && !mBitmap.isRecycled()) {
			mBitmap.isRecycled();
			mBitmap = null;
		}
		if (bgBitmap != null && !bgBitmap.isRecycled()) {
			bgBitmap.isRecycled();
			bgBitmap = null;
		}
		
		System.gc();
	}

	public boolean isRun() {
		return mIsActive;
	}

	private void waitForBitmap() {
		while (mBitmap == null) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void setpainteaser(Boolean Eraseflag) {
		//this.Eraseflag = Eraseflag;
		mBrush.reset();
		mBrush.setAntiAlias(true);
		mBrush.setDither(true);
		mBrush.setStyle(Paint.Style.STROKE);
		mBrush.setStrokeJoin(Paint.Join.ROUND);
		mBrush.setStrokeCap(Cap.ROUND);

		
		
		if (Eraseflag) {
			mBrush.setColor(Color.TRANSPARENT);
			mBrushSize = (int) MyApplication.getAppContext().getResources().getDimension(R.dimen.mBrushSize_TRANSPARENT);
			mBrush.setStrokeWidth(mBrushSize);
			mBrush.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		} else {
			mBrush.setColor(Color.RED);     
			mBrushSize = (int) MyApplication.getAppContext().getResources().getDimension(R.dimen.mBrushSize_red);
			mBrush.setStrokeWidth(mBrushSize);
		}

	}

}
