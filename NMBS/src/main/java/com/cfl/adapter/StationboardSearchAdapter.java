package com.cfl.adapter;

import java.util.Date;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.cfl.R;
import com.cfl.application.NMBSApplication;
import com.cfl.async.AsyncImageLoader;
import com.cfl.model.StationBoardRow;
import com.cfl.model.TrainIcon;
import com.cfl.services.impl.TrainIconsService;
import com.cfl.util.DateUtils;
import com.cfl.util.FileManager;
import com.cfl.util.ImageUtil;
import com.cfl.util.Utils;

public class StationboardSearchAdapter extends BaseExpandableListAdapter{
	private Activity context;
	private ExpandableListView listView;
	private TrainIconsService trainIconsService = NMBSApplication.getInstance().getTrainIconsService();
	private Map<String,List<StationBoardRow>> mapList;
	private final static String TAG = StationboardSearchAdapter.class.getSimpleName();
	private LayoutInflater mInflater;

	public StationboardSearchAdapter(Activity context, Map<String,List<StationBoardRow>> mapList, ExpandableListView listView){
		this.context = context;
		this.listView = listView;
		this.mapList = mapList;

		mInflater = LayoutInflater.from(context);
	}

	public int getGroupCount() {
		return mapList.keySet().size();
	}

	public int getChildrenCount(int groupPosition) {
		return mapList.get(mapList.keySet().toArray()[groupPosition]).size();
	}

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		if(convertView == null) convertView = mInflater.inflate(R.layout.li_stationboard_result_group, null);
		TextView tagDate = (TextView) convertView.findViewById(R.id.iv_date);
		TextView tvWatchout = (TextView) convertView.findViewById(R.id.tv_watchout);
		Date date = DateUtils.stringToDate(mapList.keySet().toArray()[groupPosition].toString());
		//Log.e("date", "date..." + mapList.keySet().toArray()[groupPosition].toString());
		tagDate.setText(DateUtils.dateTimeToString(date, "EEEE dd MMMM yyyy"));
		if (groupPosition == 0){
			tvWatchout.setVisibility(View.GONE);
			tagDate.setVisibility(View.GONE);
		}else{
			tvWatchout.setVisibility(View.VISIBLE);
			tagDate.setVisibility(View.VISIBLE);
		}

		return convertView;
	}




	public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.li_stationboard_result, null);
			//convertView = inflater.inflate(R.layout.li_stationboard_result, null);
			viewHolder = new ViewHolder();
			viewHolder.startDate = (TextView) convertView.findViewById(R.id.station_board_search_result_start_date);
			viewHolder.dateInfo = (TextView)convertView.findViewById(R.id.station_board_search_result_date_info);

			viewHolder.stationName = (TextView) convertView.findViewById(R.id.station_board_search_result_station_name);
			viewHolder.stationTrack = (TextView) convertView.findViewById(R.id.station_board_search_result_track);
			viewHolder.trainType = (ImageView) convertView.findViewById(R.id.station_board_search_result_train_type);
			viewHolder.trainNr = (TextView) convertView.findViewById(R.id.station_board_search_result_train_nr);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final StationBoardRow stationBoardRow = mapList.get(mapList.keySet().toArray()[groupPosition]).get(childPosition);
		if(stationBoardRow != null){
			if(stationBoardRow.isCancelled()){
				viewHolder.dateInfo.setText(R.string.general_cancelled);
			}else{

				if(stationBoardRow.getDelay() > 0){
					viewHolder.dateInfo.setText("+" + DateUtils.msToMinuteAndSecond((long)stationBoardRow.getDelay()));
				}else {
					viewHolder.dateInfo.setText("");
				}
			}
			viewHolder.stationTrack.setText(stationBoardRow.getTrack());
			viewHolder.trainNr.setText(stationBoardRow.getCarrier() + " " + stationBoardRow.getTrainNr());
			viewHolder.stationName.setText(stationBoardRow.getStationName());
			viewHolder.startDate.setText(DateUtils.FormatToHHMMFromDate(stationBoardRow.getDateTime()));
			final String tag = stationBoardRow.getStationName() + stationBoardRow.getDateTime() + stationBoardRow.getTrainNr();
			TrainIcon trainIcon = NMBSApplication.getInstance().getTrainIconsService().getTrainIcon(stationBoardRow.getCarrier());
			viewHolder.trainType.setTag(tag);
			if(trainIcon == null){
				if (viewHolder.trainType.getTag() != null && viewHolder.trainType.getTag().equals(tag)){
					viewHolder.trainType.setImageResource(ImageUtil.getTrainTypeImageId(stationBoardRow.getCarrier()));
				}
			}else{
				if(ImageUtil.getTrainTypeImageId(stationBoardRow.getCarrier()) == R.drawable.icon_train){
					String imageUrl = ImageUtil.convertImageExtension(trainIcon.getIcon(Utils.getDeviceDensity(context)));
					String fullImageUrl = context.getString(R.string.server_url_host) + imageUrl;
					AsyncImageLoader.getInstance().loadDrawable(context, fullImageUrl, imageUrl, viewHolder.trainType, null, new AsyncImageLoader.ImageCallback() {
						public void imageLoaded(Bitmap imageDrawable, String imageUrl, View view) {
							if(imageDrawable == null){
								((ImageView) view).setImageResource(ImageUtil.getTrainTypeImageId(stationBoardRow.getCarrier()));
							}else{
								if (view.getTag() != null && view.getTag().equals(tag)){
									((ImageView) view).setImageBitmap(imageDrawable);
								}
							}
						}});
				}else{
					viewHolder.trainType.setImageResource(ImageUtil.getTrainTypeImageId(stationBoardRow.getCarrier()));
				}
			}
		}
		return convertView;
	}

	private static class ViewHolder {
		TextView startDate;
		TextView dateInfo;

		TextView stationName;
		TextView stationTrack;
		ImageView trainType;
		TextView trainNr;
	}
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public Object getGroup(int groupPosition) {
		return null;
	}

	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	public long getGroupId(int groupPosition) {
		return 0;
	}

	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	public boolean hasStableIds() {
		return false;
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
		this.listView.expandGroup(groupPosition);
	}
}
