package com.cfl.activities;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cfl.R;
import com.cfl.activity.BaseActivity;
import com.cfl.application.NMBSApplication;
import com.cfl.model.GeneralSetting;
import com.cfl.services.IClickToCallService;
import com.cfl.services.IMasterService;
import com.cfl.services.IMessageService;
import com.cfl.services.impl.ServiceConstant;
import com.cfl.services.impl.SettingService;
import com.cfl.services.impl.TestService;
import com.cfl.util.GoogleAnalyticsUtil;
import com.cfl.util.NetworkUtils;
import com.cfl.util.TrackerConstant;
import com.cfl.util.Utils;

public class MenuActivity extends BaseActivity {

	private SettingService settingService = null;
	private IMessageService messageService;
	private IMasterService masterService;
	private static final String Intent_key_ticketCount = "ticketCount";
	private static final String Intent_key_realtimeCount = "realtimeCount";
	private static final String Intent_key_messageCount = "messageCount";
	private int ticketCount, realtimeCount, messateCount;

	private IClickToCallService clickToCallService;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		settingService = ((NMBSApplication) getApplication()).getSettingService();
		//settingService.initLanguageSettings();
		messageService = ((NMBSApplication) getApplication()).getMessageService();
		masterService = ((NMBSApplication) getApplication()).getMasterService();
		clickToCallService = ((NMBSApplication) getApplication()).getClickToCallService();


		ticketCount = getIntent().getIntExtra(Intent_key_ticketCount, 0);
		realtimeCount = getIntent().getIntExtra(Intent_key_realtimeCount, 0);
		messateCount = getIntent().getIntExtra(Intent_key_messageCount, 0);
/*

		tvMenuTicketCount = (TextView) findViewById(R.id.tv_menu_ticket_count);
		tvMenuRealtimeCount = (TextView) findViewById(R.id.tv_menu_realtime_count);
		tvMenuMessageCount = (TextView) findViewById(R.id.tv_menu_message_count);
		tvMenuTicketCount.setText("(" + ticketCount + ")");
		tvMenuRealtimeCount.setText("(" + realtimeCount + ")");
		tvMenuMessageCount.setText("(" + messateCount + ")");*/

		GoogleAnalyticsUtil.getInstance().sendScreen(MenuActivity.this, TrackerConstant.NAVIGATION_MENU);
	}
	
	public void bookTicktes(View view){
		GeneralSetting generalSetting = masterService.loadGeneralSetting();
		//Log.d("bookTicket", "bookticket...");
		if(generalSetting != null && generalSetting.getBookingUrl() != null && !generalSetting.getBookingUrl().isEmpty()){
			//Utils.openProwser(this, generalSetting.getBookingUrl(), clickToCallService);
			if(NetworkUtils.isOnline(MenuActivity.this)) {
				GoogleAnalyticsUtil.getInstance().sendScreen(MenuActivity.this, TrackerConstant.BOOKING);
				startActivity(WebViewActivity.createIntent(MenuActivity.this,
						Utils.getUrl(generalSetting.getBookingUrl()), WebViewActivity.BOOKING_FLOW, ""));
			}
		}
		finishMenu();
	}	
	
	public void lowestFares(View view){
		GeneralSetting generalSetting = masterService.loadGeneralSetting();
		if(generalSetting != null && generalSetting.getLffUrl()!= null && !generalSetting.getLffUrl().isEmpty()){
			//Utils.openProwser(this, generalSetting.getLffUrl(), clickToCallService);
			if(NetworkUtils.isOnline(MenuActivity.this)) {
				GoogleAnalyticsUtil.getInstance().sendScreen(MenuActivity.this, TrackerConstant.LLF);
				startActivity(WebViewActivity.createIntent(MenuActivity.this,
						Utils.getUrl(generalSetting.getLffUrl()), WebViewActivity.BOOKING_FLOW, ""));
			}

		}
		finishMenu();
	}
	
	public void myTickets(View view){
		startActivity(MyTicketsActivity.createIntent(MenuActivity.this));
		finishMenu();
	}	
	
	public void uploadTickets(View view){
		startActivity(UploadDossierActivity.createUploadDossierIntent(this, NMBSApplication.PAGE_UploadTicket, null, null, null, null));
		finishMenu();
	}	
	
	/*public void realtimeAlerts(View view){
		startActivity(com.nmbs.activities.AlertActivity.createIntent(this));
		finishMenu();
	}	*/
	
/*	public void stationBoard(View view){
		startActivity(com.nmbs.activities.StationBoardActivity.createIntent(MenuActivity.this));
		finishMenu();
	}	*/
	
	public void trainschedules(View view){
		startActivity(ScheduleSearchActivity.createIntent(this));
		finishMenu();
	}	
	
/*	public void stations(View view){
		startActivity(com.nmbs.activities.StationInfoActivity.createIntent(MenuActivity.this));
		finishMenu();
	}*/
	
/*	public void messages(View view){
		startActivity(com.nmbs.activities.MessageActivity.createIntent(MenuActivity.this, messageService.getMessageResponse()));
		finishMenu();
	}	*/
	
	public void settings(View view){
		
		startActivity(SettingsActivity.createIntent(this));
		finishMenu();
	}	
	
	public void about(View view){
		GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.NAVIGATION_CATEGORY,TrackerConstant.NAVIGATION_SELECT_WIZARD,"");
		startActivity(WizardActivity.createIntent(this, WizardActivity.Wizard_Home));
		finishMenu();
	}

	public void test(View view){
		startActivity(TestActivity.createIntent(this));
	}

	public void close(View view){
		finishMenu();
	}
	
	private void finishMenu(){
		finish();
	}
	
	public static Intent createIntent(Context context, int ticketCount, int realtimeCount, int messageCount) {
		Intent intent = new Intent(context, MenuActivity.class);
		intent.putExtra(Intent_key_ticketCount, ticketCount);
		intent.putExtra(Intent_key_realtimeCount, realtimeCount);
		intent.putExtra(Intent_key_messageCount, messageCount);
		return intent;
	}
}
