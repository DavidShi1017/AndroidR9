package com.nmbs.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nmbs.R;
import com.nmbs.activities.view.QuickActionBar;
import com.nmbs.application.NMBSApplication;
import com.nmbs.async.AsyncImageLoader;
import com.nmbs.model.Connection;
import com.nmbs.model.Dossier;
import com.nmbs.model.Leg;
import com.nmbs.model.RealTimeInfoLeg;
import com.nmbs.model.TrainIcon;
import com.nmbs.model.TrainInfo;
import com.nmbs.util.DateUtils;
import com.nmbs.util.ImageUtil;
import com.nmbs.util.Utils;

import java.util.List;

/**
 * Created by Richard on 3/9/16.
 */
public class ConnectionDetailLegAdapter {
    private LayoutInflater layoutInflater;
    private List<Leg> legList;
    private List<RealTimeInfoLeg> realTimeInfoLegs;
    private List<TrainIcon> trainIconList;
    private Activity activity;
    private Leg leg;
    private RealTimeInfoLeg realTimeInfoLeg;
    public ConnectionDetailLegAdapter(Activity activity, List<Leg> objects, List<TrainIcon> trainIconList, List<RealTimeInfoLeg> realTimeInfoLegs){
        this.activity = activity;
        this.legList = objects;
        this.trainIconList = trainIconList;
        this.realTimeInfoLegs = realTimeInfoLegs;
        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void getConnectionDetailLegView(int position, LinearLayout linearLayout, Dossier dossier, Connection connection) {
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
        TextView tvMidWalk = (TextView) convertView.findViewById(R.id.tv_travel_adapter_mid_walk);
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

        TextView tvOriginWalk = (TextView) convertView.findViewById(R.id.tv_travel_adapter_origin_walk);
        ScrollView svOriginInfoIcons = (ScrollView) convertView.findViewById(R.id.sv_adapter_origin_info_icons);
        LinearLayout llOriginInfo = (LinearLayout) convertView.findViewById(R.id.ll_travel_adapter_origin_train_info);
        LinearLayout llOriginBuyTickets = (LinearLayout) convertView.findViewById(R.id.ll_travel_adapter_origin_buytickets);
        LinearLayout llOriginNoTicketNeed = (LinearLayout) convertView.findViewById(R.id.ll_travel_adapter_origin_no_ticket_need);

        ScrollView svTrainInfoIcons = (ScrollView) convertView.findViewById(R.id.sv_trainInfo_icons);
        TextView tvMidTrainTypeNumeber = (TextView) convertView.findViewById(R.id.tv_travel_adapter_mid_train_type_numeber);
        LinearLayout llMidTrainInfo = (LinearLayout) convertView.findViewById(R.id.ll_travel_adapter_mid_traininfo);

        LinearLayout llMidBuyTickets = (LinearLayout) convertView.findViewById(R.id.ll_travel_adapter_mid_buytickets);
        LinearLayout llMidNoTicketNeed = (LinearLayout) convertView.findViewById(R.id.ll_travel_adapter_mid_no_ticket_need);

        TextView tvOriginLegStatus = (TextView) convertView.findViewById(R.id.tv_travel_adapter_origin_legstatus);
        TextView tvMidLegStatus = (TextView) convertView.findViewById(R.id.tv_travel_adapter_mid_legstatus);



        if (this.legList != null && this.legList.size() > 0){
            leg = this.legList.get(position);
        }
        if (this.realTimeInfoLegs != null && this.realTimeInfoLegs.size() > 0){
            realTimeInfoLeg = this.realTimeInfoLegs.get(position);
        }


        if (position == 0){
            if(leg != null){
                downloadTrainIcon(originTrainTypeIcon, leg);
                originLayout.setVisibility(View.VISIBLE);
                orginTrainTypeAndNumberLayout.setVisibility(View.VISIBLE);


                midLayout.setVisibility(View.GONE);
                midTrainTypeAndNumberLayout.setVisibility(View.GONE);
                originTime.setText(DateUtils.FormatToHHMMFromDate(leg.getDastUpdated()));
                originStationName.setText(leg.getOriginName());

                int trainInfosCount = initTrainInfoIcons(leg.getTrainInfos(), originTrainInfoIconLayout, svOriginInfoIcons);
                originDeparturePlatform.setText(this.legList.get(position).getDeparturePlatform());
                originTrainTypeAndNumber.setText(leg.getTrainType() + " " + leg.getTrainNr());
                //leg.setLegStatus("David");
                if(leg.getLegStatus() != null && !leg.getLegStatus().isEmpty()){
                    tvOriginLegStatus.setVisibility(View.VISIBLE);
                    tvOriginLegStatus.setText(leg.getLegStatus());
                }else{
                    tvOriginLegStatus.setVisibility(View.GONE);
                }


                if(leg.isNightTrain()){
                    originIsNightIcon.setVisibility(View.VISIBLE);
                }else{
                    originIsNightIcon.setVisibility(View.INVISIBLE);
                }
                if(!leg.isTrainLeg()){
                    tvOriginWalk.setVisibility(View.VISIBLE);
                    llOriginInfo.setVisibility(View.GONE);

                    originTrainTypeIcon.setImageResource(R.drawable.ic_walk);
                    originListBar.setImageResource(R.drawable.ic_planner_dash_line);
                    calculatedWalkTime(leg, tvOriginWalk);
                    if(dossier != null && connection.hasLegInSameZone(dossier.getTravelSegments(), connection)){
                        llOriginNoTicketNeed.setVisibility(View.VISIBLE);
                    }else{
                        llOriginNoTicketNeed.setVisibility(View.GONE);
                    }
                    //leg.setHasWarning(true);
                    if(leg.isHasWarning()){
                        llOriginBuyTickets.setVisibility(View.VISIBLE);
                    }else {
                        llOriginBuyTickets.setVisibility(View.GONE);
                    }
                }else{
                    if(dossier != null && connection.hasLegInSameZone(dossier.getTravelSegments(), connection)){
                        llOriginNoTicketNeed.setVisibility(View.VISIBLE);
                    }else{
                        llOriginNoTicketNeed.setVisibility(View.GONE);
                    }
                    //leg.setHasWarning(true);
                    if(leg.isHasWarning()){
                        llOriginBuyTickets.setVisibility(View.VISIBLE);
                    }else {
                        llOriginBuyTickets.setVisibility(View.GONE);
                    }
                    tvOriginWalk.setVisibility(View.GONE);
                    llOriginInfo.setVisibility(View.VISIBLE);
                    if(trainInfosCount > 2){
                        llOriginInfo.setOrientation(LinearLayout.VERTICAL);
                    }else if(trainInfosCount <= 2){
                        llOriginInfo.setOrientation(LinearLayout.HORIZONTAL);
                    }
                }
            }
            if(realTimeInfoLeg != null){
                if(realTimeInfoLeg.getRealTimeDepartureDelta() != null && !"".equals(realTimeInfoLeg.getRealTimeDepartureDelta())){
                    originDelayTime.setText(realTimeInfoLeg.getRealTimeDepartureDelta());
                    originDelayTime.setVisibility(View.VISIBLE);
                }else{
                    originDelayTime.setVisibility(View.INVISIBLE);
                }
                if(realTimeInfoLeg.isDeparturePlatformChanged()){
                    originDeparturePlatform.setTextColor(activity.getResources().getColor(R.color.textcolor_error));
                }
            }

        }else{

            Leg leg = this.legList.get(position);
            Leg previousLeg = this.legList.get(position - 1);

            if(leg != null){
                downloadTrainIcon(midTrainTypeIcon, leg);

                int trainInfosCount = initTrainInfoIcons(leg.getTrainInfos(), midTrainInfoIconLayout, svTrainInfoIcons);
                midStationName.setText(leg.getOriginName());
                midDepatureTime.setText(DateUtils.FormatToHHMMFromDate(leg.getDastUpdated()));
                departureForm.setText(leg.getDeparturePlatform());
                tvMidTrainTypeNumeber.setText(leg.getTrainType()+ " " + leg.getTrainNr());
                //leg.setLegStatus("David");
                if(leg.getLegStatus() != null && !leg.getLegStatus().isEmpty()){
                    tvMidLegStatus.setVisibility(View.VISIBLE);
                    tvMidLegStatus.setText(leg.getLegStatus());
                }else{
                    tvMidLegStatus.setVisibility(View.GONE);
                }
                if(leg.isNightTrain()){
                    midIsNightIcon.setVisibility(View.VISIBLE);
                }else{
                    midIsNightIcon.setVisibility(View.INVISIBLE);
                }
                //leg.setHasWarning(true);
                //Log.e("Leg", "HasWarning..." + leg.isHasWarning());
                if(!leg.isTrainLeg()){
                    tvMidWalk.setVisibility(View.VISIBLE);
                    llMidTrainInfo.setVisibility(View.GONE);
                    midTrainTypeIcon.setImageResource(R.drawable.ic_walk);
                    midListBar.setImageResource(R.drawable.ic_planner_dash_line);
                    calculatedWalkTime(leg, tvMidWalk);
                    if(dossier != null && connection.hasLegInSameZone(dossier.getTravelSegments(), connection)){
                        llMidNoTicketNeed.setVisibility(View.VISIBLE);
                    }else{
                        llMidNoTicketNeed.setVisibility(View.GONE);
                    }
                    if(leg.isHasWarning()){
                        llMidBuyTickets.setVisibility(View.VISIBLE);
                    }else {
                        llMidBuyTickets.setVisibility(View.GONE);
                    }
                }else{
                    if(dossier != null && connection.hasLegInSameZone(dossier.getTravelSegments(), connection)){
                        llMidNoTicketNeed.setVisibility(View.VISIBLE);
                    }else{
                        llMidNoTicketNeed.setVisibility(View.GONE);
                    }
                    if(leg.isHasWarning()){
                        llMidBuyTickets.setVisibility(View.VISIBLE);
                    }else {
                        llMidBuyTickets.setVisibility(View.GONE);
                    }
                    tvMidWalk.setVisibility(View.GONE);
                    llMidTrainInfo.setVisibility(View.VISIBLE);
                    if(trainInfosCount > 2){
                        llMidTrainInfo.setOrientation(LinearLayout.VERTICAL);
                    }else if(trainInfosCount <= 2){
                        llMidTrainInfo.setOrientation(LinearLayout.HORIZONTAL);
                    }
                }
            }

            if(previousLeg != null){
                midArriveTime.setText(DateUtils.FormatToHHMMFromDate(previousLeg.getArrivalDateTime()));
                arriveForm.setText(previousLeg.getArrivalPlatform());
            }

            if(this.realTimeInfoLegs != null){
                RealTimeInfoLeg previousRealTimeInfoLeg = this.realTimeInfoLegs.get(position - 1);
                RealTimeInfoLeg realTimeInfoLeg = this.realTimeInfoLegs.get(position);

                if(realTimeInfoLeg != null){
                    if(realTimeInfoLeg.isDeparturePlatformChanged()){
                        departureForm.setTextColor(activity.getResources().getColor(R.color.textcolor_error));
                    }
                    if(realTimeInfoLeg.getRealTimeDepartureDelta() != null && !"".equals(realTimeInfoLeg.getRealTimeDepartureDelta())){
                        midDepatureDelayTime.setVisibility(View.VISIBLE);
                        midDepatureDelayTime.setText(realTimeInfoLeg.getRealTimeDepartureDelta());
                    }else{
                        midDepatureDelayTime.setVisibility(View.GONE);
                    }
                }

                if(previousRealTimeInfoLeg != null){
                    if(previousRealTimeInfoLeg.isArrivalPlatformChanged()) {
                        arriveForm.setTextColor(activity.getResources().getColor(R.color.textcolor_error));
                    }
                    if (previousRealTimeInfoLeg.getRealTimeArrivalDelta() != null && !"".equals(previousRealTimeInfoLeg.getRealTimeArrivalDelta())){
                        midArriveDelayTime.setText(previousRealTimeInfoLeg.getRealTimeArrivalDelta());
                    }
                }
            }
            }


        if(position == 0 && position == legList.size()-1){
            midLayout.setVisibility(View.GONE);
            midTrainTypeAndNumberLayout.setVisibility(View.GONE);
        }

        if(legList != null && position == legList.size() - 1){
            destinationLayout.setVisibility(View.VISIBLE);
            if(leg != null){
                destName.setText(leg.getDestinationName());
                destTime.setText(DateUtils.FormatToHHMMFromDate(leg.getArrivalDateTime()));
                destinationArrivePlatform.setText(leg.getArrivalPlatform());
            }
            if(this.realTimeInfoLegs != null){
                RealTimeInfoLeg realTimeInfoLeg = this.realTimeInfoLegs.get(position);
                if(realTimeInfoLeg != null){
                    if(realTimeInfoLeg.getRealTimeArrivalDelta() != null && !"".equals(realTimeInfoLeg.getRealTimeArrivalDelta())){
                        destDelayTime.setVisibility(View.VISIBLE);
                        destDelayTime.setText(realTimeInfoLeg.getRealTimeArrivalDelta());
                    }else{
                        destDelayTime.setVisibility(View.GONE);
                    }
                    destinationArrivePlatform.setText(realTimeInfoLeg.getArrivalPlatform());
                    if(realTimeInfoLeg.isArrivalPlatformChanged()){
                        destinationArrivePlatform.setTextColor(activity.getResources().getColor(R.color.textcolor_error));
                    }
                }
            }
        }
        linearLayout.addView(convertView);
    }

    private void downloadTrainIcon(ImageView imageView, final Leg realTimeInfoLeg){

        TrainIcon trainIcon = NMBSApplication.getInstance().getTrainIconsService().getTrainIcon(realTimeInfoLeg.getTrainType());
        boolean isHasTrainIcon = false;
        if(trainIcon != null){
            isHasTrainIcon = true;
            String imageUrl = ImageUtil.convertImageExtension(trainIcon.getIcon(Utils.getDeviceDensity(activity)));
            String fullImageUrl = activity.getString(R.string.server_url_host) + imageUrl;
           // Log.e("TrainType", "TrainType====" + realTimeInfoLeg.getTrainType());
            //Log.e("TrainType", "imageUrl====" + imageUrl);
            if(imageUrl == null || imageUrl.isEmpty()){
                //Log.e("TrainType", "TrainType is null====");
                //Log.e("TrainType", "TrainType is id====" + ImageUtil.getTrainTypeImageId(realTimeInfoLeg.getTrainType()));
                imageView.setImageResource(ImageUtil.getTrainTypeImageId(realTimeInfoLeg.getTrainType()));
            }else{
                if(ImageUtil.getTrainTypeImageId(realTimeInfoLeg.getTrainType()) == R.drawable.icon_train){
                    AsyncImageLoader.getInstance().loadDrawable(
                            activity, fullImageUrl, imageUrl,
                            imageView, null, new AsyncImageLoader.ImageCallback() {
                                public void imageLoaded(Bitmap imageDrawable, String imageUrl, View view) {
                                    if(imageDrawable == null){
                                        ((ImageView) view).setImageResource(ImageUtil.getTrainTypeImageId(realTimeInfoLeg.getTrainType()));
                                    }else{
                                        ((ImageView) view).setImageBitmap(imageDrawable);
                                    }
                                }});
                }else{
                    imageView.setImageResource(ImageUtil.getTrainTypeImageId(realTimeInfoLeg.getTrainType()));
                }

            }
        }



        if(!isHasTrainIcon){
            imageView.setImageResource(R.drawable.icon_train);
        }
    }

    private void calculatedWalkTime(Leg leg, TextView textView){
        String walkTime = "";
        int minutes = 0;
        int hours = 0;
        if(leg.getDuration() != null){
            minutes = DateUtils.getMinutes(leg.getDuration());
            hours = DateUtils.getHours(leg.getDuration());
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

    private int initTrainInfoIcons(List<TrainInfo> trainInfos, LinearLayout linearLayout, ScrollView view){
        int trainInfosCount = 0;
        if(trainInfos != null && trainInfos.size() > 0){
            view.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(activity.getResources().getDimensionPixelSize(R.dimen.train_info), activity.getResources().getDimensionPixelSize(R.dimen.train_info));
            layout.setMargins(5, 0, 0, 0);
            for(TrainInfo trainInfo:trainInfos){

                if(trainInfo.getKey().equals("BF")||trainInfo.getKey().equals("BR")||trainInfo.getKey().equals("ZR")){
                    createImageForTrainInfo(R.drawable.ic_traininfor_restaurant, layout, linearLayout);
                    trainInfosCount ++;
                }
                if(trainInfo.getKey().equals("BT")||trainInfo.getKey().equals("BW")||trainInfo.getKey().equals("QP")){
                    createImageForTrainInfo(R.drawable.ic_traininfor_bar, layout, linearLayout);
                    trainInfosCount ++;
                }
                if(trainInfo.getKey().equals("MB")||trainInfo.getKey().equals("MN")){
                    createImageForTrainInfo(R.drawable.ic_traininfor_trolley, layout, linearLayout);
                    trainInfosCount ++;
                }
                if(trainInfo.getKey().equals("FB")||trainInfo.getKey().equals("FF")||trainInfo.getKey().equals("FK")||trainInfo.getKey().equals("FR")||trainInfo.getKey().equals("FS")||trainInfo.getKey().equals("FT")||trainInfo.getKey().equals("h")){
                    createImageForTrainInfo(R.drawable.ic_traininfor_bike, layout, linearLayout);
                    trainInfosCount ++;
                }
                if(trainInfo.getKey().equals("WL")||trainInfo.getKey().equals("WN")||trainInfo.getKey().equals("y")){
                    createImageForTrainInfo(R.drawable.ic_traininfor_wifi, layout,linearLayout);
                    trainInfosCount ++;
                }

            }
        }else{
            view.setVisibility(View.GONE);
        }
        return trainInfosCount;
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

        bar.setOnDismissListener(new PopupWindow.OnDismissListener() {

            public void onDismiss() {
                //rootView.setVisibility(View.GONE);
                //rootView.setAlpha(0.0f);
            }
        });

    }
}
