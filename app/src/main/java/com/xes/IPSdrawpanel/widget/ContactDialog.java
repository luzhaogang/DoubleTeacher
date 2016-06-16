/**
 * 
 */
package com.xes.IPSdrawpanel.widget;

import com.xes.IPSdrawpanel.R;
import com.xes.IPSdrawpanel.util.DensityUtil;
import com.xes.IPSdrawpanel.widget.ContactItemView.OnContactItemSelectedListener;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * @author jiashuai.xujs@alibaba-inc.com 2014-2-22 涓嬪崍1:51:58
 * 
 */
public class ContactDialog implements OnContactItemSelectedListener {

	private Context context;

	private SlideinDialog dialog;
	private TextView textview_contact_title;
	private OnContactSelectedListener listener;

	public ContactDialog(Context context, OnContactSelectedListener listener) {
		this.context = context;
		this.listener = listener;
	}

	public void show(int width, int height) {
		if (dialog == null) {
			initDialog(width, height);
		}
		if (!dialog.isShowing()) {
			dialog.show();
		}
	}

	public void hide() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	public void destroy() {
		hide();
		dialog = null;
	}

	private void initDialog(int width, int height) {
		dialog = new SlideinDialog(context, R.style.DefaultDialog);
		dialog.setCanceledOnTouchOutside(false);
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.contact_list, null);
		dialog.setContentView(view);
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				
			}
		});
		dialog.setWidth(DensityUtil.getWidth()/5);
		dialog.setHeight(DensityUtil.getHeight()*3/20);
		initViews(view);
	}

	private void initViews(View rootView) {
		TextView text1 = (TextView) rootView.findViewById(R.id.text1);
		text1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				listener.OnContactSelected(1, null, null);
			}
		});
		TextView text2 = (TextView) rootView.findViewById(R.id.text2);
		text2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				listener.OnContactSelected(2, null, null);
			}
		});
	}

	public interface OnContactSelectedListener {
		public void OnContactSelected(int itemId, CharSequence name, CharSequence number);
	}

	@Override
	public void onContactItemSelected(int itemId, CharSequence name, CharSequence number) {
		if (this.listener != null) {
			listener.OnContactSelected(itemId, name, number);
		}
	}

}
