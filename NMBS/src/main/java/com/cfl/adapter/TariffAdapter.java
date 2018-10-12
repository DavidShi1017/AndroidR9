package com.cfl.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cfl.R;
import com.cfl.activities.DossierDetailActivity;
import com.cfl.model.Connection;
import com.cfl.model.Dossier;
import com.cfl.model.DossierSummary;
import com.cfl.model.DossierTravelSegment;
import com.cfl.model.Tariff;
import com.cfl.model.TariffCondition;
import com.cfl.services.impl.DossierDetailsService;
import com.cfl.util.ComparatorConnectionDate;
import com.cfl.util.ComparatorTravelSegmentDate;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Richard on 2/26/16.
 */
public class TariffAdapter {

    private LayoutInflater layoutInflater;
    private Activity activity;
    public TariffAdapter(Activity activity){
        this.activity = activity;
        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void getTariffView(LinearLayout linearLayout, final TariffCondition condition) {
        View convertView = null;
        if(convertView == null)
            convertView = layoutInflater.inflate(R.layout.li_tariff, null);
        TextView tvConditions = (TextView)convertView.findViewById(R.id.tv_conditions);

        if(condition != null){
            if(condition.getKey() != null){
                if(!condition.getKey().equalsIgnoreCase("Ticket info")){
                    tvConditions.setText(Html.fromHtml("<b>" + condition.getKey() + "</b>: " + condition.getValue()));
                }
            }
        }
        linearLayout.addView(convertView);
    }
}
