/**
 * 
 */
package com.xes.IPSdrawpanel.widget;

import com.xes.IPSdrawpanel.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

public class SlideinDialog extends Dialog{

	private Window window = null;
	private int width;
	private int height;
	
	public SlideinDialog(Context context) {
		super(context);
	}

	public SlideinDialog(Context context, int theme) {
		super(context, theme);
	}

	protected SlideinDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public void windowDeploy(){
        window = getWindow();
        window.setWindowAnimations(R.style.AnimationSlidein);
        WindowManager.LayoutParams lp = window.getAttributes();
        int width = getWidth();
        int height = getHeight();
        if(width > 0){
            lp.width = width;
        }
        if(height > 0){
        	lp.height = height;
        }
        lp.gravity = Gravity.RIGHT;
        
        window.setAttributes(lp);
    }
	
	@Override
	public void show() {
		windowDeploy();
		super.show();
	}

	@Override
	public void hide() {
		super.hide();
	}

	public void destory(){
		window = null;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
}
