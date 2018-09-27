package com.cfl.adapter;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;


import com.cfl.R;

import com.cfl.activity.LinearLayoutForListView;

import com.cfl.model.DossierAftersalesResponse;
import com.cfl.model.Order;
import com.cfl.model.StationBoard;

import com.cfl.model.TravelSegment;
import com.cfl.services.IAssistantService;
import com.cfl.util.DateUtils;
import com.cfl.util.ImageUtil;


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
public class TicketsDetailSeatlocationAdapter extends ArrayAdapter<TravelSegment>{
	
	//private final static String TAG = TicketsDetailSeatlocationAdapter.class.getSimpleName();
	final static class ViewHolder {
		
		private TextView originStationName;
		//private TextView destinationStationName;
		private TextView departureDate;
		private TextView departureTime;	
		private View cancelledLine;
		private TextView stateTextView;
		private TextView trainNrLable;
		private TextView trainNr;
		private TextView noSeat;

		private LinearLayoutForListView linearLayoutForListView;
		private TextView seperate;

		//private TextView coach2Seperate;
		private ImageView trainTypeIcon;
		private TextView asteriskTextView;
	}
	private IAssistantService assistantService;
	private ViewHolder viewHolder;;
	private LayoutInflater layoutInflater;
	private Context context;
	private DossierAftersalesResponse dossierAftersalesResponse;
	private Order order;
	public TicketsDetailSeatlocationAdapter(Context context, int textViewResourceId,
			List<TravelSegment> objects, DossierAftersalesResponse dossierAftersalesResponse, 
			IAssistantService assistantService, Order order) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.dossierAftersalesResponse = dossierAftersalesResponse;
		this.assistantService = assistantService;
		this.order = order;
	}
    /**
     * {@inheritDoc}
     */

    /**
     * {@inheritDoc}
     */
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.tickets_detail_seatlocation_adapter, null);
			viewHolder = new ViewHolder();
			//viewHolder.passenger2LinearLayout = convertView.findViewById(R.id.tickets_detail_seatlocation_adapter_view_passenger2_LinearLayout);
			
			viewHolder.linearLayoutForListView = (LinearLayoutForListView) convertView.findViewById(R.id.tickets_detail_seatlocation_adapter_view_passengers_list);
			
			
			
			
			viewHolder.originStationName = (TextView) convertView.findViewById(R.id.tickets_detail_seatlocation_adapter_view_from_station_textview);	
			//viewHolder.destinationStationName = (TextView) convertView.findViewById(R.id.tickets_detail_seatlocation_adapter_view_to_station_textview);
			viewHolder.departureDate = (TextView) convertView.findViewById(R.id.tickets_detail_seatlocation_adapter_view_start_date_textview);
			viewHolder.departureTime = (TextView) convertView.findViewById(R.id.tickets_detail_seatlocation_adapter_view_start_time_textview);
			viewHolder.trainNr = (TextView) convertView.findViewById(R.id.tickets_detail_seatlocation_adapter_view_train_nr_textview);
			viewHolder.noSeat = (TextView) convertView.findViewById(R.id.tickets_detail_seatlocation_adapter_view_no_seat_textview);
			
			viewHolder.cancelledLine = convertView.findViewById(R.id.cancelled_line);
			viewHolder.stateTextView = (TextView) convertView.findViewById(R.id.tickets_adapter_view_station_state_textview);
			
			//viewHolder.passenger2CoachNumber = (TextView) convertView.findViewById(R.id.tickets_detail_seatlocation_adapter_view_passenger2_coach_number_textview);
			
			//viewHolder.passenger2SeatNumber = (TextView) convertView.findViewById(R.id.tickets_detail_seatlocation_adapter_view_passenger2_seat_number_textview);
			viewHolder.trainTypeIcon = (ImageView) convertView.findViewById(R.id.tickets_detail_seatlocation_adapter_view_logo_imageview);
			
			//viewHolder.passenger2Name = (TextView) convertView.findViewById(R.id.tickets_detail_seatlocation_adapter_view_passenger2_name_textview);
			viewHolder.trainNrLable = (TextView) convertView.findViewById(R.id.tickets_detail_seatlocation_adapter_view_train_name_textview);
			viewHolder.seperate = (TextView) convertView.findViewById(R.id.tickets_detail_seatlocation_adapter_view_start_seperate_textview);
			viewHolder.asteriskTextView = (TextView) convertView.findViewById(R.id.tickets_detail_seatlocation_adapter_view_asterisk_textview);
			
			
			//viewHolder.coach2Seperate = (TextView) convertView.findViewById(R.id.tickets_detail_seatlocation_adapter_view_coach2_seperate_textview);
			convertView.setTag(viewHolder);			
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		//viewHolder.originStationName.setText(this.getItem(position).getOrigin());
		if (this.getItem(position) != null) {
			viewHolder.originStationName.setText(this.getItem(position).getOrigin()  + 
					"  " + context.getString(R.string.group_list_item_tag_middle_line) + "  " +
					this.getItem(position).getDestination());
			//viewHolder.originStationName.setText(this.getItem(position).getOrigin());
			viewHolder.departureDate.setText(DateUtils.dateToString(context, this.getItem(position).getDeparture()));
			viewHolder.departureTime.setText(DateUtils.FormatToHHMMFromDate(this.getItem(position).getDepartureTime()));
			viewHolder.trainNr.setText(this.getItem(position).getTrainNr());
			
			viewHolder.trainTypeIcon.setImageResource(ImageUtil.getTrainTypeImageId(this.getItem(position).getTrainType()));
			/*if (this.getItem(position).getSeatLocations() != null && this.getItem(position).getSeatLocations().size() > 0) {
				Passenger passenger = this.dossierAftersalesResponse.getPassengerByPassengerId(this.getItem(position).getSeatLocations().get(0).getPassengerId());
				if (passenger != null) {
					
					if ( passenger.getName() != null && !StringUtils.equalsIgnoreCase("", passenger.getName())) {
						viewHolder.passenger1Name.setText(passenger.getName() + ": ");
					}else {
						
						viewHolder.passenger1Name.setText(context.getString(R.string.general_passenger) + " 1:");
					}
					
				}
				viewHolder.passenger1CoachNumber.setText(context.getString(R.string.general_coach) + " " + this.getItem(position).getSeatLocations().get(0).getCoachNumber());
				viewHolder.passenger1SeatNumber.setText(context.getString(R.string.general_seat) + " " + this.getItem(position).getSeatLocations().get(0).getSeatNumber());
				if (this.getItem(position).getSeatLocations().size() > 1) {
					//viewHolder.passenger2LinearLayout.setVisibility(View.VISIBLE);
					passenger = this.dossierAftersalesResponse.getPassengerByPassengerId(this.getItem(position).getSeatLocations().get(1).getPassengerId());
					if (passenger.getName() != null && !StringUtils.equalsIgnoreCase("", passenger.getName())) {
						//viewHolder.passenger2Name.setText(passenger.getName() + ": ");
						
					}else {
						//viewHolder.passenger2Name.setText(context.getString(R.string.general_passenger) + " 2:");
					}
					
					//viewHolder.passenger2CoachNumber.setText(context.getString(R.string.general_coach) + " " + this.getItem(position).getSeatLocations().get(1).getCoachNumber());
					//viewHolder.passenger2SeatNumber.setText(context.getString(R.string.general_seat) + " " + this.getItem(position).getSeatLocations().get(1).getSeatNumber());
				}*/
				
			TicketsDetailSeatlocationPassengersInfoAdapter adapter = new TicketsDetailSeatlocationPassengersInfoAdapter(context, 
					R.layout.tickets_detail_seatlocation_adapter_passengers_info, this.getItem(position).getSeatLocations(),this.dossierAftersalesResponse);
			viewHolder.linearLayoutForListView.setAdapter(adapter);
			}
			if(this.getItem(position).isHasReservation() == false && StringUtils.equalsIgnoreCase(this.getItem(position).getParentId(), "")){
				viewHolder.trainNrLable.setVisibility(View.GONE);
				viewHolder.departureDate.setText(DateUtils.dateToString(context, this.getItem(position).getValidityStartDate()));
				viewHolder.departureTime.setVisibility(View.GONE);
				viewHolder.seperate.setVisibility(View.GONE);
				viewHolder.linearLayoutForListView.setVisibility(View.GONE);
				viewHolder.noSeat.setVisibility(View.VISIBLE);
				/*
				  viewHolder.coachSeperate.setVisibility(View.GONE);
				  viewHolder.passenger1Name.setVisibility(View.GONE);
				viewHolder.passenger1CoachNumber.setVisibility(View.GONE);
				viewHolder.passenger1SeatNumber.setText("No seating information.");*/
				
				if (this.getItem(position).getSeatLocations().size() > 1) {
					//viewHolder.passenger2SeatNumber.setText("No seating information.");
					//viewHolder.coach2Seperate.setVisibility(View.GONE);
					//viewHolder.passenger2Name.setVisibility(View.GONE);
					//viewHolder.passenger2CoachNumber.setVisibility(View.GONE);
				}
			
		
			}
			StationBoard stationBoard = assistantService.getParentRealTimeForTravelSegments(context, this.getItem(position).getId());
			if(stationBoard == null){
				if (order.isHasDuplicatedStationboard() && order.getDepartureDate().after(new Date())) {
					List<StationBoard>stationBoards = assistantService.getDuplicatedStationBoard(context, order.getDuplicatedStationboardId());
					if (stationBoards != null && stationBoards.size() > 0) {
						stationBoard = stationBoards.get(0);
					}
					//System.out.println("getDuplicatedStationboardId====" +  order.getDuplicatedStationboardId());
				}
			}
			
			
			displayStationDelay(stationBoard);
		
		
		return convertView;
	}
	
	private void displayStationDelay(StationBoard stationBoard){
		if (stationBoard != null) {
			if (stationBoard.isCallSuccessFul()) {
				viewHolder.stateTextView.setVisibility(View.VISIBLE);
				viewHolder.asteriskTextView.setVisibility(View.INVISIBLE);
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
				if (stationBoard.isCancelled()) {
					viewHolder.stateTextView.setText(context.getString(R.string.general_cancelled));
					viewHolder.stateTextView.setTextColor(context.getResources().getColor(R.color.textcolor_error));
					viewHolder.cancelledLine.setVisibility(View.VISIBLE);
				}else {
					viewHolder.cancelledLine.setVisibility(View.GONE);
				}
			}else {
				viewHolder.asteriskTextView.setVisibility(View.VISIBLE);
				viewHolder.stateTextView.setVisibility(View.GONE);
				viewHolder.cancelledLine.setVisibility(View.GONE);
			}			
		}
	}
	

}
