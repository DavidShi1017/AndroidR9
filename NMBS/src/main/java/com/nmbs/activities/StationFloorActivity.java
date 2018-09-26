package com.nmbs.activities;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.view.Window;


import com.nmbs.R;
import com.nmbs.activity.BaseActivity;

import com.nmbs.application.NMBSApplication;

import com.nmbs.model.StationInfo;
import com.nmbs.services.IAssistantService;

import com.nmbs.services.impl.SettingService;
import com.nmbs.util.FileManager;

import com.nmbs.util.LocaleChangedUtils;
import com.nmbs.util.Utils;

import java.io.File;

import java.security.NoSuchAlgorithmException;

/**
 * Activity used for displaying the UI element, user can do some interbehavior
 * with this.
 * 
 * @author: David
 */
public class StationFloorActivity extends BaseActivity {

	private static final String INTENT_KEY_STATION = "StationInfo";

	private IAssistantService assistantService;
	private SettingService settingService;
	private StationInfo stationInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.setToolBarStyle(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		LocaleChangedUtils.initLanguageSettings(getApplicationContext());// Setting
		settingService = ((NMBSApplication) getApplication()).getSettingService();
		//settingService.initLanguageSettings();
		assistantService = ((NMBSApplication) getApplication()).getAssistantService();													// application
		setContentView(R.layout.activity_station_floor);

		bindAllViewsElement();

		setViewStateBasedOnModel();

	}

	/**
	 * Utility method for every Activity who needs to start this Activity.
	 * 
	 * @param context
	 * @param stationInfo
	 */
	public static Intent createIntent(Context context, StationInfo stationInfo) {
		Intent intent = new Intent(context, StationFloorActivity.class);
		intent.putExtra(INTENT_KEY_STATION, stationInfo);
		return intent;
	}
	
	// Bind all view elements
	private void bindAllViewsElement(){

		//barcodeImageView.setMaxZoom(8f);			
	}
	private void setViewStateBasedOnModel(){
		stationInfo = (StationInfo) getIntent().getSerializableExtra(INTENT_KEY_STATION);

		if(stationInfo != null){
			try {
				String pdf = stationInfo.getFloorPlanDownloadURL();
				int index = pdf.indexOf(".");
				pdf = pdf.substring(0, index);
				String fileName = Utils.sha1(pdf) + ".pdf";
				BitmapFactory.Options options = new BitmapFactory.Options();
				//options.inTempStorage = new byte[16 * 1024];
				options.inSampleSize = 1;
				//options.inJustDecodeBounds = true;
				//Log.e("mBitmap", "fileName..." + fileName);
				File file = FileManager.getInstance().getExternalStoragePrivateFile(getApplicationContext(), "StationFloor", fileName);
				if (file.exists()) {
					Uri path = Uri.fromFile(file);
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(path, "application/pdf");
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

					try {
						startActivity(intent);
					} catch (ActivityNotFoundException e) {
						//Log.e(TAG, "ActivityNotFoundException, Open PDF Failed", e);
					}
				} else {

					//Toast.makeText(applicationContext, applicationContext.getString(R.string.alert_status_service_not_available), Toast.LENGTH_SHORT).show();
				}
				/*FileManager.getInstance().getExternalStoragePrivateFilePath(getApplicationContext(), "StationFloor", fileName);
				Bitmap mBitmap = BitmapFactory.decodeFile(FileManager.getInstance().getExternalStoragePrivateFilePath(getApplicationContext(), "StationFloor", fileName), options);

				Log.e("mBitmap", "mBitmap..." + mBitmap);
				barcodeImageView.setImageBitmap(mBitmap);*/
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}

		//barcodeImageView.setMaxZoom(8f);			
	}
	

	public void onSaveInstanceState(Bundle outState) {
    	
    
    	//outState.putSerializable("OfferService", offerService.getOfferResponse());
    	super.onSaveInstanceState(outState);
	}
}
