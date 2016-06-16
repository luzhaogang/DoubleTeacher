package com.xes.IPSdrawpanel.service;

import com.xes.IPSdrawpanel.activity.BaseActivity;

import android.app.Service;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;


public class NetService extends Service {

    private long total_data = TrafficStats.getTotalRxBytes();
    private Handler mHandler;
    //几秒刷新一次
    private final int count = 5;

    /**
     * 定义线程周期性地获取网速
     */
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            //定时器
            mHandler.postDelayed(mRunnable, count * 1000);
            Message msg = mHandler.obtainMessage();
            msg.what = 1;
            msg.arg1 = getNetSpeed();
            mHandler.sendMessage(msg);
        }
    };
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    //float real_data = (float)msg.arg1;
                	Bundle bdl  = new Bundle();
                	bdl.putInt("network", msg.arg1);
                  /*  if(msg.arg1  > 1024 ){
                    	
                    	Log.e(Utility.LOG_TAG, msg.arg1 / 1024 + "kb/s");
                        //System.out.println(msg.arg1 / 1024 + "kb/s");                    
                    }
                    else{
                    	Log.e(Utility.LOG_TAG, msg.arg1 + "b/s");
                       // System.out.println(msg.arg1 + "b/s");    
                    	
                    }*/
                    Intent intents = new Intent(BaseActivity.ACTION_NETWORKSPEED);
                    intents.putExtras(bdl);
            		sendBroadcast(intents);
                }
            }
        };
        
        

    }
    
    /**
     * 核心方法，得到当前网速
     * @return
     */
    private int getNetSpeed() {  
        long traffic_data = TrafficStats.getTotalRxBytes() - total_data;
        total_data = TrafficStats.getTotalRxBytes();
        return (int)traffic_data /count ;
    }

    /**
     * 启动服务时就开始启动线程获取网速
     */
    @Override
    public void onStart(Intent intent, int startId) {
        mHandler.postDelayed(mRunnable, 0);
    };

    /**
     * 在服务结束时删除消息队列
     */
    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(mRunnable);
        super.onDestroy();
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



}