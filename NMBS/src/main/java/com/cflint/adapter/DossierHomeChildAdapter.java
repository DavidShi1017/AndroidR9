package com.cflint.adapter;

import android.app.Activity;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cflint.R;
import com.cflint.activities.DossierDetailActivity;
import com.cflint.activities.TicketsDetailActivity;
import com.cflint.application.NMBSApplication;
import com.cflint.model.Dossier;
import com.cflint.model.DossierSummary;
import com.cflint.model.DossierTravelSegment;
import com.cflint.model.RealTimeInfoResponse;
import com.cflint.model.RealTimeInfoTravelSegment;
import com.cflint.model.TrainIcon;
import com.cflint.services.impl.DossierDetailsService;
import com.cflint.util.DateUtils;
import com.cflint.util.Utils;

import java.util.List;

/**
 * Created by David on 2/26/16.
 */
public class DossierHomeChildAdapter {

    private LayoutInflater layoutInflater;
    private Activity activity;
    private DossierTravelSegment dossierTravelSegment;
    private Dossier dossier;
    public DossierHomeChildAdapter(Activity activity){
        this.activity = activity;

        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void getHomeTravelSegmentView(final DossierTravelSegment dossierTravelSegment, LinearLayout linearLayout,
                                         final Dossier dossier, final DossierSummary dossierSummary) {
        this.dossierTravelSegment = dossierTravelSegment;
        this.dossier = dossier;
        View convertView = null;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.li_dossier_home_child, null);
        }

        RelativeLayout llTicketInfo = (RelativeLayout)convertView.findViewById(R.id.ll_dossier_home_child_info);
        TextView tvSegmentStation = (TextView)convertView.findViewById(R.id.tv_home_child_segment_station);
        TextView tvDepartureDate = (TextView)convertView.findViewById(R.id.tv_home_child_departure_date);
        TextView tvTrainInfo = (TextView) convertView.findViewById(R.id.tv_home_child_segment_traintype);
        TextView tvRealtimeResult = (TextView) convertView.findViewById(R.id.tv_home_child_realtime_info);
        llTicketInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NMBSApplication.getInstance().getDossierDetailsService().setCurrentDossier(null);
                NMBSApplication.getInstance().getDossierDetailsService().setCurrentDossierSummary(null);
                activity.startActivity(DossierDetailActivity.createIntent(activity.getApplicationContext(), dossier, dossierSummary));
            }
        });
        if(dossierTravelSegment != null && dossier != null){
            tvSegmentStation.setText(dossierTravelSegment.getOriginStationName() + " - " + dossierTravelSegment.getDestinationStationName());
            tvDepartureDate.setText(DateUtils.dateTimeToString(dossierTravelSegment.getDepartureDateTime(), "HH:mm a"));
            String trainType = dossierTravelSegment.getTrainType();
            TrainIcon trainIcon = NMBSApplication.getInstance().getTrainIconsService().getTrainIcon(dossierTravelSegment.getTrainType());
            if(trainIcon != null){
                if(trainIcon.getBrandName() != null && !trainIcon.getBrandName().isEmpty()){
                    trainType = trainIcon.getBrandName();
                }
            }
            tvTrainInfo.setText(trainType);
        }
        if(dossierTravelSegment != null && dossier != null) {
            DossierDetailsService dossierDetailsService = NMBSApplication.getInstance().getDossierDetailsService();
            RealTimeInfoResponse realTimeInfoResponse = dossierDetailsService.readRealTimeInfoById(dossierTravelSegment.getTravelSegmentId(), activity.getApplication());
            RealTimeInfoTravelSegment realTimeInfoTravelSegment = (RealTimeInfoTravelSegment) dossierDetailsService.getRealTime(realTimeInfoResponse);
            if (realTimeInfoTravelSegment != null && realTimeInfoResponse != null) {
                //Log.d("RealTime", "RealTime Info TravelSegment id is..." + realTimeInfoResponse.getId());
                //Log.d("RealTime", "RealTime Info TravelSegment IsSuccess??..." + realTimeInfoResponse.getIsSuccess());
                //Log.d("RealTime", "RealTime Info TravelSegment..LegStatus...." + realTimeInfoTravelSegment.getLegStatus());
                //Log.d("RealTime", "RealTime Info TravelSegment..RealTimeDepartureDelta...." + realTimeInfoTravelSegment.getRealTimeDepartureDelta());
                //Log.d("RealTime", "RealTime Info TravelSegment..RealTimeArrivalDelta...." + realTimeInfoTravelSegment.getRealTimeArrivalDelta());
                if (realTimeInfoTravelSegment.getLegStatus() != null && !realTimeInfoTravelSegment.getLegStatus().isEmpty()) {
                    tvRealtimeResult.setVisibility(View.VISIBLE);
                    tvRealtimeResult.setText(realTimeInfoTravelSegment.getLegStatus());
                    tvRealtimeResult.setTextColor(activity.getResources().getColor(R.color.textcolor_error));
                } else {
                    if (realTimeInfoTravelSegment.getRealTimeDepartureDelta() != null && !realTimeInfoTravelSegment.getRealTimeDepartureDelta().isEmpty()) {
                        tvRealtimeResult.setVisibility(View.VISIBLE);
                        tvRealtimeResult.setText(realTimeInfoTravelSegment.getRealTimeDepartureDelta());
                        tvRealtimeResult.setTextColor(activity.getResources().getColor(R.color.textcolor_error));
                    } else {
                        if (realTimeInfoTravelSegment.getRealTimeArrivalDelta() != null && !realTimeInfoTravelSegment.getRealTimeArrivalDelta().isEmpty()) {
                            tvRealtimeResult.setVisibility(View.VISIBLE);
                            tvRealtimeResult.setText(activity.getResources().getString(R.string.realtime_arrival_delay));
                            tvRealtimeResult.setTextColor(activity.getResources().getColor(R.color.textcolor_error));
                        }
                    }
                    tvRealtimeResult.setVisibility(View.VISIBLE);
                    tvRealtimeResult.setTextColor(activity.getResources().getColor(R.color.textcolor_error));
                }

                if ((realTimeInfoTravelSegment.getLegStatus() == null || realTimeInfoTravelSegment.getLegStatus().isEmpty())
                        && (realTimeInfoTravelSegment.getRealTimeDepartureDelta() == null || realTimeInfoTravelSegment.getRealTimeDepartureDelta().isEmpty())
                        && realTimeInfoTravelSegment.getRealTimeArrivalDelta() == null || realTimeInfoTravelSegment.getRealTimeArrivalDelta().isEmpty()) {
                    tvRealtimeResult.setVisibility(View.VISIBLE);
                    tvRealtimeResult.setText(activity.getResources().getString(R.string.general_ontime));
                    tvRealtimeResult.setTextColor(activity.getResources().getColor(R.color.tertiary_text_light));
                }
            }
        }
        linearLayout.addView(convertView);
    }
}
