package com.cfl.adapter;

import java.util.List;

import org.apache.commons.lang.StringUtils;


import com.cfl.R;


import com.cfl.model.DossierAftersalesResponse;
import com.cfl.model.Passenger;
import com.cfl.model.SeatLocation;


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
public class TicketsDetailSeatlocationPassengersInfoAdapter extends ArrayAdapter<SeatLocation>{
	
	
	final static class ViewHolder {
		
		private TextView passenger1Name;//, passenger2Name;
	}
	
	private LayoutInflater layoutInflater;
	private Context context;
	private DossierAftersalesResponse dossierAftersalesResponse;
	public TicketsDetailSeatlocationPassengersInfoAdapter(Context context, int textViewResourceId,
			List<SeatLocation> objects, DossierAftersalesResponse dossierAftersalesResponse) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.dossierAftersalesResponse = dossierAftersalesResponse;
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
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.tickets_detail_seatlocation_adapter_passengers_info, null);
			viewHolder = new ViewHolder();
			
			//viewHolder.passenger1CoachNumber = (TextView) convertView.findViewById(R.id.tickets_detail_seatlocation_adapter_view_passenger1_coach_number_textview);
			
			//viewHolder.passenger1SeatNumber = (TextView) convertView.findViewById(R.id.tickets_detail_seatlocation_adapter_view_passenger1_seat_number_textview);		
			
			viewHolder.passenger1Name = (TextView) convertView.findViewById(R.id.tickets_detail_seatlocation_adapter_view_passenger1_name_textview);
	
			convertView.setTag(viewHolder);			
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (this.getItem(position) != null) {
				
				if(this.getItem(position).getPassengerId() == null){
						viewHolder.passenger1Name.setText(context.getString(R.string.general_passenger) + " " +  (position + 1) + " :    " + 
								context.getString(R.string.general_coach) + "  " + this.getItem(position).getCoachNumber() + 
								"    " + context.getString(R.string.group_list_item_tag_middle_dot) + "    " + 
								context.getString(R.string.general_seat) + "  " + this.getItem(position).getSeatNumber());
				}else{
					Passenger passenger = this.dossierAftersalesResponse.getPassengerByPassengerId(this.getItem(position).getPassengerId());
					if (passenger != null) {
						
						if ( passenger.getName() != null && !StringUtils.equalsIgnoreCase("", passenger.getName())) {
							viewHolder.passenger1Name.setText(passenger.getName() + ":    " + 
									context.getString(R.string.general_coach) + "  " + this.getItem(position).getCoachNumber() + 
									"    " + context.getString(R.string.group_list_item_tag_middle_dot) + "    " + 
									context.getString(R.string.general_seat) + "  " + this.getItem(position).getSeatNumber());
						}else {
							
							viewHolder.passenger1Name.setText(context.getString(R.string.general_passenger) + " " +  (position + 1) + " :    " + 
									context.getString(R.string.general_coach) + "  " + this.getItem(position).getCoachNumber() + 
									"    " + context.getString(R.string.group_list_item_tag_middle_dot) + "    " + 
									context.getString(R.string.general_seat) + "  " + this.getItem(position).getSeatNumber());
						}
						
					}
				}
				/*viewHolder.passenger1CoachNumber.setText(context.getString(R.string.general_coach) + " " + this.getItem(position).getCoachNumber());
				viewHolder.passenger1SeatNumber.setText(context.getString(R.string.general_seat) + " " + this.getItem(position).getSeatNumber());*/

			}
		
		return convertView;
	}

}
