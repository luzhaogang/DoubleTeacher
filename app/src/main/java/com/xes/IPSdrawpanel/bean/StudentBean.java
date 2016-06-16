package com.xes.IPSdrawpanel.bean;

import java.io.Serializable;

public class StudentBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// ѧԱID
	public String stuId;
	// ѧԱ���֢�
	public String stuName;
	public String classId;
	// ѧԱͷ��
	public String stuIcon;
	// ѧ���绰
	public String stuPhone;
	// ������
	public String errorCount;
	// ����ѧԱ�ύ��ҵ����
	public String weekHomeworkNum;
	// �ۼ��ύ��ҵ����
	public String allHomework;
	// �����ύ����
	public String weekQuesNum;
	// �ۼ��ύ����
	public String allQuesNum;
	// ������ȷ����
	public String weekRightQuesNum;
	// �ۼ���ȷ����
	public String allRightQuesNum;
	// ���ܴ�������
	public String weekErrorQuesNum;
	// �ۼƴ�������
	public String allErrorQuesNum;
	// ���ܱ��������
	public String weekMarkQuesNum;
	// �ۼƱ��������
	public String allMarkQuesNum;
	// �ۼƷ�������
	public String allFeedbackNum;
	// 1 �ѷ��� 2 δ����
	public String isFeedback;
	

	public String getWeekHomeworkNum() {
		return weekHomeworkNum;
	}

	public void setWeekHomeworkNum(String weekHomeworkNum) {
		this.weekHomeworkNum = weekHomeworkNum;
	}

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	public String getAllHomework() {
		return allHomework;
	}

	public void setAllHomework(String allHomework) {
		this.allHomework = allHomework;
	}

	public String getWeekQuesNum() {
		return weekQuesNum;
	}

	public void setWeekQuesNum(String weekQuesNum) {
		this.weekQuesNum = weekQuesNum;
	}

	public String getAllQuesNum() {
		return allQuesNum;
	}

	public void setAllQuesNum(String allQuesNum) {
		this.allQuesNum = allQuesNum;
	}

	public String getWeekRightQuesNum() {
		return weekRightQuesNum;
	}

	public void setWeekRightQuesNum(String weekRightQuesNum) {
		this.weekRightQuesNum = weekRightQuesNum;
	}

	public String getAllRightQuesNum() {
		return allRightQuesNum;
	}

	public void setAllRightQuesNum(String allRightQuesNum) {
		this.allRightQuesNum = allRightQuesNum;
	}

	public String getWeekErrorQuesNum() {
		return weekErrorQuesNum;
	}

	public void setWeekErrorQuesNum(String weekErrorQuesNum) {
		this.weekErrorQuesNum = weekErrorQuesNum;
	}

	public String getAllErrorQuesNum() {
		return allErrorQuesNum;
	}

	public void setAllErrorQuesNum(String allErrorQuesNum) {
		this.allErrorQuesNum = allErrorQuesNum;
	}

	public String getWeekMarkQuesNum() {
		return weekMarkQuesNum;
	}

	public void setWeekMarkQuesNum(String weekMarkQuesNum) {
		this.weekMarkQuesNum = weekMarkQuesNum;
	}

	public String getAllMarkQuesNum() {
		return allMarkQuesNum;
	}

	public void setAllMarkQuesNum(String allMarkQuesNum) {
		this.allMarkQuesNum = allMarkQuesNum;
	}

	public String getAllFeedbackNum() {
		return allFeedbackNum;
	}

	public void setAllFeedbackNum(String allFeedbackNum) {
		this.allFeedbackNum = allFeedbackNum;
	}

	public String getIsFeedback() {
		return isFeedback;
	}

	public void setIsFeedback(String isFeedback) {
		this.isFeedback = isFeedback;
	}

	public String getStuPhone() {
		return stuPhone;
	}

	public void setStuPhone(String stuPhone) {
		this.stuPhone = stuPhone;
	}

	public String getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(String errorCount) {
		this.errorCount = errorCount;
	}

	public String getStuId() {
		return stuId;
	}

	public void setStuId(String stuId) {
		this.stuId = stuId;
	}

	public String getStuName() {
		return stuName;
	}

	public void setStuName(String stuName) {
		this.stuName = stuName;
	}

	public String getStuIcon() {
		return stuIcon;
	}

	public void setStuIcon(String stuIcon) {
		this.stuIcon = stuIcon;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
