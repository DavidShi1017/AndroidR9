package com.cflint.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;

import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cflint.R;
import com.cflint.activities.view.DialogAlertError;
import com.cflint.activities.view.DialogMyOptions;
import com.cflint.activity.BaseActivity;
import com.cflint.application.NMBSApplication;
import com.cflint.async.ProfileInfoAsyncTask;
import com.cflint.model.ExtensionScheduleQuery;
import com.cflint.model.GeneralSetting;
import com.cflint.model.ScheduleQuery;
import com.cflint.model.Station;
import com.cflint.model.TravelRequest;
import com.cflint.services.IClickToCallService;
import com.cflint.services.IMasterService;
import com.cflint.services.IMessageService;
import com.cflint.services.IScheduleService;
import com.cflint.services.impl.SettingService;
import com.cflint.util.DateUtils;
import com.cflint.util.FunctionConfig;
import com.cflint.util.GoogleAnalyticsUtil;
import com.cflint.util.MenuUtil;
import com.cflint.util.NetworkUtils;
import com.cflint.util.TrackerConstant;
import com.cflint.util.Utils;

import java.util.Date;

/**
 * Activity used for displaying the UI element, user can do some inner behavior with this.
 *
 * User can select a category to see the details.
 */
public class ScheduleSearchActivity extends BaseActivity {
	private final static String TAG = ScheduleSearchActivity.class.getSimpleName();
	public  static final int REQUEST_FROM_STATION = 0;
	public  static final int REQUEST_TO_STATION = 1;
	public  static final int REQUEST_VIA_STATION = 3;
	public  static final int REQUEST_DATETIME = 2;
	public  static final int REQUEST_ERROR = 222;
	public  static final String SCHEDULE_LAST_QUERY_ID = "001";
	private ProgressDialog progressDialog;
	private IScheduleService scheduleService;
	private SettingService settingService;
	private ScheduleQuery scheduleQuery;
	private String dateFormatted;
	private RelativeLayout fromStaionLayout, toStationLayout;
	private LinearLayout dateLayout, deleteTrainNrLayout, deleteViaStationLayout;
	private TextView tvDateTime, fromStationTextView, toStationTextView, fromStationSynoniemTextView,
			toStationSynoniemTextView, viaStationNameTextView, viaStationSynoniemTextView, tvError;
	private String fromCode = "", toCode = "";
	private String fromStationName, toStationName, fromStationSynoniem, toStationSynoniem;
	private String viaCode = "";
	private String viaName = "";
	private TravelRequest.TimePreference selectedTimePreference = TravelRequest.TimePreference.DEPARTURE;
	private EditText trainNrEditText;
	private ImageView ivReverse;
	private DialogAlertError dialogError;
	private Date date;
	private DrawerLayout mDrawerLayout;
	private LinearLayout mDrawerList;
	private TextView tvMenuTicketCount, tvMenuRealtimeCount, tvMenuMessageCount;
	private IMasterService masterService;
	private IClickToCallService clickToCallService;
	private IMessageService messageService;
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
		setContentView(R.layout.activity_schedule);
		bindAllViewElements();
		bindService();
		bindListener();
		initData();
		GoogleAnalyticsUtil.getInstance().sendScreen(ScheduleSearchActivity.this, TrackerConstant.SCHEDULE_TravelWish);
	}



	private void initData(){
		ExtensionScheduleQuery extensionScheduleQuery = scheduleService.getLastQuery(SCHEDULE_LAST_QUERY_ID);
		if(extensionScheduleQuery != null){
			if(!"".equals(extensionScheduleQuery.getOriginName())){
				this.fromStationTextView.setText(extensionScheduleQuery.getOriginName());
				fromStationTextView.setTextColor(getResources().getColor(R.color.textcolor_thirdaction));
				this.fromCode = extensionScheduleQuery.getOriginStationRcode();
				this.fromStationName = extensionScheduleQuery.getOriginName();

				//this.fromStationSynoniem = extensionScheduleQuery.getViaStationName();
			}

			if(!"".equals(extensionScheduleQuery.getDestinationName())){
				this.toStationTextView.setText(extensionScheduleQuery.getDestinationName());
				this.toCode = extensionScheduleQuery.getDestinationStationRcode();
				this.toStationName = extensionScheduleQuery.getDestinationName();
				//this.toStationSynoniem = extensionScheduleQuery.getOriginName();
				toStationTextView.setTextColor(getResources().getColor(R.color.textcolor_thirdaction));

			}

			this.trainNrEditText.setText(extensionScheduleQuery.getTrainNr());
			if("".equals(extensionScheduleQuery.getTrainNr())){
				this.deleteTrainNrLayout.setVisibility(View.GONE);
			}else{
				this.deleteTrainNrLayout.setVisibility(View.VISIBLE);
			}
			if(!"".equals(extensionScheduleQuery.getViaStationName())){
				this.viaStationNameTextView.setText(extensionScheduleQuery.getViaStationName());
				viaStationNameTextView.setTextColor(getResources().getColor(R.color.textcolor_thirdaction));
				this.viaCode = extensionScheduleQuery.getViaStationRcode();
				this.viaName = extensionScheduleQuery.getViaStationName();
				deleteViaStationLayout.setVisibility(View.VISIBLE);
			}else{
				this.deleteViaStationLayout.setVisibility(View.GONE);
			}
			if(extensionScheduleQuery.getDateTime()!=null){
				//Log.e("ScheduleQuery", "ScheduleQuery..." + extensionScheduleQuery.getDateTime());
				tvDateTime.setTextColor(getResources().getColor(R.color.textcolor_thirdaction));
				Date date = new Date();
				if (extensionScheduleQuery.getDateTime().after(date)){
					//Log.e("ScheduleQuery", "ScheduleQuery...extensionScheduleQuery.getDateTime().after(date)..");
					this.date = extensionScheduleQuery.getDateTime();
					this.tvDateTime.setText(DateUtils.dateTimeToString(extensionScheduleQuery.getDateTime(), "dd MMM yyyy - HH:mm"));
					this.dateFormatted = DateUtils.dateTimeToString(extensionScheduleQuery.getDateTime(), "dd MMM yyyy - HH:mm");
				}else {
					this.tvDateTime.setText(DateUtils.dateTimeToString(date, "dd MMM yyyy - HH:mm"));
					this.dateFormatted = DateUtils.dateTimeToString(date, "dd MMM yyyy - HH:mm");
				}
			}

			this.selectedTimePreference = extensionScheduleQuery.getTimePreference();

		}

	}

	private void bindListener(){
		this.trainNrEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				deleteTrainNrLayout.setVisibility(View.VISIBLE);
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	private void bindAllViewElements(){
		this.tvDateTime = (TextView)findViewById(R.id.tv_schedle_datetime);
		this.fromStationTextView = (TextView) findViewById(R.id.tv_schedule_from);
		this.toStationTextView = (TextView) findViewById(R.id.tv_schedule_to);
		this.fromStationSynoniemTextView = (TextView) findViewById(R.id.tv_schedule_from_station_synoniem);
		this.toStationSynoniemTextView = (TextView) findViewById(R.id.tv_schedule_to_station_synoniem);
		this.viaStationNameTextView = (TextView) findViewById(R.id.tv_schedule_via_station);
		this.viaStationSynoniemTextView = (TextView) findViewById(R.id.tv_schedule_via_station_synoniem);
		this.fromStaionLayout = (RelativeLayout) findViewById(R.id.rl_schedule_from);
		this.toStationLayout = (RelativeLayout) findViewById(R.id.rl_schedule_to);
		this.dateLayout = (LinearLayout) findViewById(R.id.ll_schedule_date);
		this.deleteTrainNrLayout = (LinearLayout) findViewById(R.id.ll_schedule_delete_train_nr);
		this.deleteViaStationLayout = (LinearLayout) findViewById(R.id.ll_schedule_delete_via_station);
		this.trainNrEditText = (EditText) findViewById(R.id.et_schedule_train_nr);
		this.ivReverse = (ImageView) findViewById(R.id.iv_reverse);
		this.tvError = (TextView) findViewById(R.id.tv_error);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (LinearLayout) findViewById(R.id.ll_left_menus);
	}

	private void bindService(){
		scheduleService = ((NMBSApplication)getApplication()).getScheduleService();
		settingService = ((NMBSApplication)getApplication()).getSettingService();
	}

	public void deleteTrainNr(View view){
		this.trainNrEditText.setText("");
		this.deleteTrainNrLayout.setVisibility(View.GONE);
	}

	public void deleteViaStation(View view){
		this.viaStationNameTextView.setText(getResources().getString(R.string.schedule_enter_station));
		this.viaStationNameTextView.setTextColor(getResources().getColor(R.color.textcolor_primaryaction));
		this.viaCode = "";
		this.viaName = "";
		this.viaStationSynoniemTextView.setText("");
		this.deleteViaStationLayout.setVisibility(View.GONE);
	}

	public static Intent createIntent(Context context){
		Intent intent = new Intent(context, ScheduleSearchActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		return intent;
	}

	public static Intent createIntent(Context context,boolean isFromScheduleDetail){
		Intent intent = new Intent(context, ScheduleSearchActivity.class);
		if(isFromScheduleDetail){
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		}
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
		bindAllViewElements();
		if(fromCode != null && !fromCode.isEmpty() && toCode != null && !toCode.isEmpty()){
			//ivReverse.setClickable(true);
			ivReverse.setImageResource(R.drawable.ic_change);
		}else{
			//ivReverse.setClickable(false);
			ivReverse.setImageResource(R.drawable.ic_change_disabled);
		}
	}


	// show progressDialog.
	private void showProgressDialog(ProgressDialog progressDialog) {
		if(progressDialog == null){
			this.progressDialog = new ProgressDialog(this);
			this.progressDialog.setCanceledOnTouchOutside(false);
			this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			this.progressDialog.setMessage(this.getString(R.string.alert_loading));
			this.progressDialog.show();
			//Log.e(TAG, "showProgressDialog");
		}

	}
	//hide progressDialog
	private void hideProgressDialog(ProgressDialog progressDialog) {
		if (progressDialog != null) {
			progressDialog.hide();
			progressDialog.dismiss();
			this.progressDialog = null;
			//Log.e(TAG, "hideProgressDialog");
		}
	}

	public void selectFromStation(View view){
		startActivityForResult(StationsActivity.createIntentFromSchedule(ScheduleSearchActivity.this,
				this.fromCode, this.toCode, this.viaCode, REQUEST_FROM_STATION), REQUEST_FROM_STATION);
	}

	public void selectToStation(View view){
		startActivityForResult(StationsActivity.createIntentFromSchedule(ScheduleSearchActivity.this,
				this.fromCode, this.toCode, this.viaCode, REQUEST_TO_STATION), REQUEST_TO_STATION);
	}

	public void selectViaStation(View view){
		startActivityForResult(StationsActivity.createIntentFromSchedule(ScheduleSearchActivity.this,
				this.fromCode, this.toCode, this.viaCode, REQUEST_VIA_STATION), REQUEST_VIA_STATION);
	}

	public void searchTrain(View view){
		if(fromCode == null||"".equals(fromCode)){
			fromStaionLayout.setBackground(getResources().getDrawable(R.drawable.group_error));
			tvError.setVisibility(View.VISIBLE);
			//return;
		}else{
			fromStaionLayout.setBackground(getResources().getDrawable(R.drawable.group_default));
			tvError.setVisibility(View.GONE);
		}


		if(toCode == null||"".equals(toCode)){
			toStationLayout.setBackground(getResources().getDrawable(R.drawable.group_error));
			tvError.setVisibility(View.VISIBLE);
		}else{
			toStationLayout.setBackground(getResources().getDrawable(R.drawable.group_default));
			tvError.setVisibility(View.GONE);
		}

		if(dateFormatted == null||"".equals(dateFormatted)){
			dateLayout.setBackground(getResources().getDrawable(R.drawable.group_error));
			tvError.setVisibility(View.VISIBLE);
			return;
		}else{
			dateLayout.setBackground(getResources().getDrawable(R.drawable.group_default));
			tvError.setVisibility(View.GONE);
		}

		String trainNr = "";
		if(trainNrEditText.getText()!=null&&!"".equals(trainNrEditText.getText())){
			trainNr = trainNrEditText.getText().toString();
		}
		//Log.e("ScheduleQuery", "dateFormatted..." + dateFormatted);
		if(date == null){
			date = new Date();
		}
		scheduleService.insertLastQuery(SCHEDULE_LAST_QUERY_ID,this.fromCode,this.toCode,this.viaCode,dateFormatted,selectedTimePreference.toString(),trainNr,this.fromStationName,this.toStationName,this.viaName);
		startActivityForResult(com.cflint.activities.ScheduleResultActivity.createIntent(ScheduleSearchActivity.this,
				new ScheduleQuery(this.fromCode, this.toCode, this.viaCode, trainNr, date, selectedTimePreference)), REQUEST_ERROR);
	}

	public void nowDate(View view){
		date = new Date();
		dateFormatted = DateUtils.dateTimeToString(date, DateUtils.DATE_FORMAT_MONTH_AD);
		selectedTimePreference = TravelRequest.TimePreference.DEPARTURE;
		setTvDatetime();
	}

	private void setTvDatetime(){
		tvDateTime.setText(dateFormatted);
		tvDateTime.setTextColor(getResources().getColor(R.color.textcolor_thirdaction));
	}

	public void selectDatetime(View view){
		startActivityForResult(DateTimeActivity.createIntent(ScheduleSearchActivity.this, true, date, selectedTimePreference), REQUEST_DATETIME);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_DATETIME:
				if(data != null){
					dateFormatted = (String) data.getSerializableExtra(DateTimeActivity.PRMA_DATE_FORMATTED);
					date = (Date) data.getSerializableExtra(DateTimeActivity.PRMA_DATE);
					selectedTimePreference = (TravelRequest.TimePreference) data.getSerializableExtra(DateTimeActivity.PRMA_TIME_PREFERENCE);
					setTvDatetime();
				}
				break;

			case REQUEST_VIA_STATION:
				if(data != null){
					Station station = (Station)data.getSerializableExtra(StationsActivity.PRMA_STATION);
					if (station != null){
						this.deleteViaStationLayout.setVisibility(View.VISIBLE);
						viaCode = station.getCode();
						viaName = station.getName();
						viaStationNameTextView.setText(station.getName());
						viaStationNameTextView.setTextColor(getResources().getColor(R.color.textcolor_thirdaction));
						if (station.getSynoniem() != null && !station.getSynoniem().isEmpty()) {
							viaStationSynoniemTextView.setText(station.getSynoniem());
							viaStationSynoniemTextView.setVisibility(View.VISIBLE);
						}
					}
				}
				break;
			case REQUEST_TO_STATION:
				if(data != null){
					Station station = (Station)data.getSerializableExtra(StationsActivity.PRMA_STATION);
					if (station != null){
						toCode = station.getCode();
						toStationName = station.getName();
						toStationSynoniem = station.getSynoniem();
						toStationTextView.setText(station.getName());
						toStationTextView.setTextColor(getResources().getColor(R.color.textcolor_thirdaction));
						if (station.getSynoniem() != null && !station.getSynoniem().isEmpty()) {
							toStationSynoniemTextView.setText(station.getSynoniem());
							toStationSynoniemTextView.setVisibility(View.VISIBLE);
						}
					}
				}
				break;
			case REQUEST_FROM_STATION:
				if(data != null){
					Station station = (Station)data.getSerializableExtra(StationsActivity.PRMA_STATION);
					if (station != null){
						fromCode = station.getCode();
						fromStationName = station.getName();
						fromStationSynoniem = station.getSynoniem();
						fromStationTextView.setText(station.getName());
						fromStationTextView.setTextColor(getResources().getColor(R.color.textcolor_thirdaction));
						if (station.getSynoniem() != null && !station.getSynoniem().isEmpty()) {
							fromStationSynoniemTextView.setText(station.getSynoniem());
							fromStationSynoniemTextView.setVisibility(View.VISIBLE);
						}
					}
				}
				break;
			case REQUEST_ERROR:
				//Log.i(TAG, "REQUEST_ERROR.... ");
				if(data != null){
					String error = (String) data.getSerializableExtra(ScheduleResultActivity.Intent_Key_Error);
					//Log.i(TAG, "showError.... " + error);
					if(!isFinishing()){
						dialogError = new DialogAlertError(ScheduleSearchActivity.this, getResources().getString(R.string.general_information), error);
						dialogError.show();
					}
				}

					//setTvDatetime();

				break;
			default:
				break;
		}
	}

	public void reverseOD(View view){

		//Log.d(TAG, "fromStationSynoniem=======" + fromStationSynoniem);
		//Log.d(TAG, "toStationSynoniem=======" + toStationSynoniem);
		if(fromCode != null && !fromCode.isEmpty() && toCode != null && !toCode.isEmpty()){
			this.fromStationTextView.setText(this.toStationName);
			this.toStationTextView.setText(this.fromStationName);
			this.fromStationSynoniemTextView.setText(toStationSynoniem);

			if(fromStationSynoniem != null && !fromStationSynoniem.isEmpty()){
				this.toStationSynoniemTextView.setVisibility(View.VISIBLE);
				this.toStationSynoniemTextView.setText(fromStationSynoniem);
			}else{
				this.toStationSynoniemTextView.setVisibility(View.GONE);
			}
			if(toStationSynoniem != null && !toStationSynoniem.isEmpty()){
				this.fromStationSynoniemTextView.setVisibility(View.VISIBLE);
				this.fromStationSynoniemTextView.setText(toStationSynoniem);
			}else{
				this.fromStationSynoniemTextView.setVisibility(View.GONE);
			}

			String tempStationName = this.fromStationName;
			String tempStationCode = this.fromCode;
			String tepStationSynoniem = this.fromStationSynoniem;
			this.fromStationName = this.toStationName;
			this.toStationName = tempStationName;
			this.fromCode = this.toCode;
			this.toCode = tempStationCode;
			this.fromStationSynoniem = this.toStationSynoniem;
			this.toStationSynoniem = tepStationSynoniem;

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

		MenuUtil.stationBoard(this, mDrawerLayout, mDrawerList);
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
		mDrawerLayout.closeDrawer(GravityCompat.END);
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
			boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
			if(drawerOpen){
				mDrawerLayout.closeDrawer(GravityCompat.END);
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	public void close(View view){
		finish();
	}
}
