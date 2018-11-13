package com.cflint.adapter;



import java.util.List;


import org.apache.commons.lang.StringUtils;

import android.content.Context;



import android.view.LayoutInflater;
import android.view.View;


import android.view.ViewGroup;
import android.view.View.OnClickListener;


import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;


import android.widget.ImageView;

import android.widget.TextView;


import com.cflint.R;

import com.cflint.async.RealTimeAsyncTask;
import com.cflint.dataaccess.restservice.impl.StationBoardDataService;
import com.cflint.model.DossierResponse.OrderItemStateType;

import com.cflint.model.OfferQuery.ComforClass;
import com.cflint.model.Order;
import com.cflint.model.StationBoard;


import com.cflint.services.IAssistantService;


import com.cflint.util.DateUtils;
import com.cflint.util.ImageUtil;

import com.cflint.util.Utils;


public class TicketsAdapter extends ArrayAdapter<Order>{
	
	//private static final String TAG = TicketsAdapter.class.getSimpleName();
	
	private Context context;
	
	final static class ViewHolder {
		private TextView originStationName;
		private TextView destinationStationName;
		private TextView departureDate;
		private TextView departureTime;	
		private TextView trainTypeName;
		private TextView passenger;
		private TextView classLevel;
		private TextView ebs;
		private TextView checkin;
		private TextView notConfirmed;
		private TextView departureLabel;
		private View cancelledView;
		private ImageView trainTypeIcon;
		private View ticketsView;
		private Button removeButton;
		private TextView seperate;
		private TextView rightArrow;
		private View timeRelativeLayout;
		private LinearLayout linearLayout;
		private LinearLayout dashLine;
		private View cancelledLine;
		private TextView stateTextView, seperateTextView;
		private LinearLayout errorLayout;
		private TextView errorTextview;
	}
	
	ViewHolder viewHolder;
	private LayoutInflater layoutInflater;
	private IAssistantService assistantService;
	private boolean isHome;
	private boolean isNotHistory;
	private ReloadCallback reloadCallback;
	private LinearLayout progressBar;
	private boolean isHasError;
	public TicketsAdapter(Context context, int textViewResourceId,
			List<Order> objects, IAssistantService assistantService, boolean isHome, boolean isNotHistory, boolean isHasError) {
		super(context, textViewResourceId, objects);
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		this.assistantService = assistantService;
		this.isHome = isHome;
		this.isNotHistory = isNotHistory;
		this.isHasError = isHasError;
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setReloadCallback(ReloadCallback reloadCallback){
		
		this.reloadCallback = reloadCallback;
	}
	 /**
     * {@inheritDoc}
     */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		
		
		if (convertView == null) { 
			viewHolder = new ViewHolder(); 		
			if (isHome) {
				convertView = layoutInflater.inflate(R.layout.tickets_with_home_adapter, null);
			}else {
				convertView = layoutInflater.inflate(R.layout.tickets_adapter, null);
			}
			
				
			viewHolder.originStationName = (TextView) convertView.findViewById(R.id.tickets_adapter_view_from_station_textview);
			viewHolder.destinationStationName = (TextView) convertView.findViewById(R.id.tickets_adapter_view_to_station_textview);
			viewHolder.departureDate = (TextView) convertView.findViewById(R.id.tickets_adapter_view_start_date_textview);
			viewHolder.departureTime = (TextView) convertView.findViewById(R.id.tickets_adapter_view_start_time_textview);
			viewHolder.trainTypeName = (TextView) convertView.findViewById(R.id.tickets_adapter_view_train_name_textview);
			viewHolder.passenger = (TextView) convertView.findViewById(R.id.tickets_adapter_view_passenger_count_textview);
			viewHolder.classLevel = (TextView) convertView.findViewById(R.id.tickets_adapter_view_class_level_textview);
			viewHolder.ebs = (TextView) convertView.findViewById(R.id.tickets_adapter_view_ebs_textview);
			viewHolder.checkin = (TextView) convertView.findViewById(R.id.tickets_adapter_view_checkin_textview);
			viewHolder.notConfirmed = (TextView) convertView.findViewById(R.id.tickets_adapter_view_not_confirmed_textview);
			viewHolder.cancelledView =  convertView.findViewById(R.id.tickets_adapter_view_cancelled_linearlayout);
			viewHolder.trainTypeIcon = (ImageView) convertView.findViewById(R.id.tickets_adapter_view_logo_imageview);
			viewHolder.ticketsView =  convertView.findViewById(R.id.tickets_adapter_view_tickets_confirmed);
			viewHolder.removeButton = (Button)convertView.findViewById(R.id.tickets_adapter_view_remove_Button);
			viewHolder.departureLabel = (TextView)convertView.findViewById(R.id.tickets_adapter_view_departure_label_textview);
			viewHolder.seperate = (TextView)convertView.findViewById(R.id.tickets_adapter_view_start_seperate_textview);
			viewHolder.rightArrow = (TextView)convertView.findViewById(R.id.tickets_adapter_view_right_arrow);
			viewHolder.timeRelativeLayout = convertView.findViewById(R.id.tickets_adapter_view_start_time_textview_RelativeLayout);
			viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.tickets_adapter_view_child_view);
			viewHolder.dashLine = (LinearLayout) convertView.findViewById(R.id.tickets_adapter_view_child_dash_line);
			viewHolder.cancelledLine = convertView.findViewById(R.id.cancelled_line);
			viewHolder.stateTextView = (TextView) convertView.findViewById(R.id.tickets_adapter_view_station_state_textview);
			viewHolder.seperateTextView = (TextView) convertView.findViewById(R.id.tickets_adapter_view_first_seperate_textview);
			progressBar = (LinearLayout) convertView.findViewById(R.id.progressBarLayout);
			viewHolder.errorLayout = (LinearLayout) convertView.findViewById(R.id.service_error_Layout);
			viewHolder.errorTextview = (TextView) convertView.findViewById(R.id.tickets_adapter_view_retrieving_real_time_service_error_textview);
			
			
			convertView.setTag(viewHolder);		
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
/*		System.out.println("DNR::::::::" + this.getItem(position).getDNR() + 
				"  departureDate::::::::" + DateUtils.dateToString(context, this.getItem(position).getDepartureDate()) + 
				"  departureTime:::::::::" + DateUtils.timeToString(context, this.getItem(position).getDepartureDate()) + 
				"  SortDepartureDate:::::::::" + DateUtils.dateTimeToString(this.getItem(position).getSortDepartureDate()) + 
				"  getDirection::::::::::" + this.getItem(position).getDirection() + 
				"  getOrderState" + this.getItem(position).getOrderState());*/
		//System.out.println("getSortDepartureDate====" + this.getItem(position).getSortDepartureDate());
		
		viewHolder.departureLabel.setText(context.getString(R.string.general_departure) + ":");
		viewHolder.originStationName.setText(this.getItem(position).getOrigin());
		viewHolder.destinationStationName.setText(this.getItem(position).getDestination());
		viewHolder.departureDate.setText(DateUtils.dateToString(context, this.getItem(position).getDepartureDate()));
		String time = DateUtils.FormatToHHMMFromDate(this.getItem(position).getDepartureDate());
		boolean isNoTime = false;
		if(!StringUtils.equalsIgnoreCase(time, "00:00")){
			viewHolder.departureTime.setText(DateUtils.timeToString(context, this.getItem(position).getDepartureDate()));
		}else{
			isNoTime = true;
			
			viewHolder.timeRelativeLayout.setVisibility(View.GONE);
			viewHolder.departureTime.setVisibility(View.GONE);
			viewHolder.seperate.setVisibility(View.GONE);
		}
		
		String trainName = Utils.getTrainNameByTrainType(context, this.getItem(position).getTrainType());
		if (trainName != null && !trainName.isEmpty()) {
			viewHolder.trainTypeName.setText(trainName);
		}else {
			viewHolder.seperateTextView.setVisibility(View.GONE);
		}
		
		// Set passenger
		int passengerCount = this.getItem(position).getPersonNumber();		
		if(passengerCount == 1){
			viewHolder.passenger.setText("1 " + context.getString(R.string.general_passenger));						
		}else{
			viewHolder.passenger.setText(passengerCount + " " + context.getString(R.string.general_passengers));			
		}
		//Set ComforClass
		ComforClass comforClass = this.getItem(position).getTravelclass();
		
		if(comforClass == ComforClass.FIRST){
			viewHolder.classLevel.setText(context.getString(R.string.planner_view_class_1st));							
		}else{
			viewHolder.classLevel.setText(context.getString(R.string.planner_view_class_2nd));			
		}
		//Set isIncludesEBS
		boolean isIncludesEBS =  this.getItem(position).isIncludesEBS();
		
		if (isIncludesEBS) {
			viewHolder.ebs.setVisibility(View.VISIBLE);
		}else {
			viewHolder.ebs.setVisibility(View.GONE);
		}
		
		//Set orderState
		int orderState = this.getItem(position).getOrderState();
		
		if (orderState == OrderItemStateType.OrderItemStateTypeProvisional.ordinal() ||
				orderState == OrderItemStateType.OrderItemStateTypeUnknown.ordinal()) {			
			viewHolder.cancelledView.setVisibility(View.GONE);
			if (this.getItem(position).getTravelSegmentID() == null || StringUtils.equals("", this.getItem(position).getTravelSegmentID())) {
				viewHolder.notConfirmed.setVisibility(View.VISIBLE);
				viewHolder.notConfirmed.setText(context.getString(R.string.mytickets_detail_not_retrieved));	
				convertView.setBackgroundColor(context.getResources().getColor(R.color.rose_pick_color));
				//convertView.setBackgroundColor(context.getResources().getColor(R.color.rose_pick_color));
				
				viewHolder.rightArrow.setVisibility(View.GONE);
				viewHolder.cancelledView.setVisibility(View.GONE);
			}else {
				viewHolder.notConfirmed.setVisibility(View.VISIBLE);
				convertView.setBackgroundColor(context.getResources().getColor(R.color.rose_pick_color));
				
				
			}
			if (orderState == OrderItemStateType.OrderItemStateTypeProvisional.ordinal()) {
				
				if (Boolean.valueOf(this.getItem(position).getRulfillmentFailed())) {
					viewHolder.notConfirmed.setText(context.getResources().getString(R.string.assistant_overview_provisional_fulfillmentfailed));
				}				
			}
						
		}else if (orderState == OrderItemStateType.OrderItemStateTypeConfirmed.ordinal()) {
					
			viewHolder.cancelledView.setVisibility(View.GONE);
			boolean isCorrupted = this.getItem(position).isCorrupted();
			
			if (this.getItem(position).getTravelSegmentID() == null || StringUtils.equals("", this.getItem(position).getTravelSegmentID())) {
				viewHolder.notConfirmed.setVisibility(View.VISIBLE);
				viewHolder.notConfirmed.setText(context.getString(R.string.mytickets_detail_not_retrieved));	
				viewHolder.rightArrow.setVisibility(View.GONE);
				convertView.setBackgroundColor(context.getResources().getColor(R.color.rose_pick_color));
			}else {
				if (isCorrupted) {
					viewHolder.notConfirmed.setVisibility(View.VISIBLE);
					viewHolder.notConfirmed.setText(context.getString(R.string.mytickets_overview_datadownload_failed));
					convertView.setBackgroundColor(context.getResources().getColor(R.color.rose_pick_color));
				}else {
					viewHolder.notConfirmed.setVisibility(View.GONE);
				}
			}						
			
			displayRealTime(position, isNoTime);
			
		}else if (orderState == OrderItemStateType.OrderItemStateTypeCancelled.ordinal()) {
			viewHolder.ticketsView.setVisibility(View.GONE);
			viewHolder.cancelledView.setVisibility(View.VISIBLE);
			viewHolder.removeButton.setTag(this.getItem(position).getDNR());
			viewHolder.removeButton.setOnClickListener(removeButtonOnClickListener);
		}

		
		// Set trainTypeIcon
		if (StringUtils.equalsIgnoreCase(trainName, "Eurostar")) {
			viewHolder.checkin.setVisibility(View.VISIBLE);
		}else {
			viewHolder.checkin.setVisibility(View.GONE);
		}
		viewHolder.trainTypeIcon.setImageResource(ImageUtil.getTrainTypeImageId(this.getItem(position).getTrainType()));
		if (isHome) {
			viewHolder.trainTypeIcon.setVisibility(View.GONE);
		}else {
			viewHolder.trainTypeIcon.setVisibility(View.VISIBLE);
		}
		return convertView;
	}
	
	private void displayRealTime(int position, boolean isNoTime){
		
		if (isNotHistory) {
			
			
			List<StationBoard> stationBoards = assistantService.getRealTimeForTravelSegments(context, this.getItem(position).getTravelSegmentID(), 
					this.getItem(position).getDNR(), true);
			if (stationBoards == null || stationBoards.size() == 0) {
				if (this.getItem(position).isHasDuplicatedStationboard()) {
					stationBoards = assistantService.getDuplicatedStationBoard(context, this.getItem(position).getDuplicatedStationboardId());
					System.out.println("getDuplicatedStationboardId====" +  this.getItem(position).getDuplicatedStationboardId());
				}
				
			}
			/*System.out.println("stationBoards from stationBoards ====" + stationBoards);
			if (stationBoards == null || stationBoards.size() == 0) {
				System.out.println("no stationBoard====");
			}*/
			
			for (StationBoard stationBoard : stationBoards) {
				if (stationBoard != null) {
					System.out.println("stationBoard====" + stationBoard.getStationRCode());
					System.out.println("stationBoard====" + stationBoard.getType());
					System.out.println("stationBoard====" + stationBoard.isCallSuccessFul());
					System.out.println("stationBoard====" + stationBoard.getDnrStr());
					System.out.println("stationBoard====" + stationBoard.getTravelSegmentID());
				}
			}

			if (stationBoards != null && stationBoards.size() > 0) {

				
				if (!RealTimeAsyncTask.isRealTimeFinished) {
					if (RealTimeAsyncTask.isRefreshingOneDnr) {
						if (RealTimeAsyncTask.REFRESHING_DNR != null 
								&& StringUtils.equalsIgnoreCase(RealTimeAsyncTask.REFRESHING_DNR, this.getItem(position).getDNR())){
							progressBar.setVisibility(View.VISIBLE);
						}
					}else {
						progressBar.setVisibility(View.VISIBLE);
					}
					
					//progressBar.setVisibility(View.VISIBLE);
					
					//System.out.println("progressBar====VISIBLE");
					
				}else {
					
					progressBar.setVisibility(View.GONE);
				}
				if (isHasError) {

					viewHolder.errorLayout.setVisibility(View.VISIBLE);
				}else {
					viewHolder.errorLayout.setVisibility(View.GONE);
				}
				
				if (stationBoards.size() == 1) {
					//ONE child segments
					StationBoard stationBoard = stationBoards.get(0);
					if (stationBoard != null) {
						if (!stationBoard.isCallSuccessFul()) {
							if (StringUtils.equalsIgnoreCase(stationBoard.getType(), StationBoardDataService.STATIONBOARD_TYPE_A)) {
								viewHolder.errorLayout.setVisibility(View.VISIBLE);
							}
							
						}else {
							viewHolder.errorLayout.setVisibility(View.GONE);
						}

						if (StringUtils.equalsIgnoreCase(this.getItem(position).getTravelSegmentID(), 
								stationBoard.getTravelSegmentID())) {
							//Display itself realtime data.
							System.out.println("TravelSegmentID is same====");
							
							/*TicketsChildAdapter ticketsChildAdapter = new TicketsChildAdapter(context, stationBoards, isHome);
							for (int i = 0; i < stationBoards.size(); i++) {
								ticketsChildAdapter.getView(i, viewHolder.linearLayout);
							}*/
							//displayStationDelay(stationBoard);
							
							viewHolder.dashLine.setVisibility(View.GONE);
						}else {
							if (isNoTime) {
								//display child segment + real time status of the child segment
								viewHolder.dashLine.setVisibility(View.VISIBLE);
								TicketsChildAdapter ticketsChildAdapter = new TicketsChildAdapter(context, stationBoards, isHome);
								for (int i = 0; i < stationBoards.size(); i++) {
									ticketsChildAdapter.getView(i, viewHolder.linearLayout);
								}
							}else if (this.getItem(position).getDepartureDate().equals(stationBoard.getDateTime())) {
								//no need to display the child segment, immediately display real time status next to departure time parent
								//displayStationDelay(stationBoard);
								viewHolder.dashLine.setVisibility(View.GONE);
							}
						}
					}
					
				}else {
					
					//MULTIPLE child segments
					
					StationBoard stationBoard = assistantService.getParentRealTimeForTravelSegments(context, this.getItem(position).getTravelSegmentID());
					
					//displayStationDelay(stationBoard);
					
					viewHolder.dashLine.setVisibility(View.VISIBLE);
					TicketsChildAdapter ticketsChildAdapter = new TicketsChildAdapter(context, stationBoards, isHome);
					boolean isError = false;
					for (int i = 0; i < stationBoards.size(); i++) {
						if (stationBoards.get(i) != null) {
							if (!stationBoards.get(i).isCallSuccessFul()) {
								if (StringUtils.equalsIgnoreCase(stationBoards.get(i).getType(), StationBoardDataService.STATIONBOARD_TYPE_A)) {
									isError = true;
								}
								
							}							
						}
						
						ticketsChildAdapter.getView(i, viewHolder.linearLayout);
					}
					if (isError) {
						viewHolder.errorLayout.setVisibility(View.VISIBLE);
						viewHolder.errorTextview.setText(context.getResources().getString(R.string.alert_bulkquery_partially_failed));
						
					}else {
						viewHolder.errorLayout.setVisibility(View.GONE);
					}
				}		
				if (progressBar.getVisibility() == View.VISIBLE) {
					viewHolder.errorLayout.setVisibility(View.GONE);
				}
			}								
		}
	}
	/*
	private void displayStationDelay(StationBoard stationBoard){
		if (stationBoard != null) {
			if (stationBoard.isCallSuccessFul()) {
				viewHolder.stateTextView.setVisibility(View.VISIBLE);
				String delay = stationBoard.getDelay();
				if (delay != null && !"".equalsIgnoreCase(delay)) {
					double delayTime = Double.valueOf(delay);
					if (delayTime > 0) {
						String delayString = DateUtils.msToMinuteAndSecond((long)delayTime);
						viewHolder.stateTextView.setText(context.getString(R.string.plus_sign) + " " + delayString);
					}else {
						viewHolder.stateTextView.setText(context.getString(R.string.general_ontime));
						viewHolder.stateTextView.setTextColor(context.getResources().getColor(R.color.ticket_on_time));
					}
				}
				
				
			}else {
				viewHolder.stateTextView.setVisibility(View.GONE);
				viewHolder.cancelledLine.setVisibility(View.GONE);
			}	
			if (stationBoard.isCancelled()) {
				viewHolder.stateTextView.setText(context.getString(R.string.general_cancelled));
				viewHolder.stateTextView.setTextColor(context.getResources().getColor(R.color.textcolor_error));
				viewHolder.cancelledLine.setVisibility(View.VISIBLE);
			}else {
				viewHolder.cancelledLine.setVisibility(View.GONE);
			}
		}
	}*/
	
	private OnClickListener removeButtonOnClickListener = new OnClickListener() {

		public void onClick(View v) {
			
			String dnr = v.getTag().toString();
			assistantService.deleteDataByDNR(dnr);
			
			if (reloadCallback != null) {
				reloadCallback.reloadData();
			}
			
			//context.sendBroadcast(new Intent(AssistantActivity.ACTION_RELOAD_DATA));
			
			
		}		
	};
	public interface ReloadCallback {
		public void reloadData();
	}
	
	public void showProgressBar(){
		progressBar.setVisibility(View.VISIBLE);
	}
	
	public void hideProgressBar(){
		progressBar.setVisibility(View.GONE);
		
		
	}

	public boolean isHasError() {
		return isHasError;
	}

	
}
