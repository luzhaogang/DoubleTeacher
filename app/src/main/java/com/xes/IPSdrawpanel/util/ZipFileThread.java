/**
 * <pre>
 * Title: 		ZipFileThread.java
 * Project: 	GugleFile
 * Type:		org.leoly.guglefile.threads.ZipFileThread
 * Author:		255507
 * Create:	 	2012-2-22 下午5:42:15
 * Copyright: 	Copyright (c) 2012
 * Company:		
 * <pre>
 */
package com.xes.IPSdrawpanel.util;

import java.io.File;

import com.xes.IPSdrawpanel.bean.SubmitCorrectInfo;
import com.xes.IPSdrawpanel.service.CommitResultService;
import com.xes.IPSdrawpanel.widget.PainterCanvas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

/**
 * <pre>
 * 压缩/解压缩线�?
 * </pre>
 * @author 255507
 * @version 1.0, 2012-2-22
 */
public class ZipFileThread extends Thread
{
	// 后续处理对象
	private Handler handler;

	// 处理的文�?
	private File file;
	
	// 处理的文件路�?
	private String handlePath;

	// 处理的一�? 文件
	private File[] files;

	private boolean isZip;
	
	public PainterCanvas mCanvas;
	
	private File Picfile;
	
	private SubmitCorrectInfo submitCorrectInfos = new SubmitCorrectInfo();
	
	private Activity context;

	@Override
	public void run()
	{
		// 如果是压缩操�?
		//Message msg = null;
		int msgValue = -1;
		if (isZip)
		{//压缩
			mCanvas.savesImge(Picfile,handler);
			msgValue = ZipTool.zip(handlePath + ".zip", files);
			Log.e(Utility.LOG_TAG,"图片语音压缩状态恢复：3是成功"+msgValue);
		}
		else
		{//解压
			String filePath = file.getAbsolutePath();
			msgValue = ZipTool.unzip(filePath, handlePath);
		}
		Intent intent = new Intent(context,CommitResultService.class);
		Bundle bd = new Bundle();
		bd.putSerializable("submitCorrectInfo", submitCorrectInfos);
		intent.putExtras(bd);
		context.startService(intent);
		//msg = new Message();
		//Bundle bundle = new Bundle();
		//bundle.putInt("OPTION_STATUS", msgValue);
		//msg.setData(bundle);
		//handler.sendMessage(msg);
	}

	public Handler getHandler()
	{
		return handler;
	}

	public void setHandler(Handler handler)
	{
		this.handler = handler;
	}

	public File getFile()
	{
		return file;
	}

	public void setFile(File file)
	{
		this.file = file;
	}

	public File[] getFiles()
	{
		return files;
	}

	public void setFiles(File[] files)
	{
		this.files = files;
	}
	
	public void setmCanvas(PainterCanvas mCanvas,File Picfile)
	{
		this.mCanvas = mCanvas;
		this.Picfile = Picfile;
	}

	public boolean isZip()
	{
		return isZip;
	}

	public void setSubmitCorrectInfo(SubmitCorrectInfo submitCorrectInfo)
	{
		submitCorrectInfos.answerId = submitCorrectInfo.answerId;
		submitCorrectInfos.quesId = submitCorrectInfo.quesId;
		submitCorrectInfos.startTime = submitCorrectInfo.startTime; 
		submitCorrectInfos.pictureUrl = submitCorrectInfo.pictureUrl;
		submitCorrectInfos.pigairesult = submitCorrectInfo.pigairesult;
		submitCorrectInfos.endTime =submitCorrectInfo.endTime; 
		submitCorrectInfos.loadType = submitCorrectInfo.loadType;
	}
	
	public void setZip(boolean isZip)
	{
		this.isZip = isZip;
	}

	public String getHandlePath()
	{
		return handlePath;
	}

	public void setHandlePath(String handlePath)
	{
		this.handlePath = handlePath;
	}
	
	
	public void setContext(Activity context){
		this.context = context;
	}

}
