package com.nmbs.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.nmbs.R;
import com.nmbs.activities.view.DialogAlertError;
import com.nmbs.activity.BaseActivity;
import com.nmbs.adapter.AlertDeleteDnrTitleAdapter;
import com.nmbs.application.NMBSApplication;
import com.nmbs.async.DeleteAllSubScriptionAsyncTask;
import com.nmbs.async.DeleteSubScriptionAsyncTask;
import com.nmbs.model.DossierSummary;
import com.nmbs.model.HafasUser;
import com.nmbs.model.Subscription;
import com.nmbs.services.IPushService;
import com.nmbs.services.impl.DossierDetailsService;
import com.nmbs.services.impl.ServiceConstant;
import com.nmbs.services.impl.SettingService;
import com.nmbs.util.ActivityConstant;
import com.nmbs.util.GoogleAnalyticsUtil;
import com.nmbs.util.TrackerConstant;
import com.nmbs.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity used for displaying the UI element, user can do some inner behavior with this.
 *
 * User can select a category to see the details.
 */
public class AlertDeleteDnrActivity extends BaseActivity {
	private final static String TAG = AlertDeleteDnrActivity.class.getSimpleName();
	private IPushService pushService;
	private AlertDeleteDnrTitleAdapter alertConnectionAdapter;
	private LinearLayout dnrReferenceLayout;
	private List<Subscription> subscriptionList;
	private String deleteDnr;
	private ProgressDialog progressDialog;
	private ServiceDeleteSubscriptionReceiver serviceDeleteSubscriptionReceiver;
	private final static String Inten_Key_IsDnr = "IsDnr";

	private DialogAlertError dialogError;
	private HafasUser hafasUser;
	private boolean isDeleteAll = false;
	private SettingService settingService;
	private boolean isDnr;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Utils.setToolBarStyle(this);
		settingService = ((NMBSApplication) getApplication()).getSettingService();
		//settingService.initLanguageSettings();
		setContentView(R.layout.activity_alert_delete);
		isDnr = getIntent().getBooleanExtra(Inten_Key_IsDnr, false);
		bindService();
		bindView();
		//initData();
		GoogleAnalyticsUtil.getInstance().sendScreen(AlertDeleteDnrActivity.this, TrackerConstant.ALERT_DeleteDNR);
	}

	public void deleteSubscription(Subscription subscription, String deleteDnr){
		this.deleteDnr = deleteDnr;
		showWaitDialog();
		isDeleteAll = false;
		DeleteSubScriptionAsyncTask asyncTask = new DeleteSubScriptionAsyncTask(pushService,
				settingService.getCurrentLanguagesKey(), getApplicationContext(),subscription.getSubscriptionId());
		asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


	}

	public void deleteAllSubscription(List<Subscription> subscriptionList){
		showWaitDialog();
		isDeleteAll = true;
		List<String> dnrList = new ArrayList<String>();
		if(isDnr){
			for(Subscription subscription : subscriptionList){
				if(subscription != null && subscription.getDnr() != null && !subscription.getDnr().isEmpty()){
					dnrList.add(subscription.getDnr());
				}
			}
		}
		DeleteAllSubScriptionAsyncTask asyncTask = new DeleteAllSubScriptionAsyncTask(subscriptionList,pushService,settingService.getCurrentLanguagesKey(),
				getApplicationContext(), isDnr, dnrList,DeleteAllSubScriptionAsyncTask.Need_To_Delete_Subscription, true);
		asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}



	public void deleteAllSubscription(View view){
		deleteAllSubscription(this.subscriptionList);

	}

	private void bindService(){
		this.pushService = ((NMBSApplication) getApplication()).getPushService();
		this.settingService = ((NMBSApplication) getApplication()).getSettingService();
	}

	private void bindView(){
		this.dnrReferenceLayout = (LinearLayout) findViewById(R.id.ll_alert_delete);
	}

	private void createSubScriptionReceiver(){
		if (serviceDeleteSubscriptionReceiver == null) {
			serviceDeleteSubscriptionReceiver = new ServiceDeleteSubscriptionReceiver();
			registerReceiver(serviceDeleteSubscriptionReceiver, new IntentFilter(ServiceConstant.PUSH_DELETE_SUBSCRIPTION_ACTION));
		}
	}

	private void initData(){

		dnrReferenceLayout.removeAllViews();
		if(isDnr){
			subscriptionList = pushService.readAllSubscriptionsWithDnr();
		}
		if(subscriptionList == null || subscriptionList.size() == 0){
			finish();
		}
		List<String> dnrs = new ArrayList<>() ;

		//Log.e(TAG, "subscriptionList...." + subscriptionList.size());
		alertConnectionAdapter = new AlertDeleteDnrTitleAdapter(AlertDeleteDnrActivity.this, dnrs);
		for(int i=0;i<this.subscriptionList.size();i++){
			if(!dnrs.contains(subscriptionList.get(i).getDnr())){
				dnrs.add(subscriptionList.get(i).getDnr());
				alertConnectionAdapter.getAlertDeleteTitleView(i, this.dnrReferenceLayout);
			}
		}
	}

	public static Intent createIntent(Context context, boolean isDnr){
		//Log.d(TAG, "isDnr...." + isDnr);
		Intent intent = new Intent(context, AlertDeleteDnrActivity.class);
		intent.putExtra(Inten_Key_IsDnr, isDnr);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
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
		bindView();
		createSubScriptionReceiver();
		initData();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if(serviceDeleteSubscriptionReceiver != null){
			unregisterReceiver(serviceDeleteSubscriptionReceiver);
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
					progressDialog = ProgressDialog.show(AlertDeleteDnrActivity.this, null,
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

	class ServiceDeleteSubscriptionReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			hideWaitDialog();
			boolean isDeleteSuccess = false;
			int resultNumber = 0;
			//Log.e(TAG, "ServiceDeleteSubscriptionReceiver...");
			initData();
			if(isDeleteAll){
				//Log.e(TAG, "isDeleteAll..." + isDeleteAll);
				resultNumber = intent.getIntExtra(ActivityConstant.DELETE_SUBSCRIPTION_RESULT,0);
				switch (resultNumber){
					case 0:
						if(!isFinishing()){
							dialogError = new DialogAlertError(AlertDeleteDnrActivity.this, getResources().getString(R.string.alert_delete_failure_title),
									getResources().getString(R.string.alert_delete_subscription_failure_info));
							dialogError.show();
						}

						break;
					case 1:
						if(!isFinishing()){
							dialogError = new DialogAlertError(AlertDeleteDnrActivity.this, getResources().getString(R.string.alert_delete_failure_title),
									getResources().getString(R.string.alert_subscription_not_all_deleted));
							dialogError.show();
						}
						break;
					case 2:

						break;
				}
			}else{
				isDeleteSuccess = intent.getBooleanExtra(ActivityConstant.DELETE_SUBSCRIPTION_RESULT,false);
				if(isDeleteSuccess){
					if(deleteDnr != null && !deleteDnr.isEmpty()){
						DossierDetailsService dossierDetailsService = NMBSApplication.getInstance().getDossierDetailsService();
						DossierSummary dossierSummary = dossierDetailsService.getDossier(deleteDnr);
						if(dossierSummary != null){
							dossierSummary.setDossierPushEnabled(false);
							//Log.d("PushEnabled", "Deleted dnr is...." + deleteDnr + "....PushEnabled..." + dossierSummary.isDossierPushEnabled());
							dossierDetailsService.updateDossier(dossierSummary);
						}
					}

				}else{
					if(!isFinishing()){
						dialogError = new DialogAlertError(AlertDeleteDnrActivity.this, getResources().getString(R.string.alert_delete_failure_title),
								getResources().getString(R.string.alert_delete_subscription_failure_info));
						dialogError.show();
					}
				}
			}
		}
	}
}
