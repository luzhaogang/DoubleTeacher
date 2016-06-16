package com.xes.IPSdrawpanel.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;
@Table(name="ProblemBank")
public class ProblemBank implements Serializable {
	private static final long serialVersionUID = 1L;
	@Column(name = "id", isId = true)
	public int id;
	@Column(name = "num")
	public int num;// 题号
	@Column(name = "knowledgepointid")
	public int knowledgepointid;
	@Column(name = "knowledgepoint")
	public String knowledgepoint;
	@Column(name = "title")
	public String title;
	@Column(name = "answer")
	public String answer;
	@Column(name = "a")
	public String a;
	@Column(name = "b")
	public String b;
	@Column(name = "c")
	public String c;
	@Column(name = "d")
	public String d;
	@Column(name = "e")
	public String e;
	@Column(name = "f")
	public String f;
	@Column(name = "provenance")
	public String provenance; // 出处
	@Column(name = "analysis")
	public String analysis;
	@Column(name = "fallibleparsing")
	public String fallibleparsing;
	@Column(name = "city")
	public String city;
	@Column(name = "term")
	public String term;
	@Column(name = "book_versions")
	public String book_versions;
	@Column(name = "selectedAnswer")
	public String selectedAnswer;
	@Column(name = "contentImg")
	public String contentImg = "";
	@Column(name = "analysisAudioUrl")
	public String analysisAudioUrl;
	@Column(name = "stuAnswerUrl")
	public String stuAnswerUrl;
	@Column(name = "errorReason")
	public String errorReason;
	@Column(name = "quesId")
	public String quesId;
	
	public String getQuesId() {
		return quesId;
	}

	public void setQuesId(String quesId) {
		this.quesId = quesId;
	}

	public String getErrorReason() {
		return errorReason;
	}

	public void setErrorReason(String errorReason) {
		this.errorReason = errorReason;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getStuAnswerUrl() {
		return stuAnswerUrl;
	}

	public void setStuAnswerUrl(String stuAnswerUrl) {
		this.stuAnswerUrl = stuAnswerUrl;
	}

	public String getContentImg() {
		return contentImg;
	}

	public void setContentImg(String contentImg) {
		this.contentImg = contentImg;
	}

	public String getAnalysisAudioUrl() {
		return analysisAudioUrl;
	}

	public void setAnalysisAudioUrl(String analysisAudioUrl) {
		this.analysisAudioUrl = analysisAudioUrl;
	}

	public String getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(String accuracy) {
		this.accuracy = accuracy;
	}

	public Map<String, String> getSelectRatio() {
		return selectRatio;
	}

	public void setSelectRatio(Map<String, String> selectRatio) {
		this.selectRatio = selectRatio;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<String> selectedAnswerImageUrl = new ArrayList<String>();
	public int pageid;
	public int type;
	// 正确�?
	public String accuracy;

	public Map<String, String> selectRatio = new HashMap<String, String>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getKnowledgepointid() {
		return knowledgepointid;
	}

	public void setKnowledgepointid(int knowledgepointid) {
		this.knowledgepointid = knowledgepointid;
	}

	public String getKnowledgepoint() {
		return knowledgepoint;
	}

	public void setKnowledgepoint(String knowledgepoint) {
		this.knowledgepoint = knowledgepoint;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getA() {
		return a;
	}

	public void setA(String a) {
		this.a = a;
	}

	public String getB() {
		return b;
	}

	public void setB(String b) {
		this.b = b;
	}

	public String getC() {
		return c;
	}

	public void setC(String c) {
		this.c = c;
	}

	public String getD() {
		return d;
	}

	public void setD(String d) {
		this.d = d;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getE() {
		return e;
	}

	public void setE(String e) {
		this.e = e;
	}

	public String getF() {
		return f;
	}

	public void setF(String f) {
		this.f = f;
	}

	public String getProvenance() {
		return provenance;
	}

	public void setProvenance(String provenance) {
		this.provenance = provenance;
	}

	public String getAnalysis() {
		return analysis;
	}

	public void setAnalysis(String analysis) {
		this.analysis = analysis;
	}

	public String getFallibleparsing() {
		return fallibleparsing;
	}

	public void setFallibleparsing(String fallibleparsing) {
		this.fallibleparsing = fallibleparsing;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getBook_versions() {
		return book_versions;
	}

	public void setBook_versions(String book_versions) {
		this.book_versions = book_versions;
	}

	public String getSelectedAnswer() {
		return selectedAnswer;
	}

	public void setSelectedAnswer(String selectedAnswer) {
		this.selectedAnswer = selectedAnswer;
	}

	public int getPageid() {
		return pageid;
	}

	public void setPageid(int pageid) {
		this.pageid = pageid;
	}

	public List<String> getSelectedAnswerImageUrl() {
		return selectedAnswerImageUrl;
	}

	public void setSelectedAnswerImageUrl(List<String> selectedAnswerImageUrl) {
		this.selectedAnswerImageUrl = selectedAnswerImageUrl;
	}


	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
