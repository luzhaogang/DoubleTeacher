package com.xes.IPSdrawpanel.util;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.xes.IPSdrawpanel.MyApplication;
import com.xes.IPSdrawpanel.R;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.util.Log;
import android.widget.ImageView;

public class ImageHttpUtile {

	private static ImageLoader sImageLoader = MyApplication.getImageLoader();
	private static ImageHttpUtile instance;
	private static RequestQueue mQueue = MyApplication.mRequestQueue;

	public static ImageHttpUtile getInstance() {
		if (null == instance) {
			instance = new ImageHttpUtile();
		}
		return instance;
	}

	public interface RequestListener {

		void onSuccess(Bitmap response);

		void onError(VolleyError errorMsg);
	}

	// 缩率图
	public static void getImageBitmapRatio(String url, final RequestListener requestListener) {
		Log.e(Utility.LOG_TAG, url);
		ImageRequest imgRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
			@Override
			public void onResponse(Bitmap arg0) {
				requestListener.onSuccess(arg0);
			}
		}, 100, 100, Config.ARGB_8888, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				requestListener.onError(arg0);
				Log.e(Utility.LOG_TAG, "无法获取图片");
			}
		});
		mQueue.add(imgRequest);
	}

	// 原图
	public static void getImageBitmap(String url, final RequestListener requestListener) {

		ImageRequest imgRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
			@Override
			public void onResponse(Bitmap arg0) {
				requestListener.onSuccess(arg0);
			}
		}, 0, 0, Config.ARGB_8888, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				requestListener.onError(arg0);
			}
		});
		imgRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mQueue.add(imgRequest);
	}

	public static void setImageView(String url, ImageView imageView) {
		// imageView是一个ImageView实例
		// ImageLoader.getImageListener的第二个参数是默认的图片resource id
		// 第三个参数是请求失败时候的资源id，可以指定为0
		ImageListener listener = ImageLoader.getImageListener(imageView, android.R.drawable.ic_menu_rotate, android.R.drawable.ic_delete);
		sImageLoader.get(url, listener);
	}

	public static void setVolleyImageview(String url, NetworkImageView ImageView) {
		ImageView.setDefaultImageResId(R.drawable.pic_boy);
		ImageView.setErrorImageResId(R.drawable.pic_boy);
		ImageView.setImageUrl(url, sImageLoader);
	}
}
