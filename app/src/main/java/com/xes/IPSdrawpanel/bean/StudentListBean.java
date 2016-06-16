package com.xes.IPSdrawpanel.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StudentListBean implements Serializable {
	/**
	 * 学员列表
	 */
	private static final long serialVersionUID = 1L;
	public int id;
	public String classId;
	public String stuId;
	//聊天ID
	public String easemobId;
	public String stuName;
	public String stuIcon;
	public String stuTotalScore;
	public String stuScore1;
	public String stuScore2;
	public String stuScore3;
	public String isSubmit;
	public int type;//1单聊/2群组
	public List<quesinfosBean> quesinfosBeans = new ArrayList<quesinfosBean>();
}
