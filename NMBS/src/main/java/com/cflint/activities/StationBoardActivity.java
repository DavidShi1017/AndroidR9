package com.cflint.activities;

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

import com.cflint.R;
import com.cflint.activities.view.DialogAlertError;
import com.cflint.activities.view.DialogMyOptions;
import com.cflint.activity.BaseActivity;
import com.cflint.application.NMBSApplication;
import com.cflint.async.ProfileInfoAsyncTask;
import com.cflint.dataaccess.restservice.IStationBoardDataService;
import com.cflint.dataaccess.restservice.impl.StationBoardDataService;
import com.cflint.model.GeneralSetting;
import com.cflint.model.Station;
import com.cflint.model.StationBoardLastQuery;
import com.cflint.model.StationBoardQuery;
import com.cflint.model.StationBoardQuery.StationBoardFeedbackTypes;
import com.cflint.model.Train;
import com.cflint.model.TravelRequest.TimePreference;
import com.cflint.services.IAssistantService;
import com.cflint.services.IClickToCallService;
import com.cflint.services.IMasterService;
import com.cflint.services.IMessageService;
import com.cflint.services.impl.SettingService;
import com.cflint.util.DateUtils;
import com.cflint.util.FunctionConfig;
import com.cflint.util.GoogleAnalyticsUtil;
import com.cflint.util.NetworkUtils;
import com.cflint.util.TrackerConstant;
import com.cflint.util.Utils;

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
			startActivity(com.cflint.activities.StationboardSearchResultActivity.createIntent(StationBoardActivity.this, stationBoardQuery, true));
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
		isGoto = true;
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		if(drawerOpen){
			mDrawerLayout.closeDrawer(GravityCompat.END);
			mDrawerLayout.setDrawerListener(
					new DrawerLayout.SimpleDrawerListener() {
						@Override
						public void onDrawerClosed(View drawerView) {
							super.onDrawerClosed(drawerView);
							if(isGoto){
								startActivity(com.cflint.activities.MessageActivity.createIntent(StationBoardActivity.this,
										messageService.getMessageResponse()));
								isGoto = false;
								finish();
							}
						}
					});
		}
	}

	public void test(View view){
		startActivity(TestActivity.createIntent(this));
		finish();
	}

	public void stationBoard(View view){
		mDrawerLayout.closeDrawer(GravityCompat.END);

	}

	public void stations(View view){

		isGoto = true;
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		if(drawerOpen){
			mDrawerLayout.closeDrawer(GravityCompat.END);
			mDrawerLayout.setDrawerListener(
					new DrawerLayout.SimpleDrawerListener() {
						@Override
						public void onDrawerClosed(View drawerView) {
							super.onDrawerClosed(drawerView);
							if(isGoto){
								startActivity(com.cflint.activities.StationInfoActivity.createIntent(StationBoardActivity.this));
								isGoto = false;
								finish();
							}
						}
					});
		}
		//startActivity(com.nmbs.activities.CallCenterActivity.createIntent(MainActivity.this,0,null,null));
	}

	public void realtimeAlerts(View view){

		isGoto = true;
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		if(drawerOpen){
			mDrawerLayout.closeDrawer(GravityCompat.END);
			mDrawerLayout.setDrawerListener(
					new DrawerLayout.SimpleDrawerListener() {
						@Override
						public void onDrawerClosed(View drawerView) {
							super.onDrawerClosed(drawerView);
							if(isGoto){
								startActivity(com.cflint.activities.AlertActivity.createIntent(StationBoardActivity.this));
								isGoto = false;
								finish();
							}
						}
					});
		}
	}

	public void bookTicktes(View view){
		GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.HOME,TrackerConstant.HOME_BOOK_YOUR_TICKET,TrackerConstant.HOME_BOOK_YOUR_TICKET_LABEL);
		final GeneralSetting generalSetting = masterService.loadGeneralSetting();

		isGoto = true;
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		if(drawerOpen){
			mDrawerLayout.closeDrawer(GravityCompat.END);
			mDrawerLayout.setDrawerListener(
					new DrawerLayout.SimpleDrawerListener() {
						@Override
						public void onDrawerClosed(View drawerView) {
							super.onDrawerClosed(drawerView);
							if(isGoto){
								if(generalSetting != null && generalSetting.getBookingUrl() != null && !generalSetting.getBookingUrl().isEmpty()){
									//Utils.openProwser(StationBoardActivity.this, generalSetting.getBookingUrl(), clickToCallService);
									if(NetworkUtils.isOnline(StationBoardActivity.this)) {
										GoogleAnalyticsUtil.getInstance().sendScreen(StationBoardActivity.this, TrackerConstant.BOOKING);
										startActivity(WebViewActivity.createIntent(StationBoardActivity.this,
												Utils.getUrl(generalSetting.getBookingUrl()), WebViewActivity.BOOKING_FLOW, ""));
									}
								}
								isGoto = false;
								finish();
							}
						}
					});
		}
	}

	public void lowestFares(View view){
		GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.HOME,TrackerConstant.HOME_SELECT_LOWESTFARES,TrackerConstant.HOME_SELECT_LOWESTFARES_LABEL);
		final GeneralSetting generalSetting = masterService.loadGeneralSetting();

		isGoto = true;
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		if(drawerOpen){
			mDrawerLayout.closeDrawer(GravityCompat.END);
			mDrawerLayout.setDrawerListener(
					new DrawerLayout.SimpleDrawerListener() {
						@Override
						public void onDrawerClosed(View drawerView) {
							super.onDrawerClosed(drawerView);
							if(isGoto){
								if(generalSetting != null && generalSetting.getLffUrl()!= null && !generalSetting.getLffUrl().isEmpty()){
									//Utils.openProwser(StationBoardActivity.this, generalSetting.getLffUrl(), clickToCallService);
									if(NetworkUtils.isOnline(StationBoardActivity.this)) {
										GoogleAnalyticsUtil.getInstance().sendScreen(StationBoardActivity.this, TrackerConstant.LLF);
										startActivity(WebViewActivity.createIntent(StationBoardActivity.this,
												Utils.getUrl(generalSetting.getLffUrl()), WebViewActivity.BOOKING_FLOW, ""));
									}
								}
								isGoto = false;
								finish();
							}
						}
					});
		}
	}
	public void trainschedules(View view){
		isGoto = true;
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		if(drawerOpen){
			mDrawerLayout.closeDrawer(GravityCompat.END);
			mDrawerLayout.setDrawerListener(
					new DrawerLayout.SimpleDrawerListener() {
						@Override
						public void onDrawerClosed(View drawerView) {
							super.onDrawerClosed(drawerView);
							if(isGoto){
								startActivity(ScheduleSearchActivity.createIntent(StationBoardActivity.this));
								isGoto = false;
								finish();
							}
						}
					});
		}

	}
	private boolean isGoto;
	public void settings(View view){
		isGoto = true;
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		if(drawerOpen){
			mDrawerLayout.closeDrawer(GravityCompat.END);
			mDrawerLayout.setDrawerListener(
					new DrawerLayout.SimpleDrawerListener() {
						@Override
						public void onDrawerClosed(View drawerView) {
							super.onDrawerClosed(drawerView);
							if(isGoto){
								startActivity(SettingsActivity.createIntent(StationBoardActivity.this));
								isGoto = false;
								finish();
							}
						}
					});
		}
	}

	public void about(View view){

		isGoto = true;
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		if(drawerOpen){
			mDrawerLayout.closeDrawer(GravityCompat.END);
			mDrawerLayout.setDrawerListener(
					new DrawerLayout.SimpleDrawerListener() {
						@Override
						public void onDrawerClosed(View drawerView) {
							super.onDrawerClosed(drawerView);
							if(isGoto){
								startActivity(WizardActivity.createIntent(StationBoardActivity.this, WizardActivity.Wizard_Home));
								isGoto = false;
								finish();
							}
						}
					});
		}
	}
	public void myTickets(View view){

		isGoto = true;
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		if(drawerOpen){
			mDrawerLayout.closeDrawer(GravityCompat.END);
			mDrawerLayout.setDrawerListener(
					new DrawerLayout.SimpleDrawerListener() {
						@Override
						public void onDrawerClosed(View drawerView) {
							super.onDrawerClosed(drawerView);
							if(isGoto){
								startActivity(MyTicketsActivity.createIntent(StationBoardActivity.this));
								isGoto = false;
								finish();
							}
						}
					});
		}
	}

	public void uploadTickets(View view){
		isGoto = true;
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		if(drawerOpen){
			mDrawerLayout.closeDrawer(GravityCompat.END);
			mDrawerLayout.setDrawerListener(
					new DrawerLayout.SimpleDrawerListener() {
						@Override
						public void onDrawerClosed(View drawerView) {
							super.onDrawerClosed(drawerView);
							if(isGoto){
								startActivity(UploadDossierActivity.createUploadDossierIntent(StationBoardActivity.this, NMBSApplication.PAGE_UploadTicket, null, null, null, null));
								isGoto = false;
								finish();
							}
						}
					});
		}
	}
	public void myOption(View view){
		//startActivity(LoginActivity.createIntent(MainActivity.this));
		final GeneralSetting generalSetting = masterService.loadGeneralSetting();
		isGoto = true;
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		if(drawerOpen){
			mDrawerLayout.closeDrawer(GravityCompat.END);
			mDrawerLayout.setDrawerListener(
					new DrawerLayout.SimpleDrawerListener() {
						@Override
						public void onDrawerClosed(View drawerView) {
							super.onDrawerClosed(drawerView);
							if(isGoto){
								if(NMBSApplication.getInstance().getLoginService().isLogon()){
									startActivity(WebViewActivity.createIntent(getApplicationContext(),
											Utils.getUrl(generalSetting.getCommercialTtlListUrl()), WebViewActivity.NORMAL_FLOW, ""));
								}else{
									if(!isFinishing()){
										DialogMyOptions dialogMyOptions = new DialogMyOptions(StationBoardActivity.this);
										dialogMyOptions.show();
									}

								}
								isGoto = false;
							}
						}
					});
		}else{
			if(NMBSApplication.getInstance().getLoginService().isLogon()){
				startActivity(WebViewActivity.createIntent(getApplicationContext(),
						Utils.getUrl(generalSetting.getCommercialTtlListUrl()), WebViewActivity.NORMAL_FLOW, ""));
			}else{
				if(!isFinishing()){
					DialogMyOptions dialogMyOptions = new DialogMyOptions(this);
					dialogMyOptions.show();
				}

			}
		}


	}

	public void loginOrManage(View view){
		//startActivity(LoginActivity.createIntent(MainActivity.this));
		final GeneralSetting generalSetting = masterService.loadGeneralSetting();
		isGoto = true;
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		if(drawerOpen){
			mDrawerLayout.closeDrawer(GravityCompat.END);
			mDrawerLayout.setDrawerListener(
					new DrawerLayout.SimpleDrawerListener() {
						@Override
						public void onDrawerClosed(View drawerView) {
							super.onDrawerClosed(drawerView);
							if(isGoto){
								if(NMBSApplication.getInstance().getLoginService().isLogon()){
									ProfileInfoAsyncTask profileInfoAsyncTask = new ProfileInfoAsyncTask(getApplicationContext(), null);
									profileInfoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
									startActivity(WebViewCreateActivity.createIntent(getApplicationContext(), Utils.getUrl(generalSetting.getProfileOverviewUrl())));

								}else{
									startActivity(LoginActivity.createIntent(getApplicationContext(), ""));
								}
								isGoto = false;
							}
						}
					});
		}else{
			if(NMBSApplication.getInstance().getLoginService().isLogon()){
				ProfileInfoAsyncTask profileInfoAsyncTask = new ProfileInfoAsyncTask(getApplicationContext(), null);
				profileInfoAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				startActivity(WebViewCreateActivity.createIntent(getApplicationContext(), Utils.getUrl(generalSetting.getProfileOverviewUrl())));
			}else{
				startActivity(LoginActivity.createIntent(getApplicationContext(), ""));
			}
		}
	}
	private TextView tvMenuOptionCount;
	private RelativeLayout rlMenuMyOption;
	public void clickMenu(View view) {
		//startActivity(MenuActivity.createIntent(this, ticketCount, realtimeCount, messateCount));
		tvMenuTicketCount = (TextView) findViewById(R.id.tv_menu_ticket_count);
		tvMenuRealtimeCount = (TextView) findViewById(R.id.tv_menu_realtime_count);
		tvMenuMessageCount = (TextView) findViewById(R.id.tv_menu_message_count);
		tvMenuTicketCount.setText("(" + MainActivity.ticketCount + ")");
		tvMenuRealtimeCount.setText("(" + MainActivity.realtimeCount + ")");
		tvMenuMessageCount.setText("(" + MainActivity.messateCount + ")");

		rlMenuMyOption = (RelativeLayout) findViewById(R.id.rl_menu_traintickets_content_myoptions);
		tvMenuOptionCount = (TextView) findViewById(R.id.tv_menu_option_count);
		tvMenuOptionCount.setText("(" + MainActivity.optionCount + ")");
		tvMenuOptionCount.setText("(" + MainActivity.optionCount + ")");
		TextView tvMenuLogon = (TextView) findViewById(R.id.tv_menu_logon);
		if(!NMBSApplication.getInstance().getLoginService().isLogon()){
			rlMenuMyOption.setAlpha(0.3f);
			tvMenuOptionCount.setText("");
			tvMenuLogon.setText(getResources().getString(R.string.menu_content_loginorcreateprofile));
		}else {
			tvMenuLogon.setText(getResources().getString(R.string.menu_content_manageprofile));
		}
		if(NMBSApplication.getInstance().getMasterService().loadGeneralSetting() != null
				&& NMBSApplication.getInstance().getMasterService().loadGeneralSetting().getCommercialTtlListUrl() != null
				&& !NMBSApplication.getInstance().getMasterService().loadGeneralSetting().getCommercialTtlListUrl().isEmpty()){
			rlMenuMyOption.setVisibility(View.VISIBLE);
		}else {
			rlMenuMyOption.setVisibility(View.GONE);
		}
		RelativeLayout rlMenuBook = findViewById(R.id.rl_menu_traintickets_content_bookticktes);

		if(NMBSApplication.getInstance().getMasterService().loadGeneralSetting() != null
				&& NMBSApplication.getInstance().getMasterService().loadGeneralSetting().getBookingUrl() != null
				&& !NMBSApplication.getInstance().getMasterService().loadGeneralSetting().getBookingUrl().isEmpty()){
			rlMenuBook.setVisibility(View.VISIBLE);
		}else {
			rlMenuBook.setVisibility(View.GONE);
		}
		RelativeLayout rlMenuLowestfares = findViewById(R.id.rl_menu_traintickets_content_lowestfares);
		RelativeLayout rlMenuLogin = findViewById(R.id.rl_menu_traintickets_content_login);

		if(FunctionConfig.kFunLFFEntry){
			rlMenuLowestfares.setVisibility(View.VISIBLE);
		}else {
			rlMenuLowestfares.setVisibility(View.GONE);
		}

		if(FunctionConfig.kFunMyProfile){
			rlMenuMyOption.setVisibility(View.VISIBLE);
			rlMenuLogin.setVisibility(View.VISIBLE);
		}else {
			rlMenuMyOption.setVisibility(View.GONE);
			rlMenuLogin.setVisibility(View.GONE);
		}
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
