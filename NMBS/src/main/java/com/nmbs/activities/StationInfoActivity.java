package com.nmbs.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nmbs.R;
import com.nmbs.activities.view.DialogMyOptions;
import com.nmbs.activity.BaseActivity;
import com.nmbs.adapter.StationInfoAdapter;
import com.nmbs.application.NMBSApplication;
import com.nmbs.async.GetStationInfoAsync;
import com.nmbs.async.ProfileInfoAsyncTask;
import com.nmbs.model.GeneralSetting;
import com.nmbs.model.StationInfo;
import com.nmbs.model.StationInfoResponse;
import com.nmbs.services.IClickToCallService;
import com.nmbs.services.IMasterService;
import com.nmbs.services.IMessageService;
import com.nmbs.services.IStationInfoService;
import com.nmbs.services.impl.ServiceConstant;
import com.nmbs.services.impl.SettingService;
import com.nmbs.util.ActivityConstant;
import com.nmbs.util.FunctionConfig;
import com.nmbs.util.GoogleAnalyticsUtil;
import com.nmbs.util.MenuUtil;
import com.nmbs.util.NetworkUtils;
import com.nmbs.util.TrackerConstant;
import com.nmbs.util.Utils;

import java.util.List;

/**
 * Activity used for displaying the UI element, user can do some inner behavior with this.
 * 
 * User can select a category to see the details.
 */
public class StationInfoActivity extends BaseActivity{
	private ListView stationListView;
	private StationInfoAdapter stationInfoAdapter;
	private ProgressDialog progressDialog;
	private IStationInfoService stationInfoService;
	private SettingService settingService = null;
	private List<StationInfo> stationInfoList;
	private StationInfoStateReceiver stationInfoStateReceiver;
	private StationInfoResponse stationInfoResponse;
	private TextView tvMenuTicketCount, tvMenuRealtimeCount, tvMenuMessageCount;

	private IMasterService masterService;
	private IClickToCallService clickToCallService;
	private IMessageService messageService;
	private DrawerLayout mDrawerLayout;
	private LinearLayout mDrawerList;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Utils.setToolBarStyle(this);
		stationInfoService = ((NMBSApplication) getApplication()).getStationInfoService();
		settingService = ((NMBSApplication) getApplication()).getSettingService();
		//settingService.initLanguageSettings();
		messageService = ((NMBSApplication) getApplication()).getMessageService();
		masterService = ((NMBSApplication) getApplication()).getMasterService();
		clickToCallService = ((NMBSApplication) getApplication()).getClickToCallService();
		setContentView(R.layout.activity_stationinfo);
		bindAllViewElements();
		bindService();
		getIntentValue();
		createReceiver();
		init();
		GoogleAnalyticsUtil.getInstance().sendScreen(StationInfoActivity.this, "Station_List");
	}

	/**
	 * To init all external class
	 */
	private void init(){
		if (!GetStationInfoAsync.isFinished){
			showWaitDialog();
		}else{
			stationInfoResponse = stationInfoService.getStationInfoResponseInLocal(getApplicationContext());
			setViewData();
		}


		/*GetStationInfoAsync getStationInfoAsync = new GetStationInfoAsync(stationInfoService,settingService,this);
		getStationInfoAsync.execute((Void) null);*/
	}

	private void setViewData(){
		if(stationInfoResponse != null){
			stationInfoList = stationInfoResponse.getStations();
			stationInfoAdapter = new StationInfoAdapter(getApplicationContext(),stationInfoList);
			this.stationListView.setDivider(null);
			this.stationListView.setAdapter(stationInfoAdapter);
		}
		hideWaitDialog();
	}

	private void createReceiver(){
		if (stationInfoStateReceiver == null) {
			stationInfoStateReceiver = new StationInfoStateReceiver();
			registerReceiver(stationInfoStateReceiver, new IntentFilter(ServiceConstant.STATION_INFO_SERVICE_ACTION));
		}
	}

	/**
	 * To bind all view element
	 */
	private void bindAllViewElements(){
		this.stationListView = (ListView) findViewById(R.id.stationInfoList);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (LinearLayout) findViewById(R.id.ll_left_menus);
	}

	/**
	 * To implement all service
	 */
	private void bindService() {

	}

	/**
	 * get Intent value from last activity
	 */
	public void getIntentValue(){

	}

    public static Intent createIntent(Context context){
		Intent intent = new Intent(context, StationInfoActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		return intent;
	}

	@Override
	protected void onDestroy() {
		if(stationInfoStateReceiver != null){
			unregisterReceiver(stationInfoStateReceiver);
		}

		super.onDestroy();
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
	private void showWaitDialog() {
		if(isFinishing()){
			return;
		}
		runOnUiThread(new Runnable() {
			public void run() {
				if (progressDialog == null) {
					//Log.e(TAG, "Show Wait Dialog....");
					progressDialog = ProgressDialog.show(StationInfoActivity.this, null,
							getString(R.string.general_loading), true);

				}
			}
		});
	}

	// hide progressDialog
	private void hideWaitDialog() {
		runOnUiThread(new Runnable() {
			public void run() {
				//Log.e(TAG, "Hide Wait Dialog....");
				if (progressDialog != null) {
					//Log.e(TAG, "progressDialog is.... " + progressDialog);
					progressDialog.dismiss();
					progressDialog = null;
				}
			}
		});

	}

	class StationInfoStateReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (ServiceConstant.STATION_INFO_SERVICE_ACTION.equalsIgnoreCase(intent.getAction().toString())) {
				stationInfoResponse = (StationInfoResponse) intent.getSerializableExtra(ActivityConstant.STATION_INFO_RESPONSE);
				setViewData();
			}
		}
	}
    
    @Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onResume() {
		super.onResume();
		bindAllViewElements();
		this.stationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startActivity(StationInfoDetailActivity.createIntent(StationInfoActivity.this,stationInfoList.get(position)));
			}
		});
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
		mDrawerLayout.closeDrawer(GravityCompat.END);
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
	public void close(View view){
		finish();
	}
}
