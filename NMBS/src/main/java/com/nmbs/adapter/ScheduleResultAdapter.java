package com.nmbs.adapter;

import android.content.Context;
import android.graphics.Bitmap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nmbs.R;
import com.nmbs.activities.ScheduleResultActivity;
import com.nmbs.async.AsyncImageLoader;
import com.nmbs.model.RealTimeConnection;
import com.nmbs.model.RealTimeInfoLeg;
import com.nmbs.model.TrainIcon;
import com.nmbs.util.DateUtils;
import com.nmbs.util.ImageUtil;
import com.nmbs.util.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ScheduleResultAdapter extends BaseExpandableListAdapter{
	private Context context;
	private ScheduleResultActivity scheduleResultActivity;
	private ExpandableListView listView;
	private LayoutInflater inflater;
	private Map<String,List<RealTimeConnection>> mapList;
	private List<TrainIcon> trainIconList;
	private final static String TAG = ScheduleResultAdapter.class.getSimpleName();
	public ScheduleResultAdapter(Context context, Map<String, List<RealTimeConnection>> mapList, ExpandableListView listView,ScheduleResultActivity scheduleResultActivity,List<TrainIcon> trainIconList){
		this.context = context;
		this.listView = listView;
		this.mapList = mapList;
		this.scheduleResultActivity = scheduleResultActivity;
		this.trainIconList = trainIconList;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getGroupCount() {
		if(mapList.keySet().size() == 0){
			return 1;
		}else{
			return mapList.keySet().size();
		}
	}

	public int getChildrenCount(int groupPosition) {
		return mapList.get(mapList.keySet().toArray()[groupPosition]).size();
	}

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if(mapList == null||mapList.keySet().toArray().length == 0){
			convertView = inflater.inflate(R.layout.li_not_schedule_result, null);
		}else{
			if(convertView == null) convertView = inflater.inflate(R.layout.li_stationboard_result_group, null);
			View viewLine = convertView.findViewById(R.id.v_stationboard_result_line);
			TextView tagDate = (TextView) convertView.findViewById(R.id.iv_date);
			TextView tvWatchout = (TextView) convertView.findViewById(R.id.tv_watchout);
			Date date = DateUtils.stringToDate(scheduleResultActivity,mapList.keySet().toArray()[groupPosition].toString());
			//Log.e("date", "date..." + mapList.keySet().toArray()[groupPosition].toString());
			tagDate.setText(DateUtils.dateTimeToString(date, "EEEE dd MMMM yyyy"));
			if (groupPosition == 0){
				tvWatchout.setVisibility(View.GONE);
			}else{
				viewLine.setVisibility(View.GONE);
				tvWatchout.setVisibility(View.VISIBLE);
				tagDate.setVisibility(View.VISIBLE);
			}
		}
		return convertView;
	}

	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		RealTimeConnection realTimeConnection = mapList.get(mapList.keySet().toArray()[groupPosition]).get(childPosition);

			if(convertView == null){
			convertView = inflater.inflate(R.layout.li_schedule_result, null);
			viewHolder = new ViewHolder();
			viewHolder.trainTypeIcon01 = (ImageView) convertView.findViewById(R.id.iv_schedule_connection_train_type_icon_first);
			viewHolder.trainTypeIcon02 = (ImageView) convertView.findViewById(R.id.iv_schedule_connection_train_type_icon_second);
			viewHolder.trainTypeIcon03 = (ImageView) convertView.findViewById(R.id.iv_schedule_connection_train_type_icon_third);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}


		TextView departureTimeView = (TextView)convertView.findViewById(R.id.tv_li_schedule_result_departure_time);
		TextView arriveTimeView = (TextView) convertView.findViewById(R.id.tv_li_schedule_result_arrive_time);
		TextView durationTimeView = (TextView) convertView.findViewById(R.id.tv_li_schedule_result_duration_time);
		TextView transferView = (TextView) convertView.findViewById(R.id.tv_li_schedule_result_transfer);
		View notPossibleView = convertView.findViewById(R.id.v_li_schedule_line_view);
		LinearLayout notPossibleLayout = (LinearLayout) convertView.findViewById(R.id.ll_li_schedule_result_connection_not_possible);
		TextView tvNotPossible = (TextView) convertView.findViewById(R.id.tv_not_possible);

		TextView departureDelayTimeView = (TextView) convertView.findViewById(R.id.tv_li_schedule_result_departure_delay_time);
		TextView arriveDelayTimeView = (TextView) convertView.findViewById(R.id.tv_li_schedule_result_arrive_delay_time);
		TextView nightView = (TextView) convertView.findViewById(R.id.tv_li_schedule_result_night_label);
		ImageView nightImageView = (ImageView) convertView.findViewById(R.id.iv_li_schedule_result_night_image);
		ImageView magnifyingGlassView = (ImageView) convertView.findViewById(R.id.iv_li_schedule_result_magnifying_glass);

		ImageView notPossibleImage = (ImageView) convertView.findViewById(R.id.iv_schedule_result_connection_not_possible);
		LinearLayout additionalTrainTypeLayout = (LinearLayout) convertView.findViewById(R.id.ll_schedule_connection_train_type_icon_additional);

		Map<String,TrainIcon> tempMap = new LinkedHashMap<>(/*new Comparator<String>(){
			public int compare(String obj1,String obj2){
				return obj2.compareTo(obj1);
			}
		}*/);

		for(RealTimeInfoLeg realTimeInfoLeg:realTimeConnection.getRealTimeInfoLegs()){

			boolean isHaveTrainIcon = false;
			for(TrainIcon trainIcon:(trainIconList ==null?new ArrayList<TrainIcon>():trainIconList)){
				for(String icon:trainIcon.getLinkedTrainBrands()){
					if(realTimeInfoLeg != null && realTimeInfoLeg.getTrainType() != null){
						if(icon != null && icon.contains(realTimeInfoLeg.getTrainType())){
							tempMap.put(realTimeInfoLeg.getTrainType(),trainIcon);
							isHaveTrainIcon = true;
						}
					}

				}
			}
			if(!isHaveTrainIcon){
				tempMap.put("DefaultTrainTypeIcon",null);
			}
		}

		if(tempMap.size()>2){
			additionalTrainTypeLayout.setVisibility(View.VISIBLE);
		}else{
			additionalTrainTypeLayout.setVisibility(View.GONE);
		}
		//Log.e("tempMap", "tempMap...." + tempMap.keySet().toArray().length);
		for(int i = tempMap.keySet().toArray().length - 1; i >= 0; i--){
			//Log.e("tempMap", "tempMap...." + String.valueOf(i));
			TrainIcon tempTrainIcon = tempMap.get(tempMap.keySet().toArray()[i]);
			String trainType = (String)tempMap.keySet().toArray()[i];
			if(tempTrainIcon == null){
				switch (i){
					case 0:
						viewHolder.trainTypeIcon01.setImageResource(R.drawable.icon_train);
						//viewHolder.trainTypeIcon01.setTag(0);
						break;
					case 1:
						viewHolder.trainTypeIcon02.setImageResource(R.drawable.icon_train);
						//viewHolder.trainTypeIcon01.setTag(1);
						break;
					case 2:
						viewHolder.trainTypeIcon03.setImageResource(R.drawable.icon_train);
						//viewHolder.trainTypeIcon01.setTag(2);
						break;
				}
			}else{

					String imageUrl = ImageUtil.convertImageExtension(tempTrainIcon.getIcon(Utils.getDeviceDensity(scheduleResultActivity)));
					String fullImageUrl = context.getString(R.string.server_url_host) + imageUrl;
					switch (i){
						case 0:
							//if (view.getTag() != null && view.getTag().equals(tag)){
							viewHolder.trainTypeIcon01.setImageResource(R.drawable.icon_train);
							if(viewHolder.trainTypeIcon01.getTag() == null){
								viewHolder.trainTypeIcon01.setTag(viewHolder.trainTypeIcon01);
								downloadImage(viewHolder.trainTypeIcon01,fullImageUrl,imageUrl,trainType, realTimeConnection.getReconCtx() + realTimeConnection.getDeparture() + 0);
							}else{
								downloadImage((ImageView) viewHolder.trainTypeIcon01.getTag(),fullImageUrl,imageUrl,trainType, realTimeConnection.getReconCtx() + realTimeConnection.getDeparture() + 0);
							}


							//}

							break;
						case 1:
							viewHolder.trainTypeIcon02.setImageResource(R.drawable.icon_train);
							if(viewHolder.trainTypeIcon02.getTag() == null){
								viewHolder.trainTypeIcon02.setTag(viewHolder.trainTypeIcon02);
								downloadImage(viewHolder.trainTypeIcon02,fullImageUrl,imageUrl,trainType, realTimeConnection.getReconCtx() + realTimeConnection.getDeparture() + 1);

							}else{
								downloadImage((ImageView) viewHolder.trainTypeIcon02.getTag(),fullImageUrl,imageUrl,trainType, realTimeConnection.getReconCtx() + realTimeConnection.getDeparture() + 0);
							}
							//viewHolder.trainTypeIcon02.setTag(realTimeConnection.getReconCtx() + realTimeConnection.getDeparture() + 1);
							//tag = tags.get(i);
							break;
						case 2:
							viewHolder.trainTypeIcon03.setImageResource(R.drawable.icon_train);
							if(viewHolder.trainTypeIcon03.getTag() == null){
								viewHolder.trainTypeIcon03.setTag(viewHolder.trainTypeIcon03);
								downloadImage(viewHolder.trainTypeIcon03,fullImageUrl,imageUrl,trainType, realTimeConnection.getReconCtx() + realTimeConnection.getDeparture() + 2);
							}else{
								downloadImage((ImageView) viewHolder.trainTypeIcon03.getTag(),fullImageUrl,imageUrl,trainType, realTimeConnection.getReconCtx() + realTimeConnection.getDeparture() + 0);
							}
							//viewHolder.trainTypeIcon03.setTag(realTimeConnection.getReconCtx() + realTimeConnection.getDeparture() + 2);
							//tag = tags.get(i);

							break;
					}

			}


		}



		magnifyingGlassView.setImageResource(R.drawable.ic_magnifying); // default value
		departureTimeView.setText(DateUtils.FormatToHHMMFromDate(realTimeConnection.getDeparture()));
		arriveTimeView.setText(DateUtils.FormatToHHMMFromDate(realTimeConnection.getArrival()));
		durationTimeView.setText(DateUtils.FormatToHrDate(realTimeConnection.getDuration(), context));

		if(realTimeConnection.getNumberOfTransfers() == 0){
			transferView.setText(context.getString(R.string.general_direct_train));
		}else if(realTimeConnection.getNumberOfTransfers() > 1){
			transferView.setText(realTimeConnection.getNumberOfTransfers() + " "+context.getString(R.string.general_transfers));
		}else{
			transferView.setText(realTimeConnection.getNumberOfTransfers() + " "+context.getString(R.string.general_transfer));
		}

		if(realTimeConnection.isTransferNotPossible()||realTimeConnection.isConnectionNotPossible()){
			notPossibleView.setVisibility(View.VISIBLE);
			notPossibleView.setVisibility(View.VISIBLE);
			notPossibleImage.setVisibility(View.VISIBLE);
		}else{
			notPossibleView.setVisibility(View.GONE);
			notPossibleView.setVisibility(View.GONE);
			notPossibleImage.setVisibility(View.GONE);
		}


		if(realTimeConnection.getRealTimeDepartureDelta()!=null&&!"".equals(realTimeConnection.getRealTimeDepartureDelta())){
			arriveDelayTimeView.setVisibility(View.VISIBLE);
			departureDelayTimeView.setVisibility(View.VISIBLE);
			departureDelayTimeView.setText(realTimeConnection.getRealTimeDepartureDelta());
		}else{
			arriveDelayTimeView.setVisibility(View.GONE);
			departureDelayTimeView.setVisibility(View.GONE);
		}

		if(realTimeConnection.getRealTimeArrivalDelta()!=null&&!"".equals(realTimeConnection.getRealTimeArrivalDelta())){
			arriveDelayTimeView.setVisibility(View.VISIBLE);
			departureDelayTimeView.setVisibility(View.VISIBLE);
			arriveDelayTimeView.setText(realTimeConnection.getRealTimeArrivalDelta());
		}else{
			if(departureDelayTimeView.getVisibility() == View.VISIBLE){
				arriveDelayTimeView.setVisibility(View.VISIBLE);
				departureDelayTimeView.setVisibility(View.VISIBLE);
			}else{
				arriveDelayTimeView.setVisibility(View.GONE);
				departureDelayTimeView.setVisibility(View.GONE);
			}

		}
		//realTimeConnection.setConnectionNotPossible(true);
		if(realTimeConnection.isConnectionNotPossible() && realTimeConnection.isTransferNotPossible()){
			notPossibleLayout.setVisibility(View.VISIBLE);
			tvNotPossible.setText(context.getString(R.string.schedule_connection_not_possible));
		}else{
			if(realTimeConnection.isConnectionNotPossible()){
				notPossibleLayout.setVisibility(View.VISIBLE);
				tvNotPossible.setText(context.getString(R.string.schedule_connection_not_possible));
			}else if(realTimeConnection.isTransferNotPossible()){
				notPossibleLayout.setVisibility(View.VISIBLE);
				tvNotPossible.setText(context.getString(R.string.dossier_detail_transfer_not_possible));
			}else{
				notPossibleLayout.setVisibility(View.GONE);
			}
		}

		/*if((realTimeConnection.getRealTimeDepartureDelta()!=null&&!"".equals(realTimeConnection.getRealTimeDepartureDelta()))||
				(realTimeConnection.getRealTimeArrivalDelta()!=null&&!"".equals(realTimeConnection.getRealTimeArrivalDelta()))||
				realTimeConnection.isConnectionNotPossible()||
				realTimeConnection.isTransferNotPossible()){
			magnifyingGlassView.setImageResource(R.drawable.ic_magnifying_red);
		}*/
		if(realTimeConnection.isTransferNotPossible() || realTimeConnection.isConnectionNotPossible()
				|| (realTimeConnection.getHafasMessages() != null && realTimeConnection.getHafasMessages().size() > 0)
				|| realTimeConnection.hasLegStatus()
				|| (realTimeConnection.getRealTimeDepartureDelta() != null && !realTimeConnection.getRealTimeDepartureDelta().isEmpty())
				|| (realTimeConnection.getRealTimeArrivalDelta() != null && !realTimeConnection.getRealTimeArrivalDelta().isEmpty())){
			magnifyingGlassView.setImageResource(R.drawable.ic_magnifying_red);
		}else{
			magnifyingGlassView.setImageResource(R.drawable.ic_magnifying);
		}



		for(RealTimeInfoLeg realTimeInfoLeg : realTimeConnection.getRealTimeInfoLegs()){
			if(realTimeInfoLeg != null){
				if(realTimeInfoLeg.isNightTrain()){
					nightView.setVisibility(View.VISIBLE);
					nightImageView.setVisibility(View.VISIBLE);
				}else{
					nightView.setVisibility(View.GONE);
					nightImageView.setVisibility(View.GONE);
				}

				if((realTimeInfoLeg.getLegStatus()!=null&&!realTimeInfoLeg.getLegStatus().isEmpty())){
					magnifyingGlassView.setImageResource(R.drawable.ic_magnifying_red);
				}
			}

		}

		return convertView;
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

	private void downloadImage(final ImageView imageView, String fullImageUrl, String imageUrl, final String defaultTrainType, final String tag){

		//Log.e("tag", "tag...." + tag);
		if(ImageUtil.getTrainTypeImageId(defaultTrainType) == R.drawable.icon_train){
			AsyncImageLoader.getInstance().loadDrawable(
					context, fullImageUrl, imageUrl,
					imageView, null, new AsyncImageLoader.ImageCallback() {
						public void imageLoaded(Bitmap imageDrawable,
												String imageUrl, View view) {
							if(imageDrawable == null){
								if (view.getTag() != null && view.getTag().equals(imageView)){
									((ImageView) view).setImageResource(ImageUtil.getTrainTypeImageId(defaultTrainType));
								}
							}else{
								if (view.getTag() != null && view.getTag().equals(imageView)){
									((ImageView) view).setImageBitmap(imageDrawable);
								}
							}
						}
					});
		}else{
			if (imageView.getTag() != null && imageView.getTag().equals(imageView)){
				imageView.setImageResource(ImageUtil.getTrainTypeImageId(defaultTrainType));
			}

		}

	}

	private static class ViewHolder {

		ImageView trainTypeIcon01;
		ImageView trainTypeIcon02;
		ImageView trainTypeIcon03;
	}
}
