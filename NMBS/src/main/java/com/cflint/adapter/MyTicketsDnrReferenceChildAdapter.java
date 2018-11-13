package com.cflint.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cflint.R;
import com.cflint.activities.DossierDetailActivity;
import com.cflint.application.NMBSApplication;
import com.cflint.model.Connection;
import com.cflint.model.Dossier;
import com.cflint.model.DossierSummary;
import com.cflint.model.DossierTravelSegment;
import com.cflint.model.MyTicket;
import com.cflint.services.impl.TestService;
import com.cflint.util.DateUtils;

/**
 * Created by David on 2/26/16.
 */
public class MyTicketsDnrReferenceChildAdapter {

    private LayoutInflater layoutInflater;
    private Activity activity;

    public MyTicketsDnrReferenceChildAdapter(Activity activity){
        this.activity = activity;
        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void getMyTicketsDnrReferenceChildView(MyTicket myTicket, LinearLayout linearLayout,
                                                  final Dossier dossier, final DossierSummary dossierSummary) {
        View convertView = null;
        if(convertView == null)
            convertView = layoutInflater.inflate(R.layout.li_my_tickets_dnr_reference_child, null);
            convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NMBSApplication.getInstance().getDossierDetailsService().setCurrentDossier(null);
                NMBSApplication.getInstance().getDossierDetailsService().setCurrentDossierSummary(null);
                activity.startActivity(DossierDetailActivity.createIntent(activity.getApplicationContext(), dossier, dossierSummary));
            }
        });
        TextView tvStationName = (TextView) convertView.findViewById(R.id.tv_station_name);
        TextView tvDate = (TextView) convertView.findViewById(R.id.tv_date);


        if(myTicket != null){
            tvStationName.setText(myTicket.getOriginStationName() + " - " + myTicket.getDestinationStationName());

            tvDate.setText(DateUtils.dateTimeToString(myTicket.getDeparture(), "EEEE dd MMMM yyyy"));
        }

        linearLayout.addView(convertView);
    }
}
