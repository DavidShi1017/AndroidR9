package com.nmbs.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nmbs.R;

import com.nmbs.model.StationInformationResult;


public class TicketDetailStationAdapter extends ArrayAdapter<StationInformationResult>{

	final static class ViewHolder {
		public TextView nameView;
	}
	
	private LayoutInflater layoutInflater;
	public TicketDetailStationAdapter(Context context, int textViewResourceId,
			List<StationInformationResult> objects) {
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
			convertView = layoutInflater.inflate(R.layout.ticket_detail_station_adapter, null);
			viewHolder = new ViewHolder();
			viewHolder.nameView = (TextView) convertView.findViewById(R.id.ticket_detail_station_adapter_station_name);	
			convertView.setTag(viewHolder);			
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.nameView.setText(this.getItem(position).getName());	

		return convertView;
	}



}
