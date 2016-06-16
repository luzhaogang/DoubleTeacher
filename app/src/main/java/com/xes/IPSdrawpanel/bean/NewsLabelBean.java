package com.xes.IPSdrawpanel.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name="NewsLabelBean")
public class NewsLabelBean {
	private static final long serialVersionUID = 1L;
	@Column(name = "id", isId = true)
	public int id;
	@Column(name = "teaId")
	public String teaId;
	@Column(name = "toBeCorrectNum")
	public int toBeCorrectNum ;//待批改作业总数
	@Column(name = "correctedNum")
	public int correctedNum ;//批改完成任务数量
	@Column(name = "correctingNum")
	public int correctingNum ;//正在批改的数量
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public int getToBeCorrectNum() {
		return toBeCorrectNum;
	}
	public void setToBeCorrectNum(int toBeCorrectNum) {
		this.toBeCorrectNum = toBeCorrectNum;
	}
	public int getCorrectedNum() {
		return correctedNum;
	}
	public void setCorrectedNum(int correctedNum) {
		this.correctedNum = correctedNum;
	}
	public int getCorrectingNum() {
		return correctingNum;
	}
	public void setCorrectingNum(int correctingNum) {
		this.correctingNum = correctingNum;
	}
	public String getTeaId() {
		return teaId;
	}
	public void setTeaId(String teaId) {
		this.teaId = teaId;
	}
   

}
