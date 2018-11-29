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
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nmbs.R;
import com.nmbs.activities.view.DialogAlertError;
import com.nmbs.activities.view.DialogMyOptions;
import com.nmbs.activity.BaseActivity;
import com.nmbs.adapter.AlertConnectionAdapter;
import com.nmbs.application.NMBSApplication;
import com.nmbs.async.GetSubScriptionListAsyncTask;
import com.nmbs.async.ProfileInfoAsyncTask;
import com.nmbs.model.GeneralSetting;
import com.nmbs.model.HafasUser;
import com.nmbs.model.Subscription;
import com.nmbs.services.IClickToCallService;
import com.nmbs.services.IMasterService;
import com.nmbs.services.IMessageService;
import com.nmbs.services.IPushService;
import com.nmbs.services.impl.ServiceConstant;
import com.nmbs.services.impl.SettingService;
import com.nmbs.util.DateUtils;
import com.nmbs.util.GoogleAnalyticsUtil;
import com.nmbs.util.NetworkUtils;
import com.nmbs.util.TrackerConstant;
import com.nmbs.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Activity used for displaying the UI element, user can do some inner behavior with this.
 *
 * User can select a category to see the details.
 */
public class AlertActivity extends BaseActivity {
	private final static String TAG = AlertActivity.class.getSimpleName();
	private IPushService pushService;
	private AlertConnectionAdapter alertConnectionAdapter;
	private LinearLayout dnrReferenceLayout;
	private LinearLayout connectionLayout;
	private TextView connectionBtn, dnrReferenceBtn, notAlertTextView;
	private RelativeLayout alertArrow;
	private TextView tvAlertHeader, tvNoConnecgionLayout;
	private AlertGetServiceStateReceiver alertGetServiceStateReceiver;
	private List<Subscription> subscriptionList;
	private List<Subscription> notDnrSubscriptionList = new ArrayList<Subscription>();
	private Map<String,List<Subscription>> dnrSubScriptionList = new LinkedHashMap<>();
	private ImageView deleteButtonView, ivRefresh;
	private ProgressDialog progressDialog;
	private boolean isDeleteDnrReference;
	private Subscription currentDeleteSubscription;
	private String currentDeleteDnr;
	private DialogAlertError dialogError;
	private HafasUser hafasUser;
	private boolean isDeleteAll = false;
	private SettingService settingService;
	private boolean isDnrRerence;
	private boolean isShowDelete;
	private boolean isDnr = true;
	private Timer timer;
	private TimerTask timerTask;
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
		setContentView(R.layout.activity_alert);
		getIntentValue();
		bindService();
		bindView();
		initData();
		GoogleAnalyticsUtil.getInstance().sendScreen(AlertActivity.this, TrackerConstant.ALERT_LIST);
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(progressDialog != null){
							subscriptionList = pushService.readAllSubscriptions();
							calculateSubscriptionList();
							alertConnectionAdapter = new AlertConnectionAdapter(AlertActivity.this,notDnrSubscriptionList,dnrSubScriptionList);
							refreshConnectionList(isShowDelete);
							hideWaitDialog();
						}
					}
				});

			}
		};
		timer.schedule(timerTask, 15000);
	}


	public void goToScheduleDetail(String subscriptionId){
		startActivity(PushNotificationErrorActivity.createIntent(this, subscriptionId, NMBSApplication.PAGE_ALERT));
	}

	public void refreshAllSubscription(View view){
		showWaitDialog();
		RefreshAllSubscriptionAsync refreshAllSubscriptionAsync = new RefreshAllSubscriptionAsync();
		refreshAllSubscriptionAsync.execute((Void)null);
	}

	private void bindService(){
		this.pushService = ((NMBSApplication) getApplication()).getPushService();
		this.settingService = ((NMBSApplication) getApplication()).getSettingService();
	}

	private void bindView(){
		this.dnrReferenceLayout = (LinearLayout) findViewById(R.id.ll_alert_delete);
		this.connectionLayout = (LinearLayout) findViewById(R.id.ll_alert_connection_layout);
		this.connectionBtn = (TextView) findViewById(R.id.tv_alert_connection_btn);
		this.dnrReferenceBtn = (TextView) findViewById(R.id.tv_alert_dnr_reference_connection_btn);

		this.tvAlertHeader = (TextView) findViewById(R.id.tv_alert_header);
		this.alertArrow = (RelativeLayout) findViewById(R.id.rl_alert_arrow);
		this.tvNoConnecgionLayout = (TextView) findViewById(R.id.tv_alert_no_connections);
		this.notAlertTextView = (TextView) findViewById(R.id.tv_alert_no_connections);
		this.deleteButtonView = (ImageView) findViewById(R.id.iv_alert_header_delete);
		this.ivRefresh = (ImageView) findViewById(R.id.iv_refresh);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (LinearLayout) findViewById(R.id.ll_left_menus);
	}

	private void initData(){
		if(alertGetServiceStateReceiver == null){
			alertGetServiceStateReceiver = new AlertGetServiceStateReceiver();
			registerReceiver(alertGetServiceStateReceiver, new IntentFilter(ServiceConstant.PUSH_GET_SUBSCRIPTION_LIST_ACTION));
		}
		hafasUser = pushService.getUser();
		showWaitDialog();
		GetSubScriptionListAsyncTask asyncTask = new GetSubScriptionListAsyncTask(pushService,settingService.getCurrentLanguagesKey(),getApplicationContext());
		asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private void calculateSubscriptionList(){
		this.notDnrSubscriptionList.clear();
		this.dnrSubScriptionList.clear();
		this.subscriptionList = this.pushService.readAllSubscriptions();
		for(Subscription subscription:this.subscriptionList){
			if(!subscription.getDnr().equals("")){
				if(dnrSubScriptionList.containsKey(subscription.getDnr())){
					dnrSubScriptionList.get(subscription.getDnr()).add(subscription);
					Collections.sort(dnrSubScriptionList.get(subscription.getDnr()), new Comparator<Subscription>() {
						public int compare(Subscription o1, Subscription o2) {
							Date tempDate1 = DateUtils.stringToDateTime(o1.getDeparture());
							Date tempDate2 = DateUtils.stringToDateTime(o2.getDeparture());
							if (tempDate1.getTime() - tempDate2.getTime() > 0) {
								return 1;
							} else {
								return -1;
							}
						}
					});
				}else{
					List<Subscription> subscriptions = new ArrayList<Subscription>();
					subscriptions.add(subscription);
					Collections.sort(subscriptions, new Comparator<Subscription>() {
						public int compare(Subscription o1, Subscription o2) {
							Date tempDate1 = DateUtils.stringToDateTime(o1.getDeparture());
							Date tempDate2 = DateUtils.stringToDateTime(o2.getDeparture());
							if (tempDate1.getTime() - tempDate2.getTime() > 0) {
								return 1;
							} else {
								return -1;
							}
						}
					});
					dnrSubScriptionList.put(subscription.getDnr(), subscriptions);
				}

			}else{
				notDnrSubscriptionList.add(subscription);
			}
		}
		if(dnrSubScriptionList.keySet().size() >0){
			//Log.e("Subscription", "Subscription size is:::" + dnrSubScriptionList.keySet().size());
			//dnrView();
			//isDnr = true;
			if(isDnr){
				dnrView();
				//connectionView();
			}else{
				connectionView();
			}
		}else{
			isDnr = false;
			connectionView();
		}
		Collections.sort(notDnrSubscriptionList, new Comparator<Subscription>() {
			public int compare(Subscription o1, Subscription o2) {
				Date tempDate1 = DateUtils.stringToDateTime(o1.getDeparture());
				Date tempDate2 = DateUtils.stringToDateTime(o2.getDeparture());
				if (tempDate1.getTime() - tempDate2.getTime() > 0) {
					return 1;
				} else {
					return -1;
				}
			}
		});
	}

	private void refreshConnectionList(boolean isShowDelete){

		if(!isDnr){
			if(this.notDnrSubscriptionList.size()==0){
				this.notAlertTextView.setVisibility(View.VISIBLE);
				ivRefresh.setVisibility(View.GONE);
				this.deleteButtonView.setVisibility(View.GONE);
			}else{
				this.notAlertTextView.setVisibility(View.GONE);
				ivRefresh.setVisibility(View.VISIBLE);
				this.deleteButtonView.setVisibility(View.VISIBLE);
			}
		}else{
			if(this.dnrSubScriptionList.size()==0){
				this.notAlertTextView.setVisibility(View.VISIBLE);
				ivRefresh.setVisibility(View.GONE);
				this.deleteButtonView.setVisibility(View.GONE);
			}else{
				this.notAlertTextView.setVisibility(View.GONE);
				this.deleteButtonView.setVisibility(View.VISIBLE);
				ivRefresh.setVisibility(View.VISIBLE);
			}
		}
		refreshDnrSubscriptionList(isShowDelete);
		//Log.e(TAG, "isShowDelete...refreshConnectionList..." + isShowDelete);
		refreshNotDnrSubscriptionList(isShowDelete);
	}

	private void refreshDnrSubscriptionList(boolean isShowDelete){
		this.dnrReferenceLayout.removeAllViews();

		//Log.e(TAG, "isShowDelete...dnrSubScriptionList size..." + dnrSubScriptionList.size());
		if(dnrSubScriptionList != null && dnrSubScriptionList.size() > 0){
			if(isShowDelete == true){
				this.tvAlertHeader.setVisibility(View.GONE);
				this.alertArrow.setVisibility(View.GONE);
			}

		}else {
			this.tvAlertHeader.setVisibility(View.VISIBLE);
			this.alertArrow.setVisibility(View.VISIBLE);
		}
		if(dnrSubScriptionList != null && dnrSubScriptionList.size() > 0){
			for(int j=0;j<this.dnrSubScriptionList.size();j++){
				alertConnectionAdapter.getAlertDnrReferenceView(j,this.dnrReferenceLayout);
			}
		}
	}

	private void refreshNotDnrSubscriptionList(boolean isShowDelete){
		this.connectionLayout.removeAllViews();
		if(notDnrSubscriptionList != null && notDnrSubscriptionList.size() > 0){
			if(isShowDelete == true){

				this.tvAlertHeader.setVisibility(View.GONE);
				this.alertArrow.setVisibility(View.GONE);
			}
		}else {

			this.tvAlertHeader.setVisibility(View.VISIBLE);
			this.alertArrow.setVisibility(View.VISIBLE);
		}
		if(notDnrSubscriptionList != null && notDnrSubscriptionList.size() > 0){
			for(int i=0;i<this.notDnrSubscriptionList.size();i++){
				alertConnectionAdapter.getAlertConnectionView(i, this.connectionLayout);
			}
		}
	}

	/*public void changeToDnrReferenceList(View view){
		Log.e(TAG, "changeToDnrReferenceList...");
		this.connectionBtn.setBackgroundResource(R.color.background_group_title);
		this.dnrReferenceBtn.setBackgroundResource(R.color.background_secondaryaction);
		this.connectionLayout.setVisibility(View.VISIBLE);
		this.dnrReferenceLayout.setVisibility(View.GONE);

		if(this.notDnrSubscriptionList.size()==0){
			this.notAlertTextView.setVisibility(View.VISIBLE);
			this.deleteButtonView.setVisibility(View.GONE);
			ivRefresh.setVisibility(View.GONE);
		}else{
			this.notAlertTextView.setVisibility(View.GONE);
			this.deleteButtonView.setVisibility(View.VISIBLE);
			ivRefresh.setVisibility(View.VISIBLE);
		}
		isDeleteAll = false; // to monitor delete action in dossier detail
		currentTab = 0;
		isDnr = false;
	}*/

	private void connectionView(){
		this.connectionBtn.setBackgroundResource(R.color.background_group_title);
		this.dnrReferenceBtn.setBackgroundResource(R.color.background_secondaryaction);
		this.connectionLayout.setVisibility(View.VISIBLE);
		this.dnrReferenceLayout.setVisibility(View.GONE);

		if(this.notDnrSubscriptionList.size()==0){
			this.notAlertTextView.setVisibility(View.VISIBLE);
			this.deleteButtonView.setVisibility(View.GONE);
			ivRefresh.setVisibility(View.GONE);
		}else{
			this.notAlertTextView.setVisibility(View.GONE);
			this.deleteButtonView.setVisibility(View.VISIBLE);
			ivRefresh.setVisibility(View.VISIBLE);
		}
		isDeleteAll = false; // to monitor delete action in dossier detail
		//currentTab = 0;
		//isDnr = false;
	}

	private void dnrView(){
		//Log.e("SubScription", "Show dnrView");
		this.connectionBtn.setBackgroundResource(R.color.background_secondaryaction);
		this.dnrReferenceBtn.setBackgroundResource(R.color.background_group_title);
		this.connectionLayout.setVisibility(View.GONE);
		this.dnrReferenceLayout.setVisibility(View.VISIBLE);
		if(this.dnrSubScriptionList.keySet().size()==0){
			this.notAlertTextView.setVisibility(View.VISIBLE);
			this.deleteButtonView.setVisibility(View.GONE);
			ivRefresh.setVisibility(View.GONE);
		}else{
			this.notAlertTextView.setVisibility(View.GONE);
			this.deleteButtonView.setVisibility(View.VISIBLE);
			ivRefresh.setVisibility(View.VISIBLE);
		}
		isDeleteAll = true;
		//currentTab = 1; // to monitor delete action in dossier detail
		//isDnr = true;
	}


/*	public void changeToConnectionList(View view){
		Log.e(TAG, "changeToConnectionList...");
		dnrView();
		this.connectionBtn.setBackgroundResource(R.color.background_secondaryaction);
		this.dnrReferenceBtn.setBackgroundResource(R.color.background_group_title);
		this.connectionLayout.setVisibility(View.GONE);
		this.dnrReferenceLayout.setVisibility(View.VISIBLE);
		if(this.dnrSubScriptionList.keySet().size()==0){
			this.notAlertTextView.setVisibility(View.VISIBLE);
			this.deleteButtonView.setVisibility(View.GONE);
			ivRefresh.setVisibility(View.GONE);
		}else{
			this.notAlertTextView.setVisibility(View.GONE);
			this.deleteButtonView.setVisibility(View.VISIBLE);
			ivRefresh.setVisibility(View.VISIBLE);
		}
		isDeleteAll = true;
		currentTab = 1; // to monitor delete action in dossier detail
		isDnr = true;
	}*/

	public void trainAlerts(View view){
		isDnr = false;
		connectionView();
	}
	public void bookingAlerts(View view){
		isDnr = true;
		dnrView();
	}
	public void getIntentValue(){


	}

	public static Intent createIntent(Context context){
		Intent intent = new Intent(context, AlertActivity.class);
		//intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

		return intent;
	}

	public void deleteAlert(View view){
		if(isDnr){
			startActivity(AlertDeleteDnrActivity.createIntent(getApplicationContext(), isDnr));
		}else{
			startActivity(AlertDeleteActivity.createIntent(getApplicationContext(), isDnr));
		}

		/*if(isShowDelete == false){
			isShowDelete = true;
		}else {
			isShowDelete = false;
		}
		if(this.deleteHeaderLayout.getVisibility() == View.GONE){
			this.deleteHeaderLayout.setVisibility(View.VISIBLE);
			this.tvAlertHeader.setVisibility(View.GONE);
			this.alertArrow.setVisibility(View.GONE);
			refreshConnectionList(isShowDelete);
		}else{
			this.deleteHeaderLayout.setVisibility(View.GONE);
			this.tvAlertHeader.setVisibility(View.VISIBLE);
			this.alertArrow.setVisibility(View.VISIBLE);
			refreshConnectionList(isShowDelete);
		}*/
	}

	@Override
	protected void onPause() {
		//Log.d(TAG, "onPause");
		super.onPause();
	}

	@Override
	protected void onResume() {
		//Log.d(TAG, "onResume");
		bindView();
		if(GetSubScriptionListAsyncTask.isAlertSubscriptionFinished){
			subscriptionList = pushService.readAllSubscriptions();
			calculateSubscriptionList();
			alertConnectionAdapter = new AlertConnectionAdapter(AlertActivity.this,notDnrSubscriptionList,dnrSubScriptionList);
			refreshConnectionList(isShowDelete);
		}

		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if (alertGetServiceStateReceiver != null) {
			unregisterReceiver(alertGetServiceStateReceiver);
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

	private void showWaitDialog() {
		if(isFinishing()){
			return;
		}
		runOnUiThread(new Runnable() {
			public void run() {
				if (progressDialog == null) {
					//Log.e(TAG, "Show Wait Dialog....");
					progressDialog = ProgressDialog.show(AlertActivity.this, null,
							getString(R.string.realtime_alerts_loading_alerts), true);
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
				if (progressDialog != null && progressDialog.isShowing()) {
					//Log.e(TAG, "progressDialog is.... " + progressDialog);
					progressDialog.dismiss();
					progressDialog = null;
				}
			}
		});

	}

	class RefreshAllSubscriptionAsync extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try{
				boolean isOk = pushService.getSubscriptionsByUserId(hafasUser.getUserId());
				if(isOk){
					subscriptionList = pushService.readAllSubscriptions();
					calculateSubscriptionList();
					refreshConnectionList(isShowDelete);
				}else{
					if(!isFinishing()){
						dialogError = new DialogAlertError(AlertActivity.this, getResources().getString(R.string.general_information),
								getResources().getString(R.string.alert_subscription_missingID));
						dialogError.show();
					}

				}

			}catch (Exception e){

				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			hideWaitDialog();
			super.onPostExecute(result);
		}


	}

	class AlertGetServiceStateReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (ServiceConstant.PUSH_GET_SUBSCRIPTION_LIST_ACTION.equalsIgnoreCase(intent.getAction().toString())) {
				subscriptionList = pushService.readAllSubscriptions();
				calculateSubscriptionList();
				alertConnectionAdapter = new AlertConnectionAdapter(AlertActivity.this,notDnrSubscriptionList,dnrSubScriptionList);
				refreshConnectionList(isShowDelete);
				hideWaitDialog();
			}

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
								startActivity(com.nmbs.activities.MessageActivity.createIntent(AlertActivity.this, messageService.getMessageResponse()));
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
								startActivity(com.nmbs.activities.StationBoardActivity.createIntent(AlertActivity.this,null));
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
								startActivity(com.nmbs.activities.StationInfoActivity.createIntent(AlertActivity.this));
								isGoto = false;
								finish();
							}
						}
					});
		}
		//startActivity(com.nmbs.activities.CallCenterActivity.createIntent(MainActivity.this,0,null,null));
	}

	public void realtimeAlerts(View view){
		mDrawerLayout.closeDrawer(GravityCompat.END);
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
									//Utils.openProwser(AlertActivity.this, generalSetting.getBookingUrl(), clickToCallService);
									if(NetworkUtils.isOnline(AlertActivity.this)){
										GoogleAnalyticsUtil.getInstance().sendScreen(AlertActivity.this, TrackerConstant.BOOKING);
										startActivity(WebViewActivity.createIntent(AlertActivity.this,
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
								if(generalSetting != null && generalSetting.getLffUrl()!= null && !generalSetting.getLffUrl().isEmpty()) {
									//Utils.openProwser(AlertActivity.this, generalSetting.getLffUrl(), clickToCallService);

									if (NetworkUtils.isOnline(AlertActivity.this)) {
										GoogleAnalyticsUtil.getInstance().sendScreen(AlertActivity.this, TrackerConstant.LLF);
										startActivity(WebViewActivity.createIntent(AlertActivity.this,
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
								startActivity(ScheduleSearchActivity.createIntent(AlertActivity.this));
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
								startActivity(SettingsActivity.createIntent(AlertActivity.this));
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
								startActivity(WizardActivity.createIntent(AlertActivity.this, WizardActivity.Wizard_Home));
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
								startActivity(MyTicketsActivity.createIntent(AlertActivity.this));
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
								startActivity(UploadDossierActivity.createUploadDossierIntent(AlertActivity.this, NMBSApplication.PAGE_UploadTicket, null, null, null, null));
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
										GoogleAnalyticsUtil.getInstance().sendScreen(AlertActivity.this, TrackerConstant.CommercialTTLListUrl);
									}

								}else{
									if(!isFinishing()){
										DialogMyOptions dialogMyOptions = new DialogMyOptions(AlertActivity.this);
										dialogMyOptions.show();
									}
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
					GoogleAnalyticsUtil.getInstance().sendScreen(AlertActivity.this, TrackerConstant.CommercialTTLListUrl);
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
									GoogleAnalyticsUtil.getInstance().sendScreen(AlertActivity.this, TrackerConstant.ProfileOverviewUrl);

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
				GoogleAnalyticsUtil.getInstance().sendScreen(AlertActivity.this, TrackerConstant.ProfileOverviewUrl);
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
