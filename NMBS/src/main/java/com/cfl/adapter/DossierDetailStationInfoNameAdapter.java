package com.cfl.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cfl.R;
import com.cfl.activities.StationInfoDetailActivity;
import com.cfl.model.StationInfo;

/**
 * Created by David on 2/26/16.
 */
public class DossierDetailStationInfoNameAdapter {

    private LayoutInflater layoutInflater;
    private Activity activity;

    public DossierDetailStationInfoNameAdapter(Activity activity){
        this.activity = activity;
        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void getStationInfoView(final StationInfo stationInfo, LinearLayout linearLayout) {
        View convertView = null;
        if(convertView == null)
            convertView = layoutInflater.inflate(R.layout.li_dossier_detail_stationinfo_station_name, null);
        TextView tvStationName = (TextView)convertView.findViewById(R.id.tv_segment_station_name);

        if(stationInfo != null) {
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.startActivity(StationInfoDetailActivity.createIntent(activity, stationInfo));
                }
            });
            tvStationName.setText(stationInfo.getName().toUpperCase());

        }
        linearLayout.addView(convertView);
    }
}
