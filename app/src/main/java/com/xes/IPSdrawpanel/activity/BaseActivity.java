package com.xes.IPSdrawpanel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.xes.IPSdrawpanel.R;
import com.xes.huanxin.myapplication.HXSDKHelper;

import java.util.List;

public class BaseActivity extends FragmentActivity {
	
	public static final String ACTION_UPDATE_MESSAGE = "com.xes.IPSdrawpanel.fragment.update";
	public static final String ACTION_HUANXIN = "com.xes.IPSdrawpanel.HUANXIN";
	public static final String ACTION_NETWORKSPEED = "com.xes.IPSdrawpanel.NETWORKSPEED";
	public static final String ACTION_submit = "com.xes.IPSdrawpanel.ACTION_submit";
	public static final String ACTION_GUIDE_CLASS = "com.xes.IPSdrawpanel.ACTION_GUIDE_CLASS";
	public static final String ACTION_RESTART = "com.xes.IPSdrawpanel.ACTION_RESTART";
	public static final String ACTION_CHAT = "com.xes.IPSdrawpanel.ACTION_CHAT";
	public static final String  ACTION_EventListener = "com.xes.IPSdrawpanel.EventListener";
	public static final String CUSTOM_KEY_USERNAME = "userName";// 发送者的名字
	public static final String CUSTOM_KEY_AVATARURL = "avatarUrl";// 发送者的头像url
	public static final String ACTION_GUIDE_DISSMISS = "com.xes.IPSdrawpanel.ACTION_GUIDE_DISSMISS";

	public final static int UPDATEVIEW = 1;
	public final static int UPDATEVIEWERROR = 2;
	public final static int UPDATECLASSCONUT = 3;

	public enum AnimType {
		DISABLE, FADE, ZOOM,SIDE
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		  //http://stackoverflow.com/questions/4341600/how-to-prevent-multiple-instances-of-an-activity-when-it-is-launched-with-differ/
        //理论上应该放在launcher activity,放在基类中所有集成此库的app都可以避免此问题
        if(!isTaskRoot()){
            Intent intent = getIntent();
            String action = intent.getAction();
            if(intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)){
                finish();
                return;
            }
        }
	}

	public Fragment openFragment(Class<? extends Fragment> clazz, Bundle args, String tag, boolean addToBackStack, AnimType animType) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment fragment = fragmentManager.findFragmentByTag(tag);
		if (fragment != null && fragmentManager.getBackStackEntryCount() > 0) {
			fragmentManager.popBackStack(fragmentManager.getBackStackEntryAt(0).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}
		fragment = Fragment.instantiate(this, clazz.getName(), args);
		openFragment(fragment, tag, addToBackStack, animType);
		return fragment;
	}

	public void openFragment(Fragment fragment, String tag, boolean addToBackStack, AnimType animType) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction ft = fragmentManager.beginTransaction();
		if (addToBackStack) {
			ft.addToBackStack("F#"  + tag);
		}

		List<Fragment> fragmentList = fragmentManager.getFragments();
		if (fragmentList != null && fragmentList.size() > 0) {
			switch (animType) {
			case DISABLE:
				break;
			case FADE:
				ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
				break;
			case ZOOM:
				ft.setCustomAnimations(R.anim.zoom_open_enter, R.anim.zoom_open_exit, R.anim.zoom_close_enter, R.anim.zoom_close_exit);
				break;
			case SIDE:
				ft.setCustomAnimations(R.anim.side_in_right, R.anim.side_out_left, R.anim.side_in_left, R.anim.side_out_right);
			}
		}
		/*if(content ==1){
			ft.replace(R.id.content, fragment, tag);	
		}*/
	     ft.replace(R.id.content, fragment, tag);	
		ft.commit();
	}



	@Override
	protected void onResume() {
		super.onResume();
	    // onresume时，取消notification显示
        HXSDKHelper.getInstance().getNotifier().reset();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	}

	
	

}
