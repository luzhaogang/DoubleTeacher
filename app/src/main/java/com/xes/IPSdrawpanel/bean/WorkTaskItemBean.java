package com.xes.IPSdrawpanel.bean;
import java.io.Serializable;
import java.util.ArrayList;

public class WorkTaskItemBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public String paperId;// �Ծ�id
	public String paperName; // ����
	public String className; // �༶����
	//public String classId; // �༶����
	public String stageCode; // �׶δ��� 1����ǰ 2������ 3���κ�
	public String publishTime;// ����ʱ��
	public String masterTeaId;// ������ʦid
	public String tchName;// 
	public String teacherName;//������ʦ����;
	public String stuNums;// �༶������
	public String submitNums;// �ύ������
	public String unsubmitNums;// δ�ύ����
	public String alreadyCorrectedNums;// ����������
	//public String notCorrectCount;// δ��������
	public String paperStatus; // �Ծ�״̬ 0�������� 1���ѷ��� 2�������� 
	public String publishWordId;//������ҵ����ID
	public ArrayList<StudentBean> alreadyCorrectedList = new ArrayList<StudentBean>();
	public ArrayList<StudentBean> unsubmitList = new ArrayList<StudentBean>();
}
