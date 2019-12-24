package com.nmbs.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nmbs.R;
import com.nmbs.activities.view.DialogAlertError;
import com.nmbs.activities.view.DialogMyOptions;
import com.nmbs.activity.BaseActivity;
import com.nmbs.application.NMBSApplication;
import com.nmbs.async.ProfileInfoAsyncTask;
import com.nmbs.dataaccess.restservice.IStationBoardDataService;
import com.nmbs.dataaccess.restservice.impl.StationBoardDataService;
import com.nmbs.model.GeneralSetting;
import com.nmbs.model.Station;
import com.nmbs.model.StationBoardLastQuery;
import com.nmbs.model.StationBoardQuery;
import com.nmbs.model.StationBoardQuery.StationBoardFeedbackTypes;
import com.nmbs.model.Train;
import com.nmbs.model.TravelRequest.TimePreference;
import com.nmbs.services.IAssistantService;
import com.nmbs.services.IClickToCallService;
import com.nmbs.services.IMasterService;
import com.nmbs.services.IMessageService;
import com.nmbs.services.impl.SettingService;
import com.nmbs.util.DateUtils;
import com.nmbs.util.FunctionConfig;
import com.nmbs.util.GoogleAnalyticsUtil;
import com.nmbs.util.MenuUtil;
import com.nmbs.util.NetworkUtils;
import com.nmbs.util.TrackerConstant;
import com.nmbs.util.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Activity used for displaying the UI element, user can do some inner behavior with this.
 * 
 * User can select a category to see the details.
 */
public class StationBoardActivity extends BaseActivity {
	private final static String TAG = StationBoardActivity.class.getSimpleName();
	public  static final int REQUEST_STATION = 0;
	public  static final int REQUEST_DATETIME = 1;
	private TextView tvDepartures, tvArrivals, tvDatetime, tvError, tvStation, tvStationSynoniem;
	private RelativeLayout rlStation;
	private StationBoardQuery stationBoardQuery;
	private List<Train> trains = new ArrayList<Train>();
	private String rCode, name, synoniem;
	private Date stationTime = null;
	private StationBoardQuery selectedStationQuery;
	private TimePreference timePreference = TimePreference.DEPARTURE;
	private LinearLayout llDate;
	private String dateFormatted;
	private IAssistantService assitantService;
	private SettingService settingService;
	private TextView tvMenuTicketCount, tvMenuRealtimeCount, tvMenuMessageCount;
	private IMasterService masterService;
	private IMessageService messageService;
	private IClickToCallService clickToCallService;

	private DrawerLayout mDrawerLayout;
	private LinearLayout mDrawerList;
	private Uri uri;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Utils.setToolBarStyle(this);
		settingService = ((NMBSApplication) getApplication()).getSettingService();
		//settingService.initLanguageSettings();
		messageService = ((NMBSApplication) getApplication()).getMessageService();
		masterService = ((NMBSApplication) getApplication()).getMasterService();
		clickToCallService = ((NMBSApplication) getApplication()).getClickToCallService();
		setContentView(R.layout.activity_stationboard);
		bindService();
		bindAllViewElements();

		getIntentValue();
		initStationBoard();
		GoogleAnalyticsUtil.getInstance().sendScreen(StationBoardActivity.this, TrackerConstant.STATIONBOARD_TravelWish);

	}

	public void setViewStateBasedOnModel(){

	}

	private void bindService() {
		assitantService = ((NMBSApplication) getApplication()).getAssistantService();
	}

	public void departures(View view){
		timePreference = TimePreference.DEPARTURE;
		setDeparturesView();
	}

	public void arrivals(View view){
		timePreference = TimePreference.ARRIVAL;
		setArrivalsView();
	}

	private void setArrivalsView(){
		tvDepartures.setBackgroundColor(getResources().getColor(R.color.background_secondaryaction));
		tvArrivals.setBackgroundColor(getResources().getColor(R.color.background_group_title));
	}

	private void setDeparturesView(){
		tvDepartures.setBackgroundColor(getResources().getColor(R.color.background_group_title));
		tvArrivals.setBackgroundColor(getResources().getColor(R.color.background_secondaryaction));
	}

	public void selectStation(View view){
		startActivityForResult(StationsActivity.createIntent(StationBoardActivity.this), REQUEST_STATION);
	}
	public void selectDatetime(View view){
		startActivityForResult(DateTimeActivity.createIntent(StationBoardActivity.this, stationTime), REQUEST_DATETIME);
	}
	public void nowDate(View view){
		Date date = new Date();
		dateFormatted = DateUtils.dateTimeToString(date, DateUtils.DATE_FORMAT_MONTH_AD);
		setTvDatetime();
	}


	private void setTvDatetime(){
		stationTime = DateUtils.stringToDateTime(dateFormatted, DateUtils.DATE_FORMAT_MONTH_AD);
		tvDatetime.setText(dateFormatted);
		llDate.setBackground(getResources().getDrawable(R.drawable.group_default));
		tvDatetime.setTextColor(getResources().getColor(R.color.textcolor_thirdaction));
	}
	private void bindAllViewElements(){
		tvDepartures = (TextView) findViewById(R.id.tv_departures);
		tvArrivals = (TextView) findViewById(R.id.tv_arrivals);
		rlStation = (RelativeLayout) findViewById(R.id.rl_station);
		tvDatetime = (TextView) findViewById(R.id.tv_datetime);
		tvError = (TextView) findViewById(R.id.tv_error);
		tvStation = (TextView) findViewById(R.id.tv_station);
		tvStationSynoniem = (TextView) findViewById(R.id.tv_station_synoniem);
		llDate = (LinearLayout) findViewById(R.id.ll_date);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (LinearLayout) findViewById(R.id.ll_left_menus);
	}

	private void initStationBoard(){
		StationBoardLastQuery lastQuery = assitantService.getLastQuery();
		if(lastQuery != null){

			tvStation.setTextColor(getResources().getColor(R.color.textcolor_thirdaction));
			tvStation.setText(lastQuery.getName());
			rCode = lastQuery.getStationRCode();
			name = lastQuery.getName();
			if (lastQuery.getSynoniem() != null && !lastQuery.getSynoniem().isEmpty()){
				tvStationSynoniem.setVisibility(View.VISIBLE);
				tvStationSynoniem.setText(lastQuery.getSynoniem());
			}
			Date date = new Date();
			if (lastQuery.getDateTime().after(date)){
				dateFormatted = DateUtils.dateTimeToString(lastQuery.getDateTime(), DateUtils.DATE_FORMAT_MONTH_AD);
				stationTime = lastQuery.getDateTime();
			}else {
				dateFormatted = DateUtils.dateTimeToString(date, DateUtils.DATE_FORMAT_MONTH_AD);
				stationTime = date;
			}
			if (lastQuery.getTimePreference() == TimePreference.ARRIVAL){
				setArrivalsView();
			}else{
				setDeparturesView();
			}
			setTvDatetime();
		}
	}

	public void showStationboardResult(View view){
		stationBoardQuery = new StationBoardQuery(rCode, stationTime, timePreference, trains);
		stationBoardQuery.setName(name);
		stationBoardQuery.setSynoniem(synoniem);
		StationBoardFeedbackTypes isValidate = stationBoardQuery.validate();
		if(StationBoardFeedbackTypes.CORRECT == isValidate){
			startActivity(com.nmbs.activities.StationboardSearchResultActivity.createIntent(StationBoardActivity.this, stationBoardQuery, true));
			IStationBoardDataService stationBoardDataService = new StationBoardDataService();
			stationBoardDataService.saveStationBoardLastQuery(stationBoardQuery, getApplicationContext());
			tvError.setVisibility(View.GONE);
		}else{
			validationFeedback(isValidate);
		}
	}


	public void validationFeedback(StationBoardFeedbackTypes enumValide) {
		tvError.setVisibility(View.VISIBLE);
		switch (enumValide) {
		case WARNINGSTATION:
			rlStation.setBackground(getResources().getDrawable(R.drawable.group_error));
			tvError.setText(getString(R.string.general_required_fields));
			//Toast.makeText(this, R.string.alert_stationboard_no_station, Toast.LENGTH_LONG).show();
			break;
		case WARNINGDATETIME:

			llDate.setBackground(getResources().getDrawable(R.drawable.group_error));
			tvError.setText(getString(R.string.general_required_fields));
			//Toast.makeText(this, R.string.alert_stationboard_no_datetime, Toast.LENGTH_LONG).show();
			break;
		default:
			tvError.setVisibility(View.GONE);
			break;
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_DATETIME:
			if(data != null){
				dateFormatted = (String) data.getSerializableExtra(DateTimeActivity.PRMA_DATE_FORMATTED);
				tvError.setVisibility(View.GONE);
				setTvDatetime();
				//Log.d(TAG, "onActivityResult, get dateFormatted from DateTimeActivity, the date is ::::" + dateFormatted);
			}
			break;
		case REQUEST_STATION:
			if(data != null){
				Station station = (Station)data.getSerializableExtra(StationsActivity.PRMA_STATION);
				if (station != null){
					rCode = station.getCode();
					name = station.getName();
					//Log.d(TAG, "onActivityResult, get station from StationsActivity, the station is ::::" + station.getName());
					tvStation.setText(station.getName());
					tvStation.setTextColor(getResources().getColor(R.color.textcolor_thirdaction));
					if (station.getSynoniem() != null && !station.getSynoniem().isEmpty()){
						tvStationSynoniem.setText(station.getSynoniem());
						synoniem = station.getSynoniem();
						tvStationSynoniem.setVisibility(View.VISIBLE);
						rlStation.setBackground(getResources().getDrawable(R.drawable.group_default));
						if(tvError.getText().toString().equalsIgnoreCase(getString(R.string.alert_stationboard_no_station))){
							tvError.setVisibility(View.GONE);
						}

						//Log.d(TAG, "onActivityResult, get station from StationsActivity, the station is ::::" + station.getName());
					}else{
						tvStationSynoniem.setVisibility(View.GONE);
					}
				}
			}
			break;
		default:
			break;
		}
	}
	
	public void getIntentValue(){
		uri = (Uri) getIntent().getParcelableExtra("uri");
	}
	
    public static Intent createIntent(Context context,Uri uri){
		Intent intent = new Intent(context, StationBoardActivity.class);
		//intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

		intent.putExtra("uri", uri);
		return intent;
	}
    
    @Override
	protected void onPause() {
		//Log.d(TAG, "onPause");
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		bindAllViewElements();
		setViewStateBasedOnModel();
		if(StationboardSearchResultActivity.hasError){
			DialogAlertError dialogError = new DialogAlertError(this, getResources().getString(R.string.general_information),
					getResources().getString(R.string.general_server_unavailable));
			dialogError.show();
			StationboardSearchResultActivity.hasError = false;
		}
	}


	public void messages(View view) {
		MenuUtil.messages(this, mDrawerLayout, mDrawerList, messageService);
	}

	public void test(View view){
		startActivity(TestActivity.createIntent(this));
		finish();
	}

	public void stationBoard(View view){
		mDrawerLayout.closeDrawer(GravityCompat.END);

	}

	public void stations(View view){
		MenuUtil.stations(this, mDrawerLayout, mDrawerList);
	}

	public void realtimeAlerts(View view){
		MenuUtil.realtimeAlerts(this, mDrawerLayout, mDrawerList);
	}

	public void bookTicktes(View view){
		MenuUtil.bookTicktes(this, mDrawerLayout, mDrawerList, masterService);
	}

	public void lowestFares(View view){
		MenuUtil.lowestFares(this, mDrawerLayout, mDrawerList, masterService);
	}
	public void trainschedules(View view){
		MenuUtil.trainschedules(this, mDrawerLayout, mDrawerList);
	}

	public void settings(View view){
		MenuUtil.settings(this, mDrawerLayout, mDrawerList);
	}

	public void about(View view){
		MenuUtil.about(this, mDrawerLayout, mDrawerList);
	}
	public void myTickets(View view){
		MenuUtil.myTickets(this, mDrawerLayout, mDrawerList);
	}

	public void uploadTickets(View view){
		MenuUtil.uploadTickets(this, mDrawerLayout, mDrawerList);
	}
	public void myOption(View view){
		//startActivity(LoginActivity.createIntent(MainActivity.this));
		MenuUtil.myOption(this, mDrawerLayout, mDrawerList, masterService);
	}

	public void loginOrManage(View view){
		//startActivity(LoginActivity.createIntent(MainActivity.this));
		MenuUtil.loginOrManage(this, mDrawerLayout, mDrawerList, masterService);
	}

	public void clickMenu(View view) {
		//startActivity(MenuActivity.createIntent(this, ticketCount, realtimeCount, messateCount));
		MenuUtil.clickMenu(this);
		mDrawerLayout.openDrawer(GravityCompat.END);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (KeyEvent.KEYCODE_BACK == keyCode) {
			if (uri != null) {
				Intent myIntent = new Intent(this.getApplicationContext().getApplicationContext(), MainActivity.class);
				myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	public void close(View view){
		finish();
	}
}
