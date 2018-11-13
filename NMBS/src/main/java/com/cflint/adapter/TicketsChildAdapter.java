package com.cflint.adapter;



import java.util.List;

import org.apache.commons.lang.StringUtils;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.LinearLayout;

import android.widget.ImageView;

import android.widget.TextView;

import com.cflint.R;

import com.cflint.dataaccess.restservice.impl.StationBoardDataService;
import com.cflint.model.StationBoard;
import com.cflint.util.DateUtils;
import com.cflint.util.ImageUtil;


public class TicketsChildAdapter{
	
	//private static final String TAG = TicketsAdapter.class.getSimpleName();
	
	private Context context;		
	private TextView stationName;	
	private TextView departureDate;
	private TextView stateTextView;
	private TextView asteriskTextView;
	private ImageView logoImageView;
	private View cancelledLine;
	private boolean isHome;
	private LayoutInflater layoutInflater;
	private List<StationBoard> stationBoards;
	public TicketsChildAdapter(Context context, List<StationBoard> stationBoards, boolean isHome) {
		
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		this.isHome = isHome;
		this.stationBoards = stationBoards;
	}
	
	
	public long getItemId(int position) {
		return position;
	}


	 /**
     * {@inheritDoc}
     */
	
	public void getView(final int position, LinearLayout linearLayout) {
		
		View convertView = null;
		StationBoard stationBoard = null;
		if (stationBoards != null) {
			stationBoard = stationBoards.get(position);
		}
		if (convertView == null) { 
			if (isHome) {
				convertView = layoutInflater.inflate(R.layout.tickets_child_with_home_adapter, null);
			}else {
				convertView = layoutInflater.inflate(R.layout.tickets_child_adapter, null);
			}
			
			logoImageView = (ImageView) convertView.findViewById(R.id.tickets_child_adapter_view_logo_imageview);
			stationName = (TextView) convertView.findViewById(R.id.tickets_child_adapter_view_from_station_textview);
			departureDate = (TextView) convertView.findViewById(R.id.tickets_child_adapter_view_departure_label_textview);
			stateTextView = (TextView) convertView.findViewById(R.id.tickets_child_adapter_view_station_state_textview);
			asteriskTextView = (TextView) convertView.findViewById(R.id.tickets_child_adapter_view_asterisk_textview);
			cancelledLine = convertView.findViewById(R.id.cancelled_line);
			

			if (stationBoard != null) {
				stationName.setText(stationBoard.getOriginStationName() + " - " + stationBoard.getDestinationStationName());
				departureDate.setText(DateUtils.FormatToHHMMFromDate(stationBoard.getDateTime()));
				//displayStationDelay(stationBoard);
				if (stationBoard.getTrainCategory() != null && !stationBoard.getTrainCategory().isEmpty()) {
					logoImageView.setImageResource(ImageUtil.getTrainTypeImageId(stationBoard.getTrainCategory()));
				}		
			}
						
			if (isHome) {
				logoImageView.setVisibility(View.GONE);
			}
		} 
		linearLayout.addView(convertView);
	}
	
	/*private void displayStationDelay(StationBoard stationBoard){
		if (stationBoard != null) {
			if (stationBoard.isCallSuccessFul()) {
				stateTextView.setVisibility(View.VISIBLE);
				asteriskTextView.setVisibility(View.INVISIBLE);
				String delay = stationBoard.getDelay();
				if (delay != null && !"".equalsIgnoreCase(delay)) {
					double delayTime = Double.valueOf(delay);
					if (delayTime > 0) {
						String delayString = DateUtils.msToMinuteAndSecond((long)delayTime);
						stateTextView.setText(context.getString(R.string.plus_sign) + " " + delayString);
					}else {
						stateTextView.setText(context.getString(R.string.general_ontime));
						stateTextView.setTextColor(context.getResources().getColor(R.color.ticket_on_time));
					}
				}else {
					stateTextView.setText("");
				}

			}else {
				if (StringUtils.equalsIgnoreCase(stationBoard.getType(), StationBoardDataService.STATIONBOARD_TYPE_A)) {
					
					asteriskTextView.setVisibility(View.VISIBLE);
				}else {
					asteriskTextView.setVisibility(View.INVISIBLE);
				}
				stateTextView.setVisibility(View.GONE);
				cancelledLine.setVisibility(View.GONE);
			}	
			//System.out.println("stationBoard.isCancelled()=======" + stationBoard.isCancelled());
			if (stationBoard.isCancelled()) {
				stateTextView.setText(context.getString(R.string.general_cancelled));
				stateTextView.setTextColor(context.getResources().getColor(R.color.textcolor_error));
				cancelledLine.setVisibility(View.VISIBLE);
			}else {
				cancelledLine.setVisibility(View.GONE);
			}
		}
	}*/
}
