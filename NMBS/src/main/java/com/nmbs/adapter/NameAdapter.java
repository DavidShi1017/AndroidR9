package com.nmbs.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nmbs.R;
import com.nmbs.model.SeatLocation;

/**
 * Created by David on 3/9/16.
 */
public class NameAdapter {
    private LayoutInflater layoutInflater;

    private Activity activity;
    public NameAdapter(Activity activity){
        this.activity = activity;
        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void getNameView(final String name, LinearLayout linearLayout) {
        View convertView = null;
        if(convertView == null)
            convertView = layoutInflater.inflate(R.layout.li_name, null);

        TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);

        tvName.setText(name);

        linearLayout.addView(convertView);
    }
}
