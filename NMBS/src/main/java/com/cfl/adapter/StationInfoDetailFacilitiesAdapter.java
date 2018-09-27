package com.cfl.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cfl.R;
import com.cfl.model.StationFacility;

import java.util.List;


public class StationInfoDetailFacilitiesAdapter{

	private LayoutInflater layoutInflater;
	private List<StationFacility> stationFacilityList;
	public StationInfoDetailFacilitiesAdapter(Context context, List<StationFacility> objects){
		this.stationFacilityList = objects;
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void getFacilitiesView(int position, LinearLayout linearLayout) {
		View convertView = null;
		if(convertView == null)
			convertView = layoutInflater.inflate(R.layout.station_info_detail_facilities_item_view, null);
			
		TextView nameView = (TextView) convertView.findViewById(R.id.tv_station_info_detail_facilities_title);
		
		nameView.setText(stationFacilityList.get(position).getTitle());

		linearLayout.addView(convertView);
	}
}
