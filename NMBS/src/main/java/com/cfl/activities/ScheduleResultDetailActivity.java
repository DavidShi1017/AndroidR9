package com.cfl.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cfl.R;
import com.cfl.activities.view.DialogAlertError;
import com.cfl.activity.BaseActivity;
import com.cfl.adapter.ScheduleDetailLegAdapter;
import com.cfl.adapter.ScheduleHafasMessageAdapter;
import com.cfl.application.NMBSApplication;
import com.cfl.async.CreateSubScriptionAsyncTask;
import com.cfl.async.ScheduleRefreshAsyncTask;
import com.cfl.model.Dossier;
import com.cfl.model.DossierDetailsResponse;
import com.cfl.model.DossierSummary;
import com.cfl.model.RealTimeConnection;
import com.cfl.model.ScheduleDetailRefreshModel;
import com.cfl.model.Subscription;
import com.cfl.model.TrainIcon;
import com.cfl.services.IPushService;
import com.cfl.services.IScheduleService;
import com.cfl.services.impl.DossierDetailsService;
import com.cfl.services.impl.ServiceConstant;
import com.cfl.services.impl.SettingService;
import com.cfl.util.ActivityConstant;
import com.cfl.util.DateUtils;
import com.cfl.util.GoogleAnalyticsUtil;
import com.cfl.util.TrackerConstant;
import com.cfl.util.Utils;

import java.util.Date;
import java.util.List;

/**
 * Activity used for displaying the UI element, user can do some inner behavior with this.
 *
 * User can select a category to see the details.
 */
public class ScheduleResultDetailActivity extends BaseActivity {
	private final static String TAG = StationboardSearchResultActivity.class.getSimpleName();
	private static boolean isDisplayAlert = true;
	private RealTimeConnection currentRealTimeConnection;
	private TextView stationNameView, departureTimeView, transferView, tvTitle;
	private ImageView menuView;
	private LinearLayout hafasMessageLinearLayout, legLinearLayout, setAlertSuccessLayout, bottomActionView;
	private Button setTrainAlertButton;
	private ScheduleHafasMessageAdapter scheduleHafasMessageAdapter;
	private ScheduleDetailLegAdapter scheduleDetailLegAdapter;

	private IScheduleService scheduleService;
	private View vLine;
	private DossierDetailsService dossierDetailsService;
	private IPushService pushService;
	private ProgressDialog progressDialog;
	private DialogAlertError dialogError;
	private ServiceCreateSubscriptionReceiver serviceCreateSubscriptionReceiver;
	private boolean isRefresh;
	private ScheduleServiceStateReceiver scheduleServiceStateReceiver;
	private SettingService settingService;
	private List<TrainIcon> trainIconList ;
	private Subscription subscription;
	private String subscriptionId;
	private boolean isDnrRealted = false;
	private final static String Intent_Key_Page = "Page";
	private int page;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Utils.setToolBarStyle(this);
		settingService = ((NMBSApplication) getApplication()).getSettingService();
		//settingService.initLanguageSettings();
		setContentView(R.layout.activity_schedule_result_detail);
		bindAllViewElement();
		initService();
		registerReceiver();
		createSubScriptionReceiver();
		getIntentValue(getIntent());
		initData();
		GoogleAnalyticsUtil.getInstance().sendScreen(ScheduleResultDetailActivity.this, TrackerConstant.SCHEDULE_ConnectionDetails);
	}

	private void initService(){
		scheduleService = ((NMBSApplication)getApplication()).getScheduleService();
		settingService = ((NMBSApplication)getApplication()).getSettingService();
		this.pushService = ((NMBSApplication)getApplication()).getPushService();
	}

	private void initData(){
		if (page == NMBSApplication.PAGE_PUSH){
			tvTitle.setText(getResources().getString(R.string.push_title));
			vLine.setVisibility(View.GONE);
		}else if(page == NMBSApplication.PAGE_ALERT){
			tvTitle.setText(getResources().getString(R.string.general_realtime_alerts));
			vLine.setVisibility(View.VISIBLE);
		}else{
			tvTitle.setText(getResources().getString(R.string.schedule_title));
			vLine.setVisibility(View.GONE);
		}
		trainIconList = scheduleService.getTrainIcons();
		if(this.currentRealTimeConnection != null){
			this.stationNameView.setText(this.currentRealTimeConnection.getOriginStationName()+" - "+this.currentRealTimeConnection.getDestinationStationName());
			this.departureTimeView.setText(DateUtils.dateTimeToString(this.currentRealTimeConnection.getDeparture(), "EEEE dd MMMM yyyy"));
			if(this.currentRealTimeConnection.getNumberOfTransfers() == 0){

				this.transferView.setText(this.getString(R.string.general_direct_train) + " - " + DateUtils.FormatToHrDate(currentRealTimeConnection.getDuration(), getApplicationContext()));
			}else{
				if(this.currentRealTimeConnection.getNumberOfTransfers() == 1){
					transferView.setText(currentRealTimeConnection.getNumberOfTransfers() + " "+getString(R.string.general_transfer)+ " - " + DateUtils.FormatToHrDate(currentRealTimeConnection.getDuration(), getApplicationContext()));
				}else{
					transferView.setText(currentRealTimeConnection.getNumberOfTransfers() + " "+getString(R.string.general_transfers)+ " - " + DateUtils.FormatToHrDate(currentRealTimeConnection.getDuration(), getApplicationContext()));

				}
			}
			this.hafasMessageLinearLayout.removeAllViews();
/*			HafasMessage hafasMessage = new HafasMessage("DAVID", "Shi", "scheduleHafasMessageAdapter,", " currentRealTimeConnection", "2016-08-15", "2016-08-16");
			HafasMessage hafasMessage2 = new HafasMessage("DAVID", "Shi", "scheduleHafasMessageAdapter,", " currentRealTimeConnection", "2016-08-15", "2016-08-16");

			List<HafasMessage> hafasMessages = new ArrayList<>();
			hafasMessages.add(hafasMessage);
			hafasMessages.add(hafasMessage2);
			currentRealTimeConnection.setHafasMessages(hafasMessages);*/
			scheduleHafasMessageAdapter = new ScheduleHafasMessageAdapter(this,this.currentRealTimeConnection.getHafasMessages());

			for(int i=0;i<this.currentRealTimeConnection.getHafasMessages().size();i++){
				scheduleHafasMessageAdapter.getScheduleHafasMessageView(i, this.hafasMessageLinearLayout);
			}
			this.legLinearLayout.removeAllViews();
			scheduleDetailLegAdapter = new ScheduleDetailLegAdapter(this,this.currentRealTimeConnection.getRealTimeInfoLegs(),trainIconList);
			for(int j=0;j<this.currentRealTimeConnection.getRealTimeInfoLegs().size();j++){
				scheduleDetailLegAdapter.getScheduleDetailLegView(j,this.legLinearLayout);
			}
			try{
				if(isDisplayAlert){
					this.bottomActionView.setVisibility(View.VISIBLE);
					boolean isHasSubscription = this.pushService.getSubscriptionFromLocal(Utils.sha1(currentRealTimeConnection.getReconCtx()));
					//Log.e("Subscription", "Subscription====" + isHasSubscription);
					if(isHasSubscription){
						this.setAlertSuccessLayout.setVisibility(View.VISIBLE);
						this.setTrainAlertButton.setVisibility(View.GONE);
					}
				}
			}catch (Exception e){
				e.printStackTrace();
			}

			if(isDnrRealted){
				this.bottomActionView.setVisibility(View.VISIBLE);
				this.setAlertSuccessLayout.setVisibility(View.GONE);
				this.setTrainAlertButton.setVisibility(View.VISIBLE);
				this.setTrainAlertButton.setText(getString(R.string.push_booking_details));
			}

		}
	}

	private void bindAllViewElement(){
		this.stationNameView = (TextView) findViewById(R.id.tv_schedule_result_detail_station_name);
		this.departureTimeView = (TextView) findViewById(R.id.tv_schedule_detail_station_departure_time);
		this.transferView = (TextView) findViewById(R.id.tv_schedule_detail_transfer);
		this.hafasMessageLinearLayout = (LinearLayout) findViewById(R.id.ll_schedule_detail_hafas_message_layout);
		this.legLinearLayout = (LinearLayout) findViewById(R.id.ll_schedule_detail_leg_layout);

		this.setAlertSuccessLayout = (LinearLayout) findViewById(R.id.ll_set_train_alert_success);
		this.setTrainAlertButton = (Button) findViewById(R.id.btn_set_train_alert);
		this.menuView = (ImageView) findViewById(R.id.iv_schedule_detail_menu);
		this.bottomActionView = (LinearLayout) findViewById(R.id.ll_schedule_result_detail_bottom);
		this.tvTitle = (TextView) findViewById(R.id.tv_title);
		this.vLine = (View) findViewById(R.id.v_bottom_line);
	}

	public void getIntentValue(Intent intent){
		currentRealTimeConnection = (RealTimeConnection) intent.getSerializableExtra(ActivityConstant.SCHEDULE_DETAIL);
		page = intent.getIntExtra(Intent_Key_Page, 0);
		RealTimeConnection pushRealTimeConnection = (RealTimeConnection)intent.getSerializableExtra(ActivityConstant.RECEIVE_PUSH_CONNECTION);
		subscriptionId = intent.getStringExtra(ActivityConstant.RECEIVE_PUSH_SUBSCRIPTION_ID);
		isDnrRealted = intent.getBooleanExtra(ActivityConstant.RECEIVE_PUSH_IS_DNR_RELATED,false);

		if(pushRealTimeConnection != null){
			currentRealTimeConnection = pushRealTimeConnection;
			//this.menuView.setVisibility(View.VISIBLE);
		}
	}

	public static Intent createIntent(Context context,RealTimeConnection realTimeConnection, int page){

		Intent intent = new Intent(context, ScheduleResultDetailActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		intent.putExtra(ActivityConstant.SCHEDULE_DETAIL, realTimeConnection);
		intent.putExtra(Intent_Key_Page, page);
		isDisplayAlert = true;
		return intent;
	}

	public static Intent createPushIntent(Context context,String sid, RealTimeConnection realTimeConnection,boolean isDnrRelated, int page){
		Intent intent = new Intent(context, ScheduleResultDetailActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		intent.putExtra(ActivityConstant.RECEIVE_PUSH_SUBSCRIPTION_ID, sid);
		intent.putExtra(ActivityConstant.RECEIVE_PUSH_CONNECTION, realTimeConnection);
		intent.putExtra(ActivityConstant.RECEIVE_PUSH_IS_DNR_RELATED,isDnrRelated);
		intent.putExtra(Intent_Key_Page, page);
		isDisplayAlert = false;
		return intent;
	}

	public void clickMenu(View view){
		startActivity(SettingsActivity.createIntent(this));
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

	private void createSubScriptionReceiver(){
		if (serviceCreateSubscriptionReceiver == null) {
			serviceCreateSubscriptionReceiver = new ServiceCreateSubscriptionReceiver();
			registerReceiver(serviceCreateSubscriptionReceiver, new IntentFilter(ServiceConstant.PUSH_CREATE_SUBSCRIPTION_ACTION));
		}
	}

	@Override
	protected void onDestroy() {
		if(scheduleServiceStateReceiver != null){
			unregisterReceiver(scheduleServiceStateReceiver);
		}

		if(serviceCreateSubscriptionReceiver != null){
			unregisterReceiver(serviceCreateSubscriptionReceiver);
		}

		super.onDestroy();
	}

	private void registerReceiver(){
		if (scheduleServiceStateReceiver == null) {
			scheduleServiceStateReceiver = new ScheduleServiceStateReceiver();
			registerReceiver(scheduleServiceStateReceiver, new IntentFilter(ServiceConstant.SCHEDULE_DETAIL_SERVICE_ACTION));
		}
	}

	public void refresh(View view){
		isRefresh = true;
		refreshDetailData(this.currentRealTimeConnection.getReconCtx(),this.currentRealTimeConnection.getDeparture());
	}

	private void refreshDetailData(String connectionId,Date departure){
		ScheduleDetailRefreshModel scheduleDetailRefreshModel = new ScheduleDetailRefreshModel(connectionId,departure);
		ScheduleRefreshAsyncTask scheduleRefreshAsyncTask = new ScheduleRefreshAsyncTask(scheduleService,settingService,this);
		scheduleRefreshAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, scheduleDetailRefreshModel);
		showWaitDialog();
	}

	class ScheduleServiceStateReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (ServiceConstant.SCHEDULE_DETAIL_SERVICE_ACTION.equalsIgnoreCase(intent.getAction().toString())) {
				RealTimeConnection realTimeConnection = (RealTimeConnection)intent.getSerializableExtra(ActivityConstant.SCHEDULE_QUERY_REFRESH);
				if(realTimeConnection != null){
					currentRealTimeConnection = realTimeConnection;
					initData();
				}else{
					isRefresh = false;
					dialogError = new DialogAlertError(ScheduleResultDetailActivity.this, getResources().getString(R.string.general_information), getResources().getString(R.string.schedule_set_train_alert_failure_info));
					dialogError.show();
				}
				hideWaitDialog();
			}
		}
	}

	private void showWaitDialog() {
		runOnUiThread(new Runnable() {
			public void run() {
				if (progressDialog == null) {
					//Log.e(TAG, "Show Wait Dialog....");
					progressDialog = ProgressDialog.show(ScheduleResultDetailActivity.this, null,
							getString(R.string.general_loading), true);
					progressDialog.setCanceledOnTouchOutside(false);

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


	public void back(View view){
		if(isRefresh){
			finish();
			//startActivity(ScheduleSearchActivity.createIntent(this,true));
		}else{
			finish();
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(isRefresh){
				finish();
				//startActivity(ScheduleSearchActivity.createIntent(this,true));
			}else{
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	class ServiceCreateSubscriptionReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			hideWaitDialog();
			String subscriptionId = intent.getStringExtra(ActivityConstant.CREATE_SUBSCRIPTION_RESULT);
			if(!subscriptionId.equals("")){
				setTrainAlertButton.setVisibility(View.GONE);
				setAlertSuccessLayout.setVisibility(View.VISIBLE);
				try{
					pushService.saveSubscriptionInLocal(new Subscription(subscriptionId,Utils.sha1(currentRealTimeConnection.getReconCtx()),currentRealTimeConnection.getReconCtx(),currentRealTimeConnection.getOriginStationRcode(),currentRealTimeConnection.getDestinationStationRcode(),
							DateUtils.getDateWithTimeZone(currentRealTimeConnection.getDeparture()),currentRealTimeConnection.getOriginStationName(),currentRealTimeConnection.getDestinationStationName(),"",""));
				}catch (Exception e){
					e.printStackTrace();
				}
			}else{
				dialogError = new DialogAlertError(ScheduleResultDetailActivity.this, getResources().getString(R.string.schedule_set_train_alert_failure_title), getResources().getString(R.string.schedule_set_train_alert_failure_info));
				dialogError.show();
			}
		}
	}

	public void createSubscription(View view){

		if(((Button)view).getText().equals(getString(R.string.push_booking_details))){
			subscription = pushService.getSubscriptionById(subscriptionId);
			dossierDetailsService = new DossierDetailsService(this);
			DossierSummary dossierSummary = dossierDetailsService.getDossier(subscription.getDnr());
			if(dossierSummary != null) {
				DossierDetailsResponse dossierResponse = dossierDetailsService.getDossierDetail(dossierSummary);
				if (dossierResponse != null) {
					Dossier dossier = dossierResponse.getDossier();
					dossierDetailsService.setCurrentDossier(null);
					dossierDetailsService.setCurrentDossierSummary(null);
					startActivity(DossierDetailActivity.createIntent(this,dossier,dossierSummary));
				}
			}
		}else{
			GoogleAnalyticsUtil.getInstance().sendEvent(TrackerConstant.SCHEDULE_CATEGORY,TrackerConstant.SCHEDULE_SET_TRAIN_ALERT,"");
			this.showWaitDialog();
			CreateSubScriptionAsyncTask asyncTask = new CreateSubScriptionAsyncTask(pushService,this.currentRealTimeConnection,settingService.getCurrentLanguagesKey(),getApplicationContext());
			asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}
}
