package com.cfl.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cfl.R;
import com.cfl.activities.AlertActivity;
import com.cfl.activities.AlertDeleteActivity;
import com.cfl.application.NMBSApplication;
import com.cfl.dataaccess.restservice.impl.DossierDetailDataService;
import com.cfl.model.DossierSummary;
import com.cfl.model.Station;
import com.cfl.model.Subscription;
import com.cfl.services.impl.DossierDetailsService;
import com.cfl.services.impl.StationService;
import com.cfl.services.impl.TestService;
import com.cfl.util.DateUtils;
import com.cfl.util.GoogleAnalyticsUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by David on 3/20/16.
 */
public class AlertDeleteConnectionAdapter {

    private AlertDeleteActivity alertActivity;
    private LayoutInflater layoutInflater;
    private List<Subscription> subscriptionList;
    private StationService stationService;
    public AlertDeleteConnectionAdapter(AlertDeleteActivity context, List<Subscription> notDnrList) {
        this.alertActivity = context;
        this.subscriptionList = notDnrList;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        stationService = NMBSApplication.getInstance().getStationService();
    }

    public void getAlertConnectionView(final int position, LinearLayout linearLayout) {
        View convertView = null;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.li_alert_delete, null);
        }

        TextView stationName = (TextView)convertView.findViewById(R.id.tv_alert_subscription_station_name);
        TextView stationTime = (TextView)convertView.findViewById(R.id.tv_alert_subscription_station_time);
        if(subscriptionList != null && subscriptionList.size() > 0){
            final Subscription subscription = subscriptionList.get(position);
            if(subscription != null){
                final String dnr = subscription.getDnr();
                //Log.e("subscriptionList", "subscriptionList...." + this.subscriptionList.get(position).getOriginStationName());
                //stationName.setText(alertActivity.getResources().getString(R.string.DBooking_NoSeatsAvailable));
                Station stationOrigin = stationService.getStation(subscription.getOriginStationRcode(), NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey());
                Station stationDestination = stationService.getStation(subscription.getDestinationStationRcode(), NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey());
                String stationOriginName = "";
                String stationDestinationName = "";
                if(stationOrigin != null){
                    stationOriginName = stationOrigin.getName();
                }else{
                    stationOrigin = stationService.getStationExtra(subscription.getOriginStationRcode(), NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey());
                    if(stationOrigin != null){
                        stationOriginName = stationOrigin.getName();
                    }
                }
                if(stationDestination != null){
                    stationDestinationName = stationDestination.getName();
                }else{
                    stationDestination = stationService.getStationExtra(subscription.getDestinationStationRcode(), NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey());
                    if(stationDestination != null){
                        stationDestinationName = stationDestination.getName();
                    }
                }
                stationName.setText(stationOriginName + " - " + stationDestinationName);
                Date tempDate = DateUtils.stringToDateTime(subscription.getDeparture());
                stationTime.setText(DateUtils.dateTimeToString(tempDate, "EEEE dd MMMM yyyy")+" - "+DateUtils.FormatToHHMMFromDate(tempDate));

                LinearLayout deleteBtn = (LinearLayout) convertView.findViewById(R.id.ll_alert_connection_delete_button);
                deleteBtn.setVisibility(View.VISIBLE);
                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GoogleAnalyticsUtil.getInstance().sendScreen(alertActivity, "Alert_Delete");
                        alertActivity.deleteSubscription(subscription, dnr);
                    }
                });
            }
        }

        linearLayout.addView(convertView);
    }
}
