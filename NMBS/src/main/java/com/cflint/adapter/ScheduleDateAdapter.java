package com.cflint.adapter;

import android.app.Activity;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cflint.R;
import com.cflint.activities.ScheduleResultActivity;
import com.cflint.model.RealTimeConnection;
import com.cflint.model.SeatLocation;
import com.cflint.model.TrainIcon;
import com.cflint.util.DateUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by David on 3/9/16.
 */
public class ScheduleDateAdapter {
    private LayoutInflater layoutInflater;
    private List<TrainIcon> trainIconList;
    private ScheduleResultActivity activity;
    private Map<String,List<RealTimeConnection>> mapList;
    private ScheduleAdapter scheduleAdapter;
    public ScheduleDateAdapter(ScheduleResultActivity activity, List<TrainIcon> trainIconList, Map<String,List<RealTimeConnection>> mapList){
        this.activity = activity;
        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.trainIconList = trainIconList;
        this.mapList = mapList;
    }

    public void getScheduleDateView(final String dateStr, LinearLayout linearLayout, int groupPosition) {
        View convertView = null;
        if(convertView == null)
            convertView = layoutInflater.inflate(R.layout.li_stationboard_result_group, null);

        View viewLine = convertView.findViewById(R.id.v_stationboard_result_line);
        TextView tagDate = (TextView) convertView.findViewById(R.id.iv_date);
        TextView tvWatchout = (TextView) convertView.findViewById(R.id.tv_watchout);
        Date date = DateUtils.stringToDate(dateStr);
        LinearLayout llSchedule = (LinearLayout) convertView.findViewById(R.id.ll_schedules);
        //Log.e("date", "date..." + dateStr);
        //Log.e("date", "date..." + date);
        tagDate.setText(DateUtils.dateTimeToString(date, "EEEE dd MMMM yyyy"));
        if (groupPosition == 0){
            tvWatchout.setVisibility(View.GONE);
        }else{
            viewLine.setVisibility(View.GONE);
            tvWatchout.setVisibility(View.VISIBLE);
            tagDate.setVisibility(View.VISIBLE);
        }
        //if(scheduleAdapter == null){
        scheduleAdapter = null;
            scheduleAdapter = new ScheduleAdapter(activity, trainIconList);
        //}
        for(int i = 0; i < mapList.get(mapList.keySet().toArray()[groupPosition]).size(); i++){
            scheduleAdapter.getScheduleView(llSchedule, mapList.get(mapList.keySet().toArray()[groupPosition]).get(i));
        }

        linearLayout.addView(convertView);
    }
}
