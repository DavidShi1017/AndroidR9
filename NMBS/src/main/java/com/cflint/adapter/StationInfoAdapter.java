package com.cflint.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cflint.R;
import com.cflint.model.StationInfo;

import java.util.List;


public class StationInfoAdapter extends BaseAdapter{

	private LayoutInflater layoutInflater;
	private List<StationInfo> objects;
	public StationInfoAdapter(Context context, List<StationInfo> objects){
		this.objects = objects;
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return objects.size();
	}

	public StationInfo getItem(int position) {

		return objects.get(position);
	}

	public long getItemId(int position) {

		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(convertView == null)
			convertView = layoutInflater.inflate(R.layout.station_info_item_view, null);
			
		TextView nameView = (TextView) convertView.findViewById(R.id.tv_station_info_name);
		
		nameView.setText(objects.get(position).getName().toUpperCase());
		
		return convertView;
	}
}
