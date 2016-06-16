package com.xes.IPSdrawpanel.dao;

import java.util.ArrayList;
import java.util.List;

import org.xutils.DbManager;
import org.xutils.x;

import com.xes.IPSdrawpanel.MyApplication;
import com.xes.IPSdrawpanel.bean.ClassBean;
import com.xes.IPSdrawpanel.util.Utility;

import android.util.Log;

public class ClassBeanDao {
	private static DbManager manager;

	public ClassBeanDao() {
		manager = x.getDb(MyApplication.getDaoConfig());
	}

	/**
	 * 
	 * 保存数据
	 * 
	 * @return
	 */

	public void saveClassBean(ClassBean cb) {
		try {
			ClassBean classbean = getClassBean(cb.classId);
			if (classbean == null) {
				Log.e(Utility.LOG_TAG, "保存班级数据");
				manager.saveBindingId(cb);
			} else {
				cb.id = classbean.id;
				updateClassBean(cb);
			}
		} catch (Exception e) {
			Log.e(Utility.LOG_TAG, "保存班级数据失败");
			e.printStackTrace();
		}

	}

	public ClassBean getClassBean(String classId) {
		ClassBean cb = null;
		try {
			cb = manager.selector(ClassBean.class).where("classId", "=", classId).findFirst();
		} catch (Exception e) {
			Log.e(Utility.LOG_TAG, "查询班级失败");
			e.printStackTrace();
		}
		Log.e(Utility.LOG_TAG, "查询班级" + "sb" + cb);
		return cb;
	}

	public ClassBean getClassBeanIsSelect() {
		ClassBean cb = null;
		try {
			cb = manager.selector(ClassBean.class).where("isSelect", "=", 2).findFirst();
		} catch (Exception e) {
			Log.e(Utility.LOG_TAG, "查询班级失败");
			e.printStackTrace();
		}
		Log.e(Utility.LOG_TAG, "查询班级" + "sb" + cb);
		return cb;
	}

	public List<ClassBean> getClassBeans() {
		List<ClassBean> cbs = new ArrayList<ClassBean>();
		try {
			cbs = manager.selector(ClassBean.class).orderBy("orderTime").findAll();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(Utility.LOG_TAG, "查询班级失败。。。。。。。。。。。。。。。");
		}
		Log.e(Utility.LOG_TAG, "查询班级数量。。。。。。。。。。。。。。。");
		return cbs;
	}

	public List<ClassBean> getClassBeanFirstPage() {
		List<ClassBean> cbs = new ArrayList<ClassBean>();
		try {
			cbs = manager.selector(ClassBean.class).where("page", "=", 1).orderBy("orderTime").findAll();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(Utility.LOG_TAG, "查询班级失败。。。。。。。。。。。。。。。");
			return cbs;
		}
		Log.e(Utility.LOG_TAG, "查询班级数量。。。。。。。。。。。。。。。");
		return cbs;
	}

	public Boolean updateClassBean(ClassBean cb) {
		try {
			Log.e(Utility.LOG_TAG, "班级更新状态任务状态.............");
			manager.update(cb, "classId", "className", "masterTeaName", "UnSubmit", "unPublish", "page", "orderTime");
			return true;
		} catch (Exception e) {
			Log.e(Utility.LOG_TAG, "更新失败");
			e.printStackTrace();
			return false;
		}
	}

	public void updateClassBeanSelect(ClassBean cb) {
		try {
			Log.e(Utility.LOG_TAG, "班级更新状态任务状态.............");
			manager.update(cb, "isSelect");
		} catch (Exception e) {
			Log.e(Utility.LOG_TAG, "更新失败");
			e.printStackTrace();
		}
	}

	public void deleteClassBean(ClassBean cb) {
		try {
			manager.delete(cb);
			Log.e(Utility.LOG_TAG, "班级表删除.............");
		} catch (Exception e) {
			Log.e(Utility.LOG_TAG, "删除失败");
			e.printStackTrace();
		}
	}

	public void deleteClassBeanById(String classId) {
		try {
			manager.execNonQuery("delete from ClassBean where classId=" + classId);
			Log.e(Utility.LOG_TAG, "data删除未提交任务.............");
		} catch (Exception e) {
			Log.e(Utility.LOG_TAG, "删除失败");
			e.printStackTrace();
		}
	}

	public void deleteClassBeanTable() {
		try {
			manager.dropTable(ClassBean.class);
			Log.e(Utility.LOG_TAG, "表已删除.............");
		} catch (Exception e) {
			Log.e(Utility.LOG_TAG, "删除失败");
			e.printStackTrace();
		}
	}

	// 删除全部数据
	public void deleteALL() {
		List<ClassBean> cbs = null;
		try {
			cbs = manager.selector(ClassBean.class).findAll();
			if (cbs != null) {
				for (int i = 0; i < cbs.size(); i++) {
					deleteClassBean(cbs.get(i));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.e(Utility.LOG_TAG, "删除classbean表数据失败。。。。。。。。。。。。。。。");
		}
	}

}
