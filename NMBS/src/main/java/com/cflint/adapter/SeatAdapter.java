package com.cflint.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cflint.R;
import com.cflint.model.HafasMessage;
import com.cflint.model.SeatLocation;

import java.util.List;

/**
 * Created by Richard on 3/9/16.
 */
public class SeatAdapter {
    private LayoutInflater layoutInflater;

    private Activity activity;
    public SeatAdapter(Activity activity){
        this.activity = activity;
        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void getSeatLocationView(final SeatLocation seatLocation, LinearLayout linearLayout) {
        View convertView = null;
        if(convertView == null)
            convertView = layoutInflater.inflate(R.layout.li_seat, null);

        TextView tvCoachLabel = (TextView) convertView.findViewById(R.id.tv_li_ticket_detail_coach_label);
        TextView tvSeatNumberLabel = (TextView) convertView.findViewById(R.id.tv_li_ticket_detail_place_label);
        TextView tvTicketCoach = (TextView) convertView.findViewById(R.id.tv_li_ticket_detail_coach_value);
        TextView tvTicketPlace = (TextView) convertView.findViewById(R.id.tv_li_ticket_detail_place_value);

        if(seatLocation != null){
            String carriage = seatLocation.getCoachNumber();
            if(carriage != null && !carriage.isEmpty()){
                tvCoachLabel.setVisibility(View.VISIBLE);
                tvCoachLabel.setText(activity.getResources().getString(R.string.general_carriage) + ": ");
                tvTicketCoach.setText(seatLocation.getCoachNumber());
            }else{
                tvCoachLabel.setVisibility(View.GONE);
            }
            String seatNumber = seatLocation.getSeatNumberInfo();
            if(seatNumber != null && !seatNumber.isEmpty()){
                tvSeatNumberLabel.setVisibility(View.VISIBLE);
                tvSeatNumberLabel.setText(" - " + activity.getResources().getString(R.string.general_place) + ": ");
                tvTicketPlace.setText(seatLocation.getSeatNumberInfo());
            }else{
                tvSeatNumberLabel.setVisibility(View.GONE);
            }
        }else{
            tvCoachLabel.setVisibility(View.GONE);
            tvSeatNumberLabel.setVisibility(View.GONE);
        }
        linearLayout.addView(convertView);
    }
}
