package com.xes.IPSdrawpanel.util;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.util.Log;


public class ImageDownLoader {

	private LruCache<String, Bitmap> mMemoryCache;
	private static ImageDownLoader imageDownLoader;
	private  OutputStream os;
	private InputStream is;
	
	public static  ImageDownLoader getImageDownLoader(){
		if(imageDownLoader==null){
			imageDownLoader = new ImageDownLoader();
			return imageDownLoader;
		}else{
			return imageDownLoader;
		}
	}
	
		
	public ImageDownLoader(){	
		int maxMemory = (int) Runtime.getRuntime().maxMemory();  
        int mCacheSize = maxMemory / 8;
		mMemoryCache = new LruCache<String, Bitmap>(mCacheSize){
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
			
		};
		
	}
	
	
	
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {  
	    if (getBitmapFromMemCache(key) == null && bitmap != null) {  
	        mMemoryCache.put(key, bitmap);  
	    }  
	}  
	 
	public Bitmap getBitmapFromMemCache(String key) {  
	    return mMemoryCache.get(key);  
	} 
	
	public Bitmap showCacheBitmap(final Activity context,final String url){
		 // final String filename = 	Util.stringToMd5(url)+".jpg";
		if(getBitmapFromMemCache(url) != null){
			Log.e(Utility.LOG_TAG, "缓存加载图片.........");
			return getBitmapFromMemCache(url);
		}		
		/*else{
			Bitmap	bitmap = null;
		 if(new File(XesliveApplication.filePath+filename).exists()){
			 Log.e(Util.LOG_TAG, "本地加载图片.........");
			 bitmap = BitmapUtil.getpathBitmap(XesliveApplication.filePath+filename);
			 return bitmap;
		}*/else{	
			Bitmap	bitmap = null;
			try {
 	    		if("http".equals(url.trim().substring(0,4))){
 	    			Log.e(Utility.LOG_TAG, "网络加载........."+url);
 	    			is = new URL(url).openStream();	
 	    		 	bitmap =  BitmapFactory.decodeStream(is);
 	    			if(bitmap==null){
 	    			 bitmap =Utility.getAssetsBitmap(context);
 	    			}
 	    		}else{
 	    			bitmap = Utility.getBitmap(context,url);
 	    		}
			} catch (IOException e) {
				 bitmap =Utility.getAssetsBitmap(context);
				 Log.e(Utility.LOG_TAG, "网络加载"+url+"出错");
				e.printStackTrace();
			}
			addBitmapToMemoryCache(url,bitmap);
		/*	//做本地文件缓存
 			new Thread(new Runnable() {
				@Override
				public void run() {
					 try {
					File file =	 new File(XesliveApplication.filePath+filename);
					if(file.exists()==false){
						file.createNewFile();
						os = new FileOutputStream(file);
						byte[] buffer = new byte[1024];
		 	   			   int length;
		 	   			   while ((length = is.read(buffer))>0){
		 	   				os.write(buffer, 0, length);
		 	   			   }
		 	   			   os.flush();
	 	 	   			   os.close();
	 	 	   			   is.close();
					}
					} catch (Exception e) {
						e.printStackTrace();
					}
 	    			
				}
			}).start(); */	  
			return bitmap;
		    } 	
		
	}
		

	public void clearCache(){
		mMemoryCache.evictAll();
	}

}