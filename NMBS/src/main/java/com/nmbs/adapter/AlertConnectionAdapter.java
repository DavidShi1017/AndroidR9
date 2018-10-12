package com.nmbs.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nmbs.R;
import com.nmbs.activities.AlertActivity;
import com.nmbs.activities.DossierDetailActivity;
import com.nmbs.application.NMBSApplication;
import com.nmbs.model.Dossier;
import com.nmbs.model.DossierDetailsResponse;
import com.nmbs.model.DossierSummary;
import com.nmbs.model.Station;
import com.nmbs.model.Subscription;
import com.nmbs.services.impl.DossierDetailsService;
import com.nmbs.services.impl.StationService;
import com.nmbs.services.impl.TestService;
import com.nmbs.util.DateUtils;
import com.nmbs.util.GoogleAnalyticsUtil;
import com.nmbs.util.TrackerConstant;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by David on 3/20/16.
 */
public class AlertConnectionAdapter {

    private AlertActivity alertActivity;
    private LayoutInflater layoutInflater;
    private List<Subscription> notDnrSubscriptionList;
    private Map<String,List<Subscription>> dnrSubscriptionList;
    private boolean isShowDeleteBtn;
    private StationService stationService;

    public AlertConnectionAdapter(AlertActivity context,
                                            List<Subscription> notDnrList,Map<String,List<Subscription>> dnrList) {
        this.alertActivity = context;
        this.notDnrSubscriptionList = notDnrList;
        this.dnrSubscriptionList = dnrList;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        stationService = NMBSApplication.getInstance().getStationService();
    }


    public void getAlertDnrReferenceView(final int position, LinearLayout linearLayout) {
        View convertView = null;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.li_alert_connection_dnr_group_view, null);
        }
        if(dnrSubscriptionList != null && dnrSubscriptionList.size() > 0){
            final List<Subscription> subscriptions = dnrSubscriptionList.get(dnrSubscriptionList.keySet().toArray()[position]);
            final String dnr = dnrSubscriptionList.keySet().toArray()[position].toString();
            RelativeLayout rlDnr = (RelativeLayout) convertView.findViewById(R.id.rl_dnr);
            TextView dnrTextView = (TextView) convertView.findViewById(R.id.tv_alert_connection_dnr_number);
            rlDnr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DossierDetailsService dossierDetailsService = new DossierDetailsService(alertActivity.getApplicationContext());
                    DossierSummary dossierSummary = dossierDetailsService.getDossier(dnr);
                    if(dossierSummary != null) {
                        DossierDetailsResponse dossierResponse = dossierDetailsService.getDossierDetail(dossierSummary);
                        if (dossierResponse != null) {
                            Dossier dossier = dossierResponse.getDossier();
                            dossierDetailsService.setCurrentDossier(null);
                            dossierDetailsService.setCurrentDossierSummary(null);
                            alertActivity.startActivity(DossierDetailActivity.createIntent(alertActivity.getApplicationContext(),dossier,dossierSummary));
                        }
                    }
                }
            });
            dnrTextView.setText(dnr);


            LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.ll_alert_dnr_reference_view);

            if(subscriptions != null && subscriptions.size() > 0){
                for(int i=0;i<subscriptions.size();i++){
                    getAlertChildConnectionView(position,i,layout);
                }
            }

            linearLayout.addView(convertView);
        }
    }

    public void getAlertChildConnectionView(int group,int position, LinearLayout linearLayout) {
        View convertView = null;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.li_alert_connection_view, null);
        }

        final Subscription subscription = dnrSubscriptionList.get(dnrSubscriptionList.keySet().toArray()[group]).get(position);

        TextView stationName = (TextView)convertView.findViewById(R.id.tv_alert_subscription_station_name);
        TextView stationTime = (TextView)convertView.findViewById(R.id.tv_alert_subscription_station_time);
        TextView tvId = (TextView)convertView.findViewById(R.id.tv_alert_subscription_id);
        TextView tvReconctx = (TextView)convertView.findViewById(R.id.tv_alert_subscription_reconctx);

        if(subscription != null){
            Station stationOrigin = stationService.getStation(subscription.getOriginStationRcode(), NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey());
            Station stationDestination = stationService.getStation(subscription.getDestinationStationRcode(), NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey());
            String stationOriginName = "";
            String stationDestinationName = "";
            if(stationOrigin != null){
                stationOriginName = stationOrigin.getName();
            }else{
                if(notDnrSubscriptionList != null && notDnrSubscriptionList.size() > 0){
                    stationOrigin = stationService.getStationExtra(this.notDnrSubscriptionList.get(position).getOriginStationRcode(), NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey());
                    if(stationOrigin != null){
                        stationOriginName = stationOrigin.getName();
                    }
                }
            }
            if(stationDestination != null){
                stationDestinationName = stationDestination.getName();
            }else{
                if(notDnrSubscriptionList != null && notDnrSubscriptionList.size() > 0){
                    stationDestination = stationService.getStationExtra(this.notDnrSubscriptionList.get(position).getDestinationStationRcode(), NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey());
                    if(stationDestination != null){
                        stationDestinationName = stationDestination.getName();
                    }
                }
            }
            stationName.setText(stationOriginName + " - " + stationDestinationName);
            Date tempDate = DateUtils.stringToDateTime(subscription.getDeparture());
            stationTime.setText(DateUtils.dateTimeToString(tempDate, "EEEE dd MMMM yyyy")+" - "+DateUtils.FormatToHHMMFromDate(tempDate));
        }


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleAnalyticsUtil.getInstance().sendScreen(alertActivity, TrackerConstant.ALERT_DetailDNR);
                if(subscription != null){
                    alertActivity.goToScheduleDetail(subscription.getSubscriptionId());
                }
            }
        });


        linearLayout.addView(convertView);
    }

    public void getAlertConnectionView(final int position, LinearLayout linearLayout) {
        View convertView = null;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.li_alert_connection_view, null);
        }

        TextView stationName = (TextView)convertView.findViewById(R.id.tv_alert_subscription_station_name);
        TextView stationTime = (TextView)convertView.findViewById(R.id.tv_alert_subscription_station_time);
        TextView tvId = (TextView)convertView.findViewById(R.id.tv_alert_subscription_id);
        TextView tvReconctx = (TextView)convertView.findViewById(R.id.tv_alert_subscription_reconctx);

        if(TestService.isTestMode){
            tvId.setVisibility(View.GONE);
            tvReconctx.setVisibility(View.GONE);
            tvId.setText("Subscription id: " + this.notDnrSubscriptionList.get(position).getSubscriptionId());
            tvReconctx.setText("ReconCtx: " + this.notDnrSubscriptionList.get(position).getReconCtx());
        }
        Station stationOrigin = stationService.getStation(this.notDnrSubscriptionList.get(position).getOriginStationRcode(), NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey());
        Station stationDestination = stationService.getStation(this.notDnrSubscriptionList.get(position).getDestinationStationRcode(), NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey());
        String stationOriginName = "";
        String stationDestinationName = "";
        if(stationOrigin != null){
            stationOriginName = stationOrigin.getName();
        }else{
            stationOrigin = stationService.getStationExtra(this.notDnrSubscriptionList.get(position).getOriginStationRcode(), NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey());
            if(stationOrigin != null){
                stationOriginName = stationOrigin.getName();
            }
        }
        if(stationDestination != null){
            stationDestinationName = stationDestination.getName();
        }else{
            stationDestination = stationService.getStationExtra(this.
                    notDnrSubscriptionList.get(position).getDestinationStationRcode(), NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey());
            if(stationDestination != null){
                stationDestinationName = stationDestination.getName();
            }
        }
        stationName.setText(stationOriginName + " - " + stationDestinationName);
        //stationName.setText(this.notDnrSubscriptionList.get(position).getOriginStationName() + " - " + this.notDnrSubscriptionList.get(position).getDestinationStationName());
        Date tempDate = DateUtils.stringToDateTime(this.notDnrSubscriptionList.get(position).getDeparture());
        stationTime.setText(DateUtils.dateTimeToString(tempDate, "EEEE dd MMMM yyyy")+" - "+DateUtils.FormatToHHMMFromDate(tempDate));


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleAnalyticsUtil.getInstance().sendScreen(alertActivity, TrackerConstant.ALERT_Detail);
                alertActivity.goToScheduleDetail(notDnrSubscriptionList.get(position).getSubscriptionId());
            }
        });

        linearLayout.addView(convertView);
    }
}
