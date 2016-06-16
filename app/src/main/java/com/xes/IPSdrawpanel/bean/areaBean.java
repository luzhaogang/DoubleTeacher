package com.xes.IPSdrawpanel.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name="areaBean")
public class areaBean {
	private static final long serialVersionUID = 1L;
	@Column(name = "id", isId = true)
	public int id;
	@Column(name = "cityName")
	public String cityName;
	@Column(name = "cityCode")
	public String cityCode;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

}
