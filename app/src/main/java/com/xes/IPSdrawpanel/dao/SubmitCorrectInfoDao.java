package com.xes.IPSdrawpanel.dao;

import java.util.List;

import org.xutils.DbManager;
import org.xutils.x;

import com.xes.IPSdrawpanel.MyApplication;
import com.xes.IPSdrawpanel.api.impl.DrawInterfaceService;
import com.xes.IPSdrawpanel.bean.SubmitCorrectInfo;
import com.xes.IPSdrawpanel.util.Utility;

import android.util.Log;

public class SubmitCorrectInfoDao {
	private static DbManager manager;
	private String LogClassName = this.getClass().getName();
	public SubmitCorrectInfoDao() {
		manager = x.getDb(MyApplication.getDaoConfig());
	}

	/**
	 * 
	 * 保存数据
	 * 
	 * @return
	 */

	public void saveSubmitCorrectInfo(SubmitCorrectInfo si) {
		try {
			SubmitCorrectInfo submitCorrectInfo = getSubmitCorrectInfo(si.answerId);
			if (submitCorrectInfo == null) {
				Log.e(Utility.LOG_TAG+LogClassName, "保存数据");
				manager.saveBindingId(si);
			} else {
				si.id = submitCorrectInfo.id;
				si.cacheIsSUC = submitCorrectInfo.cacheIsSUC;
				si.upLoadstate = submitCorrectInfo.upLoadstate;
				si.submitCount = submitCorrectInfo.submitCount;
				si.taskSubmitTime = submitCorrectInfo.taskSubmitTime;
				si.pictureUrl = submitCorrectInfo.pictureUrl;
				updateSubmitCorrectInfo(si);
			}
		} catch (Exception e) {
			Log.e(Utility.LOG_TAG, "保存数据失败");
			e.printStackTrace();
		}

	}

	public SubmitCorrectInfo getSubmitCorrectInfo(String answerId) {
		SubmitCorrectInfo sb = null;
		try {
			sb = manager.selector(SubmitCorrectInfo.class).where("answerId", "=", answerId).findFirst();
		} catch (Exception e) {
			Log.e(Utility.LOG_TAG, "查询失败");
			e.printStackTrace();
		}
		Log.e(Utility.LOG_TAG, "查询未提交任务" + "sb" + sb + "answerId" + answerId);
		return sb;
	}
	
	public SubmitCorrectInfo getSubmitCorrectInfo() {
		SubmitCorrectInfo sb = null;
		try {
			sb = manager.selector(SubmitCorrectInfo.class).where("upLoadstate", "=", 2).orderBy("submitTime", false).findFirst();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(Utility.LOG_TAG, "查询未完成任务失败。。。。。。。。。。。。。。。");
		}
		Log.e(Utility.LOG_TAG, "查询未完成任务。。。。。。。。。。。。。。。" + sb);
		return sb;
	}

	// 删除全部数据(数据库和文件)
	public void deleteALL() {
		List<SubmitCorrectInfo> sbs = null;
		try {
			sbs = manager.selector(SubmitCorrectInfo.class).findAll();
			if(sbs!=null){
				for (int i = 0; i < sbs.size(); i++) {
					DrawInterfaceService.deleteFile(sbs.get(i).stuAnswer);
					deleteSubmitCorrectInfo(sbs.get(i));
				}	
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(Utility.LOG_TAG, "获取未缓存成功的数据。。。。。。。。。。。。。。。");
		}
	}

	// 获取未缓存成功的数据
	public List<SubmitCorrectInfo> getSubmitCorrectInfoCacheIsSUC() {
		List<SubmitCorrectInfo> sbs = null;
		try {
			sbs = manager.selector(SubmitCorrectInfo.class).where("cacheIsSUC", "=", 2).findAll();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(Utility.LOG_TAG, "获取未缓存成功的数据。。。。。。。。。。。。。。。");
		}
		Log.e(Utility.LOG_TAG, "获取未缓存成功的数据。。。。。。。。。。。。。。。");

		return sbs;
	}

	/**
	 * 更新数据
	 * 
	 * @return
	 */
	public void updateSubmitCorrectInfo(SubmitCorrectInfo sci) {
		try {
			Log.e(Utility.LOG_TAG, "data更新状态任务状态.............");
				manager.update(sci, "startTime","endTime","upLoadstate", "submitCount", "taskSubmitTime", "cacheIsSUC","pictureUrl", "pictureUrlMD5", "pigairesult","loadType","isBOSOK");
		} catch (Exception e) {
			Log.e(Utility.LOG_TAG, LogClassName + "updateSubmitCorrectInfo 数据库更新失败 --------------- ");
			e.printStackTrace();
		}
	}

	public void deleteSubmitCorrectInfo(SubmitCorrectInfo sci) {
		try {
			manager.delete(sci);
		} catch (Exception e) {
			Log.e(Utility.LOG_TAG, "删除失败");
			e.printStackTrace();
		}
	}

	public SubmitCorrectInfo getSubmitCorrectInfoFirst() {
		SubmitCorrectInfo sb = null;
		try {
			sb = manager.selector(SubmitCorrectInfo.class).where("upLoadstate", "=", 0).orderBy("getworkTaskTime", false).findFirst();
		} catch (Exception e) {
			Log.e(Utility.LOG_TAG, "查询失败");
			e.printStackTrace();
		}
		Log.e(Utility.LOG_TAG, "查询未提交任务" + "sb" + sb);
		return sb;
	}

	public boolean getSubmitCorrectInfoUPloadFail(String taskId) {
		SubmitCorrectInfo sb = null;
		try {
			sb = manager.selector(SubmitCorrectInfo.class).where("upLoadstate", "=", 2).and("answerId","=",taskId).findFirst();
			if(sb != null){
				Log.e(Utility.LOG_TAG, "getSubmitCorrectInfoUPloadFail--本地数据库含有该任务----");
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			Log.e(Utility.LOG_TAG, "查询失败");
			e.printStackTrace();
		}
		Log.e(Utility.LOG_TAG, "getSubmitCorrectInfoUPloadFail------");
		return false;
	}

	public long getSubmitCorrectInfoUPloadFailNum() {
		try {
			long str =  manager.selector(SubmitCorrectInfo.class).where("upLoadstate", "=", 2).count();
			Log.e(Utility.LOG_TAG, "本地缓存未提交数量str1<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"+str);
			return str;
		} catch (Exception e) {
			Log.e(Utility.LOG_TAG, "本地缓存<<<<<<<<<<<<<<<<<<<<<<<<<<<<<查询失败");
			e.printStackTrace();
		}
		return 0;
	}
	//查询提交中的数量
	public long getSubmitCorrectIngInfoNum() {
		long count = 0;
		try {
			count = manager.selector(SubmitCorrectInfo.class).where("submitCount", "=", 0).and("upLoadstate", "=", 4).count();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(Utility.LOG_TAG, "查询提交中任务。。。。。。。。。。。。。。。");
		}
		Log.e(Utility.LOG_TAG, "查询提交中任务。。。。。。。。。。。。。。。" + count);
		return count;
	}
	//查询该任务是否提交
	public SubmitCorrectInfo getTaskIsUping(String answerId) {
		SubmitCorrectInfo sb = null;
		try {
			sb =  manager.selector(SubmitCorrectInfo.class).where("upLoadstate", "=", 2).and("answerId", "=", answerId).findFirst();
			Log.e(Utility.LOG_TAG, "查询该任务是否提交str1<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		} catch (Exception e) {
			Log.e(Utility.LOG_TAG, "查询该任务是否提交<<<<<<<<<<<<<<<<<<<<<<<<<<<<<查询失败");
			e.printStackTrace();
		}
		return sb;
	}
	public long getSubmitCorrectInfoNum() {
		long count = 0;
		try {
			count = manager.selector(SubmitCorrectInfo.class).where("submitCount", "=", 0).and("upLoadstate", "=", 0).count();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(Utility.LOG_TAG, "查询未完成任务失败。。。。。。。。。。。。。。。");
		}
		Log.e(Utility.LOG_TAG, "查询缓存未完成的任务。。。。。。。。。。。。。。。" + count);
		return count;
	}

	public void deleteSubmitCorrectInfoById(String answerId) {
		try {
			manager.execNonQuery("delete from SubmitCorrectInfo where answerId=" + answerId);
			Log.e(Utility.LOG_TAG, "data删除未提交任务.............");
		} catch (Exception e) {
			Log.e(Utility.LOG_TAG, "删除失败");
			e.printStackTrace();
		}
	}

	public SubmitCorrectInfo getSubmitCorrectInfopictureUrlMD5(String pictureUrlMD5) {
		SubmitCorrectInfo sb = null;
		try {
			sb = manager.selector(SubmitCorrectInfo.class).where("pictureUrlMD5", "=", pictureUrlMD5).findFirst();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(Utility.LOG_TAG, "获取未缓存成功的数据失败。。。。。。。。。。。。。。。");
		}
		return sb;
	}

}
