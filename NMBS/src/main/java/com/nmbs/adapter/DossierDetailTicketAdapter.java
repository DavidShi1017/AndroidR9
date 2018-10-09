package com.nmbs.adapter;

import android.app.Activity;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import android.widget.TextView;

import com.nmbs.R;

import com.nmbs.activities.TicketsDetailActivity;
import com.nmbs.application.NMBSApplication;
import com.nmbs.async.RealTimeInfoAsyncTask;
import com.nmbs.dataaccess.restservice.impl.DossierDetailDataService;
import com.nmbs.model.Connection;
import com.nmbs.model.Dossier;
import com.nmbs.model.DossierSummary;
import com.nmbs.model.DossierTravelSegment;
import com.nmbs.model.PDF;
import com.nmbs.model.Passenger;
import com.nmbs.model.RealTimeInfoRequestParameter;
import com.nmbs.model.RealTimeInfoResponse;
import com.nmbs.model.RealTimeInfoTravelSegment;
import com.nmbs.model.SeatLocation;
import com.nmbs.model.Ticket;
import com.nmbs.model.TrainIcon;
import com.nmbs.services.impl.DossierDetailsService;
import com.nmbs.util.ComparatorConnectionDate;
import com.nmbs.util.ComparatorTravelSegmentDate;
import com.nmbs.util.DateUtils;
import com.nmbs.util.FileManager;
import com.nmbs.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by David on 2/26/16.
 */
public class DossierDetailTicketAdapter {

    private LayoutInflater layoutInflater;
    private Activity activity;
    private DossierTravelSegment dossierTravelSegment;
    private Dossier dossier;
    private TextView tvSegmentStation, tvDepartureDate, tvArrivalDate, tvTrainInfo, tvArrival;
    private DossierDetailsService dossierDetailsService;
    private final static String TAG = DossierDetailTicketAdapter.class.getSimpleName();
    public DossierDetailTicketAdapter(Activity activity){
        this.activity = activity;
        dossierDetailsService = ((NMBSApplication) activity.getApplication()).getDossierDetailsService();
        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private void showRealTimeView(View convertView, boolean shouldRefresh){
        LinearLayout llRealTime = (LinearLayout)convertView.findViewById(R.id.ll_realtime);
        ProgressBar pbRealTime = (ProgressBar)convertView.findViewById(R.id.pb_realtime);
        TextView tvRealTimeResult = (TextView)convertView.findViewById(R.id.tv_realtime_result);
        TextView tvRealTimeDepartureInfo = (TextView)convertView.findViewById(R.id.tv_realtime_departure_info);
        TextView tvRealTimeArrivalInfo = (TextView)convertView.findViewById(R.id.tv_realtime_arrival_info);

        DossierDetailsService dossierDetailsService = NMBSApplication.getInstance().getDossierDetailsService();
        RealTimeInfoResponse realTimeInfoResponse = dossierDetailsService.readRealTimeInfoById(dossierTravelSegment.getTravelSegmentId(), activity.getApplication());
        RealTimeInfoTravelSegment realTimeInfoTravelSegment = (RealTimeInfoTravelSegment) dossierDetailsService.getRealTime(realTimeInfoResponse);

        if(shouldRefresh){
            //Log.e(TAG, "shouldRefresh..." + shouldRefresh + "...The dossierTravelSegment id is..." + dossierTravelSegment.getTravelSegmentId());
            if(RealTimeInfoAsyncTask.isRefreshing){
                //Log.e(TAG, "RealTimeInfoAsyncTask.isRefreshing..." + RealTimeInfoAsyncTask.isRefreshing);
                llRealTime.setVisibility(View.VISIBLE);
                pbRealTime.setVisibility(View.VISIBLE);
                tvRealTimeResult.setText(activity.getResources().getString(R.string.general_refreshing_realtime));
                tvRealTimeResult.setTextColor(activity.getResources().getColor(R.color.textcolor_thirdaction));
            }else{
                pbRealTime.setVisibility(View.GONE);
                if(realTimeInfoResponse != null){
                    llRealTime.setVisibility(View.GONE);
                    if(!realTimeInfoResponse.getIsSuccess()) {
                        llRealTime.setVisibility(View.VISIBLE);
                        tvRealTimeResult.setText(activity.getResources().getString(R.string.general_refreshing_realtime_failed));
                        tvRealTimeResult.setTextColor(activity.getResources().getColor(R.color.textcolor_error));
                    }
                    if(realTimeInfoTravelSegment != null && realTimeInfoResponse != null) {
                        //Log.d("RealTime", "RealTime Info TravelSegment id is..." + realTimeInfoResponse.getId());
                        //Log.d("RealTime", "RealTime Info TravelSegment IsSuccess??..." + realTimeInfoResponse.getIsSuccess());
                        //Log.d("RealTime", "RealTime Info TravelSegment..RealTimeArrivalDelta...." + realTimeInfoTravelSegment.getRealTimeArrivalDelta());
                        if(realTimeInfoTravelSegment.getLegStatus() != null && !realTimeInfoTravelSegment.getLegStatus().isEmpty()){
                            tvRealTimeDepartureInfo.setVisibility(View.VISIBLE);
                            tvRealTimeDepartureInfo.setText(realTimeInfoTravelSegment.getLegStatus());
                            tvRealTimeDepartureInfo.setTextColor(activity.getResources().getColor(R.color.textcolor_error));
                        }else{
                            if(realTimeInfoTravelSegment.getRealTimeDepartureDelta() != null){
                                tvRealTimeDepartureInfo.setVisibility(View.VISIBLE);
                                tvRealTimeDepartureInfo.setText(realTimeInfoTravelSegment.getRealTimeDepartureDelta());
                                tvRealTimeDepartureInfo.setTextColor(activity.getResources().getColor(R.color.textcolor_error));
                            }else{
                                tvRealTimeArrivalInfo.setVisibility(View.GONE);
                            }
                            if(realTimeInfoTravelSegment.getRealTimeArrivalDelta() != null){
                                tvRealTimeArrivalInfo.setVisibility(View.VISIBLE);
                                tvRealTimeArrivalInfo.setText(realTimeInfoTravelSegment.getRealTimeArrivalDelta());
                                tvRealTimeArrivalInfo.setTextColor(activity.getResources().getColor(R.color.textcolor_error));
                            }else {
                                tvRealTimeArrivalInfo.setVisibility(View.GONE);
                            }
                        }
                        if((realTimeInfoTravelSegment.getLegStatus() == null || realTimeInfoTravelSegment.getLegStatus().isEmpty())
                                && (realTimeInfoTravelSegment.getRealTimeDepartureDelta() == null || realTimeInfoTravelSegment.getRealTimeDepartureDelta().isEmpty())
                                && (realTimeInfoTravelSegment.getRealTimeArrivalDelta() == null || realTimeInfoTravelSegment.getRealTimeArrivalDelta().isEmpty())){
                            tvRealTimeDepartureInfo.setVisibility(View.VISIBLE);
                            tvRealTimeDepartureInfo.setText(activity.getResources().getString(R.string.general_ontime));
                            tvRealTimeDepartureInfo.setTextColor(activity.getResources().getColor(R.color.tertiary_text_light));
                        }
                    }
                }
            }

        }else{
            tvRealTimeDepartureInfo.setVisibility(View.GONE);
            tvRealTimeArrivalInfo.setVisibility(View.GONE);
            llRealTime.setVisibility(View.GONE);
            tvRealTimeResult.setVisibility(View.GONE);
        }

    }

    public void getTravelSegmentView(final DossierTravelSegment dossierTravelSegment, LinearLayout linearLayout,
                                     final Dossier dossier, final DossierSummary dossierSummary, boolean shouldRefresh,
                                     final RealTimeInfoRequestParameter realTimeInfoRequestParameter) {
        this.dossierTravelSegment = dossierTravelSegment;
        this.dossier = dossier;
        //View convertView = null;
        //if(convertView == null){
        final View convertView = layoutInflater.inflate(R.layout.li_dossier_detail_ticket, null);
       // }

        LinearLayout llTicketInfo = (LinearLayout)convertView.findViewById(R.id.ll_dossier_detail_tickets_info);
        TextView tvDossierError = (TextView)convertView.findViewById(R.id.tv_dossier_ticket_error);
        TextView tvDossierProvisional = (TextView)convertView.findViewById(R.id.tv_dossier_ticket_provisional);
        TextView tvDepartureLabel = (TextView)convertView.findViewById(R.id.tv_departure_lable);
        tvDepartureLabel.setText(activity.getResources().getString(R.string.general_departure) + ": ");
        tvSegmentStation = (TextView)convertView.findViewById(R.id.tv_segment_station);
        tvDepartureDate = (TextView)convertView.findViewById(R.id.tv_departure_date);
        tvArrivalDate = (TextView) convertView.findViewById(R.id.tv_arrival_date);
        tvTrainInfo = (TextView) convertView.findViewById(R.id.tv_train_info);
        tvArrival = (TextView) convertView.findViewById(R.id.tv_arrival);

        TextView tvPassengerInfo = (TextView) convertView.findViewById(R.id.tv_passenger_info);
        LinearLayout llABS = (LinearLayout)convertView.findViewById(R.id.ll_abs);
        LinearLayout llADS = (LinearLayout)convertView.findViewById(R.id.ll_ads);
        LinearLayout llHasOverbooking = (LinearLayout)convertView.findViewById(R.id.ll_has_overbooking);
        LinearLayout llChildTravelsegments = (LinearLayout)convertView.findViewById(R.id.ll_dossier_child_travelsegments);
        LinearLayout llLinkedSeat = (LinearLayout)convertView.findViewById(R.id.ll_linked_seat);
        TextView tvAdvance = (TextView)convertView.findViewById(R.id.tv_advance);
        Button btnViewTicket = (Button) convertView.findViewById(R.id.btn_view_ticket);

        LinearLayout llConnectionMonitor = (LinearLayout)convertView.findViewById(R.id.ll_travelsegment_monitoring_active);
        ImageView ivMonitor = (ImageView)convertView.findViewById(R.id.iv_travelsegment_monitoring);
        TextView tvMonitor = (TextView)convertView.findViewById(R.id.tv_travelsegment_monitoring);
        /*Button btnToSchedule = (Button) convertView.findViewById(R.id.btn_travelsegment_to_schedule);*/
        if(dossierTravelSegment.getTickets() != null && dossierTravelSegment.getTickets().size() > 0){
            btnViewTicket.setVisibility(View.VISIBLE);
        }else{
            btnViewTicket.setVisibility(View.GONE);
        }
        btnViewTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NMBSApplication.getInstance().getDossierDetailsService().setCurrentDossierTravelSegment(dossierTravelSegment);
                if(dossierTravelSegment.getTickets() != null && dossierTravelSegment.getTickets().size() > 0){
                    activity.startActivity(TicketsDetailActivity.createIntent(activity.getApplicationContext(), dossierTravelSegment,
                            dossier, dossierSummary, realTimeInfoRequestParameter));
                }


            }
        });
        /*btnToSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(ScheduleSearchActivity.createIntent(activity));
            }
        });*/
        if(dossierTravelSegment != null){

            if(DossierDetailsService.SegmentType_MARKETPRICE.equalsIgnoreCase(dossierTravelSegment.getSegmentType())
                    || DossierDetailsService.SegmentType_Reservation.equalsIgnoreCase(dossierTravelSegment.getSegmentType())){
                int status = dossierDetailsService.checkSubscriptionStatusForTravelSegment(dossierTravelSegment, dossier);
                if(DossierDetailsService.Dossier_Nothing == status){
                    llConnectionMonitor.setVisibility(View.GONE);
                }else{
                    llConnectionMonitor.setVisibility(View.VISIBLE);
                    if(DossierDetailsService.Dossier_Active == status){
                        ivMonitor.setImageResource(R.drawable.ic_monitor_active);
                        tvMonitor.setText(activity.getResources().getString(R.string.dossier_detail_monitoring_active));
                        tvMonitor.setTextColor(activity.getResources().getColor(R.color.tertiary_text_light));
                    }else if(DossierDetailsService.Dossier_NoActive == status){
                        ivMonitor.setImageResource(R.drawable.ic_monitor_active_not);
                        tvMonitor.setText(activity.getResources().getString(R.string.dossier_detail_monitoring_active_not));
                        tvMonitor.setTextColor(activity.getResources().getColor(R.color.textcolor_thirdaction));
                        //btnToSchedule.setVisibility(View.VISIBLE);

                    }else{
                        ivMonitor.setImageResource(R.drawable.ic_monitor_active_failed);
                        tvMonitor.setText(activity.getResources().getString(R.string.dossier_detail_monitoring_active_failed));
                        tvMonitor.setTextColor(activity.getResources().getColor(R.color.textcolor_error));
                    }
                }
            }


            showRealTimeView(convertView, shouldRefresh);
            if(dossierTravelSegment.getTariffGroupText() != null && !dossierTravelSegment.getTariffGroupText().isEmpty()){
                tvAdvance.setVisibility(View.VISIBLE);
                tvAdvance.setText(dossierTravelSegment.getTariffGroupText());
            }else{
                tvAdvance.setVisibility(View.GONE);
            }

            List<DossierTravelSegment> dossierChildTravelSegments = dossier.getChildTravelSegments(dossierTravelSegment);
            if(dossierChildTravelSegments != null && dossierChildTravelSegments.size() > 0){
                llLinkedSeat.setVisibility(View.VISIBLE);
                DossierDetailTicketLinedAdapter dossierDetailTicketLinedAdapter = new DossierDetailTicketLinedAdapter(activity);

                for(DossierTravelSegment dossierChildTravelSegment : dossierChildTravelSegments){
                    boolean shouldRefreshChild = dossierDetailsService.shouldRefresh(realTimeInfoRequestParameter, dossierChildTravelSegment, null);
                    //Log.e("LinedAdapter", "shouldRefresh..." + shouldRefreshChild + "...dossierChildTravelSegment..." + dossierChildTravelSegment.getTravelSegmentId());
                    if(dossierChildTravelSegment != null){
                        dossierDetailTicketLinedAdapter.getChildTravelSegmentView(dossierChildTravelSegment, llChildTravelsegments, dossier,
                                dossierSummary, shouldRefreshChild, realTimeInfoRequestParameter);
                    }
                }
            }else {
                llLinkedSeat.setVisibility(View.GONE);
            }
            if(DossierDetailsService.DossierStateError.equalsIgnoreCase(dossierTravelSegment.getSegmentState())){
                tvDossierError.setVisibility(View.VISIBLE);
            }else{
                tvDossierError.setVisibility(View.GONE);
            }
            if(DossierDetailsService.DossierStateInProgress.equalsIgnoreCase(dossierTravelSegment.getSegmentState())){
                tvDossierProvisional.setVisibility(View.VISIBLE);
            }else{
                tvDossierProvisional.setVisibility(View.GONE);
            }

            initSegment();
            initPassengerInfo(tvPassengerInfo);
            initABS(llABS);
            initADS(llADS);
            initOverBooking(llHasOverbooking);
            llConnectionMonitor.setVisibility(View.GONE);
        }
        convertView.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //Log.e("slRoot", "slRoot...." + convertView.getTop());
                //convertView.scrollTo(0, btnSetAlert.getTop());
            }
        });

        linearLayout.addView(convertView);

    }
    private void initSegment(){

        tvSegmentStation.setText(dossierTravelSegment.getOriginStationName() + " - " + dossierTravelSegment.getDestinationStationName());
        String trainType = dossierTravelSegment.getTrainType();
        TrainIcon trainIcon = NMBSApplication.getInstance().getTrainIconsService().getTrainIcon(dossierTravelSegment.getTrainType());
        if(trainIcon != null){
            if(trainIcon.getBrandName() != null && !trainIcon.getBrandName().isEmpty()){
                trainType = trainIcon.getBrandName();
            }
        }
        tvTrainInfo.setText(trainType + " " + dossierTravelSegment.getTrainNumber() + " - "
                + dossierDetailsService.comfortClass(dossierTravelSegment.getComfortClass()));
        Log.e("SegmentType", "SegmentType---->" + dossierTravelSegment.getSegmentType());
        if(DossierDetailsService.SegmentType_MARKETPRICE.equalsIgnoreCase(dossierTravelSegment.getSegmentType())){

            tvDepartureDate.setText(DateUtils.dateTimeToString(dossierTravelSegment.getDepartureDateTime(), DateUtils.getRightFormat()));
            tvArrivalDate.setText(DateUtils.dateTimeToString(dossierTravelSegment.getArrivalDateTime(), DateUtils.getRightFormat()));
            tvArrival.setText(activity.getResources().getString(R.string.general_arrival) + ":");
        }
        // TravelSegment with SegmentType = Admission
        else if(DossierDetailsService.SegmentType_Admission.equalsIgnoreCase(dossierTravelSegment.getSegmentType())){
            //Log.d(TAG, "TravelSegment with SegmentType = Admission AND without child TravelSegments...");
            tvDepartureDate.setText(DateUtils.dateTimeToString(dossierTravelSegment.getDepartureDateTime(), "dd MMMM yyyy"));
            tvArrivalDate.setVisibility(View.GONE);
            tvArrival.setVisibility(View.GONE);
            tvTrainInfo.setText(dossierDetailsService.comfortClass(dossierTravelSegment.getComfortClass()));
        }
        else if(DossierDetailsService.SegmentType_Reservation.equalsIgnoreCase(dossierTravelSegment.getSegmentType())){
            tvDepartureDate.setText(DateUtils.dateTimeToString(dossierTravelSegment.getDepartureDateTime(), DateUtils.getRightFormat()));
            tvArrivalDate.setText(DateUtils.dateTimeToString(dossierTravelSegment.getArrivalDateTime(), DateUtils.getRightFormat()));
            tvArrival.setText(activity.getResources().getString(R.string.general_arrival) + ":");
        }
    }

    private void initPassengerInfo(TextView tvPassengerInfo){
        List<Passenger> passengers = dossierTravelSegment.getSegmentPassengers();
        String passengerText = "";
        if(passengers != null && passengers.size() > 0){
            for (int i = 0; i < passengers.size(); i ++){
                Passenger passenger = passengers.get(i);
                if(passenger != null){
                    String name = passenger.getFirstName() + " " + passenger.getLastName();
                    if(name == null || name.isEmpty()){
                        name = dossierDetailsService.getPassengerTypeText(activity.getApplicationContext(), passenger.getPassengerType()) ;
                    }
                    SeatLocation seatLocation = dossierTravelSegment.getSeatLocation(passenger.getSegmentPassengerId());
                    if(seatLocation != null){
                        passengerText += DossierDetailsService.getPassengeText(activity.getApplicationContext(), name, seatLocation.getCoachNumber(), seatLocation.getSeatNumberInfo());
                    }else{
                        passengerText += DossierDetailsService.getPassengeText(activity.getApplicationContext(), name, null, null);
                    }
                    if(i != passengers.size() - 1){
                        passengerText += "\n";
                    }
                }
            }
        }
        tvPassengerInfo.setText(passengerText);
    }

    private void initABS(LinearLayout llABS){
        if(dossierTravelSegment.isHasABS()){
            llABS.setVisibility(View.VISIBLE);
        }else{
            llABS.setVisibility(View.GONE);
        }
    }

    private void initADS(LinearLayout llADS){
        if(dossierTravelSegment.isHasADS()){
            llADS.setVisibility(View.VISIBLE);
        }else{
            llADS.setVisibility(View.GONE);
        }
    }

    private void initOverBooking(LinearLayout llHasOverbooking){
        if(dossierTravelSegment.isHasOverbooking()){
            llHasOverbooking.setVisibility(View.VISIBLE);
        }else{
            llHasOverbooking.setVisibility(View.GONE);
        }
    }

}
