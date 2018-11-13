package com.cflint.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cflint.R;

public class PhoneNumberDialogAdapter extends BaseAdapter {

	final static class ViewHolder {
		public TextView textView;
		public ImageView imageView;

	}
	private LayoutInflater layoutInflater;
	Context context;

	private String prefixArray[];
	private int logoArray[];

	public PhoneNumberDialogAdapter(Context context, String prefixArray[],int logoArray[]){
		this.context = context;
		this.logoArray = logoArray;
		this.prefixArray = prefixArray;
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return prefixArray.length;
	}

	public String getItem(int position) {
		return prefixArray[position];
	}

	public long getItemId(int position) {

		return position;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;		
		
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.alert_dialog_phone_number_adapter, null);
			viewHolder = new ViewHolder();			
			viewHolder.textView = (TextView)convertView.findViewById(R.id.tv_alert_dialog_phone_number_text);
			viewHolder.imageView = (ImageView)convertView.findViewById(R.id.iv_alert_dialog_phone_number_logo);
			convertView.setTag(viewHolder);				
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}		
		
		viewHolder.textView.setText(prefixArray[position]);
		viewHolder.imageView.setImageResource(logoArray[position]);
		
		return convertView;
	}


}
