package com.cfl.adapter;

import java.util.List;
import com.cfl.R;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.TextView;

public class AlertDialogDataAdapter extends ArrayAdapter<String> {

	final static class ViewHolder {
		public TextView textView;
		
		
	}	
	private LayoutInflater layoutInflater;
	Context context;

	public AlertDialogDataAdapter(Context context, int textViewResourceId,
			List<String> objects) {
		super(context, textViewResourceId, objects);
		
		this.context = context;
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public long getItemId(int position) {
		return position;
	}
    /**
     * {@inheritDoc}
     */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;		
		
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.alert_dialog_data_adapter, null);
			viewHolder = new ViewHolder();			
			viewHolder.textView = (TextView)convertView.findViewById(R.id.alert_dialog_data_adapter_text);								
			convertView.setTag(viewHolder);				
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}		
		
		viewHolder.textView.setText(this.getItem(position));	
		
		return convertView;
	}


}
