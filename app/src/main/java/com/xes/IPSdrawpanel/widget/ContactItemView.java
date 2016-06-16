/**
 * 
 */
package com.xes.IPSdrawpanel.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xes.IPSdrawpanel.R;

public class ContactItemView extends RelativeLayout{

	private TextView name;
	
	private TextView number;
	
	private ViewGroup itemHeadLayout ;
	
	private View itemTopSep ;
	
	private View itemBottomSep ;
	
	private OnContactItemSelectedListener listener;
	private int itemId;
	
	public ContactItemView(Context context) {
		super(context);
		initView();
	}
	public ContactItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}
	public ContactItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	private void initView() {
		final LayoutInflater mLayoutInflater = LayoutInflater.from(getContext());
		View v = mLayoutInflater.inflate(R.layout.contact_item, null, false);
		addView(v);
		name = (TextView)v.findViewById(R.id.textview_contact_name);
		number = (TextView)v.findViewById(R.id.textview_contact_number);
		itemHeadLayout = (ViewGroup)v.findViewById(R.id.layout_contact_item_head);
		itemTopSep = (View)v.findViewById(R.id.view_contact_item_top_sep);
		itemBottomSep = (View)v.findViewById(R.id.view_contact_item_bottom_sep);
		
		this.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(listener != null){
					listener.onContactItemSelected(itemId, name.getText(), number.getText());
				}
			}
		});
	}
	
	public interface OnContactItemSelectedListener{
		public void onContactItemSelected(int itemId, CharSequence name, CharSequence number);
	}
	
	public void setOnContactItemSelectedListener(OnContactItemSelectedListener listener){
		this.listener = listener;
	}
	
	public void setItemId(int itemId){
		this.itemId = itemId;
	}
	
	public TextView getName() {
		return name;
	}
	public void setName(TextView name) {
		this.name = name;
	}
	public TextView getNumber() {
		return number;
	}
	public void setNumber(TextView number) {
		this.number = number;
	}
	public ViewGroup getItemHeadLayout() {
		return itemHeadLayout;
	}
	public void setItemHeadLayout(ViewGroup itemHeadLayout) {
		this.itemHeadLayout = itemHeadLayout;
	}
	public View getItemTopSep() {
		return itemTopSep;
	}
	public void setItemTopSep(View itemSep) {
		this.itemTopSep = itemSep;
	}
	public View getItemBottomSep() {
		return itemBottomSep;
	}
	public void setItemBottomSep(View itemBottomSep) {
		this.itemBottomSep = itemBottomSep;
	}

}
