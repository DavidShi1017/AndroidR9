package com.cflint.adapter;

import java.util.List;
import com.cflint.R;

import com.cflint.model.Facility;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.TextView;
/**
 * Adapter used on the result view to show the base info that user selected 
 *
 */
public class FacilitiesAdapter extends ArrayAdapter<Facility>{

	
	final static class ViewHolder {
		public TextView name;
		
	}
	
	private LayoutInflater layoutInflater;
	public FacilitiesAdapter(Context context, int textViewResourceId,
			List<Facility> objects) {
		super(context, textViewResourceId, objects);
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
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
			
			convertView = layoutInflater.inflate(R.layout.facilities_adapter, null);
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) convertView.findViewById(R.id.facilities_adapter_adapter_facilitie_title);	
			
			convertView.setTag(viewHolder);			
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.name.setText("-" + this.getItem(position).getDescription());	

		return convertView;
	}

}
