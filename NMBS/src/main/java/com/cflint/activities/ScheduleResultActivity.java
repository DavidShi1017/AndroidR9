package com.cflint.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cflint.R;
import com.cflint.activities.view.DialogAlertError;
import com.cflint.activity.BaseActivity;
import com.cflint.adapter.ScheduleDateAdapter;
import com.cflint.adapter.ScheduleMessageAdapter;
import com.cflint.adapter.ScheduleResultAdapter;
import com.cflint.application.NMBSApplication;
import com.cflint.exceptions.NetworkError;
import com.cflint.listeners.ScheduleSearchErrorListener;
import com.cflint.model.AdditionalScheduleQueryParameter;
import com.cflint.model.RealTimeConnection;
import com.cflint.model.ScheduleQuery;
import com.cflint.model.ScheduleResponse;
import com.cflint.model.TrainIcon;
import com.cflint.model.UserMessage;
import com.cflint.services.IScheduleService;
import com.cflint.services.impl.AsyncScheduleResponse;
import com.cflint.services.impl.ServiceConstant;
import com.cflint.services.impl.SettingService;
import com.cflint.util.ActivityConstant;
import com.cflint.util.DateUtils;
import com.cflint.util.GoogleAnalyticsUtil;
import com.cflint.util.NetworkUtils;
import com.cflint.util.RatingUtil;
import com.cflint.util.TrackerConstant;
import com.cflint.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Activity used for displaying the UI element, user can do some inner behavior with this.
 *
 * User can select a category to see the details.
 */
public class ScheduleResultActivity extends BaseActivity {
	private final static String TAG = ScheduleResultActivity.class.getSimpleName();
	private static final int MENU_REFRESH = 0;
	private TextView tvStationName;
	//private ListView listView;
	//private ExpandableListView listView;
	private LinearLayout headerView;
	private LinearLayout messageLayout;
	private ProgressDialog progressDialog;
	private MyState myState = new MyState();
	private IScheduleService scheduleService;
	private DialogAlertError dialogError;
	private ScheduleQuery scheduleQuery;
	private ScheduleResultAdapter scheduleResultAdapter;
	private ScheduleMessageAdapter scheduleMessageAdapter;
	private List<TrainIcon> trainIconList;
	private ScheduleSearchErrorListener scheduleSearchErrorListener;
	private LinearLayout llLaterTrain;
	public static final String Intent_Key_Error = "Error";
	private Map<String,List<RealTimeConnection>> mapList = new LinkedHashMap<>();
	private SettingService settingService;
	private boolean isLaterTrain;
	private LinearLayout llSchedulesDate;
	private LinearLayout llNoSchedules;
	private LinearLayout llStation;
	private ScheduleDateAdapter scheduleDateAdapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Utils.setToolBarStyle(this);
		settingService = ((NMBSApplication) getApplication()).getSettingService();
		//settingService.initLanguageSettings();
		setContentView(R.layout.activity_schedule_result);
		bindAllViewElements();
		bindService();
		getIntentValue(getIntent());
		if(scheduleQuery != null){
			reEnableState(scheduleQuery);
		}
		GoogleAnalyticsUtil.getInstance().sendScreen(ScheduleResultActivity.this, TrackerConstant.SCHEDULE_CONNECTIONLIST);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case MENU_REFRESH:
				if(scheduleQuery != null){
					reEnableState(scheduleQuery);
				}
				break;
		}
		return true;
	}

	private void showView(){
		llLaterTrain.setVisibility(View.VISIBLE);
		if(myState.isRefreshed){
			refreshConnectionList();
			myState.isRefreshed = false;
		}else if(myState.isShowMore){
			refreshConnectionList();
			myState.isShowMore = false;
		}else{
			if(!"".equals(scheduleQuery.getTrainNr())){
				if(myState.scheduleResponse.getConnections().size() == 1){
					startActivity(ScheduleResultDetailActivity.createIntent(this, myState.scheduleResponse.getConnections().get(0), NMBSApplication.PAGE_SCHEDULE));
					finish();
				}else{
					if(myState.scheduleResponse.getConnections().size() != 0){
						headerView.setVisibility(View.GONE);
						llLaterTrain.setVisibility(View.GONE);
					}
					setViewStateBasedOnModel();
				}
			}else{
				setViewStateBasedOnModel();
			}
		}
	}

	private void refreshConnectionList(){
		addGroupListData();
		/*for(int i=0;i<mapList.keySet().size();i++){
			//this.listView.expandGroup(i);
		}*/
		fillData();
		//scheduleResultAdapter.notifyDataSetChanged();
	}

	public void addGroupListData() {
		this.mapList.clear();
		headerView.setVisibility(View.VISIBLE);
		List<RealTimeConnection> realTimeConnectionList = myState.scheduleResponse.getConnections();
		Collections.sort(realTimeConnectionList, new Comparator<RealTimeConnection>() {
			public int compare(RealTimeConnection o1, RealTimeConnection o2) {
				if (o1.getDeparture().getTime() - o2.getDeparture().getTime() > 0) {
					return 1;
				} else {
					return -1;
				}
			}
		});
		if(realTimeConnectionList != null && realTimeConnectionList.size() > 0){
			for(RealTimeConnection realTimeConnection : realTimeConnectionList){
				String dateString = DateUtils.dateToString(realTimeConnection.getDeparture());

				if(!mapList.containsKey(dateString)){
					//Log.d(TAG, "dateString is not containsKey.........." + dateString);
					List<RealTimeConnection> tempConnection = new ArrayList<RealTimeConnection>();
					tempConnection.add(realTimeConnection);

					//Log.d(TAG, "put dateString is.........." + dateString);
					mapList.put(dateString, tempConnection);

				}else{
					//Log.d(TAG, "dateString is containsKey.........." + dateString);
					mapList.get(dateString).add(realTimeConnection);
					/*Collections.sort(mapList.get(dateString), new Comparator<RealTimeConnection>() {
						public int compare(RealTimeConnection o1, RealTimeConnection o2) {
							if (o1.getDeparture().getTime() - o2.getDeparture().getTime() > 0) {
								return 1;
							} else {
								return -1;
							}
						}
					});*/
				}
			}
			//Log.d(TAG, "mapList.........." + mapList.size());
		}

	}

	private int getInt(String str) {
		int i = 0;
		try {
			Pattern p = Pattern.compile("^\\d+");
			Matcher m = p.matcher(str);
			if (m.find()) {
				i = Integer.valueOf(m.group());
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return i;
	}

	private void fillData(){
		//if(scheduleDateAdapter == null){

		if(mapList == null||mapList.keySet().toArray().length == 0){
			llNoSchedules.setVisibility(View.VISIBLE);
			llSchedulesDate.setVisibility(View.GONE);
		}else{
			llNoSchedules.setVisibility(View.GONE);
			llSchedulesDate.setVisibility(View.VISIBLE);
			scheduleDateAdapter = null;
			scheduleDateAdapter = new ScheduleDateAdapter(this, trainIconList, mapList);
			//}
			llSchedulesDate.removeAllViews();
			for(int i=0;i<mapList.keySet().size();i++){
				scheduleDateAdapter.getScheduleDateView(mapList.keySet().toArray()[i].toString(), llSchedulesDate, i);
			}
		}
	}

	private void setViewStateBasedOnModel(){

		addGroupListData();
		//Log.d(TAG, "mapList.keySet().size()..........");
		//Log.d(TAG, "mapList.keySet().size().........." + mapList.keySet().size());

		fillData();
		/*scheduleResultAdapter = new ScheduleResultAdapter(this,mapList, listView,this,trainIconList);
		//listView.removeAllViews();
		listView.setAdapter(scheduleResultAdapter);

		listView.setGroupIndicator(null);
		for(int i=0;i<mapList.keySet().size();i++){
			this.listView.expandGroup(i);
		}
		this.listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				startActivity(ScheduleResultDetailActivity.createIntent(ScheduleResultActivity.this,
						mapList.get(mapList.keySet().toArray()[groupPosition]).get(childPosition),  NMBSApplication.PAGE_SCHEDULE));
				return false;
			}
		});

		scheduleResultAdapter.notifyDataSetChanged();*/
		llStation.setVisibility(View.VISIBLE);
		tvStationName.setText(myState.scheduleResponse.getOriginStationName() + " - " + myState.scheduleResponse.getDestinationStationName());
		/*List<UserMessage> userMessages = new ArrayList<UserMessage>();
		UserMessage userMessage = new UserMessage("Message Test functional","This is a functional message for you",
				"10/01-31/12","/~/media/ImagesNew/Icons/100x100_promo2.ashx","/~/media/ImagesNew/Icons/100x100_promo2.ashx?sc=0.5","/~/media/ImagesNew/OnlyRail1/Banners/Amtrak_FR.ashx",
				"/~/media/ImagesNew/OnlyRail1/Banners/Amtrak_FR.ashx?sc=0.5",true,"Read more","http://www.accept.b-europe.com/Travel/Destinations");
		userMessages.add(userMessage);
		myState.scheduleResponse.setUserMessages(userMessages);*/
		List<UserMessage> userMessageList = myState.scheduleResponse.getUserMessages();
		scheduleMessageAdapter = new ScheduleMessageAdapter(this,userMessageList);
		messageLayout.removeAllViews();

		for(int i=0;i<userMessageList.size();i++){

			scheduleMessageAdapter.getScheduleMessageView(i, messageLayout);
		}
	}

	private void bindService() {
		scheduleService = ((NMBSApplication)getApplication()).getScheduleService();
		settingService = ((NMBSApplication)getApplication()).getSettingService();
	}

	public void refresh(View view){
		if(scheduleQuery != null){
			myState.isRefreshed = true;
			reEnableState(scheduleQuery);
		}
	}

	public void back(View view){
		finish();
	}

	private void bindAllViewElements(){
		//View convertView = getLayoutInflater().inflate(R.layout.schedule_list_header_view, null);
		llStation = (LinearLayout) findViewById(R.id.ll_station);
		tvStationName = (TextView) findViewById(R.id.tv_schedule_result_station_name);
		llSchedulesDate = (LinearLayout) findViewById(R.id.ll_schedules_date);
		llNoSchedules = (LinearLayout) findViewById(R.id.ll_no_schedule);

		//listView = (ExpandableListView) findViewById(android.R.id.list);
		//footerView = getLayoutInflater().inflate(R.layout.planner_list_footer_view, null);
		llLaterTrain = (LinearLayout) findViewById(R.id.ll_later_train);
		//llLaterTrain.setVisibility(View.GONE);
		headerView = (LinearLayout) findViewById(R.id.ll_planner_list_header_LinearLayout);
		headerView.setVisibility(View.GONE);
		messageLayout = (LinearLayout) findViewById(R.id.ll_schedule_message_layout);
		//llSchedulesDate.addView(convertView);
		//listView.addFooterView(footerView);
		//listView.addHeaderView(convertView);
		//listView.addHeaderView(headerView);

		bindListeners();
	}

	private void bindListeners(){
		llLaterTrain.setOnClickListener(footerViewClickListener);
		headerView.setOnClickListener(headerViewClickListener);
	}

	private OnClickListener headerViewClickListener = new OnClickListener() {
		public void onClick(View v) {
			//Called when a view has been clicked.
			//Log.d(TAG, "headerViewClickListener");
			myState.isShowMore = true;
			AdditionalScheduleQueryParameter additionalScheduleQueryParameter = new AdditionalScheduleQueryParameter();
			additionalScheduleQueryParameter.setDirection(AdditionalScheduleQueryParameter.Direction.Backward);
			myState.asyncScheduleResponse = scheduleService.searchScheduleTrains(additionalScheduleQueryParameter, settingService);
			myState.asyncScheduleResponse.registerHandler(mHandler);
			myState.registerHandler(mHandler);
			showProgressDialog(progressDialog);
		}
	};

	private OnClickListener footerViewClickListener = new OnClickListener() {
		public void onClick(View v) {
			//Called when a view has been clicked.
			//Log.d(TAG, "footerViewClickListener");
			isLaterTrain = true;
			myState.isShowMore = true;
			AdditionalScheduleQueryParameter additionalScheduleQueryParameter = new AdditionalScheduleQueryParameter();
			additionalScheduleQueryParameter.setDirection(AdditionalScheduleQueryParameter.Direction.Forward);
			myState.asyncScheduleResponse = scheduleService.searchScheduleTrains(additionalScheduleQueryParameter, settingService);
			myState.asyncScheduleResponse.registerHandler(mHandler);
			myState.registerHandler(mHandler);
			showProgressDialog(progressDialog);
		}
	};

	public void getIntentValue(Intent intent){
		scheduleQuery = (ScheduleQuery) intent.getSerializableExtra(ActivityConstant.SCHEDULE_QUERY);
	}

	public static Intent createIntent(Context context,ScheduleQuery scheduleQuery){
		Intent intent = new Intent(context, ScheduleResultActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		intent.putExtra(ActivityConstant.SCHEDULE_QUERY, scheduleQuery);
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
		if (progressDialog != null && !this.isFinishing()) {
			progressDialog.hide();
			progressDialog.dismiss();
			this.progressDialog = null;
			//Log.e(TAG, "hideProgressDialog");
		}
	}


	// private class which contains all fields and objects who need to be kept accross Activity re-creates (configuration changes).
	private class MyState {

		public AsyncScheduleResponse asyncScheduleResponse;
		public ScheduleResponse scheduleResponse;
		private boolean isRefreshed;
		private boolean isShowMore;
		public void unRegisterHandler(){
			if (asyncScheduleResponse != null){
				asyncScheduleResponse.unregisterHandler();
			}
		}

		public void registerHandler(Handler handler){
			if (asyncScheduleResponse != null){
				asyncScheduleResponse.registerHandler(handler);
			}
		}
	}

	private void reEnableState(ScheduleQuery scheduleQuery){
		//Initial call
		//Log.i(TAG, "myState is null");
		trainIconList = scheduleService.getTrainIcons();
		int connectionStatus = NetworkUtils.getNetworkState(getApplicationContext());
		if (connectionStatus > 0 ) {
			getScheduleData(scheduleQuery);
		}else {
			/*dialogError = new DialogAlertError(ScheduleResultActivity.this, getResources().getString(R.string.general_information),
					getResources().getString(R.string.alert_no_network));
			dialogError.show();*/
			showError(getResources().getString(R.string.alert_no_network));
			//responseReceived(myState.asyncstationDetailResponse.getstationDetailResponse());
		}
		//Log.i(TAG, "Is Refreshed? " + myState.isRefreshed);
	}

	// refresh Data
	private void getScheduleData(ScheduleQuery scheduleQuery){
		myState.asyncScheduleResponse = scheduleService.searchSchedule(scheduleQuery, settingService);
		myState.asyncScheduleResponse.registerHandler(mHandler);
		myState.registerHandler(mHandler);
		showProgressDialog(progressDialog);
	}

	private void responseReceived(ScheduleResponse scheduleResponse) {

		myState.scheduleResponse = scheduleResponse;

		RatingUtil.saveScheduleSearch(getApplicationContext());
		myState.unRegisterHandler();
		//Log.e(TAG, "responseReceived....");
		//myState.asyncStationDetailResponse = null;
		showView();
		//if(!isShowLaterState){
		hideProgressDialog(progressDialog);
		//}else{
		//}
		//Log.d(TAG, "show response");
	}

	//Handler which is called when the App is refreshed.
	// The what parameter of the message decides if there was an error or not.
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case ServiceConstant.MESSAGE_WHAT_OK:
					//Log.d(TAG, "MESSAGE_WHAT_OK");
					responseReceived(myState.asyncScheduleResponse.getScheduleResponse());
					break;
				case ServiceConstant.MESSAGE_WHAT_ERROR:

					Bundle bundle = msg.getData();
					NetworkError error = (NetworkError) bundle.getSerializable(ServiceConstant.PARAM_OUT_ERROR);
					String responseErrorMessage = bundle.getString(ServiceConstant.PARAM_OUT_ERROR_MESSAGE);
					switch (error) {
						case TIMEOUT:
							hideProgressDialog(progressDialog);
							/*Toast.makeText(ScheduleResultActivity.this,
									getString(R.string.stationboard_searchfail_message),
									Toast.LENGTH_LONG).show();*/
							showError(getString(R.string.general_server_unavailable));
							//finish();
							break;
						case CustomError:
							hideProgressDialog(progressDialog);

							if (responseErrorMessage == null) {
								responseErrorMessage = getString(R.string.general_server_unavailable);
							}
							showError(responseErrorMessage);
							//Toast.makeText(ScheduleResultActivity.this, responseErrorMessage, Toast.LENGTH_LONG).show();
							//finish();
							break;
						default:
							finish();
							break;
					}
					//Log.d(TAG, "handler receive");
					break;
			}
		};
	};

	private void showError(String error){
		//Log.i(TAG, "showError.... " + error);
		if(isLaterTrain){
			if(!isFinishing()){
				DialogAlertError dialogError = new DialogAlertError(this, getResources().getString(R.string.general_information), error);
				dialogError.show();
			}

		}else{
			Intent intent = new Intent();
			intent.putExtra(Intent_Key_Error, error);
			setResult(RESULT_OK, intent);
			finish();
		}
	}
}
