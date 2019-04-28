package com.cflint.activities;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.cflint.dataaccess.database.GeneralSettingDatabaseService;
import com.cflint.services.impl.DaemonService;
import com.cflint.util.FunctionConfig;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.cflint.R;

import com.cflint.activity.BaseActivity;
import com.cflint.application.NMBSApplication;
import com.cflint.async.AutoRetrievalDossiersTask;
import com.cflint.async.CheckLastUpdatePwdAsyncTask;
import com.cflint.async.CheckOptionAsyncTask;
import com.cflint.async.CheckUpdateAsyncTask;
import com.cflint.async.GengeralSettingAsyncTask;
import com.cflint.async.GetSubScriptionListAsyncTask;
import com.cflint.async.HomeBannerAsyncTask;
import com.cflint.async.MobileMessageAsyncTask;
import com.cflint.async.ProfileInfoAsyncTask;
import com.cflint.async.RefreshMultipleDossierAsyncTask;
import com.cflint.async.StationInfoAsyncTask;
import com.cflint.async.TrainIconsAsyncTask;
import com.cflint.dataaccess.database.DatabaseHelper;
import com.cflint.dataaccess.restservice.IMasterDataService;
import com.cflint.dataaccess.restservice.impl.MasterDataService;
import com.cflint.log.LogUtils;
import com.cflint.model.HafasUser;
import com.cflint.model.LogonInfo;
import com.cflint.model.RealTimeInfoRequestParameter;
import com.cflint.preferences.SettingsPref;
import com.cflint.services.IAssistantService;
import com.cflint.services.ICheckUpdateService;
import com.cflint.services.IMasterService;
import com.cflint.services.IMessageService;
import com.cflint.services.IPushService;
import com.cflint.services.IStationInfoService;
import com.cflint.services.impl.DossierDetailsService;
import com.cflint.services.impl.DossiersUpToDateService;
import com.cflint.services.impl.PushNotificationRegistrationIntentService;
import com.cflint.services.impl.ServiceConstant;
import com.cflint.services.impl.SettingService;
import com.cflint.util.ActivityConstant;
import com.cflint.util.RatingUtil;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*import net.sqlcipher.database.SQLiteDatabase;*/
/*import net.sqlcipher.database.SQLiteDatabase;*/

// entrance view.
public class StartActivity extends BaseActivity {
	private final static String TAG = StartActivity.class.getSimpleName();
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
	private Timer t;

	//PUSH Notification
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private String subscriptionId;
	private RealTimeInfoRequestParameter realTimeInfoRequestParameter;
	private ExecutorService executorService = Executors.newFixedThreadPool(7);
	public static final int PERMISSION_CODE = 2001;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		startService(new Intent(this, DaemonService.class));
		if(getIntent().getBundleExtra(ActivityConstant.RECEIVE_PUSH_SUBSCRIPTION_ID) != null){
			subscriptionId = getIntent().getStringExtra(ActivityConstant.RECEIVE_PUSH_SUBSCRIPTION_ID);
		}
		/*Uri uri = getIntent().getData();
		String screen = "";
		if(uri != null){
			screen = uri.getQueryParameter("sid");
		}
		Log.e(TAG, "savedInstanceState....screen..." + screen);
		Bundle b = getIntent().getExtras();
		Log.e(TAG, "savedInstanceState...." + b);
		if(b != null){
			String sid = b.getString(ActivityConstant.RECEIVE_PUSH_SUBSCRIPTION_ID);
			Log.d(TAG, "savedInstanceState...." + sid);
		}*/
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
		setContentView(R.layout.start_view);
		assistantService = ((NMBSApplication) getApplication()).getAssistantService();
		masterService = ((NMBSApplication) getApplication()).getMasterService();
		settingService = ((NMBSApplication) getApplication()).getSettingService();
		messageService = ((NMBSApplication) getApplication()).getMessageService();
		dossierDetailsService = ((NMBSApplication) getApplication()).getDossierDetailsService();
		stationInfoService = ((NMBSApplication) getApplication()).getStationInfoService();
		checkUpdateService =((NMBSApplication) getApplication()).getCheckUpdateService();
		pushService = ((NMBSApplication) getApplication()).getPushService();
		dossiersUpToDateService = ((NMBSApplication) getApplication()).getDossiersUpToDateService();
		//settingService.initLanguageSettings();															// SharedPreferences
		//LocaleChangedUtils.initLanguageSettings(getApplicationContext());// Setting
		// application
		// language
		//Log.d(TAG, "onCreate....");


		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			ActivityCompat.requestPermissions(
					this,
					new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
					PERMISSION_CODE
			);
		}else{
			execute();
		}
	}



	private void execute(){
		if(!isFinishing()){
			showWaitDialog();
		}
		//GeneralSettingDatabaseService generalSettingDatabaseService = new GeneralSettingDatabaseService(this);
		//generalSettingDatabaseService.deleteMasterData(GeneralSettingDatabaseService.DB_GENERAL_SETTINGS);
		if(NMBSApplication.getInstance().getLoginService().isLogon()){
			LogonInfo logonInfo = NMBSApplication.getInstance().getLoginService().getLogonInfo();
			LogUtils.e("syncCheckPwd", " LoginService  isLogon...." );
			LogUtils.e("syncCheckPwd", " LoginService  isCheckLastUpdateTimestampPassword...." +
					NMBSApplication.getInstance().getMasterService().loadGeneralSetting().isCheckLastUpdateTimestampPassword());
			LogUtils.e("syncCheckPwd", " LoginService  getPersonId...." +
					logonInfo.getPersonId());
			LogUtils.e("syncCheckPwd", " LoginService  getLoginProvider...." +
					logonInfo.getLoginProvider());

			if(NMBSApplication.getInstance().getMasterService().loadGeneralSetting().isCheckLastUpdateTimestampPassword()
					&& logonInfo != null
					&& logonInfo != null
					&& !logonInfo.getPersonId().isEmpty()
					&& "CRIS".equalsIgnoreCase(logonInfo.getLoginProvider())){

				CheckLastUpdatePwdAsyncTask asyncTask = new CheckLastUpdatePwdAsyncTask(getApplicationContext(), handler);
				asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}else {
				gotoHome();
			}
		}else{
			gotoHome();
		}
		if(NMBSApplication.getInstance().getLoginService().isLogon() && !CheckOptionAsyncTask.isChecking){
			CheckOptionAsyncTask asyncTask = new CheckOptionAsyncTask(getApplicationContext());
			asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}



		LogUtils.e("LogUtils", " execute masterdata...." );
		MobileMessageAsyncTask.isMessageFinished = false;
		executorService.submit(new Runnable() {
			public void run() {
				LogUtils.e("LogUtils", " MobileMessageAsyncTask run....");
				MobileMessageAsyncTask mobileMessageAsyncTask = new MobileMessageAsyncTask(messageService, settingService.getCurrentLanguagesKey(), getApplicationContext());
				mobileMessageAsyncTask.downloadMessages();
			}
		});
		executorService.submit(new Runnable() {
			public void run() {
				LogUtils.e("LogUtils", " GengeralSettingAsyncTask run....");
				GengeralSettingAsyncTask gengeralSettingAsyncTask = new GengeralSettingAsyncTask(settingService.getCurrentLanguagesKey(), getApplicationContext());
				gengeralSettingAsyncTask.downloadGeneralSetting();
			}
		});

		executorService.submit(new Runnable() {
			public void run() {
				LogUtils.e("LogUtils", " storeDefaultData run....");
				IMasterDataService masterDataService = new MasterDataService();
				masterDataService.storeDefaultData(getApplicationContext(), false);
				//masterDataService.storeGeneralSettings(getApplication(), settingService.getCurrentLanguagesKey());
			}
		});
		executorService.submit(new Runnable() {
			public void run() {
				LogUtils.e("LogUtils", " TrainIconsAsyncTask run....");
				TrainIconsAsyncTask trainIconsAsyncTask = new TrainIconsAsyncTask(settingService.getCurrentLanguagesKey(), getApplicationContext());
				trainIconsAsyncTask.downloadTrainIcons();
			}
		});
		executorService.submit(new Runnable() {
			public void run() {
				LogUtils.e("LogUtils", " CheckUpdateAsyncTask run....");
				checkUpdateAsyncTask = new CheckUpdateAsyncTask(null, checkUpdateService);
				checkUpdateAsyncTask.execute(settingService);
			}
		});
		executorService.submit(new Runnable() {
			public void run() {
				LogUtils.e("LogUtils", " HomeBannerAsyncTask run....");
				HomeBannerAsyncTask asyncTask = new HomeBannerAsyncTask(settingService.getCurrentLanguagesKey(), getApplicationContext());
				asyncTask.downloadHomeBanner();
			}
		});

		executorService.submit(new Runnable() {
			public void run() {
				LogUtils.e("LogUtils", " StationInfoAsyncTask run....");
				StationInfoAsyncTask stationInfoAsyncTask = new StationInfoAsyncTask(stationInfoService,settingService,getApplicationContext(), false);
				stationInfoAsyncTask.downlaodStationInfo();

			}
		});
		if(NMBSApplication.getInstance().getLoginService().isLogon()){
			LogUtils.e("isLogon", " isLogon...." );
			if(!AutoRetrievalDossiersTask.isWorking){
				dossierDetailsService.autoRetrievalDossiers();
			}
		}
		RatingUtil.saveCurrentTimes(getApplicationContext());
		createRealTimeInfos();
		//RateUtil.resetCurrentTimes(getApplicationContext());


		EncryptDatabaseAsyncTask encryptDatabaseAsyncTask = new EncryptDatabaseAsyncTask();
		encryptDatabaseAsyncTask.execute((Void)null);


		if(FunctionConfig.kFunManagePush){
			initPushService();
		}
		//initPushService();
	}

	private android.os.Handler handler = new android.os.Handler() {
		public void handleMessage(Message msg) {
			LogUtils.e("handler", " handler...." );
			//responseReceived();
		}
	};

	private void gotoHome(){
		LogUtils.e("gotoHome", " gotoHome...." );
		t = new Timer();
		t.schedule(new TimerTask() {
			public void run() {
				try {
					responseReceived();
				} catch (Exception e) {
                    responseReceived();
				}
			}
		}, 3000);
	}

	private void createRealTimeInfos(){
		/*List<DossierSummary> dossiersActive = dossierDetailsService.getDossiers(true);
		Map<String, Object> objectMap = null;
		List<Object> objectList = null;
		if(dossiersActive != null && dossiersActive.size() > 0){
			for(DossierSummary dossierSummary : dossiersActive){
				DossierDetailsResponse dossierResponse = dossierDetailsService.getDossierDetafil(dossierSummary);
				if(dossierResponse != null){
					objectMap = dossierDetailsService.getRealTimeInfoRequesMap(dossierResponse.getDossier(), NMBSApplication.getInstance().getMasterService().loadGeneralSetting());
					objectList = dossierDetailsService.getRealTimeInfoRequestList(objectMap);
				}
			}
		}*/

		realTimeInfoRequestParameter = dossierDetailsService.getRealTimeInfoRequestParameter();
		//realTimeInfoRequestParameter.setMap(objectMap);

	}

	private void initPushService(){
		LogUtils.e("LogUtils", " initPushService....");
		if(pushService.getRegistrationId().equals("")){
			if (checkPlayServices()) {
				// Start IntentService to register this application with GCM.
				Intent intent = new Intent(this, PushNotificationRegistrationIntentService.class);
				startService(intent);
			}
		}else{
			new Thread(new Runnable(){
				public void run(){
					try{

						int start = settingService.getStartNotifiTimeIntger();
						int delay = settingService.getDelayNotifiTimeIntger();
						String userId = pushService.createAccount(new HafasUser("",pushService.getRegistrationId(),settingService.getCurrentLanguagesKey(),
								delay, delay, start));
						if("".equals(userId)){
							userId = pushService.createAccount(new HafasUser("",pushService.getRegistrationId(),settingService.getCurrentLanguagesKey(),
									delay, delay, start));
						}
						GetSubScriptionListAsyncTask asyncTask = new GetSubScriptionListAsyncTask(pushService,settingService.getCurrentLanguagesKey(),getApplicationContext());
						asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}catch (Exception e){
						e.printStackTrace();
					}
				}}).start();
		}
	}

	private void responseReceived() {
		if(t != null){
			t.cancel();
			t = null;
		}

		hideWaitDialog();
		Uri uri = getIntent().getData();

		if (uri == null) {
			//Log.w("uri is null, start Main", uri + "");
			startActivity(MainActivity.createMainIntent(
					StartActivity.this.getApplicationContext(), true, null, realTimeInfoRequestParameter,subscriptionId));
			finish();;
		} else {
			//Log.w("data", uri.toString());
			String screen = uri.getQueryParameter("screen");
			if ("UploadDNR".equalsIgnoreCase(screen)) {
				startActivity(UploadDossierActivity.createUploadDossierIntent(this, NMBSApplication.PAGE_UploadTicket, uri, null, null, null));
			}else if("Stationboard".equalsIgnoreCase(screen)){
				startActivity(StationBoardActivity.createIntent(this, uri));
			}else if("Schedule".equalsIgnoreCase(screen)){
				startActivity(ScheduleSearchActivity.createIntent(this));
			}else {
				startActivity(MainActivity.createMainIntent(
						StartActivity.this.getApplicationContext(), true, null, realTimeInfoRequestParameter,subscriptionId));
			}

		}

		this.finish();
	}
	public class EncryptDatabaseAsyncTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			File unencryptedDatabase = getDatabasePath(DatabaseHelper.DATABASE_NAME);
			//Log.d(TAG, "unencryptedDatabase...." + unencryptedDatabase.isFile());
			LogUtils.e("LogUtils", " EncryptDatabaseAsyncTask run....");
			LogUtils.e("LogUtils", " EncryptDatabaseAsyncTask run...isFile?-------->." + unencryptedDatabase.isFile());
			if (unencryptedDatabase.isFile()) {
				//Log.d(TAG, "encrypting Database....");
				File encryptedDatabase = getDatabasePath(DatabaseHelper.NEW_DATABASE_NAME);
				//encryptedDatabase.delete();
				SQLiteDatabase database = null;
				try {
					database = SQLiteDatabase.openOrCreateDatabase(unencryptedDatabase, "", null);
					database.rawExecSQL(String.format("ATTACH DATABASE '%s' AS encrypted  KEY '%s'", encryptedDatabase.getAbsolutePath(),
							DatabaseHelper.getInstance(getApplicationContext()).getDatabasePassword()));
					database.rawExecSQL("select sqlcipher_export('encrypted')");
					database.rawExecSQL("DETACH DATABASE encrypted");
					database.close();
					unencryptedDatabase.delete();
					LogUtils.e("LogUtils", " EncryptDatabaseAsyncTask run...encryptedDatabase is finished-------->.");
				} catch (Exception e) {
					e.printStackTrace();
				}
				//encryptedDatabase.delete();

			}
			//database = SQLiteDatabase.openOrCreateDatabase(encryptedDatabase, DatabaseHelper.PASSWORD, null);
			//Log.d(TAG, "onCreate");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}


	}


	// show progressDialog.
	private void showWaitDialog() {
		if(isFinishing()){
			return;
		}
		LogUtils.e("LogUtils", " showWaitDialog....");
		runOnUiThread(new Runnable() {
			public void run() {
				if (progressDialog == null) {
					//Log.e(TAG, "Show Wait Dialog....");
					progressDialog = ProgressDialog.show(StartActivity.this,
							getString(R.string.alert_loading),
							getString(R.string.alert_waiting), true);
				}
			}
		});

	}

	// hide progressDialog
	public void hideWaitDialog() {
		//Log.e(TAG, "Hide Wait Dialog....");
		LogUtils.e("LogUtils", " hideWaitDialog....");
		runOnUiThread(new Runnable() {
			public void run() {
				if (progressDialog != null) {

					//Log.e(TAG, "progressDialog is.... " + progressDialog);
					progressDialog.dismiss();
					progressDialog = null;
				}
			}
		});

	}
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStart() {
		//Log.i(TAG, "onStart");
		super.onStart();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
										   @NonNull int[] grantResults) {
		if (requestCode == PERMISSION_CODE) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				execute();
			}else {
				if(!isFinishing()){
					showMissingPermissionDialog();
				}
			}
		}
	}

/*	private void getPermissions(){
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			LogUtils.e("LogUtils", " No WRITE_EXTERNAL_STORAGE permission....");

		}else{
			execute();
		}

	}*/


	@Override
	protected void onDestroy() {
		//Log.i(TAG, "onDestroy");
		t = null;
		hideWaitDialog();
		super.onDestroy();
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
		int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (apiAvailability.isUserResolvableError(resultCode)) {
				apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
						.show();
			} else {
				//Log.i(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	private void showMissingPermissionDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
		builder.setTitle(R.string.alert_information);
		builder.setMessage(R.string.dialog_permission);
		builder.setCancelable(false);
		// 拒绝, 退出应用
		builder.setNegativeButton(R.string.general_close_app, new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int which) {
				//setResult(PERMISSIONS_DENIED);
				dialog.dismiss();
				finish();
				LogUtils.e("LogUtils", " Close app....");
			}
		});

		builder.setPositiveButton(R.string.general_settings, new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();;
				LogUtils.e("LogUtils", " Go to settings....");
				startAppSettings();
			}
		});
		builder.setOnKeyListener(new DialogInterface.OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
					return true;
				}
				return false;
			}
		});

		builder.show();
	}

	private void startAppSettings() {
		Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		intent.setData(Uri.parse("package:" + getPackageName()));
		startActivity(intent);
	}

}
