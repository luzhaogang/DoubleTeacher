package com.xes.IPSdrawpanel.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import android.util.Log;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Request.Builder;
import com.xes.IPSdrawpanel.MyApplication;
import com.xes.IPSdrawpanel.dao.LoginAreaDao;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class OkHttpUtil {

	private static OkHttpUtil instance;
	public static OkHttpClient mOkHttpClient;
	private LoginAreaDao AreaDao = new LoginAreaDao();
	public String areaCode;
	public static OkHttpUtil getInstance() {
		if (null == instance) {
			instance = new OkHttpUtil();
		}
		return instance;
	}

	public OkHttpUtil() {
		if (null == mOkHttpClient) {
			mOkHttpClient = new OkHttpClient();
			mOkHttpClient.setReadTimeout(15, TimeUnit.SECONDS);
			mOkHttpClient.setWriteTimeout(15, TimeUnit.SECONDS);
			mOkHttpClient.setConnectTimeout(15, TimeUnit.SECONDS);
		}
	}

	public interface OkRequestListener {

		void onSuccess(String response);

		void onError(String errorMsg);
	}

	public interface OkDownloadRequestListener {

		void onSuccess(String response);

		void onErrorCode(int code, String errorMsg);
	}

	public void connectPost(String url, Map<String, String> params, final OkRequestListener requestListener) throws IOException {
		FormEncodingBuilder fe = new FormEncodingBuilder();
		String method = "";
		areaCode = AreaDao.getAreaInfos().get(MyApplication.getSharedPreferences().getInt("areaPos", 0)).getCityCode();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			if("method".equals(entry.getKey())){
				method = entry.getValue();
			}
			fe.add(entry.getKey(), entry.getValue());
		}
		Log.e(Utility.LOG_TAG, "method>>>>>>>>>>>>>>>>>>>>>>>>>"+method);
		RequestBody formBody = fe.build();
		Builder requestBuilder =  new Request.Builder();
		requestBuilder.tag(method);
		Request requests = requestBuilder.url(url).post(formBody).addHeader("area", areaCode).addHeader("plat","tutor-server").addHeader("sys","xes-tutor").build();
		Call call = mOkHttpClient.newCall(requests);
		Response response;
		response = call.execute();// 同步请求
		if (response.isSuccessful()) {
			requestListener.onSuccess(response.body().string());
		} else {
			requestListener.onError(response.body().string());
		}

		/*
		 * //请求加入调度 //异步请求 call.enqueue(new Callback() {
		 * 
		 * @Override public void onFailure(Request request, IOException e) {
		 * requestListener.onError(e.toString()); }
		 * 
		 * @Override public void onResponse(final Response response) throws
		 * IOException { requestListener.onSuccess(response.body().string()); }
		 * });
		 */
	}

	public void connectGet(String url, Map<String, String> params, final OkRequestListener requestListener) throws IOException {
		if (params != null) {
			StringBuffer sb = new StringBuffer();
			sb.append(url).append("?");
			for (Map.Entry<String, String> entry : params.entrySet()) {
				sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
			}
			url = sb.substring(0, sb.length() - 1);
		}
		areaCode = AreaDao.getAreaInfos().get(MyApplication.getSharedPreferences().getInt("areaPos", 0)).getCityCode();
		final Request request = new Request.Builder().url(url).addHeader("area", areaCode).addHeader("plat","tutor-server").addHeader("sys","xes-tutor").build();
		Call call = mOkHttpClient.newCall(request);

		Response response;
		response = call.execute();// 同步请求
		if (response.isSuccessful()) {
			requestListener.onSuccess(response.body().string());
		} else {
			requestListener.onError(response.body().string());
		}

		/*
		 * //请求加入调度 异步请求 call.enqueue(new Callback() {
		 * 
		 * @Override public void onFailure(Request request, IOException e) {
		 * requestListener.onError(e.toString()); }
		 * 
		 * @Override public void onResponse(final Response response) throws
		 * IOException { requestListener.onSuccess(response.body().string()); }
		 * });
		 */
	}

	public void Uploadpost(String url, Map<String, String> params, final String filepath, final OkRequestListener requestListener) throws IOException {
		File file = new File(filepath);
		areaCode = AreaDao.getAreaInfos().get(MyApplication.getSharedPreferences().getInt("areaPos", 0)).getCityCode();
		MultipartBuilder mb = new MultipartBuilder();
		mb.type(MultipartBuilder.FORM);
		for (Map.Entry<String, String> entry : params.entrySet()) {
			mb.addFormDataPart(entry.getKey(), entry.getValue());
		}
		mb.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse(guessMimeType(file.getName())), file));
		RequestBody requestBody = mb.build();
		Request request = new Request.Builder().url(url).post(requestBody).addHeader("area", areaCode).addHeader("plat","tutor-server").addHeader("sys","xes-tutor").build();

		Call call = mOkHttpClient.newCall(request);
		Response response;
		response = call.execute();// 同步请求
		if (response.isSuccessful()) {
			requestListener.onSuccess(response.body().string());
		} else {
			requestListener.onError(response.body().string());
		}

		/*
		 * //请求加入调度 异步请求 call.enqueue(new Callback() {
		 * 
		 * @Override public void onFailure(Request request, IOException e) {
		 * requestListener.onError(e.toString()); }
		 * 
		 * @Override public void onResponse(final Response response) throws
		 * IOException { requestListener.onSuccess(response.body().string()); }
		 * });
		 */

	}

	/**
	 * 异步下载文件
	 * 
	 * @param url
	 * @param destFileDir
	 *            本地文件存储的文件夹
	 * @param callback
	 * @throws IOException
	 */
	public void downloadFile(final String url, final OkDownloadRequestListener requestListener) throws IOException {
		areaCode = AreaDao.getAreaInfos().get(MyApplication.getSharedPreferences().getInt("areaPos", 0)).getCityCode();
		final Request request = new Request.Builder().url(url).addHeader("area", areaCode).addHeader("plat","tutor-server").addHeader("sys","xes-tutor").build();
		final Call call = mOkHttpClient.newCall(request);

		Response response;
		response = call.execute();// 同步请求
		if (response.isSuccessful()) {
			InputStream is = null;
			byte[] buf = new byte[2048];
			int len = 0;
			FileOutputStream fos = null;
			try {
				is = response.body().byteStream();
				File file = new File(MyApplication.filePath + Utility.stringToMd5(url) + Utility.getFileExtension(url));
				fos = new FileOutputStream(file);
				while ((len = is.read(buf)) != -1) {
					fos.write(buf, 0, len);
				}
				fos.flush();
				// 如果下载文件成功，第一个参数为文件的绝对路径
				Log.e(Utility.LOG_TAG, "下载文件成功。。。。。");
				requestListener.onSuccess(file.getAbsolutePath());
			} catch (IOException e) {
				Log.e(Utility.LOG_TAG, "下载文件失败。。。。。" + response.body().string());
				requestListener.onErrorCode(response.code(), response.body().string());
			} finally {
				try {
					if (is != null)
						is.close();
				} catch (IOException e) {
				}
				try {
					if (fos != null)
						fos.close();
				} catch (IOException e) {
				}
			}

		} else {
			Log.e(Utility.LOG_TAG, "下载文件失败。。。。。" + response.body().string() + response.code());
			requestListener.onErrorCode(response.code(), response.body().string());
		}

		// 异步加载
		/*
		 * call.enqueue(new Callback() {
		 * 
		 * @Override public void onFailure(final Request request, final
		 * IOException e) { requestListener.onError(response.body().string()); }
		 * 
		 * @Override public void onResponse(Response response) { InputStream is
		 * = null; byte[] buf = new byte[2048]; int len = 0; FileOutputStream
		 * fos = null; try { is = response.body().byteStream(); File file = new
		 * File(destFileDir, getFileName(url)); fos = new
		 * FileOutputStream(file); while ((len = is.read(buf)) != -1) {
		 * fos.write(buf, 0, len); } fos.flush(); //如果下载文件成功，第一个参数为文件的绝对路径
		 * requestListener.onSuccess(file.getAbsolutePath()); } catch
		 * (IOException e) { requestListener.onError(response.body().string());
		 * } finally { try { if (is != null) is.close(); } catch (IOException e)
		 * { } try { if (fos != null) fos.close(); } catch (IOException e) { } }
		 * 
		 * } });
		 */
	}

	private String guessMimeType(String path) {
		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		String contentTypeFor = fileNameMap.getContentTypeFor(path);
		if (contentTypeFor == null) {
			contentTypeFor = "application/octet-stream";
		}
		return contentTypeFor;
	}
}
