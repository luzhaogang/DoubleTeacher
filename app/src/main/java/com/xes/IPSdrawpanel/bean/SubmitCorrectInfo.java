package com.xes.IPSdrawpanel.bean;



import java.io.Serializable;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * @author Administrator
 *
 */
@Table(name="SubmitCorrectInfo")
public class SubmitCorrectInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	@Column(name="id", isId = true)
	public int id;
	@Column(name = "quesId")
	public String quesId;//试题ID
	@Column(name = "startTime")
	public long startTime;//开始批改时间
	@Column(name = "endTime")
	public long endTime;//结束批改时间
	@Column(name = "pigairesult")
	public String pigairesult;//批改结果  ----正确1 错误2 半对3
	@Column(name = "pictureUrl")
	public String pictureUrl;//批改图片网络地址
	@Column(name = "pictureUrlMD5")
	public String pictureUrlMD5;//批改图片网络地址
	@Column(name = "submitCount")
	public int submitCount=0;//提交次数
	@Column(name = "taskSubmitTime")
	public long taskSubmitTime;//提交时间
	@Column(name = "upLoadstate")
	public int upLoadstate = 0;//上传状态 :1服务器回复失败，2未连接到服务器，3提交失败  //4上传中
	@Column(name = "isBOSOK")
	public int isBOSOK = 1;//如果 == 0 ，上传BOS失败，把文件提交给高
	@Column(name = "getworkTaskTime")
	public long getworkTaskTime;//获取任务的时间
	@Column(name = "stuName")
	public String stuName;//当前学生 
	@Column(name = "submittime")
	public Long submittime;//提交时间
	@Column(name = "subject")
	public String subject; //科目
	@Column(name = "answerId")
	public String answerId;//学员答题结果id
	@Column(name = "stuAnswer")
	public String stuAnswer;//学员答题记录
	@Column(name = "paperName")
	public String paperName;//卷子名称
	@Column(name = "result")
    public String result;//网络请求返回结果状态
	@Column(name = "msg")
    public String msg;//网络请求返回结果记录
	@Column(name = "cacheIsSUC")
	public int cacheIsSUC=0;//1缓存成功 2缓存失败
	@Column(name = "loadType")
	public int loadType;//获取方式1在线获取，2缓存方式获取
	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	
	
	public String getQuesId()
	{
		return quesId;
	}
	public void setQuesId(String quesId)
	{
		this.quesId = quesId;
	}
	public long getStartTime()
	{
		return startTime;
	}
	public void setStartTime(long startTime)
	{
		this.startTime = startTime;
	}
	public long getEndTime()
	{
		return endTime;
	}
	public void setEndTime(long endTime)
	{
		this.endTime = endTime;
	}
	public String getPigairesult()
	{
		return pigairesult;
	}
	public void setPigairesult(String pigairesult)
	{
		this.pigairesult = pigairesult;
	}
	public String getPictureUrl()
	{
		return pictureUrl;
	}
	public void setPictureUrl(String pictureUrl)
	{
		this.pictureUrl = pictureUrl;
	}
	
	public int getSubmitCount()
	{
		return submitCount;
	}
	public void setSubmitCount(int submitCount)
	{
		this.submitCount = submitCount;
	}
	public int getUpLoadstate()
	{
		return upLoadstate;
	}
	public void setUpLoadstate(int upLoadstate)
	{
		this.upLoadstate = upLoadstate;
	}
	public String getStuName()
	{
		return stuName;
	}
	public void setStuName(String stuName)
	{
		this.stuName = stuName;
	}
	public Long getSubmittime()
	{
		return submittime;
	}
	public void setSubmittime(Long submittime)
	{
		this.submittime = submittime;
	}
	public String getSubject()
	{
		return subject;
	}
	public void setSubject(String subject)
	{
		this.subject = subject;
	}
	
	public String getAnswerId()
	{
		return answerId;
	}
	public void setAnswerId(String answerId)
	{
		this.answerId = answerId;
	}
	public String getStuAnswer()
	{
		return stuAnswer;
	}
	public void setStuAnswer(String stuAnswer)
	{
		this.stuAnswer = stuAnswer;
	}
	public String getPaperName()
	{
		return paperName;
	}
	public void setPaperName(String paperName)
	{
		this.paperName = paperName;
	}
	public long getTaskSubmitTime() {
		return taskSubmitTime;
	}
	public void setTaskSubmitTime(long taskSubmitTime) {
		this.taskSubmitTime = taskSubmitTime;
	}
	public int getCacheIsSUC() {
		return cacheIsSUC;
	}
	public void setCacheIsSUC(int cacheIsSUC) {
		this.cacheIsSUC = cacheIsSUC;
	}
	public String getPictureUrlMD5() {
		return pictureUrlMD5;
	}
	public void setPictureUrlMD5(String pictureUrlMD5) {
		this.pictureUrlMD5 = pictureUrlMD5;
	}
	public int getLoadType() {
		return loadType;
	}
	public void setLoadType(int loadType) {
		this.loadType = loadType;
	}
	
	
}
