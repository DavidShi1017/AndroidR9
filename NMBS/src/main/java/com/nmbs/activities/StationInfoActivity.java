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
import com.nmbs.util.GoogleAnalyticsUtil;
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
		this.stationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startActivity(StationInfoDetailActivity.createIntent(StationInfoActivity.this,stationInfoList.get(position)));
			}
		});
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
								startActivity(com.nmbs.activities.MessageActivity.createIntent(StationInfoActivity.this, messageService.getMessageResponse()));
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
								startActivity(com.nmbs.activities.StationBoardActivity.createIntent(StationInfoActivity.this, null));
								isGoto = false;
								finish();
							}
						}
					});
		}
	}

	public void stations(View view){
		mDrawerLayout.closeDrawer(GravityCompat.END);
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
								startActivity(com.nmbs.activities.AlertActivity.createIntent(StationInfoActivity.this));
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
									//Utils.openProwser(StationInfoActivity.this, generalSetting.getBookingUrl(), clickToCallService);
									if(NetworkUtils.isOnline(StationInfoActivity.this)) {
										GoogleAnalyticsUtil.getInstance().sendScreen(StationInfoActivity.this, TrackerConstant.BOOKING);
										startActivity(WebViewActivity.createIntent(StationInfoActivity.this,
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
									//Utils.openProwser(StationInfoActivity.this, generalSetting.getLffUrl(), clickToCallService);
									if(NetworkUtils.isOnline(StationInfoActivity.this)) {
										GoogleAnalyticsUtil.getInstance().sendScreen(StationInfoActivity.this, TrackerConstant.LLF);
										startActivity(WebViewActivity.createIntent(StationInfoActivity.this,
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
								startActivity(ScheduleSearchActivity.createIntent(StationInfoActivity.this));
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
								startActivity(SettingsActivity.createIntent(StationInfoActivity.this));
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
								startActivity(WizardActivity.createIntent(StationInfoActivity.this, WizardActivity.Wizard_Home));
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
								startActivity(MyTicketsActivity.createIntent(StationInfoActivity.this));
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
								startActivity(UploadDossierActivity.createUploadDossierIntent(StationInfoActivity.this, NMBSApplication.PAGE_UploadTicket, null, null, null, null));
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
									if(generalSetting.getCommercialTtlListUrl() != null && !generalSetting.getCommercialTtlListUrl().isEmpty()){
										startActivity(WebViewActivity.createIntent(getApplicationContext(),
												Utils.getUrl(generalSetting.getCommercialTtlListUrl()), WebViewActivity.NORMAL_FLOW, ""));
										GoogleAnalyticsUtil.getInstance().sendScreen(StationInfoActivity.this, TrackerConstant.CommercialTTLListUrl);
									}
								}else{
									DialogMyOptions dialogMyOptions = new DialogMyOptions(StationInfoActivity.this);
									dialogMyOptions.show();
								}
								isGoto = false;
							}
						}
					});
		}else{
			if(NMBSApplication.getInstance().getLoginService().isLogon()){
				if(generalSetting.getCommercialTtlListUrl() != null && !generalSetting.getCommercialTtlListUrl().isEmpty()){
					startActivity(WebViewActivity.createIntent(getApplicationContext(),
							Utils.getUrl(generalSetting.getCommercialTtlListUrl()), WebViewActivity.NORMAL_FLOW, ""));
					GoogleAnalyticsUtil.getInstance().sendScreen(StationInfoActivity.this, TrackerConstant.CommercialTTLListUrl);
				}
			}else{
				DialogMyOptions dialogMyOptions = new DialogMyOptions(this);
				dialogMyOptions.show();
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
									GoogleAnalyticsUtil.getInstance().sendScreen(StationInfoActivity.this, TrackerConstant.ProfileOverviewUrl);
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
				GoogleAnalyticsUtil.getInstance().sendScreen(StationInfoActivity.this, TrackerConstant.ProfileOverviewUrl);
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
		TextView tvMenuLogon = (TextView) findViewById(R.id.tv_menu_logon);
		RelativeLayout rlMenuLogin = (RelativeLayout) findViewById(R.id.rl_menu_traintickets_content_login);
		if(!NMBSApplication.getInstance().getLoginService().isLogon()){
			rlMenuMyOption.setAlpha(0.3f);
			tvMenuOptionCount.setText("");
			tvMenuLogon.setText(getResources().getString(R.string.menu_content_loginorcreateprofile));
			if(NMBSApplication.getInstance().getMasterService().loadGeneralSetting() != null
					&& NMBSApplication.getInstance().getMasterService().loadGeneralSetting().getProfileOverviewUrl() != null
					&& !NMBSApplication.getInstance().getMasterService().loadGeneralSetting().getProfileOverviewUrl().isEmpty()){
				rlMenuLogin.setVisibility(View.VISIBLE);
			}else {
				rlMenuLogin.setVisibility(View.GONE);
			}
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
		mDrawerLayout.openDrawer(GravityCompat.END);
	}
	public void close(View view){
		finish();
	}
}
