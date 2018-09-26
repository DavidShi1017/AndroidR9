package com.nmbs.adapter;

import android.content.Context;
import android.graphics.Bitmap;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nmbs.R;
import com.nmbs.activities.ScheduleResultActivity;
import com.nmbs.activities.ScheduleResultDetailActivity;
import com.nmbs.activities.view.FitImageView;
import com.nmbs.application.NMBSApplication;
import com.nmbs.async.AsyncImageLoader;
import com.nmbs.model.RealTimeConnection;
import com.nmbs.model.RealTimeInfoLeg;
import com.nmbs.model.TrainIcon;
import com.nmbs.util.DateUtils;
import com.nmbs.util.ImageUtil;
import com.nmbs.util.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by David on 3/9/16.
 */
public class ScheduleAdapter {
    private LayoutInflater layoutInflater;
    private List<TrainIcon> trainIconList;
    private ScheduleResultActivity activity;
    private LinearLayout additionalTrainTypeLayout;
    private ImageView trainTypeIcon01, trainTypeIcon02, trainTypeIcon03;
    public ScheduleAdapter(ScheduleResultActivity activity, List<TrainIcon> trainIconList){
        this.activity = activity;
        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.trainIconList = trainIconList;
    }

    public void getScheduleView(LinearLayout linearLayout, final RealTimeConnection realTimeConnection) {
        View convertView = null;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.li_schedule_result, null);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   activity.startActivity(ScheduleResultDetailActivity.createIntent(activity,
                           realTimeConnection, NMBSApplication.PAGE_SCHEDULE));
               }
           });
        TextView departureTimeView = (TextView) convertView.findViewById(R.id.tv_li_schedule_result_departure_time);
        TextView arriveTimeView = (TextView) convertView.findViewById(R.id.tv_li_schedule_result_arrive_time);
        TextView durationTimeView = (TextView) convertView.findViewById(R.id.tv_li_schedule_result_duration_time);
        TextView transferView = (TextView) convertView.findViewById(R.id.tv_li_schedule_result_transfer);
        View notPossibleView = convertView.findViewById(R.id.v_li_schedule_line_view);
        LinearLayout notPossibleLayout = (LinearLayout) convertView.findViewById(R.id.ll_li_schedule_result_connection_not_possible);
        TextView tvNotPossible = (TextView) convertView.findViewById(R.id.tv_not_possible);

        TextView departureDelayTimeView = (TextView) convertView.findViewById(R.id.tv_li_schedule_result_departure_delay_time);
        TextView arriveDelayTimeView = (TextView) convertView.findViewById(R.id.tv_li_schedule_result_arrive_delay_time);
        TextView nightView = (TextView) convertView.findViewById(R.id.tv_li_schedule_result_night_label);
        ImageView nightImageView = (ImageView) convertView.findViewById(R.id.iv_li_schedule_result_night_image);
        ImageView magnifyingGlassView = (ImageView) convertView.findViewById(R.id.iv_li_schedule_result_magnifying_glass);

        ImageView notPossibleImage = (ImageView) convertView.findViewById(R.id.iv_schedule_result_connection_not_possible);
        additionalTrainTypeLayout = (LinearLayout) convertView.findViewById(R.id.ll_schedule_connection_train_type_icon_additional);

        trainTypeIcon01 = (ImageView) convertView.findViewById(R.id.iv_schedule_connection_train_type_icon_first);
        trainTypeIcon02 = (ImageView) convertView.findViewById(R.id.iv_schedule_connection_train_type_icon_second);
        trainTypeIcon03 = (ImageView) convertView.findViewById(R.id.iv_schedule_connection_train_type_icon_third);
        LinearLayout llTrainIcons = (LinearLayout) convertView.findViewById(R.id.fl_schedule_result_train_type_icon_view);

        Map<String, TrainIcon> tempMap = new LinkedHashMap<>();

        for (RealTimeInfoLeg realTimeInfoLeg : realTimeConnection.getRealTimeInfoLegs()) {
            boolean isHaveTrainIcon = false;
            if(!realTimeInfoLeg.isTrainLeg()){
                //tempMap.put("Walk", null);
            }else{
                for (TrainIcon trainIcon : (trainIconList == null ? new ArrayList<TrainIcon>() : trainIconList)) {
                    for (String icon : trainIcon.getLinkedTrainBrands()) {
                        if (realTimeInfoLeg != null && realTimeInfoLeg.getTrainType() != null) {
                            if (icon != null && icon.contains(realTimeInfoLeg.getTrainType())) {
                                tempMap.put(realTimeInfoLeg.getTrainType(), trainIcon);
                                isHaveTrainIcon = true;
                            }

                        }
                    }
                }
            }

            if (!isHaveTrainIcon) {
                if(!tempMap.containsKey("DefaultTrainTypeIcon")){
                    tempMap.put("DefaultTrainTypeIcon", null);
                }
            }

        }

        boolean isTalet = Utils.isTablet(activity);

        if(isTalet){
            setTrainIconForTablet(tempMap, llTrainIcons);
        }else{
            setTrainIconForPhone(tempMap);
        }

        magnifyingGlassView.setImageResource(R.drawable.ic_magnifying); // default value
        departureTimeView.setText(DateUtils.FormatToHHMMFromDate(realTimeConnection.getDeparture()));
        arriveTimeView.setText(DateUtils.FormatToHHMMFromDate(realTimeConnection.getArrival()));
        durationTimeView.setText(DateUtils.FormatToHrDate(realTimeConnection.getDuration(), activity));

        if (realTimeConnection.getNumberOfTransfers() == 0) {
            transferView.setText(activity.getString(R.string.general_direct_train));
        } else if (realTimeConnection.getNumberOfTransfers() > 1) {
            transferView.setText(realTimeConnection.getNumberOfTransfers() + " " + activity.getString(R.string.general_transfers));
        } else {
            transferView.setText(realTimeConnection.getNumberOfTransfers() + " " + activity.getString(R.string.general_transfer));
        }

        if (realTimeConnection.isTransferNotPossible() || realTimeConnection.isConnectionNotPossible()) {
            notPossibleView.setVisibility(View.VISIBLE);
            notPossibleView.setVisibility(View.VISIBLE);
            notPossibleImage.setVisibility(View.VISIBLE);
        } else {
            notPossibleView.setVisibility(View.GONE);
            notPossibleView.setVisibility(View.GONE);
            notPossibleImage.setVisibility(View.GONE);
        }

        if (realTimeConnection.getRealTimeDepartureDelta() != null && !"".equals(realTimeConnection.getRealTimeDepartureDelta())) {
            arriveDelayTimeView.setVisibility(View.VISIBLE);
            departureDelayTimeView.setVisibility(View.VISIBLE);
            departureDelayTimeView.setText(realTimeConnection.getRealTimeDepartureDelta());
        } else {
            arriveDelayTimeView.setVisibility(View.GONE);
            departureDelayTimeView.setVisibility(View.GONE);
        }

        if (realTimeConnection.getRealTimeArrivalDelta() != null && !"".equals(realTimeConnection.getRealTimeArrivalDelta())) {
            arriveDelayTimeView.setVisibility(View.VISIBLE);
            departureDelayTimeView.setVisibility(View.VISIBLE);
            arriveDelayTimeView.setText(realTimeConnection.getRealTimeArrivalDelta());
        } else {
            if (departureDelayTimeView.getVisibility() == View.VISIBLE) {
                arriveDelayTimeView.setVisibility(View.VISIBLE);
                departureDelayTimeView.setVisibility(View.VISIBLE);
            } else {
                arriveDelayTimeView.setVisibility(View.GONE);
                departureDelayTimeView.setVisibility(View.GONE);
            }

        }
        //realTimeConnection.setConnectionNotPossible(true);
        if (realTimeConnection.isConnectionNotPossible() && realTimeConnection.isTransferNotPossible()) {
            notPossibleLayout.setVisibility(View.VISIBLE);
            tvNotPossible.setText(activity.getString(R.string.schedule_connection_not_possible));
        } else {
            if (realTimeConnection.isConnectionNotPossible()) {
                notPossibleLayout.setVisibility(View.VISIBLE);
                tvNotPossible.setText(activity.getString(R.string.schedule_connection_not_possible));
            } else if (realTimeConnection.isTransferNotPossible()) {
                notPossibleLayout.setVisibility(View.VISIBLE);
                tvNotPossible.setText(activity.getString(R.string.dossier_detail_transfer_not_possible));
            } else {
                notPossibleLayout.setVisibility(View.GONE);
            }
        }

		/*if((realTimeConnection.getRealTimeDepartureDelta()!=null&&!"".equals(realTimeConnection.getRealTimeDepartureDelta()))||
				(realTimeConnection.getRealTimeArrivalDelta()!=null&&!"".equals(realTimeConnection.getRealTimeArrivalDelta()))||
				realTimeConnection.isConnectionNotPossible()||
				realTimeConnection.isTransferNotPossible()){
			magnifyingGlassView.setImageResource(R.drawable.ic_magnifying_red);
		}*/
        if (realTimeConnection.isTransferNotPossible() || realTimeConnection.isConnectionNotPossible()
                || (realTimeConnection.getHafasMessages() != null && realTimeConnection.getHafasMessages().size() > 0)
                || realTimeConnection.hasLegStatus()
                || (realTimeConnection.getRealTimeDepartureDelta() != null && !realTimeConnection.getRealTimeDepartureDelta().isEmpty())
                || (realTimeConnection.getRealTimeArrivalDelta() != null && !realTimeConnection.getRealTimeArrivalDelta().isEmpty())) {
            magnifyingGlassView.setImageResource(R.drawable.ic_magnifying_red);
        } else {
            magnifyingGlassView.setImageResource(R.drawable.ic_magnifying);
        }

        for (RealTimeInfoLeg realTimeInfoLeg : realTimeConnection.getRealTimeInfoLegs()) {
            if (realTimeInfoLeg != null) {
                if (realTimeInfoLeg.isNightTrain()) {
                    nightView.setVisibility(View.VISIBLE);
                    nightImageView.setVisibility(View.VISIBLE);
                } else {
                    nightView.setVisibility(View.GONE);
                    nightImageView.setVisibility(View.GONE);
                }
                if ((realTimeInfoLeg.getLegStatus() != null && !realTimeInfoLeg.getLegStatus().isEmpty())) {
                    magnifyingGlassView.setImageResource(R.drawable.ic_magnifying_red);
                }
            }
        }
        linearLayout.addView(convertView);
    }

    private void  setTrainIconForTablet(Map<String, TrainIcon> tempMap, LinearLayout llTrainIcons){
        boolean showedTrainIcon = false;
        for (int i = 0; i < tempMap.keySet().toArray().length; i++) {
            TrainIcon tempTrainIcon = tempMap.get(tempMap.keySet().toArray()[i]);
            String trainType = (String) tempMap.keySet().toArray()[i];
            FitImageView imageView = new FitImageView(activity);
            //Log.e("trainType", "trainType=====" + trainType);
            if ((tempTrainIcon == null && !"Walk".equalsIgnoreCase(trainType)) || ImageUtil.getTrainTypeImageId(trainType) == R.drawable.icon_train) {

                if(!showedTrainIcon){
                    LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layout.setMargins(10, 0, 0, 0);
                    imageView.setLayoutParams(layout);
                    imageView.setAdjustViewBounds(true);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setImageResource(R.drawable.icon_train);
                    llTrainIcons.addView(imageView);
                }
                showedTrainIcon = true;
            }else{
                LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layout.setMargins(10, 0, 0, 0);
                imageView.setLayoutParams(layout);
                imageView.setAdjustViewBounds(true);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                String imageUrl = ImageUtil.convertImageExtension(tempTrainIcon.getIcon(Utils.getDeviceDensity(activity)));
                String fullImageUrl = activity.getString(R.string.server_url_host) + imageUrl;
                downloadImage(imageView, fullImageUrl, imageUrl, trainType);
                llTrainIcons.addView(imageView);
            }
        }
    }


    private void setTrainIconForPhone(Map<String, TrainIcon> tempMap){
        if (tempMap.size() > 2) {
            additionalTrainTypeLayout.setVisibility(View.VISIBLE);
        } else {
            additionalTrainTypeLayout.setVisibility(View.GONE);
        }
        //Log.e("tempMap", "tempMap...." + tempMap.keySet().toArray().length);
        for (int i = tempMap.keySet().toArray().length - 1; i >= 0; i--) {
            //Log.e("tempMap", "tempMap...." + String.valueOf(i));
            TrainIcon tempTrainIcon = tempMap.get(tempMap.keySet().toArray()[i]);
            String trainType = (String) tempMap.keySet().toArray()[i];
            if (tempTrainIcon == null) {
                switch (i) {
                    case 0:
                        if("Walk".equalsIgnoreCase(trainType)){
                            trainTypeIcon01.setImageResource(R.drawable.ic_walk);
                        }else{
                            trainTypeIcon01.setImageResource(R.drawable.icon_train);
                        }
                        break;
                    case 1:
                        if("Walk".equalsIgnoreCase(trainType)){
                            trainTypeIcon02.setImageResource(R.drawable.ic_walk);
                        }else{
                            trainTypeIcon02.setImageResource(R.drawable.icon_train);
                        }
                        break;
                    case 2:
                        if("Walk".equalsIgnoreCase(trainType)){
                            trainTypeIcon03.setImageResource(R.drawable.ic_walk);
                        }else{
                            trainTypeIcon03.setImageResource(R.drawable.icon_train);
                        }
                        break;
                }
            } else {
                String imageUrl = ImageUtil.convertImageExtension(tempTrainIcon.getIcon(Utils.getDeviceDensity(activity)));
                String fullImageUrl = activity.getString(R.string.server_url_host) + imageUrl;
                switch (i) {
                    case 0:
                        downloadImage(trainTypeIcon01, fullImageUrl, imageUrl, trainType);
                        break;
                    case 1:
                        downloadImage(trainTypeIcon02, fullImageUrl, imageUrl, trainType);
                        break;
                    case 2:
                        downloadImage(trainTypeIcon03, fullImageUrl, imageUrl, trainType);
                        break;
                }
            }
        }
    }

    private void downloadImage(final ImageView imageView, String fullImageUrl, String imageUrl, final String defaultTrainType){

        if(ImageUtil.getTrainTypeImageId(defaultTrainType) == R.drawable.icon_train){
            AsyncImageLoader.getInstance().loadDrawable(activity, fullImageUrl, imageUrl,
                    imageView, null, new AsyncImageLoader.ImageCallback() {
                        public void imageLoaded(Bitmap imageDrawable, String imageUrl, View view) {
                            if(imageDrawable == null){
                                ((ImageView) view).setImageResource(ImageUtil.getTrainTypeImageId(defaultTrainType));
                            }else{
                                ((ImageView) view).setImageBitmap(imageDrawable);
                            }
                        }
                    });
        }else{
            imageView.setImageResource(ImageUtil.getTrainTypeImageId(defaultTrainType));
        }
    }
}
