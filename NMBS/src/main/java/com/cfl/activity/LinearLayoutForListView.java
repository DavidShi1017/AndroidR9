package com.cfl.activity;

import android.content.Context;
import android.util.AttributeSet;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

public class LinearLayoutForListView extends LinearLayout {

	private ListAdapter adapter;
	private OnClickListener onClickListener = null;
	private OnTouchListener onTouchListener = null;

	public void bindLinearLayout() {
		int count = adapter.getCount();
		for (int i = 0; i < count; i++) {
			View v = adapter.getView(i, null, null);

			v.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			v.setOnTouchListener(this.onTouchListener);
			v.setOnClickListener(this.onClickListener);
			v.setId(i);
			addView(v, i);
		}
		//Log.v("countTAG", "" + count);
	}

	public LinearLayoutForListView(Context context) {
		super(context);
	}

	public LinearLayoutForListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * Get Adapter
	 * 
	 * @return adapter
	 */
	public ListAdapter getAdpater() {
		return adapter;
	}

	/**
	 * Set data
	 * 
	 * @param adpater
	 */
	public void setAdapter(ListAdapter adpater) {
		this.adapter = adpater;
		bindLinearLayout();
	}

	public OnClickListener getOnclickListner() {
		return onClickListener;
	}

	public void setOnclickLinstener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	public OnTouchListener getOnTouchListener() {
		return onTouchListener;
	}

	public void setOnTouchListener(OnTouchListener onTouchListener) {
		this.onTouchListener = onTouchListener;
	}

}
