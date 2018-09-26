package com.nmbs.adapter;

import java.util.List;
import com.nmbs.R;

import com.nmbs.model.CollectionItem;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * Adapter used on the result view to show the base info that user selected 
 *
 */
public class CollectionItemAdapter extends ArrayAdapter<CollectionItem>{

	final static class ViewHolder {
		public TextView nameView;
		public ImageView cityIcon;
	}
	
	private LayoutInflater layoutInflater;
	private List<CollectionItem> objects; 
	public CollectionItemAdapter(Context context, int textViewResourceId,
			List<CollectionItem> objects) {
		super(context, textViewResourceId, objects);
		this.objects = objects;  
		  
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public long getItemId(int position) {
		return position;
	}
	


	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {

		
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.alert_dialog_data_adapter, null);
			viewHolder = new ViewHolder();
			viewHolder.nameView = (TextView) convertView.findViewById(R.id.alert_dialog_data_adapter_text);	
			convertView.setTag(viewHolder);			
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.nameView.setText(objects.get(position).getLable());	
		return convertView;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.alert_dialog_data_adapter, null);
			viewHolder = new ViewHolder();
			viewHolder.nameView = (TextView) convertView.findViewById(R.id.alert_dialog_data_adapter_text);	
			convertView.setTag(viewHolder);			
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.nameView.setText(objects.get(position).getLable());	
		
		return convertView;
	}

}
