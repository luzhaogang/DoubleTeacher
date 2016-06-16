package com.xes.IPSdrawpanel.dao;

import java.util.List;

import org.xutils.DbManager;
import org.xutils.x;

import com.xes.IPSdrawpanel.MyApplication;
import com.xes.IPSdrawpanel.api.impl.DrawInterfaceService;
import com.xes.IPSdrawpanel.bean.ProblemBank;
import com.xes.IPSdrawpanel.bean.SubmitCorrectInfo;
import com.xes.IPSdrawpanel.util.Utility;

import android.util.Log;

public class ProblemBankDao {
	private static DbManager manager;
	public ProblemBankDao() {
		manager = x.getDb(MyApplication.getDaoConfig());
	}
	/**
	 * 
	 * 保存数据
	 * 
	 * @return
	 */

	public void saveClassBean(ProblemBank pb) {
		try {
			ProblemBank ProbleanBean = getProblemBean(pb.quesId);
			if (ProbleanBean == null) {
				Log.e(Utility.LOG_TAG, "保存班级数据");
				manager.saveBindingId(pb);
			} else {
				updatePbBean(pb);
			}
		} catch (Exception e) {
			Log.e(Utility.LOG_TAG, "保存班级数据失败");
			e.printStackTrace();
		}
	}
	//查找
	public ProblemBank getProblemBean(String quesId) {
		ProblemBank pb = null;
		try {
			pb = manager.selector(ProblemBank.class).where("quesId", "=", quesId).findFirst();
		} catch (Exception e) {
			Log.e(Utility.LOG_TAG, "查询班级失败");
			e.printStackTrace();
		}
		Log.e(Utility.LOG_TAG, "查询班级" + "sb" + pb);
		return pb;
	}
	//更新
	public Boolean updatePbBean(ProblemBank pb) {
		try {
			Log.e(Utility.LOG_TAG, "班级更新试题.............");
			manager.update(pb);
			return true;
		} catch (Exception e) {
			Log.e(Utility.LOG_TAG, "更新失败");
			e.printStackTrace();
			return false;
		}
	}
	//删除
	public void deletePbBean(ProblemBank pb) {
		try {
			manager.delete(pb);
			Log.e(Utility.LOG_TAG, "试题删除.............");
		} catch (Exception e) {
			Log.e(Utility.LOG_TAG, "删除失败");
			e.printStackTrace();
		}
	}
	public void deletePbBeanById(String quesId) {
		try {
			manager.execNonQuery("delete from ProblemBank where quesId=" + quesId);
			Log.e(Utility.LOG_TAG, "data删除未提交任务.............");
		} catch (Exception e) {
			Log.e(Utility.LOG_TAG, "删除失败");
			e.printStackTrace();
		}
	}
	public void deletePBALL(){
		List<ProblemBank> pbs = null;
		try {
			pbs = manager.selector(ProblemBank.class).findAll();
			if(pbs!=null){
				for (int i = 0; i < pbs.size(); i++) {
					deletePbBean(pbs.get(i));
				}	
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(Utility.LOG_TAG, "删除pb全部的数据失败。。。。。。。。。。。。。。。");
		}
	}
}
