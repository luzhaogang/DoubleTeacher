package com.xes.IPSdrawpanel.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.samsung.android.sdk.pen.document.SpenNoteDoc;
import com.samsung.android.sdk.pen.engine.SpenSimpleSurfaceView;
import com.xes.IPSdrawpanel.activity.BaseActivity;
import com.xes.IPSdrawpanel.api.impl.DrawInterfaceService;
import com.xes.IPSdrawpanel.bean.SubmitCorrectInfo;
import com.xes.IPSdrawpanel.bean.TeacherBean;
import com.xes.IPSdrawpanel.dao.SubmitCorrectInfoDao;
import com.xes.IPSdrawpanel.fragment.PainterFragment1;
import com.xes.IPSdrawpanel.service.CommitBOSResultService;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;


public class ZipFileThread1 extends Thread
{
	// 后续处理对象
	private Handler handler;

	private File file;

	// 处理的文件路经
	private String handlePath;

	// 处理的文件
	private File[] files;

	private boolean isZip;
	private int spenWidth,spenHeight;
	public SpenSimpleSurfaceView mSpenSurfaceView;

	private File Picfile;

	private SubmitCorrectInfo submitCorrectInfos = new SubmitCorrectInfo();

	private Context context;

	private SpenNoteDoc mSpenNoteDoc;

	@Override
	public void run() {
		int msgValue = -1;
		if (isZip) {// 压缩
			Boolean isSuc = savesImge();
			if (isSuc) {
				Intent intent = new Intent(context, CommitBOSResultService.class);
				//Intent intent = new Intent(context, CommitResultService.class);
				Bundle bd = new Bundle();
				bd.putSerializable("submitCorrectInfo", submitCorrectInfos);
				intent.putExtras(bd);
				context.startService(intent);
				msgValue = ZipTool.zip(handlePath + ".zip", files);
				if (msgValue == 3) {
					/*Intent intent = new Intent(context, CommitBOSResultService.class);
					//Intent intent = new Intent(context, CommitResultService.class);
					Bundle bd = new Bundle();
					bd.putSerializable("submitCorrectInfo", submitCorrectInfos);
					intent.putExtras(bd);
					context.startService(intent);*/
				} else {
					//Log.e(Utility.LOG_TAG, "图片语音压缩状态恢复：3是成功" + msgValue);
					//DrawInterfaceService.modifyGetCorrectTask(TeacherBean.getInstance().teaId,submitCorrectInfos.answerId,"3");
				}
			} else {
				SubmitCorrectInfoDao submit = new SubmitCorrectInfoDao();
				submit.deleteSubmitCorrectInfo(submitCorrectInfos);
				DrawInterfaceService.modifyGetCorrectTask(TeacherBean.getInstance().teaId,submitCorrectInfos.answerId,"3");
				handler.sendEmptyMessage(PainterFragment1.GETWORK);// 通知获取下一个任务
				Log.e(Utility.LOG_TAG, "屏幕截取图片失败" + System.currentTimeMillis());
			}
		} else {
			// 解压
			String filePath = file.getAbsolutePath();
			msgValue = ZipTool.unzip(filePath, handlePath);
		}
	}

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public File[] getFiles() {
		return files;
	}

	public void setFiles(File[] files) {
		this.files = files;
	}

	public void setmCanvas(SpenSimpleSurfaceView mCanvas, SpenNoteDoc mSpenNoteDoc, File Picfile,int spenHeight,int spenWidth) {
		this.mSpenSurfaceView = mCanvas;
		this.Picfile = Picfile;
		this.mSpenNoteDoc = mSpenNoteDoc;
		this.spenHeight = spenHeight;
		this.spenWidth = spenWidth;
	}

	public boolean isZip() {
		return isZip;
	}

	public void setSubmitCorrectInfo(SubmitCorrectInfo submitCorrectInfo) {
		submitCorrectInfos.answerId = submitCorrectInfo.answerId;
		submitCorrectInfos.id = submitCorrectInfo.id;
		submitCorrectInfos.quesId = submitCorrectInfo.quesId;
		submitCorrectInfos.startTime = submitCorrectInfo.startTime;
		submitCorrectInfos.pictureUrl = submitCorrectInfo.pictureUrl;
		submitCorrectInfos.pigairesult = submitCorrectInfo.pigairesult;
		submitCorrectInfos.endTime = submitCorrectInfo.endTime;
		submitCorrectInfos.loadType = submitCorrectInfo.loadType;
	}

	public void setZip(boolean isZip) {
		this.isZip = isZip;
	}

	public String getHandlePath() {
		return handlePath;
	}

	public void setHandlePath(String handlePath) {
		this.handlePath = handlePath;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	private Boolean savesImge() {
		//只截取批改区域，不要白边
		Bitmap imgBitmap = mSpenSurfaceView.captureCurrentView(true);
		int x = (mSpenSurfaceView.getWidth()-spenWidth)/2;
        int y = (mSpenSurfaceView.getHeight()-spenHeight)/2;

		if (imgBitmap == null) {
			Log.e(Utility.LOG_TAG, "截取图片失败");
			return false;
		}
		OutputStream out = null;
		try {
			out = new FileOutputStream(Picfile);
			Bitmap imgBitmaps = Utility.compressImage(imgBitmap);
			Bitmap bitmapsss = Bitmap.createBitmap(imgBitmaps, x, y, spenWidth, spenHeight);
	        imgBitmaps.recycle();
			//质量压缩方法，100表示不压缩
	        bitmapsss.compress(CompressFormat.JPEG, 100, out);
			mSpenNoteDoc.save(out, false);
			out.close();
			bitmapsss.recycle();
			Log.e(Utility.LOG_TAG, "截取图片成功");
			handler.sendEmptyMessage(PainterFragment1.GETWORK);// 通知获取下一个任务
			Log.e(Utility.LOG_TAG, "通知获取新任务时间" + System.currentTimeMillis());
			
			Intent intents = new Intent(BaseActivity.ACTION_submit);
			context.sendBroadcast(intents);// 更新批改任务数量
			
			return true;
		} catch (IOException e) {
			if (Picfile.exists()) {
				Picfile.delete();
			}
			Log.e(Utility.LOG_TAG, "截取图片失败");
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			if (Picfile.exists()) {
				Picfile.delete();
			}
			Log.e(Utility.LOG_TAG, "截取图片失败");
			e.printStackTrace();
			return false;
		}
	}

}
