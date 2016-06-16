package com.xes.IPSdrawpanel.dao;

import org.xutils.DbManager;
import org.xutils.x;

import com.xes.IPSdrawpanel.MyApplication;
import com.xes.IPSdrawpanel.bean.NewsLabelBean;
import com.xes.IPSdrawpanel.util.Utility;

import android.util.Log;

public class NewsLabelBeanDao {
	private static DbManager manager;

	public NewsLabelBeanDao() {
		manager = x.getDb(MyApplication.getDaoConfig());
	}

	/**
	 * 
	 * 保存数据
	 * 
	 * @return
	 */

	public void saveNewsLabelBean(NewsLabelBean cb) {
		try {
			manager.saveBindingId(cb);
		} catch (Exception e) {
			Log.e(Utility.LOG_TAG+"saveNewsLabelBean", "保存消息数据失败");
			e.printStackTrace();
		}

	}

	public NewsLabelBean getNewsLabelBean(String teaId) {
		NewsLabelBean ub = null;
		try {
			ub = manager.selector(NewsLabelBean.class).where("teaId", "=", teaId).findFirst();
		} catch (Exception e) {
			Log.e(Utility.LOG_TAG+"getNewsLabelBean", "查询消息失败");
			e.printStackTrace();
		}
		return ub;
	}

	public boolean updateNewsLabelBean(NewsLabelBean ub) {
		try {
			manager.update(ub,"toBeCorrectNum","correctedNum","correctingNum");
			return true;
		} catch (Exception e) {
			Log.e(Utility.LOG_TAG+"updateNewsLabelBean", "消息更新失败");
			e.printStackTrace();
			return false;
		}
	}

	public void deleteNewsLabelBean(NewsLabelBean cb) {
		try {
			manager.delete(cb);
			Log.e(Utility.LOG_TAG, "消息删除.............");
		} catch (Exception e) {
			Log.e(Utility.LOG_TAG+"deleteNewsLabelBean", "消息删除失败");
			e.printStackTrace();
		}
	}

}
