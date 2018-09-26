package com.nmbs.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nmbs.R;
import com.nmbs.activities.view.FitImageView;
import com.nmbs.async.AsyncImageLoader;
import com.nmbs.model.StationInformation;
import com.nmbs.util.ImageUtil;

import java.util.List;


public class StationInfoDetailInformationAdapter{

	private LayoutInflater layoutInflater;
	private Activity activity;
	private List<StationInformation> stationInformationList;
	public StationInfoDetailInformationAdapter(Activity activity, List<StationInformation> objects){
		this.activity = activity;
		this.stationInformationList = objects;
		this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void getInformationView(int position, LinearLayout linearLayout) {
		View convertView = null;
		if(convertView == null)
			convertView = layoutInflater.inflate(R.layout.station_info_detail_information_item_view, null);
			
		TextView title = (TextView) convertView.findViewById(R.id.tv_station_info_detail_information_title);
		TextView content = (TextView) convertView.findViewById(R.id.tv_station_info_detail_information_content);
		FitImageView imageView = (FitImageView) convertView.findViewById(R.id.iv_station_info_detail_information_picture);

		if (stationInformationList.get(position).getImage().trim().equals("") || stationInformationList.get(position).getImage() == null) {
			imageView.setVisibility(View.GONE);
		} else {
			imageView.setVisibility(View.VISIBLE);
			String imageUrl = ImageUtil.convertImageExtension(stationInformationList.get(position).getImage());
			String fullImageUrl = imageUrl;
			//Log.e("imageUrl", "imageUrl------" + imageUrl);
			AsyncImageLoader.getInstance().loadDrawable(
					activity, fullImageUrl, imageUrl,
					imageView, null, new AsyncImageLoader.ImageCallback() {

						public void imageLoaded(Bitmap imageDrawable,
												String imageUrl, View view) {
							((ImageView) view).setImageBitmap(imageDrawable);
						}
					});
		}
		title.setText(stationInformationList.get(position).getTitle());
		content.setText(stationInformationList.get(position).getContent());
		linearLayout.addView(convertView);
	}
}
