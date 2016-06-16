package com.xes.IPSdrawpanel.dao;

import org.xutils.DbManager;
import org.xutils.x;

import com.xes.IPSdrawpanel.MyApplication;
import com.xes.IPSdrawpanel.bean.UserBean;
import com.xes.IPSdrawpanel.util.Utility;

import android.util.Log;

public class UserBeanDao {
	private static DbManager manager;

	public UserBeanDao() {
		manager = x.getDb(MyApplication.getDaoConfig());
	}

	/**
	 * 
	 * 保存数据
	 * 
	 * @return
	 */

	public void saveClassBean(UserBean cb) {
		try {
		manager.saveBindingId(cb);
		} catch (Exception e) {
			Log.e(Utility.LOG_TAG, "保存班级数据失败");
			e.printStackTrace();
		}

	}

	public UserBean getUserBean(String userid) {
		UserBean ub = null;
		try {
			ub = manager.selector(UserBean.class).
						 where("userid","=",userid).findFirst();
		} catch (Exception e) {
			Log.e(Utility.LOG_TAG, "查询聊天用户失败");
			e.printStackTrace();
		}
		//Log.e(Utility.LOG_TAG, "查询聊天用户"+"sb"+ub);
		return ub;
	}
	
	

	


	public void updateUserBean(UserBean ub){
		try {
			//Log.e(Utility.LOG_TAG, "聊天用户更新状态任务状态.............");
			manager.update(ub);	
		} catch (Exception e) {
			Log.e(Utility.LOG_TAG, "聊天用户更新失败");
			e.printStackTrace();
		}
	}

   
	public void deleteUserBean(UserBean cb){
		try {
			manager.delete(cb);
			Log.e(Utility.LOG_TAG, "聊天用户删除.............");
		} catch (Exception e) {
			Log.e(Utility.LOG_TAG, "聊天用户删除失败");
			e.printStackTrace();
		}
	}
	
	
	
	
	
	public void deleteUserBeanById(String userid){
		try {
			manager.execNonQuery("delete from UserBean where userid="+userid);
			Log.e(Utility.LOG_TAG, "聊天用户删除.............");
		} catch (Exception e) {
			Log.e(Utility.LOG_TAG, "聊天用户删除失败");
			e.printStackTrace();
		}
	}

	
}
