package com.cfl.activities;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.cfl.R;
import com.cfl.application.NMBSApplication;
import com.cfl.async.CheckUpdateAsyncTask;
import com.cfl.async.DossierUpToDateAsyncTask;
import com.cfl.async.GengeralSettingAsyncTask;
import com.cfl.async.GetSubScriptionListAsyncTask;
import com.cfl.async.HomeBannerAsyncTask;
import com.cfl.async.MobileMessageAsyncTask;
import com.cfl.async.StationInfoAsyncTask;
import com.cfl.async.TrainIconsAsyncTask;
import com.cfl.dataaccess.database.DatabaseHelper;
import com.cfl.dataaccess.restservice.IMasterDataService;
import com.cfl.dataaccess.restservice.impl.MasterDataService;
import com.cfl.model.HafasUser;
import com.cfl.model.RealTimeInfoRequestParameter;
import com.cfl.preferences.SettingsPref;
import com.cfl.services.IAssistantService;
import com.cfl.services.ICheckUpdateService;
import com.cfl.services.IMasterService;
import com.cfl.services.IMessageService;
import com.cfl.services.IPushService;
import com.cfl.services.IStationInfoService;
import com.cfl.services.impl.DossierDetailsService;
import com.cfl.services.impl.DossiersUpToDateService;
import com.cfl.services.impl.PushNotificationRegistrationIntentService;
import com.cfl.services.impl.SettingService;
import com.cfl.services.impl.TestService;
import com.cfl.util.ActivityConstant;
import com.cfl.util.RatingUtil;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*import net.sqlcipher.database.SQLiteDatabase;*/
/*import net.sqlcipher.database.SQLiteDatabase;*/

// entrance view.
public class TestActivity extends Activity {
	private final static String TAG = TestActivity.class.getSimpleName();
	//private static final String CAMPAIGN_SOURCE_PARAM = "utm_source";
	private IMasterService masterService;
	private ProgressDialog progressDialog;
	private SettingService settingService;
	private IAssistantService assistantService;
	private ICheckUpdateService checkUpdateService;
	private IMessageService messageService;
	private IStationInfoService stationInfoService;
	private CheckUpdateAsyncTask checkUpdateAsyncTask;
	private IPushService pushService;
	private DossiersUpToDateService dossiersUpToDateService;
	//private UpdateVersionReceiver updateVersionReceiver;
	private DossierDetailsService dossierDetailsService;


	//PUSH Notification
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private String subscriptionId;
	private RealTimeInfoRequestParameter realTimeInfoRequestParameter;
	private ExecutorService executorService = Executors.newFixedThreadPool(7);

	private TestService testService;
	private SwitchCompat swDeleteUserId, swCheckVersion, swDownloadPdf, swNotCreate, swNotDelete, swDalayTime;
	private TextView tvUserID, tvDemo, tvPrd;
	private HafasUser user;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		if(getIntent().getBundleExtra(ActivityConstant.RECEIVE_PUSH_SUBSCRIPTION_ID) != null){
			subscriptionId = getIntent().getStringExtra(ActivityConstant.RECEIVE_PUSH_SUBSCRIPTION_ID);
		}

		assistantService = ((NMBSApplication) getApplication()).getAssistantService();
		masterService = ((NMBSApplication) getApplication()).getMasterService();
		settingService = ((NMBSApplication) getApplication()).getSettingService();
		messageService = ((NMBSApplication) getApplication()).getMessageService();
		dossierDetailsService = ((NMBSApplication) getApplication()).getDossierDetailsService();
		stationInfoService = ((NMBSApplication) getApplication()).getStationInfoService();
		checkUpdateService =((NMBSApplication) getApplication()).getCheckUpdateService();
		pushService = ((NMBSApplication) getApplication()).getPushService();
		dossiersUpToDateService = ((NMBSApplication) getApplication()).getDossiersUpToDateService();
		settingService.initLanguageSettings();															// SharedPreferences
		//LocaleChangedUtils.initLanguageSettings(getApplicationContext());// Setting
		// application
		// language
		Log.d(TAG, "onCreate....");
		setContentView(R.layout.activity_test);
		testService = ((NMBSApplication) getApplication()).getTestService();
		bindAllViewsElement();
		bindAllListeners();
		initView();
	}
	private void bindAllViewsElement() {
		//tvEmailValue = (TextView) findViewById(R.id.tv_email_value);
		//tvPasswordValue = (TextView) findViewById(R.id.tv_password_value);
		//swAutoUpdate = (SwitchCompat) findViewById(R.id.sw_auto_update);
		swDeleteUserId = (SwitchCompat) findViewById(R.id.sw_delete_user_id);
		swCheckVersion = (SwitchCompat) findViewById(R.id.sw_check_version);
		swDownloadPdf = (SwitchCompat) findViewById(R.id.sw_download_pdf);
		swNotCreate = (SwitchCompat) findViewById(R.id.sw_not_cretat_subscription);
		swNotDelete = (SwitchCompat) findViewById(R.id.sw_not_delete_subscription);
		tvUserID = (TextView) findViewById(R.id.tv_user_id);
		swDalayTime = (SwitchCompat) findViewById(R.id.sw_delay_time);
		tvDemo = (TextView) findViewById(R.id.tv_demo);
		tvPrd = (TextView) findViewById(R.id.tv_prd);
		user = pushService.getUser();
		if(user != null){
			tvUserID.setText(user.getUserId());
		}
	}

	private void bindAllListeners() {
		swDeleteUserId.setOnCheckedChangeListener(deleteUserIdCheckedChangeListener);
		swCheckVersion.setOnCheckedChangeListener(checkVersionCheckedChangeListener);
		swDownloadPdf.setOnCheckedChangeListener(downloadPdfCheckedChangeListener);
		swNotCreate.setOnCheckedChangeListener(notCreateCheckedChangeListener);
		swNotDelete.setOnCheckedChangeListener(notDeleteCheckedChangeListener);
		swDalayTime.setOnCheckedChangeListener(dalayTimeCheckedChangeListener);
		//swFT.setOnCheckedChangeListener(ftCheckedChangeListener);
	}

	private void initView(){
		swDeleteUserId.setChecked(testService.isEmptyUser());
		swCheckVersion.setChecked(testService.isCheckAppVersion());
		swDownloadPdf.setChecked(testService.isDownloadFile());
		swNotCreate.setChecked(testService.isCreateSubscription());
		swNotDelete.setChecked(testService.isDeleteSubscription());
		swDalayTime.setChecked(testService.isDelaySubscription());
	}

	private SwitchCompat.OnCheckedChangeListener deleteUserIdCheckedChangeListener = new SwitchCompat.OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			testService.setEmptyUser(isChecked);
			user = pushService.getUser();
			if(user != null){
				tvUserID.setText(user.getUserId());
			}else{
				tvUserID.setText("");
			}
		}
	};
	private SwitchCompat.OnCheckedChangeListener checkVersionCheckedChangeListener = new SwitchCompat.OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			testService.setCheckAppVersion(isChecked);
		}
	};

	private SwitchCompat.OnCheckedChangeListener downloadPdfCheckedChangeListener = new SwitchCompat.OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			testService.setDownloadFile(isChecked);
		}
	};
	private SwitchCompat.OnCheckedChangeListener notCreateCheckedChangeListener = new SwitchCompat.OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			testService.setCreateSubscription(isChecked);
		}
	};
	private SwitchCompat.OnCheckedChangeListener dalayTimeCheckedChangeListener = new SwitchCompat.OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			testService.setDelaySubscription(isChecked);
		}
	};

	private SwitchCompat.OnCheckedChangeListener notDeleteCheckedChangeListener = new SwitchCompat.OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			testService.setDeleteSubscription(isChecked);
		}
	};
	public void backgroundRefresh(View view){
		DossierUpToDateAsyncTask asyncTask = new DossierUpToDateAsyncTask(getApplicationContext());
		asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	public void demo(View view){
		setDemoView();
		testService.setHafasUrl("Demo");
	}

	public void prd(View view){
		setPrdView();
		testService.setHafasUrl("Prd");
	}

	private void intiHafasView(){
		if(testService.getHafasUr().equalsIgnoreCase("Demo")){
			tvDemo.setBackgroundColor(getResources().getColor(R.color.background_group_title));
			tvPrd.setBackgroundColor(getResources().getColor(R.color.background_secondaryaction));
		}else{
			tvDemo.setBackgroundColor(getResources().getColor(R.color.background_secondaryaction));
			tvPrd.setBackgroundColor(getResources().getColor(R.color.background_group_title));
		}
	}

	private void setPrdView(){
		tvDemo.setBackgroundColor(getResources().getColor(R.color.background_secondaryaction));
		tvPrd.setBackgroundColor(getResources().getColor(R.color.background_group_title));
	}

	private void setDemoView(){
		tvDemo.setBackgroundColor(getResources().getColor(R.color.background_group_title));
		tvPrd.setBackgroundColor(getResources().getColor(R.color.background_secondaryaction));
	}

	public static Intent createIntent(Context context) {
		Intent intent = new Intent(context, TestActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return intent;
	}


	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStart() {
		Log.i(TAG, "onStart");
	    super.onStart();
	}
		
	@Override
	protected void onResume() {
		// showWaitDialog();
		intiHafasView();
		Log.i(TAG, "onStart..." + NMBSApplication.getInstance().getHafasUrl());
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		//Log.i(TAG, "onDestroy");
		super.onDestroy();
	}
}
