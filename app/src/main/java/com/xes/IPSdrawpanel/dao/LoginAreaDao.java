package com.xes.IPSdrawpanel.dao;

import java.util.List;

import org.xutils.DbManager;
import org.xutils.x;

import com.xes.IPSdrawpanel.MyApplication;
import com.xes.IPSdrawpanel.bean.areaBean;

public class LoginAreaDao {
	// private Context mContext;
	// private static DbUtils db;
	private static DbManager manager;

	public LoginAreaDao() {
		manager = x.getDb(MyApplication.getDaoConfig());
		// this.db = MyApplication.getDbUtils();
	}

	public void saveAreaInfo(areaBean si) {
		try {
			if (getAreaInfo(si.cityCode) == null) {
				manager.saveBindingId(si);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public areaBean getAreaInfo(String cityCode) {
		areaBean sb = null;
		try {
			sb = manager.selector(areaBean.class).where("cityCode", "=", cityCode).findFirst();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb;
	}

	public List<areaBean> getAreaInfos() {
		List<areaBean> sbs = null;
		try {
			sbs = manager.selector(areaBean.class).orderBy("id", false).findAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sbs;
	}
}
