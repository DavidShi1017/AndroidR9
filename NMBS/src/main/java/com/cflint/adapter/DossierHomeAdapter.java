package com.cflint.adapter;

import android.app.Activity;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cflint.R;
import com.cflint.activities.DossierDetailActivity;
import com.cflint.activities.TicketsDetailActivity;
import com.cflint.application.NMBSApplication;
import com.cflint.async.RealTimeInfoAsyncTask;
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
public class DossierHomeAdapter {

    private final static String TAG = DossierHomeAdapter.class.getSimpleName();
    private LayoutInflater layoutInflater;
    private Activity activity;
    private DossierTravelSegment dossierTravelSegment;
    private Dossier dossier;
    private TextView tvRealtimeResult;
    public DossierHomeAdapter(Activity activity){
        this.activity = activity;

        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private void showRealTimeResult(RealTimeInfoTravelSegment realTimeInfoTravelSegment, RealTimeInfoResponse realTimeInfoResponse){
        if(realTimeInfoTravelSegment != null && realTimeInfoResponse != null){
        //Log.d("RealTime", "RealTime Info TravelSegment id is..." + realTimeInfoResponse.getId());
        //Log.d("RealTime", "RealTime Info TravelSegment IsSuccess??..." + realTimeInfoResponse.getIsSuccess());
        //Log.d("RealTime", "RealTime Info TravelSegment..LegStatus...." + realTimeInfoTravelSegment.getLegStatus());
        //Log.d("RealTime", "RealTime Info TravelSegment..RealTimeDepartureDelta...." + realTimeInfoTravelSegment.getRealTimeDepartureDelta());
        //Log.d("RealTime", "RealTime Info TravelSegment..RealTimeArrivalDelta...." + realTimeInfoTravelSegment.getRealTimeArrivalDelta());
        if(realTimeInfoTravelSegment.getLegStatus() != null && !realTimeInfoTravelSegment.getLegStatus().isEmpty()){
            tvRealtimeResult.setVisibility(View.VISIBLE);
            tvRealtimeResult.setText(realTimeInfoTravelSegment.getLegStatus());
            tvRealtimeResult.setTextColor(activity.getResources().getColor(R.color.textcolor_error));
        }else{
            if(realTimeInfoTravelSegment.getRealTimeDepartureDelta() != null && !realTimeInfoTravelSegment.getRealTimeDepartureDelta().isEmpty()){
                tvRealtimeResult.setVisibility(View.VISIBLE);
                tvRealtimeResult.setText(realTimeInfoTravelSegment.getRealTimeDepartureDelta());
                tvRealtimeResult.setTextColor(activity.getResources().getColor(R.color.textcolor_error));
            }else{
                if(realTimeInfoTravelSegment.getRealTimeArrivalDelta() != null && !realTimeInfoTravelSegment.getRealTimeArrivalDelta().isEmpty()){
                    tvRealtimeResult.setVisibility(View.VISIBLE);
                    tvRealtimeResult.setText(activity.getResources().getString(R.string.realtime_arrival_delay));
                    tvRealtimeResult.setTextColor(activity.getResources().getColor(R.color.textcolor_error));
                }
            }
            tvRealtimeResult.setVisibility(View.VISIBLE);
            tvRealtimeResult.setTextColor(activity.getResources().getColor(R.color.textcolor_error));
        }

        if((realTimeInfoTravelSegment.getLegStatus() == null || realTimeInfoTravelSegment.getLegStatus().isEmpty())
                && (realTimeInfoTravelSegment.getRealTimeDepartureDelta() == null || realTimeInfoTravelSegment.getRealTimeDepartureDelta().isEmpty())
                && (realTimeInfoTravelSegment.getRealTimeArrivalDelta() == null || realTimeInfoTravelSegment.getRealTimeArrivalDelta().isEmpty())){
            tvRealtimeResult.setVisibility(View.VISIBLE);
            tvRealtimeResult.setText(activity.getResources().getString(R.string.general_ontime));
            tvRealtimeResult.setTextColor(activity.getResources().getColor(R.color.tertiary_text_light));
        }
    }else{
                /*tvHomeRealTime.setVisibility(View.VISIBLE);
                tvHomeRealTime.setText(activity.getResources().getString(R.string.general_refreshing_realtime_failed));
                tvHomeRealTime.setTextColor(activity.getResources().getColor(R.color.textcolor_error));*/
    }}

    public void getHomeTravelSegmentView(final DossierTravelSegment dossierTravelSegment, LinearLayout linearLayout,
                                         final Dossier dossier, final DossierSummary dossierSummary, boolean shouldRefresh) {
        this.dossierTravelSegment = dossierTravelSegment;
        this.dossier = dossier;
        View convertView = null;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.li_dossier_home, null);
        }
        boolean shouldAdd = true;
        RelativeLayout llTicketInfo = (RelativeLayout)convertView.findViewById(R.id.ll_dossier_home_info);
        TextView tvSegmentStation = (TextView)convertView.findViewById(R.id.tv_home_segment_station);
        TextView tvDepartureLabel = (TextView)convertView.findViewById(R.id.tv_departure_lable);
        tvDepartureLabel.setText(activity.getResources().getString(R.string.general_departure) + ": ");
        TextView tvDepartureDate = (TextView)convertView.findViewById(R.id.tv_home_departure_date);
        TextView tvTrainInfo = (TextView) convertView.findViewById(R.id.tv_home_train_info);
        LinearLayout llDossierChild = (LinearLayout)convertView.findViewById(R.id.ll_home_dossier_child);
        LinearLayout llHomeRealTime = (LinearLayout)convertView.findViewById(R.id.ll_home_realtime);
        ProgressBar pbHomeRealTime = (ProgressBar)convertView.findViewById(R.id.pb_home_realtime);
        TextView tvHomeRealTime = (TextView)convertView.findViewById(R.id.tv_home_realtime_status);
        tvRealtimeResult = (TextView)convertView.findViewById(R.id.tv_home_realtime_info);


        llTicketInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NMBSApplication.getInstance().getDossierDetailsService().setCurrentDossier(null);
                NMBSApplication.getInstance().getDossierDetailsService().setCurrentDossierSummary(null);
                activity.startActivity(DossierDetailActivity.createIntent(activity.getApplicationContext(), dossier, dossierSummary));
            }
        });

        //Log.e(TAG, "RealTime Info shouldRefresh..." + shouldRefresh);
        if(dossierTravelSegment != null && dossier != null){
            DossierDetailsService dossierDetailsService = NMBSApplication.getInstance().getDossierDetailsService();
            RealTimeInfoResponse realTimeInfoResponse = dossierDetailsService.readRealTimeInfoById(dossierTravelSegment.getTravelSegmentId(), activity.getApplication());
            RealTimeInfoTravelSegment realTimeInfoTravelSegment = (RealTimeInfoTravelSegment) dossierDetailsService.getRealTime(realTimeInfoResponse);


            //Log.e("isRefreshing", "isRefreshing..." + RealTimeInfoAsyncTask.isRefreshing + "...shouldRefresh..." + shouldRefresh);
            if(shouldRefresh && RealTimeInfoAsyncTask.isRefreshing){
                //Log.e("RealTime", "RealTime Info TravelSegment id is..." + realTimeInfoResponse.getId());
                llHomeRealTime.setVisibility(View.VISIBLE);
                pbHomeRealTime.setVisibility(View.VISIBLE);
                tvHomeRealTime.setText(activity.getResources().getString(R.string.general_refreshing_realtime));
                tvHomeRealTime.setTextColor(activity.getResources().getColor(R.color.textcolor_thirdaction));
            }else{
                pbHomeRealTime.setVisibility(View.GONE);
                if(realTimeInfoResponse != null){
                    llHomeRealTime.setVisibility(View.GONE);
                    if(!realTimeInfoResponse.getIsSuccess()){
                        llHomeRealTime.setVisibility(View.VISIBLE);
                        tvHomeRealTime.setText(activity.getResources().getString(R.string.general_refreshing_realtime_failed));
                        tvHomeRealTime.setTextColor(activity.getResources().getColor(R.color.textcolor_error));
                    }
                }
            }
            int count = 0;
            if(dossierTravelSegment.getSegmentPassengers() != null){
                count = dossierTravelSegment.getSegmentPassengers().size();
            }
            List<DossierTravelSegment> dossierChildTravelSegments = dossier.getChildTravelSegments(dossierTravelSegment);
            //Log.d(TAG, "dossierChildTravelSegments..." + dossierChildTravelSegments.size());
            tvSegmentStation.setText(dossierTravelSegment.getOriginStationName() + " - " + dossierTravelSegment.getDestinationStationName());
            showRealTimeResult(realTimeInfoTravelSegment, realTimeInfoResponse);
            // TravelSegment with SegmentType = Marketprice
            if(DossierDetailsService.SegmentType_MARKETPRICE.equalsIgnoreCase(dossierTravelSegment.getSegmentType())){
                //Log.d(TAG, "TravelSegment with SegmentType = Marketprice...");
                llDossierChild.setVisibility(View.GONE);
                tvDepartureDate.setText(DateUtils.dateTimeToString(dossierTravelSegment.getDepartureDateTime(), DateUtils.getRightFormat()));
                String trainType = dossierTravelSegment.getTrainType();
                TrainIcon trainIcon = NMBSApplication.getInstance().getTrainIconsService().getTrainIcon(dossierTravelSegment.getTrainType());
                if(trainIcon != null){
                    if(trainIcon.getBrandName() != null && !trainIcon.getBrandName().isEmpty()){
                        trainType = trainIcon.getBrandName();
                    }
                }
                if(count == 1){
                    tvTrainInfo.setText(trainType + " - " + count + " " + activity.getResources().getString(R.string.general_passenger) + " - "
                            + dossierDetailsService.comfortClass(dossierTravelSegment.getComfortClass()));
                }else{
                    tvTrainInfo.setText(trainType + " - " + count + " " + activity.getResources().getString(R.string.general_passengers) + " - "
                            + dossierDetailsService.comfortClass(dossierTravelSegment.getComfortClass()));
                }

            }
            // TravelSegment with SegmentType = Admission AND without child TravelSegments
            else if(DossierDetailsService.SegmentType_Admission.equalsIgnoreCase(dossierTravelSegment.getSegmentType())
                    && (dossierChildTravelSegments == null ||  dossierChildTravelSegments.size() == 0)){
                //Log.d(TAG, "TravelSegment with SegmentType = Admission AND without child TravelSegments...");
                llDossierChild.setVisibility(View.GONE);
                tvDepartureDate.setText(DateUtils.dateTimeToString(dossierTravelSegment.getDepartureDateTime(), "dd MMMM yyyy"));
                if(count == 1){
                    tvTrainInfo.setText(count + " " + activity.getResources().getString(R.string.general_passenger) + " - "
                            + dossierDetailsService.comfortClass(dossierTravelSegment.getComfortClass()));
                }else{
                    tvTrainInfo.setText(count + " " + activity.getResources().getString(R.string.general_passengers) + " - "
                            + dossierDetailsService.comfortClass(dossierTravelSegment.getComfortClass()));
                }

            }

            else if(DossierDetailsService.SegmentType_Admission.equalsIgnoreCase(dossierTravelSegment.getSegmentType()) && dossierChildTravelSegments.size() > 0){
                if(dossierChildTravelSegments != null &&  dossierChildTravelSegments.size() == 1){
                    // TravelSegment with SegmentType = Admission AND with one child TravelSegment with exactly the same OriginStationRcode & DestinationStationRcode as the Admission
                    //Log.d(TAG, "TravelSegment with SegmentType = Admission AND with one child TravelSegment with exactly the same OriginStationRcode & DestinationStationRcode as the Admission...");
                    llDossierChild.setVisibility(View.GONE);
                    DossierTravelSegment dossierTravelSegmentChild = dossierChildTravelSegments.get(0);
                    if(dossierTravelSegmentChild != null){
                        if(dossierTravelSegment.getOriginStationRcode().equalsIgnoreCase(dossierTravelSegmentChild.getOriginStationRcode())
                                && dossierTravelSegment.getDestinationStationRcode().equalsIgnoreCase(dossierTravelSegmentChild.getDestinationStationRcode())){
                            tvDepartureDate.setText(DateUtils.dateTimeToString(dossierTravelSegmentChild.getDepartureDateTime(), DateUtils.getRightFormat()));
                            String trainType = dossierTravelSegment.getTrainType();
                            TrainIcon trainIcon = NMBSApplication.getInstance().getTrainIconsService().getTrainIcon(dossierTravelSegment.getTrainType());
                            if(trainIcon != null){
                                if(trainIcon.getBrandName() != null && !trainIcon.getBrandName().isEmpty()){
                                    trainType = trainIcon.getBrandName();
                                }
                            }
                            if(count == 1){
                                tvTrainInfo.setText(trainType + " - " + count + " " + activity.getResources().getString(R.string.general_passenger) + " - "
                                        + dossierDetailsService.comfortClass(dossierTravelSegment.getComfortClass()));
                            }else{
                                tvTrainInfo.setText(trainType + " - " + count + " " + activity.getResources().getString(R.string.general_passengers) + " - "
                                        + dossierDetailsService.comfortClass(dossierTravelSegment.getComfortClass()));
                            }

                            realTimeInfoResponse = dossierDetailsService.readRealTimeInfoById(dossierTravelSegmentChild.getTravelSegmentId(), activity.getApplication());
                            realTimeInfoTravelSegment = (RealTimeInfoTravelSegment) dossierDetailsService.getRealTime(realTimeInfoResponse);
                            showRealTimeResult(realTimeInfoTravelSegment, realTimeInfoResponse);

                        }else{
                            //Log.d(TAG, "TravelSegment with SegmentType = Admission AND with one or more child TravelSegments with other ODs...");
                            llDossierChild.setVisibility(View.VISIBLE);
                            tvDepartureDate.setText(DateUtils.dateTimeToString(dossierTravelSegment.getDepartureDateTime(), "dd MMMM yyyy"));
                            if(count == 1){
                                tvTrainInfo.setText(count + " " + activity.getResources().getString(R.string.general_passenger) + " - "
                                        + dossierDetailsService.comfortClass(dossierTravelSegment.getComfortClass()));
                            }else{
                                tvTrainInfo.setText(count + " " + activity.getResources().getString(R.string.general_passengers) + " - "
                                        + dossierDetailsService.comfortClass(dossierTravelSegment.getComfortClass()));
                            }

                            DossierHomeChildAdapter dossierHomeChildAdapter = new DossierHomeChildAdapter(activity);
                            for(DossierTravelSegment dossierChildTravelSegment : dossierChildTravelSegments){
                                if(dossierTravelSegment != null){
                                    dossierHomeChildAdapter.getHomeTravelSegmentView(dossierChildTravelSegment, llDossierChild, dossier, dossierSummary);
                                }
                            }
                        }
                    }
                }else{
                    // TravelSegment with SegmentType = Admission AND with one or more child TravelSegments with other ODs
                    //Log.d(TAG, "TravelSegment with SegmentType = Admission AND with one or more child TravelSegments with other ODs...");
                    llDossierChild.setVisibility(View.VISIBLE);
                    tvDepartureDate.setText(DateUtils.dateTimeToString(dossierTravelSegment.getDepartureDateTime(), "dd MMMM yyyy"));
                    if(count == 1){
                        tvTrainInfo.setText(count + " " + activity.getResources().getString(R.string.general_passenger) + " - "
                                + dossierDetailsService.comfortClass(dossierTravelSegment.getComfortClass()));
                    }else{
                        tvTrainInfo.setText(count + " " + activity.getResources().getString(R.string.general_passengers) + " - "
                                + dossierDetailsService.comfortClass(dossierTravelSegment.getComfortClass()));
                    }

                    DossierHomeChildAdapter dossierHomeChildAdapter = new DossierHomeChildAdapter(activity);
                    for(DossierTravelSegment dossierChildTravelSegment : dossierChildTravelSegments){
                        if(dossierTravelSegment != null){
                            dossierHomeChildAdapter.getHomeTravelSegmentView(dossierChildTravelSegment, llDossierChild, dossier, dossierSummary);
                        }
                    }
                }
            }
            // TravelSegment with SegmentType = Reservation AND without a parent TravelSegment
            else if(DossierDetailsService.SegmentType_Reservation.equalsIgnoreCase(dossierTravelSegment.getSegmentType())
                    && (dossierTravelSegment.getParentTravelSegmentId() == null || dossierTravelSegment.getParentTravelSegmentId().isEmpty())){
                //Log.d(TAG, "TravelSegment with SegmentType = Reservation AND without a parent TravelSegment...");
                llDossierChild.setVisibility(View.GONE);
                tvDepartureDate.setText(DateUtils.dateTimeToString(dossierTravelSegment.getDepartureDateTime(), DateUtils.getRightFormat()));
                String trainType = dossierTravelSegment.getTrainType();
                TrainIcon trainIcon = NMBSApplication.getInstance().getTrainIconsService().getTrainIcon(dossierTravelSegment.getTrainType());
                if(trainIcon != null){
                    if(trainIcon.getBrandName() != null && !trainIcon.getBrandName().isEmpty()){
                        trainType = trainIcon.getBrandName();
                    }
                }
                if(count == 1){
                    tvTrainInfo.setText(trainType + " - " + count + " " + activity.getResources().getString(R.string.general_passenger) + " - "
                            + dossierDetailsService.comfortClass(dossierTravelSegment.getComfortClass()));
                }else{
                    tvTrainInfo.setText(trainType + " - " + count + " " + activity.getResources().getString(R.string.general_passengers) + " - "
                            + dossierDetailsService.comfortClass(dossierTravelSegment.getComfortClass()));
                }

            }
            // TravelSegment with SegmentType = Reservation AND with a parent TravelSegment
            else if(DossierDetailsService.SegmentType_Reservation.equalsIgnoreCase(dossierTravelSegment.getSegmentType())
                    && (dossierTravelSegment.getParentTravelSegmentId() != null && !dossierTravelSegment.getParentTravelSegmentId().isEmpty())){
                //Log.d(TAG, "TravelSegment with SegmentType = Reservation AND with a parent TravelSegment...");
                llDossierChild.setVisibility(View.GONE);
                shouldAdd = false;
            }
        }
        if (shouldAdd){

            linearLayout.addView(convertView);
        }
    }
}
