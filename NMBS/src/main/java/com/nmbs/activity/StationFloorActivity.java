package com.nmbs.activity;


import java.io.InputStream;

import android.content.Context;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;

import android.view.Window;


import com.nmbs.R;

import com.nmbs.application.NMBSApplication;

import com.nmbs.services.IAssistantService;
import com.nmbs.services.ISettingService;
import com.nmbs.util.LocaleChangedUtils;

/**
 * Activity used for displaying the UI element, user can do some interbehavior
 * with this.
 * 
 * @author: David
 */
public class StationFloorActivity extends BaseActivity {

	private TouchImageView barcodeImageView;
	private static final String INTENT_KEY_STRING = "stationCode";
	private String stationCode;
	private IAssistantService assistantService;
	private ISettingService settingService;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		LocaleChangedUtils.initLanguageSettings(getApplicationContext());// Setting
																			// application
																			// language
		setContentView(R.layout.station_floor_view);
		settingService = ((NMBSApplication) getApplication()).getSettingService();

		assistantService = ((NMBSApplication) getApplication()).getAssistantService();
		bindAllViewsElement();

		setViewStateBasedOnModel();

	}

	/**
	 * Utility method for every Activity who needs to start this Activity.
	 * 
	 * @param context
	 * @param
	 */
	public static Intent createIntent(Context context, String stationCode) {
		Intent intent = new Intent(context, StationFloorActivity.class);
		intent.putExtra(INTENT_KEY_STRING, stationCode);
		return intent;
	}
	
	// Bind all view elements
	private void bindAllViewsElement(){
		barcodeImageView = (TouchImageView) findViewById(R.id.barcode_for_person_view_image);
		//barcodeImageView.setMaxZoom(8f);			
	}
	private void setViewStateBasedOnModel(){
		stationCode = getIntent().getStringExtra(INTENT_KEY_STRING);
		InputStream isInputStream = assistantService.getStationFloorPlan(stationCode, settingService.getCurrentLanguagesKey());
		Bitmap mBitmap = BitmapFactory.decodeStream(isInputStream);
		
		barcodeImageView.setImageBitmap(mBitmap);
		//barcodeImageView.setMaxZoom(8f);			
	}
	

	public void onSaveInstanceState(Bundle outState) {
    	
    
    	//outState.putSerializable("OfferService", offerService.getOfferResponse());
    	super.onSaveInstanceState(outState);
	}
}
