package com.cflint.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cflint.R;
import com.cflint.model.StationMapInfo;

import java.util.List;


public class StationInfoMapItemAdapter extends BaseAdapter{

	private LayoutInflater layoutInflater;
	private List<StationMapInfo> staionMapInfoList;
	public StationInfoMapItemAdapter(Context context, List<StationMapInfo> objects){
		this.staionMapInfoList = objects;
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return staionMapInfoList.size();
	}

	public StationMapInfo getItem(int position) {

		return staionMapInfoList.get(position);
	}

	public long getItemId(int position) {

		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(convertView == null)
			convertView = layoutInflater.inflate(R.layout.station_info_map_item_view, null);

		LinearLayout iconLayout = (LinearLayout) convertView.findViewById(R.id.ll_station_info_map_icon_layout);

		ImageView iconView = (ImageView) convertView.findViewById(R.id.iv_station_info_map_icon);

		TextView name = (TextView) convertView.findViewById(R.id.tv_station_info_map_station_name);

		TextView address = (TextView) convertView.findViewById(R.id.tv_station_info_map_station_address);

		if(staionMapInfoList.get(position).isParking()){
			iconLayout.setBackgroundResource(R.drawable.round_blue_background);
			iconView.setImageResource(R.drawable.ic_stationinfo_parking);
		}else{
			iconLayout.setBackgroundResource(R.drawable.round_pink_background);
			iconView.setImageResource(R.drawable.ic_stationinfo_train);
		}

		name.setText(staionMapInfoList.get(position).getName());

		address.setText(staionMapInfoList.get(position).getAddress());
		
		return convertView;
	}
}
