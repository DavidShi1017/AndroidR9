package com.nmbs.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nmbs.R;
import com.nmbs.model.Parking;

import java.util.List;


public class StationInfoDetailParkingAdapter{

	private LayoutInflater layoutInflater;
	private List<Parking> parkingList;
	public StationInfoDetailParkingAdapter(Context context, List<Parking> objects){
		this.parkingList = objects;
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void getParkingView(int position, LinearLayout linearLayout) {
		View convertView = null;
		if(convertView == null)
			convertView = layoutInflater.inflate(R.layout.station_info_detail_parking_item_view, null);
			
		TextView title = (TextView) convertView.findViewById(R.id.tv_station_info_detail_parking_title);
		TextView address = (TextView) convertView.findViewById(R.id.tv_station_info_detail_parking_address);
		TextView content = (TextView) convertView.findViewById(R.id.tv_station_info_detail_parking_content);
		if(parkingList != null && parkingList.size() > 0){
			if(parkingList.get(position) != null){
				if(parkingList.get(position).getAddress() != null && !parkingList.get(position).getAddress().isEmpty()){
					address.setText(parkingList.get(position).getAddress());
				}else{
					address.setVisibility(View.GONE);
				}
				title.setText(parkingList.get(position).getTitle());

				content.setText(parkingList.get(position).getContent());
			}
		}
		linearLayout.addView(convertView);
	}
}
