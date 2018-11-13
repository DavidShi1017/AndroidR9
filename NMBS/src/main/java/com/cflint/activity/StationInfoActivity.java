package com.cflint.activity;


import java.util.List;

import org.apache.commons.lang.StringUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.cflint.R;
import com.cflint.adapter.FacilitiesAdapter;
import com.cflint.adapter.ParkingAdapter;
import com.cflint.adapter.TextBlockAdapter;
import com.cflint.application.NMBSApplication;
import com.cflint.model.Parking;
import com.cflint.model.StationFacilitiesBlock;
import com.cflint.model.StationInformationResult;
import com.cflint.model.StationTextBlock;
import com.cflint.services.IAssistantService;
import com.cflint.services.ISettingService;
import com.cflint.util.LocaleChangedUtils;


/**
 * Activity used for displaying the UI element, user can do some inner behavior
 * with this.
 * 
 * User can select a category to see the details.
 */
public class StationInfoActivity extends BaseActivity {
	//private final static String TAG = StationInfoActivity.class.getSimpleName();

	public static final String STATION_DETAIL_SERIALIZABLE_KEY = "station_detail_key";
	private TextView stationNameTextView, stationAddressRoadTextView, 
					stationAddressCityTextView, stationAddressCountryTextView, facilitiesTextView,
					addressTextView;

	public static final String STATION_NAME_SERIALIZABLE_KEY = "station_name_key";
			
	private StationInformationResult station;
	private LinearLayoutForListView facilityListView, parkingsListView, textblocksListView;
	private FacilitiesAdapter facilitiesAdapter;
	private View rootLayout, facilitiesLine, parkingsLine, parkingsTextView, addressLine;
	private Button pdfButton;
	private TextBlockAdapter textBlockAdapter;
	private ParkingAdapter parkingAdapter;
	private IAssistantService assistantService;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		LocaleChangedUtils.initLanguageSettings(getApplicationContext());// Setting
																			// application
																			// language
		setContentView(R.layout.station_info_view);

		//Log.e(TAG, "onCreate");
		

		assistantService = ((NMBSApplication) getApplication()).getAssistantService();

		getIntentData();
		//StationAsyncTask stationAsyncTask = new StationAsyncTask();
		//stationAsyncTask.execute((Void)null);
		
		bindAllViewElements();
		setViewStateBasedOnModel();
		//showStation();
		bindAllListeners();

	}

	// Bind all view elements
	private void bindAllViewElements() {
		
		rootLayout = findViewById(R.id.RootLayout);
		rootLayout.setVisibility(View.VISIBLE);
		stationNameTextView = (TextView)findViewById(R.id.station_info_view_station_name_textview);
		
		
		addressTextView = (TextView)findViewById(R.id.tation_info_view_station_address_TextView);
		stationAddressRoadTextView = (TextView)findViewById(R.id.station_info_view_station_address_road_TextView);
		stationAddressCityTextView = (TextView)findViewById(R.id.station_info_view_station_address_city_TextView);
				
		stationAddressCountryTextView = (TextView)findViewById(R.id.station_info_view_station_address_country_TextView);	
		addressLine = findViewById(R.id.station_info_view_station_address_line);
		
		
		
		facilitiesTextView = (TextView)findViewById(R.id.tation_info_view_station_facilities_TextView);
		
		facilityListView = (LinearLayoutForListView)findViewById(R.id.station_info_view_station_facilities_list);
		facilitiesLine = findViewById(R.id.station_info_view_station_facilities_line);
		
		parkingsLine = findViewById(R.id.station_info_view_station_parkings_line);
		parkingsTextView = (TextView)findViewById(R.id.tation_info_view_station_parkings_TextView);
		parkingsListView = (LinearLayoutForListView)findViewById(R.id.station_info_view_station_parkings_list);
		
		textblocksListView = (LinearLayoutForListView)findViewById(R.id.station_info_view_station_textblocks_list);
		pdfButton = (Button) findViewById(R.id.station_info_view_pdf_btn);

		bindAllListeners();
	}

	// Bind all listeners
	private void bindAllListeners() {
		pdfButton.setOnClickListener(pdfButtonOnClickListener);
	}
	
	private OnClickListener pdfButtonOnClickListener = new OnClickListener() {

		public void onClick(View v) { // Called whe
			//assistantService.showStationFloorPlan(station.getCode(), settingService.getCurrentLanguage());
			startActivity(StationFloorActivity.createIntent(StationInfoActivity.this, station.getCode()));
			
		}
	};

	// Show the address information of station
/*	private void showStation() {
		if (myState.stationDetailResponse != null) {
			if (myState.stationDetailResponse.getStationDetail() != null) {
				setViewStateBasedOnModel(myState.stationDetailResponse
						.getStationDetail());				
			}
		}
	}*/

	// Show the address information of station
	private void setViewStateBasedOnModel() {
		if (station != null) {
			
			stationNameTextView.setText(station.getName());
			
			String street = "";
			String number = "";
			String zip = "";
			String city = "";
			String country = "";
			if (station.getAddress() != null) {
				if (station.getAddress().getStreet() != null && !StringUtils.equalsIgnoreCase("", station.getAddress().getStreet())) {
					street = station.getAddress().getStreet();
				}
				if (station.getAddress().getNumber() != null && !StringUtils.equalsIgnoreCase("", station.getAddress().getNumber())) {
					number = station.getAddress().getNumber();
				}
				if (station.getAddress().getZip() != null && !StringUtils.equalsIgnoreCase("", station.getAddress().getZip())) {
					zip = station.getAddress().getZip();
				}
				if (station.getAddress().getCity() != null && !StringUtils.equalsIgnoreCase("", station.getAddress().getCity())) {
					city = station.getAddress().getCity();
				}
				if (station.getAddress().getCountry() != null && !StringUtils.equalsIgnoreCase("", station.getAddress().getCountry())) {
					country = station.getAddress().getCountry();
				}
			}

			if (StringUtils.equalsIgnoreCase("", street)
					&& StringUtils.equalsIgnoreCase("", number)
					&& StringUtils.equalsIgnoreCase("", zip)
					&& StringUtils.equalsIgnoreCase("", city)
					&& StringUtils.equalsIgnoreCase("", country)) {
				addressTextView.setVisibility(View.GONE);
				stationAddressRoadTextView.setVisibility(View.GONE);
				stationAddressCityTextView.setVisibility(View.GONE);
				stationAddressCountryTextView.setVisibility(View.GONE);
				addressLine.setVisibility(View.GONE);
			}else {
				
				stationAddressRoadTextView.setText(street + " " + number);
				stationAddressCityTextView.setText(zip + " " + city);	
				stationAddressCountryTextView.setText(country);
			}
			
			
			StationFacilitiesBlock facilitiesBlock = station.getFacilitiesBlock();
			if (facilitiesBlock != null && facilitiesBlock.getFacilities() != null && facilitiesBlock.getFacilities().size() > 0) {
				facilitiesAdapter = new FacilitiesAdapter(this.getApplicationContext(), R.layout.facilities_adapter, facilitiesBlock.getFacilities());
				facilityListView.setAdapter(facilitiesAdapter);
			}else{
				facilitiesTextView.setVisibility(View.GONE);
				facilityListView.setVisibility(View.GONE);
				facilitiesLine.setVisibility(View.GONE);
			}		
			setViewStateBasedOnModelForParkings(station);
			setViewStateBasedOnModelForTextblocks(station);
			shouPdfButton();
		}
	}
	
	private void shouPdfButton(){
		String existStationCode = assistantService.getExistStationCode(station.getCode());
		if (existStationCode != null) {
			pdfButton.setVisibility(View.VISIBLE);
		}
		
	}
	
	private void setViewStateBasedOnModelForParkings(StationInformationResult station){
		
		
		List<Parking> parkings = station.getParkings(); 
		
		if (parkings != null && parkings.size() > 0) {
			parkingAdapter = new ParkingAdapter(this.getApplicationContext(), R.layout.parking_adapter, parkings);
			parkingsListView.setAdapter(parkingAdapter);
		}else{
			parkingsTextView.setVisibility(View.GONE);
			parkingsListView.setVisibility(View.GONE);
			parkingsLine.setVisibility(View.GONE);
		}				
	}

	private void setViewStateBasedOnModelForTextblocks(StationInformationResult station){
		
		List<StationTextBlock> stationTextBlocks = station.getTextBlocks();
		
		if (stationTextBlocks != null && stationTextBlocks.size() > 0) {
			textBlockAdapter = new TextBlockAdapter(this.getApplicationContext(), R.layout.textblock_adapter, stationTextBlocks);
			textblocksListView.setAdapter(textBlockAdapter);
		}else{
			//parkingsTextView.setVisibility(View.GONE);
			textblocksListView.setVisibility(View.GONE);
			//parkingsLine.setVisibility(View.GONE);
		}	
	}
	// Get the intent data by special SerializableKey
	private void getIntentData() {

		station = (StationInformationResult)getIntent().getSerializableExtra(STATION_NAME_SERIALIZABLE_KEY);
		
	}



	/**
	 * Utility method for every Activity who needs to start this Activity. *
	 * This keeps the construction of the Intent and the corresponding
	 * parameters also in this class.
	 * 
	 * @param context
	 * @param stationDetailResponse
	 *            StationDetailResponse
	 */
	public static Intent createIntent(Context context, StationInformationResult stations) {
		Intent intent = new Intent(context, StationInfoActivity.class);
		intent.putExtra(STATION_NAME_SERIALIZABLE_KEY, stations);
		return intent;
	}

	@Override
	protected void onPause() {
		//Log.d(TAG, "onPause");
		super.onPause();
	}

	@Override
	protected void onResume() {
		//Log.d(TAG, "onResume");
		super.onResume();
	}

}
