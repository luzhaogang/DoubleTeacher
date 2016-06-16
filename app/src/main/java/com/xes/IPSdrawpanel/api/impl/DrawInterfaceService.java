package com.xes.IPSdrawpanel.api.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.baidubce.BceClientException;
import com.baidubce.BceServiceException;
import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.BosClientConfiguration;
import com.baidubce.services.bos.model.PutObjectResponse;
import com.xes.IPSdrawpanel.MyApplication;
import com.xes.IPSdrawpanel.api.ApiConstants;
import com.xes.IPSdrawpanel.bean.NewsLabelBean;
import com.xes.IPSdrawpanel.bean.ProblemBank;
import com.xes.IPSdrawpanel.bean.SubmitCorrectInfo;
import com.xes.IPSdrawpanel.bean.TeacherBean;
import com.xes.IPSdrawpanel.bean.areaBean;
import com.xes.IPSdrawpanel.dao.LoginAreaDao;
import com.xes.IPSdrawpanel.dao.NewsLabelBeanDao;
import com.xes.IPSdrawpanel.dao.ProblemBankDao;
import com.xes.IPSdrawpanel.dao.SubmitCorrectInfoDao;
import com.xes.IPSdrawpanel.fragment.PainterFragment1;
import com.xes.IPSdrawpanel.receiver.SendResultMessageReceiver;
import com.xes.IPSdrawpanel.util.DownloadManagerUtil;
import com.xes.IPSdrawpanel.util.OkHttpUtil;
import com.xes.IPSdrawpanel.util.OkHttpUtil.OkRequestListener;
import com.xes.IPSdrawpanel.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DrawInterfaceService {
	// 获取单个试题
	public static void checkQues(final String quesId) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("quesId", quesId);
		params.put("method", "takeQuesInfo");
		try {
			OkHttpUtil.getInstance().connectPost(ApiConstants.PPurL, params, new OkRequestListener() {
				@Override
				public void onSuccess(String response) {
					try {
						JSONObject json = new JSONObject(response);
						Log.e(Utility.LOG_TAG, "checkQues()获取单个试题" + json.toString());
						String rlt = json.getString("rlt");
						String recmsg = json.getString("msg");
						if (rlt.equals("true")) {
							if (Utility.JsonArrayIsNOTNull(json, "data")) {
								JSONObject array = json.getJSONObject("data");
								try {
									ProblemBank pb = new ProblemBank();
									pb.type = Integer.parseInt(array.getString("quesType"));
									pb.answer = Utility.escapeCharacter(array.getString("rightAnswer"));
									pb.analysis = Utility.escapeCharacter(array.getString("analysis"));
									pb.title = Utility.escapeCharacter(array.getString("content"));
									pb.analysisAudioUrl = array.getString("analysisAudioUrl");
									StringBuffer sb = new StringBuffer();
									if (Utility.JsonArrayIsNOTNull(array, "contentImg")) {
										JSONArray item = array.getJSONArray("contentImg");
										for (int s = 0; s < item.length(); s++) {
											if(!item.getString(s).equals("")){
												sb.append("<img src =" + item.getString(s) + ">");
											}
										}
										pb.contentImg = sb.toString();
										if (pb.type == 1 || pb.type == 2) {
											JSONArray options = array.getJSONArray("options");
											if (options.length() > 0) {
												JSONObject jo = options.getJSONObject(0);
												pb.a = Utility.escapeCharacter(jo.getString("content"));
											}
											if (options.length() > 1) {
												JSONObject jo = options.getJSONObject(1);
												pb.b = Utility.escapeCharacter(jo.getString("content"));
											}
											if (options.length() > 2) {
												JSONObject jo = options.getJSONObject(2);
												pb.c = Utility.escapeCharacter(jo.getString("content"));
											}
											if (options.length() > 3) {
												JSONObject jo = options.getJSONObject(3);
												pb.d = Utility.escapeCharacter(jo.getString("content"));
											}
										}
									}
									pb.quesId = quesId;
									ProblemBankDao PbDao = new ProblemBankDao();
									if(PbDao.getProblemBean(pb.quesId) != null){
										PbDao.deletePbBean(pb);
									}
									PbDao.saveClassBean(pb);
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						} else {

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onError(String errorMsg) {

				}
			});
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	// 获取待批改任务数量
	public static void takeWorkNum(final Handler myHandler) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", TeacherBean.getInstance().teaId);
		params.put("method", "takeWorkNum");
		try {
			OkHttpUtil.getInstance().connectPost(ApiConstants.PPurL, params, new OkRequestListener() {
				@Override
				public void onSuccess(String response) {
					try {
						JSONObject jo1 = new JSONObject(response);
						Log.e(Utility.LOG_TAG, "getUnCorrectNum()显示数量" + jo1.toString());
						String rlt = jo1.getString("rlt");
						String msg = jo1.getString("msg");
						if (rlt.equals("true")) {
							JSONObject data = jo1.getJSONObject("data");
							NewsLabelBean nb = new NewsLabelBean();
							SubmitCorrectInfoDao submitCorrectDao = new SubmitCorrectInfoDao();
							nb.teaId = TeacherBean.getInstance().teaId;
							nb.correctedNum = Integer.parseInt(data.getString("correctedNum"));
							nb.correctingNum = Integer.parseInt(data.getString("correctingNum"));
							if(PainterFragment1.getPainterFragment1().countentImageUrl != null && !PainterFragment1.getPainterFragment1().countentImageUrl.equals("")){
								nb.toBeCorrectNum = Integer.parseInt(data.getString("toBeCorrectNum"))+1;
							}else{
								nb.toBeCorrectNum = Integer.parseInt(data.getString("toBeCorrectNum"));
							}
							
							NewsLabelBeanDao newsLabelBeanDao = new NewsLabelBeanDao();
							NewsLabelBean newsLabelBean = newsLabelBeanDao.getNewsLabelBean(TeacherBean.getInstance().teaId);
							if (newsLabelBean == null) {
								newsLabelBeanDao.saveNewsLabelBean(nb);
								Message mess = new Message();
								mess.what = PainterFragment1.GETUNCORRECTNUMOK;
								myHandler.sendMessage(mess);
							} else {
								newsLabelBean.correctedNum = nb.correctedNum;
								newsLabelBean.correctingNum = nb.correctingNum;
								newsLabelBean.toBeCorrectNum = nb.toBeCorrectNum;
								if (newsLabelBeanDao.updateNewsLabelBean(newsLabelBean)) {
									Message mess = new Message();
									mess.what = PainterFragment1.GETUNCORRECTNUMOK;
									myHandler.sendMessage(mess);
								} else {
									Message mess = new Message();
									mess.what = PainterFragment1.GETUNCORRECTNUMERROR;
									myHandler.sendMessage(mess);
								}
							}

						} else {
							Log.e(Utility.LOG_TAG + "Method", "获取待批改数量takeWorkNum" + msg);
							Message mess = new Message();
							mess.what = PainterFragment1.GETUNCORRECTNUMERROR;
							myHandler.sendMessage(mess);
						}
					} catch (Exception e) {
						Message mess = new Message();
						mess.what = PainterFragment1.GETUNCORRECTNUMERROR;
						myHandler.sendMessage(mess);
						e.printStackTrace();
					}
				}

				@Override
				public void onError(String errorMsg) {
					Log.e(Utility.LOG_TAG + "Method", "获取待批改数量takeWorkNum" + errorMsg);
					Message mess = new Message();
					mess.what = PainterFragment1.GETUNCORRECTNUMERROR;
					myHandler.sendMessage(mess);
				}
			});
		} catch (IOException e) {
			Log.e(Utility.LOG_TAG + "Method", "获取待批改数量takeWorkNum" + e.toString());
			Message mess = new Message();
			mess.what = PainterFragment1.GETUNCORRECTNUMERROR;
			myHandler.sendMessage(mess);
			e.printStackTrace();
		}

	}

	// 提交百度BOS
	public static boolean uploadToBOS(final Context context, final String fileName, final SubmitCorrectInfo scinfo) {
		/**
		 * KEY_ID
		 */
		final String ACCESS_KEY_ID = "c580221debe44035963f446048ab12a7";
		/**
		 * ACCESS_KEY
		 */
		final String SECRET_ACCESS_KEY = "f27534f91e624ae2b7ee8eb518eb6f48";
		/** 百度文件服务器ips,BUCKET名称=tutor-server */
		final String BAIDU_BUCKET = "tutor";
		final String FILE_SERVER = "picture/" + Utility.changeTImeNow() + "/";
		/**
		 * 百度文件服务器地址
		 */
		final String BAIDU_EndPoint = "http://tutor-server.bj.bcebos.com/";
		final SubmitCorrectInfoDao scinfoDao = new SubmitCorrectInfoDao();
		try {
			BosClientConfiguration config = new BosClientConfiguration();
			// 设置HTTP最大连接数为10
			config.setMaxConnections(10);

			// 设置TCP连接超时为5000毫秒
			config.setConnectionTimeoutInMillis(1000);

			// 设置Socket传输数据超时的时间为2000毫秒
			config.setSocketTimeoutInMillis(30000);

			config.setCredentials(new DefaultBceCredentials(ACCESS_KEY_ID, SECRET_ACCESS_KEY));
			config.setEndpoint(BAIDU_EndPoint);
			BosClient client = new BosClient(config);
			// 获取Bucket的存在信息
			boolean exists = client.doesBucketExist(BAIDU_BUCKET); // 指定Bucket名称
			// 输出结果
			if (!exists) {
				client.createBucket(BAIDU_BUCKET);
			}
			File file = new File(MyApplication.filePath + fileName);
			if (file.exists()) {
				//cedeefc3-09c5-4687-b1d1-82531c1ddbba FILE_SERVER + fileName
				PutObjectResponse putObjectFromFileResponse = client.putObject(BAIDU_BUCKET,FILE_SERVER + fileName , file);
				// 打印ETag
				Log.e(Utility.LOG_TAG, "uploadToBOS" + "BOS提交成功------------" + putObjectFromFileResponse.getETag());
				scinfo.pictureUrlMD5 = "http://tutor-server.cdn.bcebos.com/tutor/"+FILE_SERVER + fileName;
				scinfoDao.updateSubmitCorrectInfo(scinfo);
				
			} else { 
				Log.e("uploadToBOS", "文件不存在");
				scinfoDao.deleteSubmitCorrectInfo(scinfo);
				modifyGetCorrectTask(TeacherBean.getInstance().teaId, scinfo.answerId, "3");
				return false;
			}

		} catch (BceServiceException e) {
			Log.e(Utility.LOG_TAG, "BOS提交失败");
			Log.e(Utility.LOG_TAG, "uploadToBOS ErrorCode: " + e.getErrorCode());
			Log.e(Utility.LOG_TAG, "uploadToBOS RequestId: " + e.getRequestId());
			Log.e(Utility.LOG_TAG, "uploadToBOS StatusCode: " + e.getStatusCode());
			Log.e(Utility.LOG_TAG, "uploadToBOS Message: " + e.getMessage());
			Log.e(Utility.LOG_TAG, "uploadToBOS ErrorType: " + e.getErrorType());
			scinfo.upLoadstate = 2;
			scinfo.submitCount += 1;
			scinfo.isBOSOK = 0;
			scinfo.pictureUrlMD5 =  fileName;
			scinfo.taskSubmitTime = System.currentTimeMillis();
			if (scinfo.submitCount == 3) {
				Log.e(Utility.LOG_TAG, "提交任务已超过3次");
				scinfo.upLoadstate = 0;
				scinfo.loadType = 1;
				// scinfoDao.updateSubmitCorrectInfo(scinfo);
				scinfoDao.deleteSubmitCorrectInfo(scinfo);
				modifyGetCorrectTask(TeacherBean.getInstance().teaId, scinfo.answerId, "3");
			} else {
				scinfoDao.updateSubmitCorrectInfo(scinfo); 
			}
			Intent intent = new Intent();
			Bundle bd = new Bundle();
			bd.putString("msg", "提交失败，请检查网络");
			intent.putExtras(bd);
			intent.setAction(SendResultMessageReceiver.ACTION_ERRO);
			context.sendBroadcast(intent);
			return false;
		} catch (BceClientException e) {
			Log.e(Utility.LOG_TAG, "BOS提交失败");
			Log.e(Utility.LOG_TAG, "uploadToBOS Message: " + e.getMessage());
			scinfo.upLoadstate = 2;
			scinfo.submitCount += 1;
			scinfo.isBOSOK = 0;
			scinfo.pictureUrlMD5 =  fileName;
			scinfo.taskSubmitTime = System.currentTimeMillis();
			if (scinfo.submitCount == 3) {
				Log.e(Utility.LOG_TAG, "提交任务已超过3次");
				scinfoDao.deleteSubmitCorrectInfo(scinfo);
				modifyGetCorrectTask(TeacherBean.getInstance().teaId, scinfo.answerId, "3");
			} else {
				scinfoDao.updateSubmitCorrectInfo(scinfo);
			}
			Intent intent = new Intent();
			Bundle bd = new Bundle();
			bd.putString("msg", "提交失败，请检查网络");
			intent.putExtras(bd);
			intent.setAction(SendResultMessageReceiver.ACTION_ERRO);
			context.sendBroadcast(intent);
			return false;
		}
		return true;
	}
	//提交BOS失败，提交文件给后台
	public static void submitTaskFile(final Context context, final SubmitCorrectInfo scinfo, final String filename){
		final SubmitCorrectInfoDao scinfoDao = new SubmitCorrectInfoDao();
		final ProblemBankDao PbDao = new ProblemBankDao();
		Map<String, String> params = new HashMap<String, String>();
		JSONObject json = new JSONObject();
		try {
			json.put("answerId", scinfo.answerId);
			json.put("result", scinfo.pigairesult);
			json.put("picture", scinfo.pictureUrlMD5);// 图片名称
			json.put("startTime", scinfo.startTime);// 开始批改时间
			json.put("endTime", scinfo.endTime);// 结束批改时间
		} catch (Exception e) {

		}
		params.put("desc", json.toString());
		
		try {
			OkHttpUtil.getInstance().Uploadpost(ApiConstants.UploadUrl, params, filename, new OkRequestListener(){
				@Override
				public void onSuccess(String response) {
					// TODO Auto-generated method stub
					JSONObject jo1;
					try {
						jo1 = new JSONObject(response);
						Log.e(Utility.LOG_TAG, "submitTaskFile()提交" + jo1.toString());
						String rlt = jo1.getString("rlt");
						if ("true".equals(rlt)) {
							Intent intent = new Intent();
							intent.setAction(SendResultMessageReceiver.ACTION_OK);
							context.sendBroadcast(intent, null);
							SubmitCorrectInfo sf = scinfoDao.getSubmitCorrectInfo(scinfo.answerId);
							if (sf != null) {
								scinfoDao.deleteSubmitCorrectInfo(sf);
							}
							ProblemBank pb = PbDao.getProblemBean(scinfo.quesId);
							if (pb != null) {
								PbDao.deletePbBean(pb);
							}
							deleteFile(scinfo.pictureUrl);
						}else{
							scinfoDao.deleteSubmitCorrectInfo(scinfo);
							modifyGetCorrectTask(TeacherBean.getInstance().teaId, scinfo.answerId, "3");
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				@Override
				public void onError(String errorMsg) {
					// TODO Auto-generated method stub
					scinfo.upLoadstate = 2;
					scinfo.submitCount += 1;
					scinfo.taskSubmitTime = System.currentTimeMillis();
					if (scinfo.submitCount == 3) {
						Log.e(Utility.LOG_TAG, "提交任务已超过3次");
						scinfoDao.deleteSubmitCorrectInfo(scinfo);
						modifyGetCorrectTask(TeacherBean.getInstance().teaId, scinfo.answerId, "3");
					} else {
						scinfoDao.updateSubmitCorrectInfo(scinfo);
					}
					Intent intent = new Intent();
					Bundle bd = new Bundle();
					bd.putString("msg", "提交失败，请检查网络");
					intent.putExtras(bd);
					intent.setAction(SendResultMessageReceiver.ACTION_ERRO);
					context.sendBroadcast(intent);
				}
			});
		} catch (IOException e1) {
			scinfo.upLoadstate = 2;
			scinfo.submitCount += 1;
			scinfo.taskSubmitTime = System.currentTimeMillis();
			if (scinfo.submitCount == 3) {
				Log.e(Utility.LOG_TAG, "提交任务已超过3次");
				scinfoDao.deleteSubmitCorrectInfo(scinfo);
				modifyGetCorrectTask(TeacherBean.getInstance().teaId, scinfo.answerId, "3");
			} else {
				scinfoDao.updateSubmitCorrectInfo(scinfo);
			}
			Intent intent = new Intent();
			Bundle bd = new Bundle();
			bd.putString("msg", "网络环境太差哦！提交失败。。。");
			intent.putExtras(bd);
			intent.setAction(SendResultMessageReceiver.ACTION_ERRO);
			context.sendBroadcast(intent);
		}
	}
	// 提交
	public static void submitCorrectTask(final Context context, final SubmitCorrectInfo scinfo, final String filename) {
		final SubmitCorrectInfoDao scinfoDao = new SubmitCorrectInfoDao();
		final ProblemBankDao PbDao = new ProblemBankDao();
		Map<String, String> params = new HashMap<String, String>();
		JSONObject json = new JSONObject();
		try {
			json.put("answerId", scinfo.answerId);
			json.put("result", scinfo.pigairesult);
			json.put("picture", scinfo.pictureUrlMD5);// 图片路径
			json.put("startTime", scinfo.startTime);// 开始批改时间
			json.put("endTime", scinfo.endTime);// 结束批改时间
		} catch (Exception e) {

		}
		params.put("desc", json.toString());
		params.put("method", "handInTask");
		try {
			OkHttpUtil.getInstance().connectPost(ApiConstants.PPurL, params, new OkRequestListener() {
				@Override
				public void onSuccess(String response) {
					// TODO Auto-generated method stub
					JSONObject jo1;
					try {
						jo1 = new JSONObject(response);
						Log.e(Utility.LOG_TAG, "submitCorrectTask()提交" + jo1.toString());
						String rlt = jo1.getString("rlt");
						if ("true".equals(rlt)) {
							Intent intent = new Intent();
							intent.setAction(SendResultMessageReceiver.ACTION_OK);
							context.sendBroadcast(intent, null);
							SubmitCorrectInfo sf = scinfoDao.getSubmitCorrectInfo(scinfo.answerId);
							if (sf != null) {
								scinfoDao.deleteSubmitCorrectInfo(sf);
							}
							ProblemBank pb = PbDao.getProblemBean(scinfo.quesId);
							if (pb != null) {
								PbDao.deletePbBean(pb);
							}
							deleteFile(scinfo.pictureUrl);
						}else{
							scinfoDao.deleteSubmitCorrectInfo(scinfo);
							modifyGetCorrectTask(TeacherBean.getInstance().teaId, scinfo.answerId, "3");
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				@Override
				public void onError(String errorMsg) {
					// TODO Auto-generated method stub
					scinfo.upLoadstate = 2;
					scinfo.submitCount += 1;
					scinfo.taskSubmitTime = System.currentTimeMillis();
					if (scinfo.submitCount == 3) {
						Log.e(Utility.LOG_TAG, "提交任务已超过3次");
						scinfoDao.deleteSubmitCorrectInfo(scinfo);
						modifyGetCorrectTask(TeacherBean.getInstance().teaId, scinfo.answerId, "3");
					} else {
						scinfoDao.updateSubmitCorrectInfo(scinfo);
					}
					Intent intent = new Intent();
					Bundle bd = new Bundle();
					bd.putString("msg", "提交失败，请检查网络");
					intent.putExtras(bd);
					intent.setAction(SendResultMessageReceiver.ACTION_ERRO);
					context.sendBroadcast(intent);
				}
			});
		} catch (IOException e1) {
			scinfo.upLoadstate = 2;
			scinfo.submitCount += 1;
			scinfo.taskSubmitTime = System.currentTimeMillis();
			if (scinfo.submitCount == 3) {
				Log.e(Utility.LOG_TAG, "提交任务已超过3次");
				scinfoDao.deleteSubmitCorrectInfo(scinfo);
				modifyGetCorrectTask(TeacherBean.getInstance().teaId, scinfo.answerId, "3");
			} else {
				scinfoDao.updateSubmitCorrectInfo(scinfo);
			}
			Intent intent = new Intent();
			Bundle bd = new Bundle();
			bd.putString("msg", "网络环境太差哦！提交失败。。。");
			intent.putExtras(bd);
			intent.setAction(SendResultMessageReceiver.ACTION_ERRO);
			context.sendBroadcast(intent);
		}

	}


	// 获取地区
	public static void getAreaTask() {
		final LoginAreaDao AreaDao = new LoginAreaDao();
		Map<String, String> params = new HashMap<String, String>();
		params.put("method", "takeCampus");
		try {
			OkHttpUtil.getInstance().connectPost(ApiConstants.PPurL, params, new OkRequestListener() {
				@Override
				public void onSuccess(String response) {
					// TODO Auto-generated method stub
					try {
						JSONObject json = new JSONObject(response);
						String rlt = json.getString("rlt");
						@SuppressWarnings("unused")
						String msg = json.getString("msg");
						if (rlt.equals("true")) {
							if (Utility.JsonArrayIsNOTNull(json, "data")) {
								JSONArray areaArr = json.getJSONArray("data");
								for (int i = 0; i < areaArr.length(); i++) {
									if (AreaDao.getAreaInfo(areaArr.getJSONObject(i).getString("id")) == null) {
										areaBean bean = new areaBean();
										bean.cityCode = areaArr.getJSONObject(i).getString("id");
										bean.cityName = areaArr.getJSONObject(i).getString("name");
										AreaDao.saveAreaInfo(bean);
									}
								}
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				@Override
				public void onError(String errorMsg) {
					// TODO Auto-generated method stub

				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 获取缓存批改任务
	public static void getCacheCorrectTask(final Context context, String teaId) {
		Map<String, String> params = new HashMap<String, String>();
		final ArrayList<String> list = new ArrayList<String>();
		params.put("userId", teaId);
		params.put("method", "cacheTasks");
		try {
			OkHttpUtil.getInstance().connectPost(ApiConstants.PPurL, params, new OkRequestListener() {
				@Override
				public void onSuccess(String response) {
					try {
						JSONObject json = new JSONObject(response);
						String rlt = json.getString("rlt");
						@SuppressWarnings("unused")
						String msg = json.getString("msg");
						Log.e(Utility.LOG_TAG, "getCacheCorrectTask()获取缓存任务------------------" + json.toString());
						if (rlt.equals("true")) {
							JSONObject jo = json.getJSONObject("data");
							if (Utility.JsonArrayIsNOTNull(jo, "correctInfoList")) {
								JSONArray jsonarray = jo.getJSONArray("correctInfoList");
								SubmitCorrectInfoDao scid = new SubmitCorrectInfoDao();
								for (int i = 0; i < jsonarray.length(); i++) {
									SubmitCorrectInfo si = new SubmitCorrectInfo();
									si.quesId = jsonarray.getJSONObject(i).getString("quesId");
									si.subject = jsonarray.getJSONObject(i).getString("subject");
									si.submittime = Long.parseLong(jsonarray.getJSONObject(i).getString("submitTime"));
									si.answerId = jsonarray.getJSONObject(i).getString("answerId");
									si.stuName = jsonarray.getJSONObject(i).getString("stuName");
									si.stuAnswer = jsonarray.getJSONObject(i).getString("stuAnswer");
									si.pictureUrlMD5 = Utility.stringToMd5(si.stuAnswer);
									si.paperName = jsonarray.getJSONObject(i).getString("paperName");
									si.loadType = 2;
									si.getworkTaskTime = System.currentTimeMillis();
									scid.saveSubmitCorrectInfo(si);
									list.add(si.stuAnswer);
								}
							}
							Log.e(Utility.LOG_TAG, "getCacheCorrectTask()获取缓存任务数量 为------" + list.size());
							DownloadManagerUtil.updateStudentClient(list);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onError(String errorMsg) {
					Log.e(Utility.LOG_TAG, errorMsg);
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 获取批改任务
	public static SubmitCorrectInfo getCorrectTask(String teaId) {
		Map<String, String> params = new HashMap<String, String>();
		JSONObject json = new JSONObject();
		try {
			json.put("teaId", teaId);
		} catch (Exception e) {
		}
		params.put("userId", teaId);
		params.put("method", "takeWorkTask");
		final SubmitCorrectInfo si = new SubmitCorrectInfo();
		try { 
			OkHttpUtil.getInstance().connectPost(ApiConstants.PPurL, params, new OkRequestListener() {
				@Override
				public void onSuccess(String response) {
					try {
						JSONObject json = new JSONObject(response);
						String rlt = json.getString("rlt");
						String msg = json.getString("msg");
						Log.e(Utility.LOG_TAG, "getCorrectTask()获取任务" + json.toString());
						if (rlt.equals("true")) {
							JSONObject jo = json.getJSONObject("data");
							si.quesId = jo.getString("quesId");
							si.subject = jo.getString("subject");
							si.submittime = Long.parseLong(jo.getString("submitTime"));
							si.answerId = jo.getString("answerId");
							si.stuName = jo.getString("stuName");
							si.stuAnswer = jo.getString("stuAnswer");
							si.paperName = jo.getString("paperName");
							si.loadType = 1;
							si.getworkTaskTime = System.currentTimeMillis();
							si.result = "1";
						} else {
							si.result = "2";
							si.msg = msg;
						}
					} catch (Exception e) {
						si.result = "3";
						e.printStackTrace();
					}
				}

				@Override
				public void onError(String errorMsg) {
					si.result = "3";
					si.msg = "网络不给力请重试";
				}
			});
		} catch (IOException e) {
			si.result = "3";
			si.msg = "网络不给力请重试";
			e.printStackTrace();
		}
		return si;
	}

	// 通知服务端正在批改哪个任务
	// status 0(未批改) 1(批改中) 2（批改完成） 3（提交失败）
	public static void modifyGetCorrectTask(String teaId, String answerId, String status) {
		Map<String, String> params = new HashMap<String, String>();
		JSONObject json = new JSONObject();
		try {
			json.put("status", status);
			json.put("answerId", answerId);
			json.put("teaId", teaId);
		} catch (Exception e) {
		}
		params.put("desc", json.toString());
		params.put("method", "notifyTask");
		try {
			OkHttpUtil.getInstance().connectPost(ApiConstants.PPurL, params, new OkRequestListener() {
				@Override
				public void onSuccess(String response) {
					try {
						JSONObject json = new JSONObject(response);
						Log.e(Utility.LOG_TAG, "modifyGetCorrectTask()通知服务端正在批改哪个任务" + json.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onError(String errorMsg) {
					Log.e(Utility.LOG_TAG, "modifyGetCorrectTask()通知服务端正在批改哪个任务" + errorMsg);
				}
			});
		} catch (IOException e) {
			Log.e(Utility.LOG_TAG, "modifyGetCorrectTask()通知服务端正在批改哪个任务" + e.toString());
			e.printStackTrace();
		}
	}

	// 登陆
	public static TeacherBean loginWithCode(final String username, final String password) {
		Map<String, String> params = new HashMap<String, String>();
		JSONObject json = new JSONObject();
		try {
			json.put("name", username);
			json.put("password", password);
		} catch (Exception e) {

		}
		params.put("method", "tutorLogin");
		params.put("desc", json.toString());
		final TeacherBean teacher = TeacherBean.getInstance();
		try {
			OkHttpUtil.getInstance().connectPost(ApiConstants.PPurL, params, new OkRequestListener() {
				@Override
				public void onSuccess(String response) {
					try {
						JSONObject jo1 = new JSONObject(response);
						Log.e(Utility.LOG_TAG, "loginWithCode()登陆" + jo1.toString());
						String rlt = jo1.getString("rlt");
						String msg = jo1.getString("msg");
						if (rlt.equals("true")) {
							JSONObject jo = jo1.getJSONObject("data");
							teacher.teaId = jo.getString("tutorId");
							teacher.phone = username;
							teacher.password = jo.getString("password");
							teacher.areaCode = jo.getString("areaCode");
							teacher.result = TeacherBean.SUCCESS;
							teacher.icon = jo.getString("icon");
							teacher.timStamp = jo.getLong("timStamp");
							teacher.timeZone = jo.getString("timeZone");
						} else {
							teacher.recmsg = msg;
							teacher.result = TeacherBean.FAIL;
						}
					} catch (Exception e) {
						teacher.recmsg = e.toString();
						teacher.result = TeacherBean.FAIL;
						e.printStackTrace();
					}
				}

				@Override
				public void onError(String errorMsg) {
					teacher.recmsg = "网络不给力请重试";
					teacher.result = TeacherBean.FAIL;
				}
			});
		} catch (IOException e) {
			teacher.recmsg = null;
			teacher.result = TeacherBean.FAIL;
			e.printStackTrace();
		}
		return teacher;
	}

	// 更改老师在线状态
	public static TeacherBean modifyStatus(final String flag) {
		Map<String, String> params = new HashMap<String, String>();
		JSONObject json = new JSONObject();
		try {
			json.put("teaId", TeacherBean.getInstance().teaId);
			json.put("status", flag);
		} catch (Exception e) {
			// TODO: handle exception
		}
		params.put("desc", json.toString());
		params.put("method", "modifyStatus");
		final TeacherBean bean = TeacherBean.getInstance();
		try {
			OkHttpUtil.getInstance().connectPost(ApiConstants.PPurL, params, new OkRequestListener() {
				@Override
				public void onSuccess(String response) {
					try {
						JSONObject json = new JSONObject(response);
						Log.e(Utility.LOG_TAG, "modifyStatus()更改老师状态" + json.toString());
						String rlt = json.getString("rlt");
						if (rlt.equals("true")) {
							bean.result = 1;
						} else {
							bean.result = 0;
						}
					} catch (Exception e) {
						bean.result = 0;
						e.printStackTrace();
					}
				}

				@Override
				public void onError(String errorMsg) {
					bean.result = 0;
				}
			});
		} catch (IOException e) {
			bean.result = 0;
			e.printStackTrace();
		}
		return bean;
	}

	// 下载图片404失败通知
	public static void errTaskHandler(final SubmitCorrectInfo submitCorrectInfo, String reason) {
		final SubmitCorrectInfoDao scinfoDao = new SubmitCorrectInfoDao();
		Map<String, String> params = new HashMap<String, String>();
		JSONObject json = new JSONObject();
		try {
			json.put("taskId", submitCorrectInfo.answerId);
			json.put("reason", reason);
		} catch (Exception e) {
		}
		params.put("method", "errTaskHandler");
		params.put("desc", json.toString());
		try {
			OkHttpUtil.getInstance().connectPost(ApiConstants.PPurL, params, new OkRequestListener() {
				@Override
				public void onSuccess(String response) {
					try {
						JSONObject jo1 = new JSONObject(response);
						String rlt = jo1.getString("rlt");
						String msg = jo1.getString("msg");
						if (rlt.equals("true")) {
							Log.e(Utility.LOG_TAG, "errTaskHandler成功" + jo1.toString());
							scinfoDao.deleteSubmitCorrectInfo(submitCorrectInfo);
						} else {
							Log.e(Utility.LOG_TAG, "errTaskHandler失败" + jo1.toString());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onError(String errorMsg) {
					Log.e(Utility.LOG_TAG, "errTaskHandler失败" + errorMsg);
				}
			});
		} catch (IOException e) {
			Log.e(Utility.LOG_TAG, "errTaskHandler失败" + e.toString());
			e.printStackTrace();
		}
	}

	public static void deleteFile(String pictureUrl) {
		try {
			File filepic = new File(MyApplication.filePath + Utility.md5(pictureUrl) + Utility.getFileExtension(pictureUrl));
			if (filepic.exists()) {
				filepic.delete();
			}
			File filepic1 = new File(MyApplication.filePath + Utility.stringToMd5(pictureUrl) + ".jpg");
			if (filepic1.exists()) {
				filepic1.delete();
			}
			File filepic2 = new File(MyApplication.filePath + Utility.stringToMd5(pictureUrl) + ".amr");
			if (filepic2.exists()) {
				filepic2.delete();
			}
			File filepic3 = new File(MyApplication.filePath + Utility.stringToMd5(pictureUrl) + ".zip");
			if (filepic3.exists()) {
				filepic3.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
