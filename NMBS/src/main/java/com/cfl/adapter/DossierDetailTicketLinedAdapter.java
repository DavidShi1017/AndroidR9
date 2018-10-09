package com.cfl.adapter;

import android.app.Activity;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cfl.R;
import com.cfl.activities.ScheduleSearchActivity;
import com.cfl.activities.TicketsDetailActivity;
import com.cfl.application.NMBSApplication;
import com.cfl.async.RealTimeInfoAsyncTask;
import com.cfl.model.Dossier;
import com.cfl.model.DossierSummary;
import com.cfl.model.DossierTravelSegment;
import com.cfl.model.Passenger;
import com.cfl.model.RealTimeInfoRequestParameter;
import com.cfl.model.RealTimeInfoResponse;
import com.cfl.model.RealTimeInfoTravelSegment;
import com.cfl.model.SeatLocation;
import com.cfl.model.TrainIcon;
import com.cfl.services.impl.DossierDetailsService;
import com.cfl.util.DateUtils;
import com.cfl.util.Utils;

import java.util.List;

/**
 * Created by David on 2/26/16.
 */
public class DossierDetailTicketLinedAdapter {

    private LayoutInflater layoutInflater;
    private Activity activity;
    private DossierTravelSegment dossierTravelSegment;
    private Dossier dossier;
    private TextView tvSegmentStationLinked, tvDepartureDateLinked, tvArrivalDateLinked, tvTrainInfoLinked;
    private DossierDetailsService dossierDetailsService;
    private final static String TAG = DossierDetailTicketLinedAdapter.class.getSimpleName();
    public DossierDetailTicketLinedAdapter(Activity activity){
        this.activity = activity;
        dossierDetailsService = ((NMBSApplication) activity.getApplication()).getDossierDetailsService();
        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private void showRealTimeView(View convertView, boolean shouldRefresh){
        LinearLayout llRealTimeLinked = (LinearLayout)convertView.findViewById(R.id.ll_realtime_linked);
        ProgressBar pbRealTimeLinked = (ProgressBar)convertView.findViewById(R.id.pb_realtime_linked);
        TextView tvRealTimeResultLinked = (TextView)convertView.findViewById(R.id.tv_realtime_result_linked);
        TextView tvRealTimeDepartureInfoLinked = (TextView)convertView.findViewById(R.id.tv_realtime_departure_info_linked);
        TextView tvRealTimeArrivalInfoLinked = (TextView)convertView.findViewById(R.id.tv_realtime_arrival_info_linked);

        DossierDetailsService dossierDetailsService = NMBSApplication.getInstance().getDossierDetailsService();
        RealTimeInfoResponse realTimeInfoResponse = dossierDetailsService.readRealTimeInfoById(dossierTravelSegment.getTravelSegmentId(), activity.getApplication());
        RealTimeInfoTravelSegment realTimeInfoTravelSegment = (RealTimeInfoTravelSegment) dossierDetailsService.getRealTime(realTimeInfoResponse);
        if(shouldRefresh){
            //Log.e("LinedAdapter", "shouldRefresh..." + shouldRefresh);
            if(RealTimeInfoAsyncTask.isRefreshing){
                //Log.e("LinedAdapter", "RealTimeInfoAsyncTask.isRefreshing..." + RealTimeInfoAsyncTask.isRefreshing);
                llRealTimeLinked.setVisibility(View.VISIBLE);
                pbRealTimeLinked.setVisibility(View.VISIBLE);
                tvRealTimeResultLinked.setText(activity.getResources().getString(R.string.general_refreshing_realtime));
                tvRealTimeResultLinked.setTextColor(activity.getResources().getColor(R.color.textcolor_thirdaction));
            }else{
                pbRealTimeLinked.setVisibility(View.GONE);
                if(realTimeInfoResponse != null){
                    llRealTimeLinked.setVisibility(View.GONE);
                    if(!realTimeInfoResponse.getIsSuccess()) {
                        llRealTimeLinked.setVisibility(View.VISIBLE);
                        tvRealTimeResultLinked.setText(activity.getResources().getString(R.string.general_refreshing_realtime_failed));
                        tvRealTimeResultLinked.setTextColor(activity.getResources().getColor(R.color.textcolor_error));
                    }
                    if(realTimeInfoTravelSegment != null && realTimeInfoResponse != null) {
                        //Log.d("LinedAdapter", "RealTime Info TravelSegment id is..." + realTimeInfoResponse.getId());
                        //Log.d("LinedAdapter", "RealTime Info TravelSegment IsSuccess??..." + realTimeInfoResponse.getIsSuccess());
                        //Log.d("LinedAdapter", "RealTime Info TravelSegment..RealTimeArrivalDelta...." + realTimeInfoTravelSegment.getRealTimeArrivalDelta());
                        if(realTimeInfoTravelSegment.getLegStatus() != null && !realTimeInfoTravelSegment.getLegStatus().isEmpty()){
                            tvRealTimeDepartureInfoLinked.setVisibility(View.VISIBLE);
                            tvRealTimeDepartureInfoLinked.setText(realTimeInfoTravelSegment.getLegStatus());
                            tvRealTimeDepartureInfoLinked.setTextColor(activity.getResources().getColor(R.color.textcolor_error));
                        }else{
                            if(realTimeInfoTravelSegment.getRealTimeDepartureDelta() != null){
                                tvRealTimeDepartureInfoLinked.setVisibility(View.VISIBLE);
                                tvRealTimeDepartureInfoLinked.setText(realTimeInfoTravelSegment.getRealTimeDepartureDelta());
                                tvRealTimeDepartureInfoLinked.setTextColor(activity.getResources().getColor(R.color.textcolor_error));
                            }else{
                                tvRealTimeArrivalInfoLinked.setVisibility(View.GONE);
                            }
                            if(realTimeInfoTravelSegment.getRealTimeArrivalDelta() != null){
                                tvRealTimeArrivalInfoLinked.setVisibility(View.VISIBLE);
                                tvRealTimeArrivalInfoLinked.setText(realTimeInfoTravelSegment.getRealTimeArrivalDelta());
                                tvRealTimeArrivalInfoLinked.setTextColor(activity.getResources().getColor(R.color.textcolor_error));
                            }else {
                                tvRealTimeArrivalInfoLinked.setVisibility(View.GONE);
                            }
                        }
                        if((realTimeInfoTravelSegment.getLegStatus() == null || realTimeInfoTravelSegment.getLegStatus().isEmpty())
                                && (realTimeInfoTravelSegment.getRealTimeDepartureDelta() == null || realTimeInfoTravelSegment.getRealTimeDepartureDelta().isEmpty())
                                && (realTimeInfoTravelSegment.getRealTimeArrivalDelta() == null || realTimeInfoTravelSegment.getRealTimeArrivalDelta().isEmpty())){
                            tvRealTimeDepartureInfoLinked.setVisibility(View.VISIBLE);
                            tvRealTimeDepartureInfoLinked.setText(activity.getResources().getString(R.string.general_ontime));
                            tvRealTimeDepartureInfoLinked.setTextColor(activity.getResources().getColor(R.color.tertiary_text_light));
                        }
                    }
                }
            }

        }else{
            tvRealTimeDepartureInfoLinked.setVisibility(View.GONE);
            tvRealTimeArrivalInfoLinked.setVisibility(View.GONE);
            llRealTimeLinked.setVisibility(View.GONE);
            tvRealTimeResultLinked.setVisibility(View.GONE);
        }
    }

    public void getChildTravelSegmentView(final DossierTravelSegment dossierTravelSegment, LinearLayout linearLayout,
                                          final Dossier dossier, final DossierSummary dossierSummary, boolean shouldRefresh,
                                          final RealTimeInfoRequestParameter realTimeInfoRequestParameter) {
        this.dossierTravelSegment = dossierTravelSegment;
        this.dossier = dossier;
        View convertView = null;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.li_dossier_detail_ticket_linked, null);
        }

        LinearLayout llTicketInfoLinked = (LinearLayout)convertView.findViewById(R.id.ll_dossier_detail_tickets_info_linked);
        TextView tvDossierErrorLinked = (TextView)convertView.findViewById(R.id.tv_dossier_ticket_linked_error);
        TextView tvDossierProvisionalLinked = (TextView)convertView.findViewById(R.id.tv_dossier_ticket_linked_provisional);

        TextView tvDepartureLabelLinked = (TextView)convertView.findViewById(R.id.tv_departure_lable_linked);
        tvDepartureLabelLinked.setText(activity.getResources().getString(R.string.general_departure) + ": ");
        TextView tvArrivalLabelLinked = (TextView)convertView.findViewById(R.id.tv_arrival_label_linked);
        tvArrivalLabelLinked.setText(activity.getResources().getString(R.string.general_arrival) + ": ");

        tvSegmentStationLinked = (TextView)convertView.findViewById(R.id.tv_segment_linked_station);
        tvDepartureDateLinked = (TextView)convertView.findViewById(R.id.tv_departure_date_linked);
        tvArrivalDateLinked = (TextView) convertView.findViewById(R.id.tv_arrival_date_linked);
        tvTrainInfoLinked = (TextView) convertView.findViewById(R.id.tv_train_info_linked);

        LinearLayout llABSLinked = (LinearLayout)convertView.findViewById(R.id.ll_abs_linked);
        LinearLayout llADSLinked = (LinearLayout)convertView.findViewById(R.id.ll_ads_linked);
        LinearLayout llHasOverbookingLinked = (LinearLayout)convertView.findViewById(R.id.ll_has_overbooking_linked);
        TextView tvAdvanceLinked = (TextView)convertView.findViewById(R.id.tv_advance_linked);
        Button btnViewTicketLinked = (Button) convertView.findViewById(R.id.btn_view_ticket_linked);
        LinearLayout llConnectionMonitorLinked = (LinearLayout)convertView.findViewById(R.id.ll_travelsegment_monitoring_active_linked);
        ImageView ivMonitorLinked = (ImageView)convertView.findViewById(R.id.iv_travelsegment_monitoring_linked);
        TextView tvMonitorLinked = (TextView)convertView.findViewById(R.id.tv_travelsegment_monitoring_linked);
        /*Button btnToScheduleLinked = (Button) convertView.findViewById(R.id.btn_travelsegment_to_schedule_linked);*/
        LinearLayout llName = (LinearLayout)convertView.findViewById(R.id.ll_names_linked);
        LinearLayout llSeatLocation = (LinearLayout)convertView.findViewById(R.id.ll_seatlocation_linked);
        if(dossierTravelSegment.getTickets() != null && dossierTravelSegment.getTickets().size() > 0){
            btnViewTicketLinked.setVisibility(View.VISIBLE);
        }else{
            btnViewTicketLinked.setVisibility(View.GONE);
        }
        btnViewTicketLinked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NMBSApplication.getInstance().getDossierDetailsService().setCurrentDossierTravelSegment(dossierTravelSegment);
                if(dossierTravelSegment.getTickets() != null && dossierTravelSegment.getTickets().size() > 0){
                    activity.startActivity(TicketsDetailActivity.createIntent(activity.getApplicationContext(), dossierTravelSegment,
                            dossier, dossierSummary, realTimeInfoRequestParameter));
                }
            }
        });
        /*btnToScheduleLinked.setOnClickListener(new View.OnClickListener() {
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
                    llConnectionMonitorLinked.setVisibility(View.GONE);
                }else{
                    llConnectionMonitorLinked.setVisibility(View.VISIBLE);
                    if(DossierDetailsService.Dossier_Active == status){
                        ivMonitorLinked.setImageResource(R.drawable.ic_monitor_active);
                        tvMonitorLinked.setText(activity.getResources().getString(R.string.dossier_detail_monitoring_active));
                        tvMonitorLinked.setTextColor(activity.getResources().getColor(R.color.tertiary_text_light));
                    }else if(DossierDetailsService.Dossier_NoActive == status){
                        ivMonitorLinked.setImageResource(R.drawable.ic_monitor_active_not);
                        tvMonitorLinked.setText(activity.getResources().getString(R.string.dossier_detail_monitoring_active_not));
                        tvMonitorLinked.setTextColor(activity.getResources().getColor(R.color.textcolor_thirdaction));
                        //btnToScheduleLinked.setVisibility(View.VISIBLE);

                    }else{
                        ivMonitorLinked.setImageResource(R.drawable.ic_monitor_active_failed);
                        tvMonitorLinked.setText(activity.getResources().getString(R.string.dossier_detail_monitoring_active_failed));
                        tvMonitorLinked.setTextColor(activity.getResources().getColor(R.color.textcolor_error));
                    }
                }
            }
            showRealTimeView(convertView, shouldRefresh);
            if(dossierTravelSegment.getTariffGroupText() != null && !dossierTravelSegment.getTariffGroupText().isEmpty()){
                tvAdvanceLinked.setVisibility(View.VISIBLE);
                tvAdvanceLinked.setText(dossierTravelSegment.getTariffGroupText());
            }else{
                tvAdvanceLinked.setVisibility(View.GONE);
            }
            if(DossierDetailsService.DossierStateError.equalsIgnoreCase(dossierTravelSegment.getSegmentState())){
                tvDossierErrorLinked.setVisibility(View.VISIBLE);
            }else{
                tvDossierErrorLinked.setVisibility(View.GONE);
            }
            if(DossierDetailsService.DossierStateInProgress.equalsIgnoreCase(dossierTravelSegment.getSegmentState())){
                tvDossierProvisionalLinked.setVisibility(View.VISIBLE);
            }else{
                tvDossierProvisionalLinked.setVisibility(View.GONE);
            }

            initSegment();
            initPassengerInfo(llName, llSeatLocation);
            initABS(llABSLinked);
            initADS(llADSLinked);
            initOverBooking(llHasOverbookingLinked);
            llConnectionMonitorLinked.setVisibility(View.GONE);
        }

        linearLayout.addView(convertView);
    }
    private void initSegment(){
        tvSegmentStationLinked.setText(dossierTravelSegment.getOriginStationName() + " - " + dossierTravelSegment.getDestinationStationName());
        String trainType = dossierTravelSegment.getTrainType();
        TrainIcon trainIcon = NMBSApplication.getInstance().getTrainIconsService().getTrainIcon(dossierTravelSegment.getTrainType());
        if(trainIcon != null){
            if(trainIcon.getBrandName() != null && !trainIcon.getBrandName().isEmpty()){
                trainType = trainIcon.getBrandName();
            }
        }
        tvTrainInfoLinked.setText(trainType + " " + dossierTravelSegment.getTrainNumber() + " - "
                + dossierDetailsService.comfortClass(dossierTravelSegment.getComfortClass()));
        if(DossierDetailsService.SegmentType_MARKETPRICE.equalsIgnoreCase(dossierTravelSegment.getSegmentType())){
            tvDepartureDateLinked.setText(DateUtils.dateTimeToString(dossierTravelSegment.getDepartureDateTime(), DateUtils.getRightFormat()));
            tvArrivalDateLinked.setText(DateUtils.dateTimeToString(dossierTravelSegment.getArrivalDateTime(), DateUtils.getRightFormat()));

        }
        // TravelSegment with SegmentType = Admission
        else if(DossierDetailsService.SegmentType_Admission.equalsIgnoreCase(dossierTravelSegment.getSegmentType())){
            //Log.d(TAG, "TravelSegment with SegmentType = Admission AND without child TravelSegments...");
            tvDepartureDateLinked.setText(DateUtils.dateTimeToString(dossierTravelSegment.getDepartureDateTime(), DateUtils.getRightFormat()));
        }
        else if(DossierDetailsService.SegmentType_Reservation.equalsIgnoreCase(dossierTravelSegment.getSegmentType())){
            tvDepartureDateLinked.setText(DateUtils.dateTimeToString(dossierTravelSegment.getDepartureDateTime(), DateUtils.getRightFormat()));
            tvArrivalDateLinked.setText(DateUtils.dateTimeToString(dossierTravelSegment.getArrivalDateTime(), DateUtils.getRightFormat()));
        }
    }

    private void initPassengerInfo(LinearLayout llName, LinearLayout llSeatLocation){
        List<Passenger> passengers = dossierTravelSegment.getSegmentPassengers();
        //String passengerText = "";
        String name = "";
        if(passengers != null && passengers.size() > 0){
            NameAdapter nameAdapter = new NameAdapter(activity);
            for (int i = 0; i < passengers.size(); i ++){
                Passenger passenger = passengers.get(i);
                if(passenger != null){
                    name = passenger.getFirstName() + " " + passenger.getLastName();
                    if(name == null || name.isEmpty()){
                        name = dossierDetailsService.getPassengerTypeText(activity.getApplicationContext(), passenger.getPassengerType()) ;
                    }
                    nameAdapter.getNameView(name, llName);
                    /*SeatLocation seatLocation = dossierTravelSegment.getSeatLocation(passenger.getSegmentPassengerId());
                    if(seatLocation != null){
                        passengerText += DossierDetailsService.getPassengeText(activity.getApplicationContext(), name, seatLocation.getCoachNumber(), seatLocation.getSeatNumberInfo());
                    }else{
                        passengerText += DossierDetailsService.getPassengeText(activity.getApplicationContext(), name, null, null);
                    }*/

                }
            }
        }
        SeatAdapter seatAdapter = new SeatAdapter(activity);
        for(SeatLocation seatLocation : dossierTravelSegment.getSeatLocations()){
            seatAdapter.getSeatLocationView(seatLocation, llSeatLocation);
        }
        //tvPassengerInfo.setText(name);
    }

    private void initABS(LinearLayout llABS){
        if(dossierTravelSegment.isHasABS()){
            llABS.setVisibility(View.GONE);
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
