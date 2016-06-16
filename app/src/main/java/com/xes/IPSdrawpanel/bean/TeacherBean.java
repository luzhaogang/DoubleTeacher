package com.xes.IPSdrawpanel.bean;

import java.io.Serializable;
public class TeacherBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static TeacherBean teacher;
	public static int SUCCESS = 1;
	public static int FAIL = 2;
	
	public static TeacherBean getInstance(){
		if(teacher == null){
			teacher = new TeacherBean();
		}
		return teacher;
	}
	public int id;
	public String teaId ="";
	public String phone;
	public String teaName;
	public String password;
	public String areaCode;
	public int result;
	public String recmsg;
	public String icon;
	public long timStamp ;//服务器 时间
	public String timeZone ;//服务器时区
	public String getTeaId() {
		return teaId;
	}
	public void setTeaId(String teaId) {
		this.teaId = teaId;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	public String getRecmsg() {
		return recmsg;
	}
	public void setRecmsg(String recmsg) {
		this.recmsg = recmsg;
	}
}
