package com.nmbs.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nmbs.R;
import com.nmbs.activities.DossierDetailActivity;
import com.nmbs.model.Connection;
import com.nmbs.model.Dossier;
import com.nmbs.model.DossierSummary;
import com.nmbs.model.DossierTravelSegment;
import com.nmbs.model.Tariff;
import com.nmbs.model.TariffCondition;
import com.nmbs.services.impl.DossierDetailsService;
import com.nmbs.util.ComparatorConnectionDate;
import com.nmbs.util.ComparatorTravelSegmentDate;

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
            tvConditions.setText(Html.fromHtml("<b>" + condition.getKey() + "</b>: " + condition.getValue()));
        }
        linearLayout.addView(convertView);
    }
}
