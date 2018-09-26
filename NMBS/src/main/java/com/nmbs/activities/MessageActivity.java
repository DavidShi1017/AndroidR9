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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nmbs.R;
import com.nmbs.activities.view.DialogMyOptions;
import com.nmbs.activity.BaseActivity;
import com.nmbs.activity.LinearLayoutForListView;
import com.nmbs.adapter.MessageAdapter;
import com.nmbs.application.NMBSApplication;
import com.nmbs.async.MobileMessageAsyncTask;
import com.nmbs.async.ProfileInfoAsyncTask;
import com.nmbs.model.GeneralSetting;
import com.nmbs.model.MobileMessage;
import com.nmbs.model.MobileMessageResponse;
import com.nmbs.services.IClickToCallService;
import com.nmbs.services.IMasterService;
import com.nmbs.services.IMessageService;
import com.nmbs.services.impl.ServiceConstant;
import com.nmbs.services.impl.SettingService;
import com.nmbs.util.GoogleAnalyticsUtil;
import com.nmbs.util.NetworkUtils;
import com.nmbs.util.TrackerConstant;
import com.nmbs.util.Utils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MessageActivity extends BaseActivity {
	
	private final static String TAG = MessageActivity.class.getSimpleName();
	private LinearLayout  messageGroupLayout;
	private LayoutInflater layoutInflater;
	private final static String MESSAGE_RESPONSE_SERIALIZABLE_KEY = "MESSAGE_RESPONSE";
	private MobileMessageResponse mobileMessageResponse;
	private RelativeLayout rl_noMessages;
	private ServiceStateReceiver serviceStateReceiver;

	private List<MobileMessage> functionalMessages;
	private List<MobileMessage> commercialMessages;
	private List<MobileMessage> railwayMessages;
	private SettingService settingService = null;
	private IMessageService messageService;
	private ProgressDialog progressDialog;
	private Timer timer;
	private TimerTask timerTask;
	private DrawerLayout mDrawerLayout;
	private LinearLayout mDrawerList;
	private TextView tvMenuTicketCount, tvMenuRealtimeCount, tvMenuMessageCount;
	private IMasterService masterService;
	private IClickToCallService clickToCallService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Utils.setToolBarStyle(this);
		settingService = ((NMBSApplication) getApplication()).getSettingService();
		//settingService.initLanguageSettings();

		setContentView(R.layout.activity_messages);
		settingService = ((NMBSApplication) getApplication()).getSettingService();
		messageService = ((NMBSApplication) getApplication()).getMessageService();
		masterService = ((NMBSApplication) getApplication()).getMasterService();
		clickToCallService = ((NMBSApplication) getApplication()).getClickToCallService();
		getIntentValue();
		bindAllView();
		if(MobileMessageAsyncTask.isMessageFinished){
			initValue();
		}
		addAllListener();
		GoogleAnalyticsUtil.getInstance().sendScreen(MessageActivity.this, TrackerConstant.MESSAGES);
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(progressDialog != null){
							hideWaitDialog();
							mobileMessageResponse = messageService.getMessageResponse();
							bindAllView();
							initValue();
							hideWaitDialog();
						}
					}
				});

			}
		};
		timer.schedule(timerTask, 15000);
	}
	
	public void getIntentValue(){
		mobileMessageResponse = (MobileMessageResponse) getIntent().getSerializableExtra(MESSAGE_RESPONSE_SERIALIZABLE_KEY);
		//Log.e("mobileMessageResponse", "mobileMessageResponse..." + mobileMessageResponse);
	}
	
	public void initValue(){
		this.layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (mobileMessageResponse != null) {
			functionalMessages = mobileMessageResponse.getMobileMessagesByType(MobileMessageResponse.MESSAGETYPE_FUNCTIONAL);
			if (functionalMessages != null && functionalMessages.size() > 0) {
				addMessageGroup(functionalMessages, MobileMessageResponse.MESSAGETYPE_FUNCTIONAL, functionalMessages.size());
			}
			commercialMessages = mobileMessageResponse.getMobileMessagesByType(MobileMessageResponse.MESSAGETYPE_COMMERCIAL);
			if (commercialMessages != null && commercialMessages.size() > 0) {
				addMessageGroup(commercialMessages, MobileMessageResponse.MESSAGETYPE_COMMERCIAL, commercialMessages.size());
			}
			railwayMessages = mobileMessageResponse.getMobileMessagesByType(MobileMessageResponse.MESSAGETYPE_RAILWAY);
			if (railwayMessages != null && railwayMessages.size() > 0) {
				addMessageGroup(railwayMessages, MobileMessageResponse.MESSAGETYPE_RAILWAY, railwayMessages.size());
			}
			Log.e("mobileMessageResponse", "functionalMessages..." + functionalMessages.size());
		}
	}
	
	public void addMessageGroup(List<MobileMessage> massages, String messageType, int messageNumber){

		rl_noMessages.setVisibility(View.GONE);
		
		View view = layoutInflater.inflate(R.layout.message_group_view, null);
		LinearLayoutForListView messageListView = (LinearLayoutForListView) view.findViewById(R.id.lv_message_list);
		TextView groupTitleTextView = (TextView) view.findViewById(R.id.tv_messages_title);
		TextView tvMessageCount = (TextView) view.findViewById(R.id.tv_count);

		ImageView ivMessageTriangle = (ImageView) view.findViewById(R.id.iv_message_triangle);
		if(messageType.equals(MobileMessageResponse.MESSAGETYPE_FUNCTIONAL)){
			groupTitleTextView.setText(R.string.message_view_functional);
			tvMessageCount.setText(String.valueOf(functionalMessages.size()));
		}else if(messageType.equals(MobileMessageResponse.MESSAGETYPE_COMMERCIAL)){
			if (functionalMessages != null && functionalMessages.size() > 0){
				ivMessageTriangle.setVisibility(View.GONE);
			}
			groupTitleTextView.setText(R.string.message_view_commercial);
			tvMessageCount.setText(String.valueOf(commercialMessages.size()));
		}else{
			if (functionalMessages != null && functionalMessages.size() > 0){
				ivMessageTriangle.setVisibility(View.GONE);

			}
			if (commercialMessages != null && commercialMessages.size() > 0){
				ivMessageTriangle.setVisibility(View.GONE);
			}
			groupTitleTextView.setText(R.string.message_view_railway);
			tvMessageCount.setText(String.valueOf(railwayMessages.size()));
		}

		MessageAdapter messageAdapter = new MessageAdapter(this, massages);
		messageListView.setAdapter(messageAdapter);
		this.messageGroupLayout.addView(view);
	}
	
	public void bindAllView(){
		this.messageGroupLayout = (LinearLayout) findViewById(R.id.message_view_group_layout);
		messageGroupLayout.removeAllViews();
		this.rl_noMessages = (RelativeLayout) findViewById(R.id.rl_no_messages);
		if (!MobileMessageAsyncTask.isMessageFinished) {
			showWaitDialog();
			registerReceiver();
		}
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (LinearLayout) findViewById(R.id.ll_left_menus);
	}

	private void registerReceiver(){
		if (serviceStateReceiver == null) {
			serviceStateReceiver = new ServiceStateReceiver();
			registerReceiver(serviceStateReceiver, new IntentFilter(ServiceConstant.MESSAGE_SERVICE_ACTION));
		}
	}

	public void addAllListener(){
		
	}

	public void refresh(View view){
		Log.e("Messages",  "refresh...");
		MobileMessageAsyncTask mobileMessageAsyncTask = new MobileMessageAsyncTask(messageService, settingService.getCurrentLanguagesKey(), getApplicationContext());
		mobileMessageAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		showWaitDialog();
		registerReceiver();
	}
	/**
	 * Utility method for every Activity who needs to start this Activity.
	 * 
	 * @param context

	 */
	public static Intent createIntent(Context context, MobileMessageResponse mobileMessageResponse) {
		Intent intent = new Intent(context, MessageActivity.class);
		intent.putExtra(MESSAGE_RESPONSE_SERIALIZABLE_KEY, mobileMessageResponse);
		return intent;
	}
	
	private void showWaitDialog() {
		runOnUiThread(new Runnable() {
			public void run() {
				if (progressDialog == null) {
					//Log.e(TAG, "Show Wait Dialog....");
					progressDialog = ProgressDialog.show(MessageActivity.this, null,
							getString(R.string.message_view_loading), true);
					
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
	
	class ServiceStateReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			//Log.d(TAG, "onReceive got MESSAGE_SERVICE_ACTION broadcast");
			if (ServiceConstant.MESSAGE_SERVICE_ACTION.equalsIgnoreCase(intent.getAction().toString())) {
				hideWaitDialog();
				mobileMessageResponse = messageService.getMessageResponse();
				bindAllView();
				initValue();
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		if(serviceStateReceiver != null){
			unregisterReceiver(serviceStateReceiver);
		}
		if(timerTask != null){
			timerTask.cancel();
			timerTask = null;
		}
		if(timer != null){
			timer.cancel();
			timer = null;
		}
		super.onDestroy();
	}
	public void btnAction(View view){

	}


	public void messages(View view) {
		mDrawerLayout.closeDrawer(GravityCompat.END);
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
								startActivity(com.nmbs.activities.StationBoardActivity.createIntent(MessageActivity.this, null));
								isGoto = false;
								finish();
							}
						}
					});
		}
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
								startActivity(com.nmbs.activities.StationInfoActivity.createIntent(MessageActivity.this));
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
								startActivity(com.nmbs.activities.AlertActivity.createIntent(MessageActivity.this));
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
									//Utils.openProwser(MessageActivity.this, generalSetting.getBookingUrl(), clickToCallService);
									if(NetworkUtils.isOnline(MessageActivity.this)) {
										GoogleAnalyticsUtil.getInstance().sendScreen(MessageActivity.this, TrackerConstant.BOOKING);
										startActivity(WebViewActivity.createIntent(MessageActivity.this,
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
									//Utils.openProwser(MessageActivity.this, generalSetting.getLffUrl(), clickToCallService);
									if(NetworkUtils.isOnline(MessageActivity.this)) {
										GoogleAnalyticsUtil.getInstance().sendScreen(MessageActivity.this, TrackerConstant.LLF);
										startActivity(WebViewActivity.createIntent(MessageActivity.this,
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
								startActivity(ScheduleSearchActivity.createIntent(MessageActivity.this));
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
								startActivity(SettingsActivity.createIntent(MessageActivity.this));
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
								startActivity(WizardActivity.createIntent(MessageActivity.this, WizardActivity.Wizard_Home));
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
								startActivity(MyTicketsActivity.createIntent(MessageActivity.this));
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
								startActivity(UploadDossierActivity.createUploadDossierIntent(MessageActivity.this, NMBSApplication.PAGE_UploadTicket, null, null, null, null));
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
										GoogleAnalyticsUtil.getInstance().sendScreen(MessageActivity.this, TrackerConstant.CommercialTTLListUrl);
									}
								}else{
									DialogMyOptions dialogMyOptions = new DialogMyOptions(MessageActivity.this);
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
					GoogleAnalyticsUtil.getInstance().sendScreen(MessageActivity.this, TrackerConstant.CommercialTTLListUrl);
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
									GoogleAnalyticsUtil.getInstance().sendScreen(MessageActivity.this, TrackerConstant.ProfileOverviewUrl);
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
				GoogleAnalyticsUtil.getInstance().sendScreen(MessageActivity.this, TrackerConstant.ProfileOverviewUrl);
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
