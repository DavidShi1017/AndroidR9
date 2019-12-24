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
import com.nmbs.log.LogUtils;
import com.nmbs.model.GeneralSetting;
import com.nmbs.model.MobileMessage;
import com.nmbs.model.MobileMessageResponse;
import com.nmbs.services.IClickToCallService;
import com.nmbs.services.IMasterService;
import com.nmbs.services.IMessageService;
import com.nmbs.services.impl.ServiceConstant;
import com.nmbs.services.impl.SettingService;
import com.nmbs.util.FunctionConfig;
import com.nmbs.util.GoogleAnalyticsUtil;
import com.nmbs.util.MenuUtil;
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
			LogUtils.e("mobileMessageResponse", "functionalMessages..." + functionalMessages.size());
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
		LogUtils.e("Messages",  "refresh...");
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
		if(isFinishing()){
			return;
		}
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
