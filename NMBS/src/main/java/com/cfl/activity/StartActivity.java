package com.cfl.activity;



import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.cfl.R;
import com.cfl.activities.UploadDossierActivity;
import com.cfl.application.NMBSApplication;
import com.cfl.async.CheckUpdateAsyncTask;
import com.cfl.async.MobileMessageAsyncTask;
import com.cfl.async.OrderAsyncTask;
import com.cfl.dataaccess.database.DatabaseHelper;
import com.cfl.dataaccess.restservice.IMasterDataService;
import com.cfl.dataaccess.restservice.impl.MasterDataService;
import com.cfl.model.Order;
import com.cfl.services.IAssistantService;
import com.cfl.services.ICheckUpdateService;
import com.cfl.services.IMasterService;
import com.cfl.services.IMessageService;
import com.cfl.services.ISettingService;
import com.cfl.services.impl.ServiceConstant;
import com.cfl.util.LocaleChangedUtils;
import com.cfl.util.RateUtil;
import com.cfl.util.SharedPreferencesUtils;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/*import net.sqlcipher.database.SQLiteDatabase;*/
/*import net.sqlcipher.database.SQLiteDatabase;*/

// entrance view.
public class StartActivity extends Activity {
	private final static String TAG = StartActivity.class.getSimpleName();
	//private static final String CAMPAIGN_SOURCE_PARAM = "utm_source";
	private IMasterService masterService;
	private ProgressDialog progressDialog;
	private ISettingService settingService;
	private IAssistantService assistantService;
	private ICheckUpdateService checkUpdateService;
	private IMessageService messageService;

	private CheckUpdateAsyncTask checkUpdateAsyncTask;
	//private UpdateVersionReceiver updateVersionReceiver;
	
	private Timer t;
	private List<Order> orderList;
	private OrderAsyncTask orderAsyncTask;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
		SharedPreferencesUtils.initSharedPreferences(getApplicationContext());// initialize
																				// SharedPreferences
		LocaleChangedUtils.initLanguageSettings(getApplicationContext());// Setting
																			// application
														// language
		Log.d(TAG, "onCreate....");
		setContentView(R.layout.start_view);
		assistantService = ((NMBSApplication) getApplication()).getAssistantService();
		masterService = ((NMBSApplication) getApplication()).getMasterService();
		settingService = ((NMBSApplication) getApplication()).getSettingService();
		messageService = ((NMBSApplication) getApplication()).getMessageService();
		checkUpdateService =((NMBSApplication) getApplication()).getCheckUpdateService();
		MobileMessageAsyncTask mobileMessageAsyncTask = new MobileMessageAsyncTask(messageService,settingService.getCurrentLanguagesKey(), getApplicationContext());
		mobileMessageAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		RateUtil.saveCurrentTimes(getApplicationContext());
		new Thread() {
			public void run() {

				IMasterDataService masterDataService = new MasterDataService();
				masterDataService.storeDefaultData(getApplicationContext(), false);
				
				
			}
		}.start();	
		//RateUtil.resetCurrentTimes(getApplicationContext());
		showWaitDialog();
		
		orderAsyncTask = new OrderAsyncTask(mHandler, MyTicketsActivity.FLAG_FIND_ORDER, masterService);
		orderAsyncTask.execute(assistantService);
		
		executeRefreshRealTimeFirstTime();
		checkUpdateAsyncTask = new CheckUpdateAsyncTask(null, checkUpdateService);
		checkUpdateAsyncTask.execute(settingService);
		
		EncryptDatabaseAsyncTask encryptDatabaseAsyncTask = new EncryptDatabaseAsyncTask();
		encryptDatabaseAsyncTask.execute((Void)null);	
		
		assistantService.setTickesHelpers(null);
		
		
	}
	
	private void executeRefreshRealTimeFirstTime(){
		/*if (updateVersionReceiver == null) {
			updateVersionReceiver = new UpdateVersionReceiver();
			registerReceiver(updateVersionReceiver, new IntentFilter(ServiceConstant.UPDATE_VERSION_ACTION));
		}	*/
		
		/*RealTimeAsyncTask realTimeAsyncTask = new RealTimeAsyncTask(settingService.getCurrentLanguage(), getApplicationContext(), null);
		realTimeAsyncTask.execute((Void)null);*/
	}
	
	 
	
	
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ServiceConstant.MESSAGE_WHAT_OK:
				asyncTaskFinished();
				//Log.d(TAG, "handler receive: Success!");
				break;
			}
		};
	};
	private void asyncTaskFinished() {
		orderList = orderAsyncTask.getListOrders();
		if (orderList != null && orderList.size() > 0) {
			// if (assistantService.isTimeToRefresh()) {
			// reEnableState();
			// }else {
			
			/*ExecuteDossierAftersalesTask executeDossierAftersalesTask = new ExecuteDossierAftersalesTask(assistantService, settingService, 
					getApplicationContext(), orderList);
			executeDossierAftersalesTask.execute((Void) null);*/

			// }
			try {
				assistantService.refreshRealTimeFirstTime(settingService.getCurrentLanguagesKey(), orderList);
			} catch (Exception e) {
				Log.d(TAG, "hrefresh RealTime Failure...");
				e.printStackTrace();
			}
		} else {
		}
	}

	
    public class EncryptDatabaseAsyncTask extends AsyncTask<Void, Void, Void>{    		
    	
    	@Override
    	protected void onPreExecute() {
    	    
    		super.onPreExecute();
    	}
    	
		@Override
		protected Void doInBackground(Void... params) {
			
			File unencryptedDatabase = getDatabasePath(DatabaseHelper.DATABASE_NAME);
			Log.d(TAG, "unencryptedDatabase...." + unencryptedDatabase.isFile());
				if (unencryptedDatabase.isFile()) {
					Log.d(TAG, "encrypting Database....");
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
			refreshMasterData();
			
			super.onPostExecute(result);
		}

		
    }

	// refresh Master Data
	private void refreshMasterData() {

		masterService.getMasterData(settingService, true);

		
		t = new Timer();
		t.schedule(new TimerTask() {
			public void run() {
				try {
					
					responseReceived();
					
				} catch (Exception e) {
					
					responseReceived();
				}
			}
		}, 2000, 2000);
				
	}

	// show progressDialog.
	private void showWaitDialog() {
				
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

	private void responseReceived() {
		t.cancel();
		t = null;
		hideWaitDialog();
		Uri uri = getIntent().getData();

		/*if (uri == null) {
			Log.w("uri is null, start Main", uri + "");
			startActivity(Main.createMainIntent(
					StartActivity.this.getApplicationContext(), true, null));
		} else {
			Log.w("data", uri.toString());
			String screen = uri.getQueryParameter("screen");
			if ("UploadDNR".equalsIgnoreCase(screen)) {
				startActivity(UploadDossierActivity
						.createUploadDossierIntent(getApplicationContext(), 0, uri));
			}else {
				startActivity(Main.createMainIntent(
						StartActivity.this.getApplicationContext(), true, null));
			}
			
		}*/
		
		this.finish();
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

		super.onResume();
	}

	@Override
	protected void onDestroy() {
		//Log.i(TAG, "onDestroy");
		t = null;
		hideWaitDialog();


		super.onDestroy();
	}


}
