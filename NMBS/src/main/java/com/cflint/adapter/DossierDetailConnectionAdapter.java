package com.cflint.adapter;

import android.app.Activity;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cflint.R;
import com.cflint.activities.DossierConnectionDetailActivity;
import com.cflint.activities.ScheduleSearchActivity;
import com.cflint.application.NMBSApplication;
import com.cflint.async.RealTimeInfoAsyncTask;
import com.cflint.model.Connection;
import com.cflint.model.Dossier;
import com.cflint.model.RealTimeConnection;
import com.cflint.model.RealTimeInfoResponse;
import com.cflint.services.impl.DossierDetailsService;
import com.cflint.util.DateUtils;
import com.cflint.util.FunctionConfig;

/**
 * Created by David on 2/26/16.
 */
public class DossierDetailConnectionAdapter {

    private LayoutInflater layoutInflater;
    private Activity activity;
    private DossierDetailsService dossierDetailsService = NMBSApplication.getInstance().getDossierDetailsService();
    public DossierDetailConnectionAdapter(Activity activity){
        this.activity = activity;
        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    private void showRealTimeView(View convertView, Connection connection, boolean shouldRefresh){
        LinearLayout llConnectionsRealTime = (LinearLayout)convertView.findViewById(R.id.ll_dossier_detail_connections_realtime);
        ProgressBar pbConnectionsRealTime = (ProgressBar)convertView.findViewById(R.id.pb_dossier_detail_connections_realtime);
        TextView tvConnectionsRealTimeResult = (TextView)convertView.findViewById(R.id.tv_dossier_detail_connections_realtime_result);
        TextView tvDepartureRealTime = (TextView)convertView.findViewById(R.id.tv_departure_realtime);
        TextView tvArrivalRealTime = (TextView)convertView.findViewById(R.id.tv_arrival_realtime);

        RealTimeInfoResponse realTimeInfoResponse = dossierDetailsService.readRealTimeInfoById(connection.getConnectionId(), activity.getApplicationContext());
        RealTimeConnection realTimeConnection = (RealTimeConnection) dossierDetailsService.getRealTime(realTimeInfoResponse);
        //Log.d("RealTime", "showRealTimeView.....");
        //Log.d("RealTime", "showRealTimeView.....");
        if (shouldRefresh) {
            if(RealTimeInfoAsyncTask.isRefreshing){
                llConnectionsRealTime.setVisibility(View.VISIBLE);
                pbConnectionsRealTime.setVisibility(View.VISIBLE);

                tvConnectionsRealTimeResult.setText(activity.getResources().getString(R.string.general_refreshing_realtime));
                tvConnectionsRealTimeResult.setTextColor(activity.getResources().getColor(R.color.textcolor_thirdaction));
            }else{
                pbConnectionsRealTime.setVisibility(View.GONE);
                if (realTimeInfoResponse != null) {
                    if (realTimeInfoResponse.getIsSuccess()) {
                        llConnectionsRealTime.setVisibility(View.GONE);
                        if(realTimeConnection != null){
                            //Log.d("RealTime", "RealTime Info Connection IsSuccess??..." + realTimeInfoResponse.getIsSuccess());
                            //Log.d("RealTime", "RealTime Info Connection..RealTimeDepartureDelta...." + realTimeConnection.getRealTimeDepartureDelta());
                            //Log.d("RealTime", "RealTime Info Connection..RealTimeArrivalDelta...." + realTimeConnection.getRealTimeArrivalDelta());
                            if(realTimeConnection.getRealTimeDepartureDelta() != null && !realTimeConnection.getRealTimeDepartureDelta().isEmpty()){
                                //Log.d("RealTime", "RealTime Info Connection..RealTimeDepartureDelta....not null....");
                                tvDepartureRealTime.setVisibility(View.VISIBLE);
                                //Log.d("RealTime", "RealTime Info Connection..RealTimeDepartureDelta....not null...." + realTimeConnection.getRealTimeDepartureDelta());
                                tvDepartureRealTime.setText(realTimeConnection.getRealTimeDepartureDelta());
                                tvDepartureRealTime.setTextColor(activity.getResources().getColor(R.color.textcolor_error));
                                //tvConnectionsRealTimeResult.setVisibility(View.VISIBLE);
                                //tvConnectionsRealTimeResult.setTextColor(activity.getResources().getColor(R.color.textcolor_error));
                            }
                            if(realTimeConnection.getRealTimeArrivalDelta() != null && !realTimeConnection.getRealTimeArrivalDelta().isEmpty()){
                                tvArrivalRealTime.setVisibility(View.VISIBLE);
                                tvArrivalRealTime.setText(realTimeConnection.getRealTimeArrivalDelta());
                                tvArrivalRealTime.setTextColor(activity.getResources().getColor(R.color.textcolor_error));
                                //tvConnectionsRealTimeResult.setVisibility(View.VISIBLE);
                            }

                            if((realTimeConnection.getRealTimeDepartureDelta() == null || realTimeConnection.getRealTimeDepartureDelta().isEmpty())
                                    && (realTimeConnection.getRealTimeArrivalDelta() == null || realTimeConnection.getRealTimeArrivalDelta().isEmpty())){
                                tvDepartureRealTime.setVisibility(View.VISIBLE);
                                tvDepartureRealTime.setText(activity.getResources().getString(R.string.general_ontime));
                                tvDepartureRealTime.setTextColor(activity.getResources().getColor(R.color.tertiary_text_light));
                                //tvConnectionsRealTimeResult.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        llConnectionsRealTime.setVisibility(View.VISIBLE);
                        tvConnectionsRealTimeResult.setText(activity.getResources().getString(R.string.general_refreshing_realtime_failed));
                        tvConnectionsRealTimeResult.setTextColor(activity.getResources().getColor(R.color.textcolor_error));
                    }
                }/*else {
                    llConnectionsRealTime.setVisibility(View.VISIBLE);
                    tvConnectionsRealTimeResult.setText(activity.getResources().getString(R.string.general_refreshing_realtime_failed));
                    tvConnectionsRealTimeResult.setTextColor(activity.getResources().getColor(R.color.textcolor_error));
                }*/
            }
        } else {
            llConnectionsRealTime.setVisibility(View.GONE);
            pbConnectionsRealTime.setVisibility(View.GONE);
            tvConnectionsRealTimeResult.setVisibility(View.GONE);
        }
    }
    public void getConnectionsView(final Connection connection, final Dossier dossier, LinearLayout linearLayout,
                                   final boolean shouldRefresh, final RealTimeConnection realTimeConnection) {
        //Log.e("realTimeConnection", "realTimeConnection.." + realTimeConnection);
        View convertView = null;
        if(convertView == null)
            convertView = layoutInflater.inflate(R.layout.li_dossier_detail_connections, null);
        TextView tvConnectionsStation = (TextView)convertView.findViewById(R.id.tv_connections_station);
        TextView tvConnectionsDate = (TextView)convertView.findViewById(R.id.tv_connections_date);
        TextView tvDepartureTime = (TextView)convertView.findViewById(R.id.tv_departure_time);
        TextView tvArrivalTime = (TextView)convertView.findViewById(R.id.tv_arrival_time);
        TextView tvDuration = (TextView)convertView.findViewById(R.id.tv_li_dossier_detail_connections_duration);
        TextView tvTransfer = (TextView)convertView.findViewById(R.id.tv_dossier_detail_connections_transfer);

        LinearLayout llTransferNotPossible = (LinearLayout)convertView.findViewById(R.id.ll_dossier_detail_connections_transfernotpossible);
        TextView tvTransferNotPossible = (TextView)convertView.findViewById(R.id.tv_dossier_detail_connections_transfernotpossible);


        LinearLayout llBuyticket = (LinearLayout)convertView.findViewById(R.id.ll_dossier_detail_connections_buyticket);
        TextView tvBuyticket = (TextView)convertView.findViewById(R.id.tv_dossier_detail_connections_buyticket);
        ImageView ivGlass = (ImageView)  convertView.findViewById(R.id.iv_dossier_detail_connections_magnifying_glass);

        LinearLayout llNoseperate = (LinearLayout)convertView.findViewById(R.id.ll_dossier_detail_connections_noseperate);
        TextView tvNoseperate = (TextView)convertView.findViewById(R.id.tv_dossier_detail_connections_noseperate);

        LinearLayout llConnectionMonitor = (LinearLayout)convertView.findViewById(R.id.ll_dossier_detail_connections_monitor);
        ImageView ivMonitor = (ImageView)convertView.findViewById(R.id.iv_monitor);
        TextView tvMonitor = (TextView)convertView.findViewById(R.id.tv_dossier_detail_connections_monitor);
        Button btnToSchedule = (Button) convertView.findViewById(R.id.btn_dossier_detail_connections_to_schedule);

        if(realTimeConnection != null){
            //Log.e("realTimeConnection", "realTimeConnection.." + realTimeConnection.isTransferNotPossible());
            //Log.e("realTimeConnection", "realTimeConnection.." + realTimeConnection.isConnectionNotPossible());

            if(realTimeConnection.isTransferNotPossible() && realTimeConnection.isConnectionNotPossible()){
                llTransferNotPossible.setVisibility(View.VISIBLE);
                tvTransferNotPossible.setText(activity.getResources().getString(R.string.schedule_connection_not_possible));

            }else{
                if(realTimeConnection.isTransferNotPossible()){
                    llTransferNotPossible.setVisibility(View.VISIBLE);
                    tvTransferNotPossible.setText(activity.getResources().getString(R.string.dossier_detail_transfer_not_possible));
                }else if(realTimeConnection.isConnectionNotPossible()){
                    llTransferNotPossible.setVisibility(View.VISIBLE);
                    tvTransferNotPossible.setText(activity.getResources().getString(R.string.schedule_connection_not_possible));
                }else{
                    llTransferNotPossible.setVisibility(View.GONE);
                }

                if(realTimeConnection.isTransferNotPossible() || realTimeConnection.isConnectionNotPossible()
                        || (realTimeConnection.getHafasMessages() != null && realTimeConnection.getHafasMessages().size() > 0)
                        || realTimeConnection.hasLegStatus()
                        || (realTimeConnection.getRealTimeDepartureDelta() != null && !realTimeConnection.getRealTimeDepartureDelta().isEmpty())
                        || (realTimeConnection.getRealTimeArrivalDelta() != null && !realTimeConnection.getRealTimeArrivalDelta().isEmpty())){
                    ivGlass.setImageResource(R.drawable.ic_magnifying_red);
                }else{
                    ivGlass.setImageResource(R.drawable.ic_magnifying);
                }

            }
        }
        btnToSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(ScheduleSearchActivity.createIntent(activity));
            }
        });
        if(connection != null){
            int status = dossierDetailsService.checkSubscriptionStatusForConnection(connection);
            //Log.d("Connection", "status..." + status);
            //Log.d("Connection", "status..isPushEnabled..." + dossierDetailsService.isPushEnabled(dossier));
            if(dossierDetailsService.isPushEnabled(dossier)  && FunctionConfig.kFunManagePush){
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
                        btnToSchedule.setVisibility(View.VISIBLE);

                    }else{
                        ivMonitor.setImageResource(R.drawable.ic_monitor_active_failed);
                        tvMonitor.setText(activity.getResources().getString(R.string.dossier_detail_monitoring_active_failed));
                        tvMonitor.setTextColor(activity.getResources().getColor(R.color.textcolor_error));
                    }
                }
            }else {
                llConnectionMonitor.setVisibility(View.GONE);
            }

            showRealTimeView(convertView, connection, shouldRefresh);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.startActivity(DossierConnectionDetailActivity.createIntent(activity.getApplicationContext(), connection, dossier, shouldRefresh, realTimeConnection));
                }
            });
            tvConnectionsStation.setText(connection.getOriginStationName() + " - " + connection.getDestinationStationName());
            tvConnectionsDate.setText(DateUtils.dateTimeToString(connection.getDeparture(), "EEEE dd MMMM yyyy"));
            tvDepartureTime.setText(DateUtils.getTime(activity.getApplicationContext(), connection.getDeparture()));
            tvArrivalTime.setText(DateUtils.getTime(activity.getApplicationContext(), connection.getArrival()));
            tvDuration.setText(DateUtils.FormatToHrDate(connection.getDuration(), activity));

            tvTransfer.setText(connection.getNumberOfTransfers(activity.getApplicationContext()));
            if(connection.hasWarning()){
                llBuyticket.setVisibility(View.VISIBLE);
                tvBuyticket.setText(connection.getWarningLegsText());
            }else{
                llBuyticket.setVisibility(View.GONE);
            }
            if(dossier != null && connection.hasLegInSameZone(dossier.getTravelSegments(), connection)){
                llNoseperate.setVisibility(View.VISIBLE);
                tvNoseperate.setText(connection.getLegInSameZoneText(dossier.getTravelSegments()));
            }else {
                llNoseperate.setVisibility(View.GONE);
            }
            //llConnectionMonitor.setVisibility(View.GONE);
        }
        linearLayout.addView(convertView);
    }
}
