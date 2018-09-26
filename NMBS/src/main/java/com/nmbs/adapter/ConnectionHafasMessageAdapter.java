package com.nmbs.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nmbs.R;
import com.nmbs.model.HafasMessage;

import java.util.List;

/**
 * Created by Richard on 3/9/16.
 */
public class ConnectionHafasMessageAdapter {
    private LayoutInflater layoutInflater;
    private List<HafasMessage> hafasMessageList;
    private Activity activity;
    public ConnectionHafasMessageAdapter(Activity activity, List<HafasMessage> objects){
        this.activity = activity;
        this.hafasMessageList = objects;
        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void getScheduleHafasMessageView(final int position, LinearLayout linearLayout) {
        View convertView = null;
        if(convertView == null)
            convertView = layoutInflater.inflate(R.layout.li_schedule_detail_hafas_message, null);

        final LinearLayout descLayout = (LinearLayout) convertView.findViewById(R.id.ll_schedule_detail_hafas_desc_layout);
        final ImageView btnHafas = (ImageView) convertView.findViewById(R.id.iv_schedule_detail_hafas_action);
        TextView title = (TextView) convertView.findViewById(R.id.tv_schedule_detail_hafas_title);
        View viewLine = convertView.findViewById(R.id.v_li_schedule_detail_hafas_line);
        title.setText(hafasMessageList.get(position).getHeader());

        TextView text = (TextView) convertView.findViewById(R.id.tv_hafas_lead_content);
        TextView url = (TextView) convertView.findViewById(R.id.tv_him_message_url);
        TextView startAndEndText = (TextView) convertView.findViewById(R.id.tv_him_message_station_start_and_end);
        if(hafasMessageList.get(position).getHeader() !=null && !hafasMessageList.get(position).getHeader().equals("")){
            title.setVisibility(View.VISIBLE);
            title.setText(hafasMessageList.get(position).getHeader());
        }else{
            title.setVisibility(View.GONE);
        }

        if(hafasMessageList.get(position).getLead()  !=null  && !hafasMessageList.get(position).getLead().equals("")
                && hafasMessageList.get(position).getText() !=null && !hafasMessageList.get(position).getText().equals("")){

            text.setText(Html.fromHtml(hafasMessageList.get(position).getText()));
        }else if(hafasMessageList.get(position).getLead() !=null && !hafasMessageList.get(position).getLead().equals("")){

            text.setText(Html.fromHtml(hafasMessageList.get(position).getLead()));
        }else if(hafasMessageList.get(position).getText() !=null && !hafasMessageList.get(position).getText().equals("")){
            text.setText(Html.fromHtml(hafasMessageList.get(position).getText()));

        }else{
            text.setVisibility(View.GONE);
        }



        if(hafasMessageList.get(position).getUrl() !=null && !hafasMessageList.get(position).equals("")){
            url.setText(hafasMessageList.get(position).getUrl());
            url.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        }else{
            url.setVisibility(View.GONE);
        }

        url.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                Uri uri = Uri.parse(hafasMessageList.get(position).getUrl());
                Intent intent = new  Intent(Intent.ACTION_VIEW, uri);
                activity.startActivity(intent);
            }});

        String stationStart = "";
        String stationEnd = "";
        String stationText = "";


        stationStart = hafasMessageList.get(position).getStationStart();
        stationEnd = hafasMessageList.get(position).getStationEnd();
        stationText = stationStart + " - " + stationEnd;
        //System.out.println("stationStart=====" + stationStart);
        //System.out.println("stationEnd=====" + stationEnd);
        if (stationStart.isEmpty() || stationEnd.isEmpty()) {
            //System.out.println("if=====");

            stationText = stationText.replaceAll("-", "");
            stationText = stationText.trim();
        }

        startAndEndText.setText(stationText);
        if(position == 0){
            descLayout.setVisibility(View.VISIBLE);
            btnHafas.setImageResource(R.drawable.ic_minus);
        }else{
            viewLine.setVisibility(View.VISIBLE);
        }

        btnHafas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(descLayout.getVisibility() == View.VISIBLE){
                    descLayout.setVisibility(View.GONE);
                    btnHafas.setImageResource(R.drawable.ic_plus);
                }else{
                    descLayout.setVisibility(View.VISIBLE);
                    btnHafas.setImageResource(R.drawable.ic_minus);
                }
            }
        });

        linearLayout.addView(convertView);
    }
}
