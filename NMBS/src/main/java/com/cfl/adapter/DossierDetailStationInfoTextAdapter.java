package com.cfl.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cfl.R;
import com.cfl.model.DossierTravelSegment;
import com.cfl.model.StationInfo;
import com.cfl.model.StationIsVirtualText;

/**
 * Created by David on 2/26/16.
 */
public class DossierDetailStationInfoTextAdapter {

    private LayoutInflater layoutInflater;
    private Activity activity;

    public DossierDetailStationInfoTextAdapter(Activity activity){
        this.activity = activity;
        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void getStationInfoView(final StationIsVirtualText stationIsVirtualText, LinearLayout linearLayout) {
        View convertView = null;
        if(convertView == null)
            convertView = layoutInflater.inflate(R.layout.li_dossier_detail_stationinfo_station_text, null);
        TextView tvStationName = (TextView)convertView.findViewById(R.id.tv_segment_station_name);
        TextView tvStationText = (TextView)convertView.findViewById(R.id.tv_segment_station_text);
        if(stationIsVirtualText != null) {
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //activity.startActivity(DossierConnectionDetailActivity.createIntent(activity.getApplicationContext(), connection, dossier));
                }
            });

            if(stationIsVirtualText.getStationText() != null && !stationIsVirtualText.getStationText().isEmpty()){
                tvStationName.setText(stationIsVirtualText.getStationName());
                tvStationText.setVisibility(View.VISIBLE);
                tvStationText.setText( " - " + stationIsVirtualText.getStationText());
            }else{
                tvStationText.setVisibility(View.GONE);
            }
        }else{
            tvStationText.setVisibility(View.GONE);
        }
        linearLayout.addView(convertView);
    }
}
