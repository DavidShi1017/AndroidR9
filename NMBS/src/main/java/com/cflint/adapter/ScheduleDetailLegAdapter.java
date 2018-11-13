package com.cflint.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.PopupWindow.OnDismissListener;
import com.cflint.R;
import com.cflint.activities.view.QuickActionBar;
import com.cflint.application.NMBSApplication;
import com.cflint.async.AsyncImageLoader;
import com.cflint.model.RealTimeInfoLeg;
import com.cflint.model.TrainIcon;
import com.cflint.model.TrainInfo;
import com.cflint.util.DateUtils;
import com.cflint.util.ImageUtil;
import com.cflint.util.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by David on 3/9/16.
 */
public class ScheduleDetailLegAdapter {
    private LayoutInflater layoutInflater;
    private List<RealTimeInfoLeg> legList;
    private List<TrainIcon> trainIconList;
    private Activity activity;

    public ScheduleDetailLegAdapter(Activity activity, List<RealTimeInfoLeg> objects,List<TrainIcon> trainIconList){
        this.activity = activity;
        this.legList = objects;
        this.trainIconList = trainIconList;
        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void getScheduleDetailLegView(int position, LinearLayout linearLayout) {
        View convertView = null;
        if(convertView == null)
            convertView = layoutInflater.inflate(R.layout.li_travel_item_view, null);

        LinearLayout originLayout = (LinearLayout) convertView.findViewById(R.id.ll_top);
        LinearLayout orginTrainTypeAndNumberLayout = (LinearLayout) convertView.findViewById(R.id.ll_top_child);
        LinearLayout midLayout = (LinearLayout) convertView.findViewById(R.id.ll_mid);
        LinearLayout midTrainTypeAndNumberLayout = (LinearLayout) convertView.findViewById(R.id.ll_mid_child);
        LinearLayout destinationLayout = (LinearLayout) convertView.findViewById(R.id.ll_bottom);
        TextView originTime = (TextView) convertView.findViewById(R.id.tv_travel_adapter_origin_time);
        TextView originDelayTime = (TextView) convertView.findViewById(R.id.tv_travel_adapter_origin_delay_time);
        TextView originStationName = (TextView) convertView.findViewById(R.id.tv_travel_adapter_origin_station_name);
        TextView midTrainTypeAndNumber = (TextView) convertView.findViewById(R.id.tv_travel_adapter_mid_train_type_numeber);
        TextView originTrainTypeAndNumber = (TextView) convertView.findViewById(R.id.tv_travel_adapter_origin_trainType_and_trainNumber);
        LinearLayout originTrainInfoIconLayout = (LinearLayout) convertView.findViewById(R.id.ll_travel_adapter_origin_train_info_icons);
        LinearLayout midTrainInfoIconLayout = (LinearLayout) convertView.findViewById(R.id.ll_travel_adapter_mid_trainInfo_icons);
        TextView midArriveTime = (TextView) convertView.findViewById(R.id.tv_travel_adapter_mid_arrive_time);
        TextView midDepatureTime = (TextView) convertView.findViewById(R.id.tv_travel_adapter_mid_departure_time);
        TextView midArriveDelayTime = (TextView) convertView.findViewById(R.id.tv_travel_adapter_mid_arrive_delay_time);
        TextView midDepatureDelayTime = (TextView) convertView.findViewById(R.id.tv_travel_adapter_mid_departure_delay_time);
        TextView midStationName = (TextView) convertView.findViewById(R.id.tv_travel_adapter_mid_station_name);
        TextView destTime = (TextView) convertView.findViewById(R.id.tv_travel_adapter_destination_time);
        TextView destDelayTime = (TextView) convertView.findViewById(R.id.tv_travel_adapter_destination_delay_time);
        TextView destName = (TextView) convertView.findViewById(R.id.tv_travel_adapter_destination_station_name);
        ImageView originIsNightIcon = (ImageView) convertView.findViewById(R.id.iv_travel_adapter_origin_is_night_icon);
        ImageView midIsNightIcon = (ImageView) convertView.findViewById(R.id.iv_travel_adapter_mid_is_night_icon);

        ImageView midTrainTypeIcon = (ImageView) convertView.findViewById(R.id.iv_travel_adapter_mid_trainType_icon);
        ImageView originTrainTypeIcon = (ImageView) convertView.findViewById(R.id.iv_travel_adapter_origin_train_icon);

        ImageView originListBar = (ImageView) convertView.findViewById(R.id.iv_travel_adapter_origin_trainType_list_bar);
        ImageView midListBar = (ImageView) convertView.findViewById(R.id.iv_travel_adapter_mid_list_bar);

        TextView arriveForm = (TextView) convertView.findViewById(R.id.tv_travel_adapter_mid_station_arrive_form);
        TextView departureForm = (TextView) convertView.findViewById(R.id.tv_travel_adapter_mid_station_departure_form);

        TextView destinationArrivePlatform = (TextView) convertView.findViewById(R.id.tv_travel_adapter_destination_arrive_platform);
        TextView originDeparturePlatform = (TextView) convertView.findViewById(R.id.tv_travel_adapter_ori_station_arrive_form);
        TextView tvMidWalk = (TextView) convertView.findViewById(R.id.tv_travel_adapter_mid_walk);
        TextView tvOriginWalk = (TextView) convertView.findViewById(R.id.tv_travel_adapter_origin_walk);
        LinearLayout llOriginBuyTickets = (LinearLayout) convertView.findViewById(R.id.ll_travel_adapter_origin_buytickets);
        LinearLayout llOriginNoTicketNeed = (LinearLayout) convertView.findViewById(R.id.ll_travel_adapter_origin_no_ticket_need);
        LinearLayout llMidBuyTickets = (LinearLayout) convertView.findViewById(R.id.ll_travel_adapter_mid_buytickets);
        LinearLayout llMidNoTicketNeed = (LinearLayout) convertView.findViewById(R.id.ll_travel_adapter_mid_no_ticket_need);
        TextView tvOriginLegStatus = (TextView) convertView.findViewById(R.id.tv_travel_adapter_origin_legstatus);
        TextView tvMidLegStatus = (TextView) convertView.findViewById(R.id.tv_travel_adapter_mid_legstatus);

        llOriginBuyTickets.setVisibility(View.GONE);
        llOriginNoTicketNeed.setVisibility(View.GONE);
        llMidBuyTickets.setVisibility(View.GONE);
        llMidNoTicketNeed.setVisibility(View.GONE);

        RealTimeInfoLeg realTimeInfoLeg = this.legList.get(position);
        if (position == 0){
            downloadTrainIcon(originTrainTypeIcon, realTimeInfoLeg);
            originLayout.setVisibility(View.VISIBLE);
            orginTrainTypeAndNumberLayout.setVisibility(View.VISIBLE);
            originTrainTypeAndNumber.setText(realTimeInfoLeg.getTrainType() + " " + realTimeInfoLeg.getTrainNr());
            midLayout.setVisibility(View.GONE);
            midTrainTypeAndNumberLayout.setVisibility(View.GONE);
            originTime.setText(DateUtils.FormatToHHMMFromDate(this.legList.get(position).getDastUpdated()));
            originStationName.setText(realTimeInfoLeg.getOriginName());
            if(this.legList.get(position).getRealTimeDepartureDelta()!=null&&!"".equals(this.legList.get(position).getRealTimeDepartureDelta())){
                originDelayTime.setText(this.legList.get(position).getRealTimeDepartureDelta());
            }

            initTrainInfoIcons(realTimeInfoLeg.getTrainInfos(),originTrainInfoIconLayout);
            if(realTimeInfoLeg.isNightTrain()){
                originIsNightIcon.setVisibility(View.VISIBLE);
            }else{
                originIsNightIcon.setVisibility(View.INVISIBLE);
            }
            if(realTimeInfoLeg.getLegStatus() != null && !realTimeInfoLeg.getLegStatus().isEmpty()){
                tvOriginLegStatus.setVisibility(View.VISIBLE);
                tvOriginLegStatus.setText(realTimeInfoLeg.getLegStatus());
            }else{
                tvOriginLegStatus.setVisibility(View.GONE);
            }
            if(!realTimeInfoLeg.isTrainLeg()){
                tvOriginWalk.setVisibility(View.VISIBLE);
                originTrainTypeIcon.setImageResource(R.drawable.ic_walk);
                originListBar.setImageResource(R.drawable.ic_planner_dash_line);
                calculatedWalkTime(realTimeInfoLeg,originTrainTypeAndNumber);
            }else{
                tvOriginWalk.setVisibility(View.GONE);
            }

            originDeparturePlatform.setText(this.legList.get(position).getDeparturePlatform());
            if(this.legList.get(position).isDeparturePlatformChanged()){
                originDeparturePlatform.setTextColor(activity.getResources().getColor(R.color.textcolor_error));
            }
        }else{
            downloadTrainIcon(midTrainTypeIcon, realTimeInfoLeg);
            initTrainInfoIcons(realTimeInfoLeg.getTrainInfos(), midTrainInfoIconLayout);
            midTrainTypeAndNumber.setText(realTimeInfoLeg.getTrainType() + " " + realTimeInfoLeg.getTrainNr());
            midStationName.setText(realTimeInfoLeg.getOriginName());
            midArriveTime.setText(DateUtils.FormatToHHMMFromDate(this.legList.get(position - 1).getArrivalDateTime()));
            midDepatureTime.setText(DateUtils.FormatToHHMMFromDate(this.legList.get(position).getDastUpdated()));
            arriveForm.setText(this.legList.get(position - 1).getArrivalPlatform());
            departureForm.setText(this.legList.get(position).getDeparturePlatform());

            if(this.legList.get(position - 1).isArrivalPlatformChanged()){
                arriveForm.setTextColor(activity.getResources().getColor(R.color.textcolor_error));
            }
            if(this.legList.get(position).isDeparturePlatformChanged()){
                departureForm.setTextColor(activity.getResources().getColor(R.color.textcolor_error));
            }
            if(this.legList.get(position-1).getRealTimeArrivalDelta()!=null&&!"".equals(this.legList.get(position-1).getRealTimeArrivalDelta())){
                midArriveDelayTime.setText(this.legList.get(position - 1).getRealTimeArrivalDelta());
            }

            if(this.legList.get(position).getRealTimeDepartureDelta()!=null&&!"".equals(this.legList.get(position).getRealTimeDepartureDelta())){
                midDepatureDelayTime.setText(this.legList.get(position).getRealTimeDepartureDelta());
            }
            if(this.legList.get(position).getLegStatus() != null && !this.legList.get(position).getLegStatus().isEmpty()){
                tvMidLegStatus.setVisibility(View.VISIBLE);
                tvMidLegStatus.setText(this.legList.get(position).getLegStatus());
            }else{
                tvMidLegStatus.setVisibility(View.GONE);
            }
            if(realTimeInfoLeg.isNightTrain()){
                midIsNightIcon.setVisibility(View.VISIBLE);
            }else{
                midIsNightIcon.setVisibility(View.INVISIBLE);
            }
            if(!realTimeInfoLeg.isTrainLeg()){
                tvMidWalk.setVisibility(View.VISIBLE);
                midTrainTypeIcon.setImageResource(R.drawable.ic_walk);
                midListBar.setImageResource(R.drawable.ic_planner_dash_line);
                calculatedWalkTime(realTimeInfoLeg, midTrainTypeAndNumber);
            }else{
                tvMidWalk.setVisibility(View.GONE);
            }
        }

        if(position == 0&&position == legList.size()-1){
            midLayout.setVisibility(View.GONE);
            midTrainTypeAndNumberLayout.setVisibility(View.GONE);
        }


        if(position == legList.size()-1){
            destinationLayout.setVisibility(View.VISIBLE);

            destName.setText(realTimeInfoLeg.getDestinationName());
            destTime.setText(DateUtils.FormatToHHMMFromDate(realTimeInfoLeg.getArrivalDateTime()));
            if(realTimeInfoLeg.getRealTimeArrivalDelta()!=null&&!"".equals(realTimeInfoLeg.getRealTimeArrivalDelta())){
                destDelayTime.setText(realTimeInfoLeg.getRealTimeArrivalDelta());
            }
            destinationArrivePlatform.setText(realTimeInfoLeg.getArrivalPlatform());
            if(realTimeInfoLeg.isArrivalPlatformChanged()){
                destinationArrivePlatform.setTextColor(activity.getResources().getColor(R.color.textcolor_error));
            }
        }


        linearLayout.addView(convertView);
    }

    private void downloadTrainIcon(ImageView imageView, final RealTimeInfoLeg realTimeInfoLeg){
        boolean isHasTrainIcon = false;
        TrainIcon trainIcon = NMBSApplication.getInstance().getTrainIconsService().getTrainIcon(realTimeInfoLeg.getTrainType());
        if(trainIcon != null){
            isHasTrainIcon = true;
            if(ImageUtil.getTrainTypeImageId(realTimeInfoLeg.getTrainType()) == R.drawable.icon_train){
                String imageUrl = ImageUtil.convertImageExtension(trainIcon.getIcon(Utils.getDeviceDensity(activity)));
                String fullImageUrl = activity.getString(R.string.server_url_host) + imageUrl;
                AsyncImageLoader.getInstance().loadDrawable(
                        activity, fullImageUrl, imageUrl,
                        imageView, null, new AsyncImageLoader.ImageCallback() {
                            public void imageLoaded(Bitmap imageDrawable,
                                                    String imageUrl, View view) {
                                if(imageDrawable == null){
                                    ((ImageView) view).setImageResource(ImageUtil.getTrainTypeImageId(realTimeInfoLeg.getTrainType()));
                                }else{
                                    ((ImageView) view).setImageBitmap(imageDrawable);
                                }
                            }
                        });
            }else{
                imageView.setImageResource(ImageUtil.getTrainTypeImageId(realTimeInfoLeg.getTrainType()));
            }
        }
        if(!isHasTrainIcon){
            imageView.setImageResource(R.drawable.icon_train);
        }
    }

    private void calculatedWalkTime(RealTimeInfoLeg realTimeInfoLeg, TextView textView){
        String walkTime = "";
        int minutes = 0;
        int hours = 0;
        if(realTimeInfoLeg.getDuration() != null){
            minutes = DateUtils.getMinutes(realTimeInfoLeg.getDuration());
            hours = DateUtils.getHours(realTimeInfoLeg.getDuration());
        }
        if (hours > 0) {
            walkTime = hours + " " + activity.getResources().getString(R.string.departuredetail_view_hours) + " ";
        }
        if (minutes > 0) {
            walkTime += minutes + " " + activity.getResources().getString(R.string.departuredetail_view_minutes) + " " +
                    activity.getResources().getString(R.string.departuredetail_view_walking);
        }
        textView.setText(walkTime);
    }

    private void initTrainInfoIcons(List<TrainInfo> trainInfos, LinearLayout linearLayout){
        if(trainInfos.size() > 0){
            LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(activity.getResources().getDimensionPixelSize(R.dimen.train_info), activity.getResources().getDimensionPixelSize(R.dimen.train_info));
            layout.setMargins(5, 0, 0, 0);
            Map<Integer,Integer> iconSet = new HashMap<Integer,Integer>();
            for(TrainInfo trainInfo:trainInfos){
                if(trainInfo.getKey().equals("BF")||trainInfo.getKey().equals("BR")||trainInfo.getKey().equals("ZR")){
                    if(!iconSet.containsKey(R.drawable.ic_traininfor_restaurant)){
                        iconSet.put(R.drawable.ic_traininfor_restaurant,R.drawable.ic_traininfor_restaurant);
                        createImageForTrainInfo(R.drawable.ic_traininfor_restaurant, layout, linearLayout);
                    }
                }
                if(trainInfo.getKey().equals("BT")||trainInfo.getKey().equals("BW")||trainInfo.getKey().equals("QP")){
                    if(!iconSet.containsKey(R.drawable.ic_traininfor_bar)){
                        iconSet.put(R.drawable.ic_traininfor_bar,R.drawable.ic_traininfor_bar);
                        createImageForTrainInfo(R.drawable.ic_traininfor_bar, layout, linearLayout);
                    }
                }
                if(trainInfo.getKey().equals("MB")||trainInfo.getKey().equals("MN")){
                    if(!iconSet.containsKey(R.drawable.ic_traininfor_trolley)) {
                        iconSet.put(R.drawable.ic_traininfor_trolley,R.drawable.ic_traininfor_trolley);
                        createImageForTrainInfo(R.drawable.ic_traininfor_trolley, layout, linearLayout);
                    }
                }
                if(trainInfo.getKey().equals("FB")||trainInfo.getKey().equals("FF")||trainInfo.getKey().equals("FK")||trainInfo.getKey().equals("FR")||trainInfo.getKey().equals("FS")||trainInfo.getKey().equals("FT")||trainInfo.getKey().equals("h")){
                    if(!iconSet.containsKey(R.drawable.ic_traininfor_bike)) {
                        iconSet.put(R.drawable.ic_traininfor_bike,R.drawable.ic_traininfor_bike);
                        createImageForTrainInfo(R.drawable.ic_traininfor_bike, layout, linearLayout);
                    }
                }
                if(trainInfo.getKey().equals("WL")||trainInfo.getKey().equals("WN")||trainInfo.getKey().equals("y")){
                    if(!iconSet.containsKey(R.drawable.ic_traininfor_wifi)) {
                        iconSet.put(R.drawable.ic_traininfor_wifi,R.drawable.ic_traininfor_wifi);
                        createImageForTrainInfo(R.drawable.ic_traininfor_wifi, layout, linearLayout);
                    }
                }
            }
        }
    }

    private void createImageForTrainInfo(final int resId, LinearLayout.LayoutParams layout, LinearLayout trainInfoLayout){
        ImageView imageView = new ImageView(this.activity);
        imageView.setLayoutParams(layout);
        imageView.setImageResource(resId);
        trainInfoLayout.addView(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = 0;
                String text = "";
                switch (resId){
                    case R.drawable.ic_traininfor_restaurant:
                        id =  R.drawable.ic_traininfor_restaurant_white;
                        text = activity.getResources().getString(R.string.departuredetail_view_traininfo_restaurant);
                        break;
                    case R.drawable.ic_traininfor_bar:
                        id =  R.drawable.ic_traininfor_bar_white;
                        text = activity.getResources().getString(R.string.departuredetail_view_traininfo_bar);
                        break;
                    case R.drawable.ic_traininfor_trolley:
                        id =  R.drawable.ic_traininfor_trolley_white;
                        text = activity.getResources().getString(R.string.departuredetail_view_traininfo_minibar);
                        break;
                    case R.drawable.ic_traininfor_bike:
                        id =  R.drawable.ic_traininfor_bike_white;
                        text = activity.getResources().getString(R.string.departuredetail_view_traininfo_bike);
                        break;
                    case R.drawable.ic_traininfor_wifi:
                        id =  R.drawable.ic_traininfor_wifi_white;
                        text = activity.getResources().getString(R.string.departuredetail_view_traininfo_wifi);
                        break;
                }
                showAletDialog(v, id, text);
            }
        });
    }

    public void showAletDialog(View v, int resId, String trainInfoText) {


        //rootView.setAlpha(0.0f);
        QuickActionBar bar = new QuickActionBar(activity);
        bar.show(v);
        View convertView = bar.getContentView();
        TextView tarinInfoText = (TextView) convertView.findViewById(R.id.dialog_train_info_text);
        ImageView tarinInfoImage = (ImageView) convertView.findViewById(R.id.dialog_train_info_image);

        tarinInfoText.setText(trainInfoText);
        tarinInfoImage.setImageResource(resId);

        //rootView.setAlpha(0.4f);

        bar.setOnDismissListener(new OnDismissListener() {

            public void onDismiss() {
                //rootView.setVisibility(View.GONE);
                //rootView.setAlpha(0.0f);
            }
        });

    }
}
