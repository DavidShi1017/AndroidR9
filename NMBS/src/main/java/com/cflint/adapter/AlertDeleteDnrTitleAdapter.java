package com.cflint.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cflint.R;
import com.cflint.activities.AlertDeleteActivity;
import com.cflint.activities.AlertDeleteDnrActivity;
import com.cflint.application.NMBSApplication;
import com.cflint.listeners.DeleteDossierListener;
import com.cflint.model.Subscription;
import com.cflint.util.GoogleAnalyticsUtil;

import java.util.List;

/**
 * Created by David on 2/26/16.
 */
public class AlertDeleteDnrTitleAdapter {

    private LayoutInflater layoutInflater;
    private AlertDeleteDnrActivity activity;
    private AlertDeleteDnrAdapter alertDeleteDnrAdapter;
    private DeleteDossierListener deleteDossierListener;
    private List<String> dnrs;
    public AlertDeleteDnrTitleAdapter(AlertDeleteDnrActivity activity, List<String> dnrs){
        this.activity = activity;
        this.alertDeleteDnrAdapter = new AlertDeleteDnrAdapter(activity);
        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.dnrs = dnrs;
    }

    public void setDnr(List<String> dnrs){
        this.dnrs = dnrs;
    }

    public void getAlertDeleteTitleView(int i, LinearLayout linearLayout) {
        View convertView = null;
        if (convertView == null)
            convertView = layoutInflater.inflate(R.layout.li_alert_delete_dnr_title, null);
        TextView tvDnr = (TextView) convertView.findViewById(R.id.tv_dnr);
        TextView tvDnrLabel = (TextView) convertView.findViewById(R.id.tv_dnr_label);
        tvDnrLabel.setText(activity.getResources().getString(R.string.general_booking_reference) + ": ");
        tvDnr.setText(dnrs.get(i));
        final List<Subscription> subscriptions = NMBSApplication.getInstance().getPushService().readAllSubscriptionsByDnr(dnrs.get(i));


        LinearLayout deleteBtn = (LinearLayout) convertView.findViewById(R.id.ll_delete_button);
        deleteBtn.setVisibility(View.VISIBLE);
        if(subscriptions != null && subscriptions.size() > 0){
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GoogleAnalyticsUtil.getInstance().sendScreen(activity, "Alert_Delete");
                    activity.deleteAllSubscription(subscriptions);
                }
            });
            LinearLayout llSubs = (LinearLayout)convertView.findViewById(R.id.ll_subs);

            for(Subscription subscription : subscriptions){
                alertDeleteDnrAdapter.getAlertDeleteView(llSubs, subscription);
            }
        }

        linearLayout.addView(convertView);
    }
}
