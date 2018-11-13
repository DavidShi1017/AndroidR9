package com.cflint.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cflint.R;
import com.cflint.activities.DossierDetailActivity;
import com.cflint.model.Connection;
import com.cflint.model.Dossier;
import com.cflint.model.DossierSummary;
import com.cflint.model.DossierTravelSegment;
import com.cflint.model.Tariff;
import com.cflint.model.TariffCondition;
import com.cflint.services.impl.DossierDetailsService;
import com.cflint.util.ComparatorConnectionDate;
import com.cflint.util.ComparatorTravelSegmentDate;

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
