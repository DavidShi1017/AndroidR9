package com.nmbs.adapter;

import android.app.Activity;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nmbs.R;
import com.nmbs.activities.AlertDeleteActivity;
import com.nmbs.activities.AlertDeleteDnrActivity;
import com.nmbs.application.NMBSApplication;
import com.nmbs.listeners.DeleteDossierListener;
import com.nmbs.model.Station;
import com.nmbs.model.Subscription;
import com.nmbs.services.impl.StationService;
import com.nmbs.util.DateUtils;
import com.nmbs.util.GoogleAnalyticsUtil;

import java.util.Date;
import java.util.List;

/**
 * Created by David on 2/26/16.
 */
public class AlertDeleteDnrAdapter {

    private LayoutInflater layoutInflater;
    private AlertDeleteDnrActivity activity;
    private MyTicketsDnrReferenceChildAdapter myTicketsDnrReferenceChildAdapter;
    private StationService stationService;
    public AlertDeleteDnrAdapter(AlertDeleteDnrActivity activity){
        this.activity = activity;
        this.myTicketsDnrReferenceChildAdapter = new MyTicketsDnrReferenceChildAdapter(activity);
        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        stationService = NMBSApplication.getInstance().getStationService();
    }

    public void getAlertDeleteView(LinearLayout linearLayout, final Subscription subscription) {
        View convertView = null;
        if (convertView == null)
            convertView = layoutInflater.inflate(R.layout.li_alert_delete_dnr, null);
        TextView tvName = (TextView) convertView.findViewById(R.id.tv_alert_subscription_station_name);
        TextView tvDnrTime = (TextView) convertView.findViewById(R.id.tv_alert_subscription_station_time);
        if(subscription != null){
            final String dnr = subscription.getDnr();
            //Log.e("subscriptionList", "subscriptionList...." + subscription.getOriginStationName());
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
            tvName.setText(stationOriginName + " - " + stationDestinationName);
            Date tempDate = DateUtils.stringToDateTime(subscription.getDeparture());
            tvDnrTime.setText(DateUtils.dateTimeToString(tempDate, "EEEE dd MMMM yyyy")+" - "+DateUtils.FormatToHHMMFromDate(tempDate));
        }

        linearLayout.addView(convertView);
    }
}
