package com.xes.IPSdrawpanel.bean;

import java.io.Serializable;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name="ClassBean")
public class ClassBean implements Serializable {
	private static final long serialVersionUID = 1L;
	@Column(name="id", isId = true)
	public int id;
	// 班级id
	@Column(name = "classId")
	public String classId;
	// 班级名称
	@Column(name = "className")
	public String className;
	// 学员人数
	@Column(name = "stuCount")
	public String stuCount;
	// 主讲老师ID\
	@Column(name = "masterTeadId")
	public String masterTeadId;
	// 主讲老师名称\
	@Column(name = "masterTeaName")
	public String masterTeaName;
	// 1 未结课 2 已结课
	@Column(name = "isFinish")
	public String isFinish;
	@Column(name = "UnSubmit")
	public int UnSubmit;//未催作业数量
	@Column(name = "unPublish")
	public int unPublish;//未发布作业数量
	@Column(name = "isSelect")
	public int isSelect;
	@Column(name = "orderTime")
	public long orderTime;
	@Column(name = "page")
	public int page;
	
	public String getStuCount() {
		return stuCount;
	}

	public void setStuCount(String stuCount) {
		this.stuCount = stuCount;
	}

	public String getMasterTeadId() {
		return masterTeadId;
	}

	public void setMasterTeadId(String masterTeadId) {
		this.masterTeadId = masterTeadId;
	}

	public String getMasterTeaName() {
		return masterTeaName;
	}

	public void setMasterTeaName(String masterTeaName) {
		this.masterTeaName = masterTeaName;
	}

	public String getIsFinish() {
		return isFinish;
	}

	public void setIsFinish(String isFinish) {
		this.isFinish = isFinish;
	}

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public int getUnSubmit() {
		return UnSubmit;
	}

	public void setUnSubmit(int unSubmit) {
		UnSubmit = unSubmit;
	}

	public int getUnPublish() {
		return unPublish;
	}

	public void setUnPublish(int unPublish) {
		this.unPublish = unPublish;
	}

	public long getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(long orderTime) {
		this.orderTime = orderTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIsSelect() {
		return isSelect;
	}

	public void setIsSelect(int isSelect) {
		this.isSelect = isSelect;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}


}
