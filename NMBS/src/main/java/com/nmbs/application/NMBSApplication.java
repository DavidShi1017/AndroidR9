package com.nmbs.application;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;


import com.google.firebase.analytics.FirebaseAnalytics;
import com.nmbs.R;
import com.nmbs.activity.SettingsActivity;
import com.nmbs.async.GetSubScriptionListAsyncTask;
import com.nmbs.async.MobileMessageAsyncTask;
import com.nmbs.exceptions.NetworkError;
import com.nmbs.log.LogUtils;
import com.nmbs.preferences.SettingsPref;

import com.nmbs.receivers.AlarmsRefreshDossierBroadcastReceiver;
import com.nmbs.receivers.CheckOptionNotificationReceiver;
import com.nmbs.receivers.CheckOptionReceiver;
import com.nmbs.receivers.UpdateAlarmsBroadcastReceiver;
import com.nmbs.services.IAssistantService;
import com.nmbs.services.ICheckUpdateService;
import com.nmbs.services.IClickToCallService;
import com.nmbs.services.IDossierService;
import com.nmbs.services.ILoginService;
import com.nmbs.services.IMasterService;
import com.nmbs.services.IMessageService;
import com.nmbs.services.INotificationService;
import com.nmbs.services.IOfferService;
import com.nmbs.services.IPushService;
import com.nmbs.services.IRegisterService;
import com.nmbs.services.IScheduleService;
import com.nmbs.services.IStationInfoService;
import com.nmbs.services.impl.AssistantService;
import com.nmbs.services.impl.CheckUpdateService;
import com.nmbs.services.impl.ClickToCallService;
import com.nmbs.services.impl.DossierDetailsService;
import com.nmbs.services.impl.DossierService;
import com.nmbs.services.impl.DossiersUpToDateService;
import com.nmbs.services.impl.LoginService;
import com.nmbs.services.impl.MasterService;
import com.nmbs.services.impl.MessageService;
import com.nmbs.services.impl.NotificationService;
import com.nmbs.services.impl.OfferService;
import com.nmbs.services.impl.PushService;
import com.nmbs.services.impl.RatingService;
import com.nmbs.services.impl.RegisterService;
import com.nmbs.services.impl.ScheduleService;
import com.nmbs.services.impl.ServiceConstant;
import com.nmbs.services.impl.SettingService;
import com.nmbs.services.impl.StationInfoService;
import com.nmbs.services.impl.StationService;
import com.nmbs.services.impl.TestService;
import com.nmbs.services.impl.TrackerService;
import com.nmbs.services.impl.TrainIconsService;
import com.nmbs.util.AppLanguageUtils;
import com.nmbs.util.ConstantLanguages;
import com.nmbs.util.CrashHandler;
import com.nmbs.util.DateUtils;
import com.nmbs.util.SharedPreferencesUtils;
import com.nmbs.util.Utils;

import net.sqlcipher.database.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 *  Entry point to the ServiceLayer of the application. Each Activity which wants access to a certain Service can ask via this global available class the Service it needs.
 *
 *  Each Service can use 'this' to have a reference to the application context, to create Intent, ... objects who need a Context object.
 *
 *   All Services are using a corresponding Interface to allow test or mock services to be used for unit & integration testing.
 *
 *
 * @author David.Shi
 *
 */
public class NMBSApplication extends MultiDexApplication {

	//private static final String TAG = NMBSApplication.class.getSimpleName();
	//OfferService, used for the planner functionality.
	private IOfferService offerService;
	private INotificationService notificationService;
	private IMasterService masterService;
	private IRegisterService registerService;
	private SettingService settingService;
	private RatingService ratingService;
	private IAssistantService assistantService;
	private IDossierService dossierService;
	private IClickToCallService clickToCallService;
	private IMessageService messageService;
	private IStationInfoService stationInfoService;
	private IScheduleService scheduleService;
	private IPushService pushService;
	private DossierDetailsService dossierDetailsService;
	private DossiersUpToDateService dossiersUpToDateService;
	private TestService testService;
	private TrainIconsService trainIconsService;
	private LoginService loginService;
	private StationService stationService;
	//private static Tracker mTracker;
	public static int tabSpec;

	public final static int PAGE_SCHEDULE = 0;
	public final static int PAGE_ALERT = 1;
	public final static int PAGE_PUSH = 2;

	public final static int PAGE_UploadTicket = 0;
	public final static int PAGE_UploadTicket_DNR = 1;
	public final static int PAGE_Dossier_Details = 2;
	/*
	 * Google Analytics configuration values.
	 */

	private ICheckUpdateService checkUpdateService;

	private boolean alarmset = false;
	public final static int REQUESTCODE_MATERIAL_SYNC = 1000;
	public final static int REQUESTCODE_EMAIL_SYNC = 2000;
	private boolean alarmsetUptudate = false;
	private boolean alarmsetRefresh = false;
	private boolean alarmsetCheckOption = false;
	private boolean alarmsetCheckOptionNotification = false;
	public final static int REQUESTCODE_UPDATE = 1001;
	public final static int REQUESTCODE_REFRESH = 1002;
	public final static int REQUESTCODE_CHECKOPTIONS = 1003;
	public final static int REQUESTCODE_CHECKOPTIONS_NOTIFICATION = 1004;
	private Activity activityTop;
	private Timer t;

	private FirebaseAnalytics mFirebaseAnalytics;
	//private IDossierPromoCodeService dossierPromoCodeService;
	/**
	 * All Services are created and initiated when the application starts.
	 *
	 * When a test version is needed, the test version should be used here.
	 */
	@Override
	public void onCreate() {
		//Log.e(TAG, "onCreate");
		LogUtils.e("NMBSApplication", "NMBSApplication--------->");
		String processName = Utils.getProcessName();
		//LogUtils.setLogDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
		//if (processName != null && !processName.isEmpty() && processName.equals(this.getPackageName())) {//判断进程名，保证只有主进程运行
			LogUtils.e("NMBSApplication", "onCreate--------->");
			SQLiteDatabase.loadLibs(this);
			offerService = new OfferService(this);
			notificationService = new NotificationService(this);
			notificationService.setNotificationOngoing(getApplicationContext(), false);
			registerService = new RegisterService(this);
			loginService = new LoginService(this);
			masterService = new MasterService(this);
			settingService = new SettingService(this);
			assistantService = new AssistantService(this);
			dossierService = new DossierService(this);

			clickToCallService = new ClickToCallService(this);
			ratingService = new RatingService(this);
			checkUpdateService = new CheckUpdateService(this);
			messageService = new MessageService(this);
			stationInfoService = new StationInfoService(this);
			scheduleService = new ScheduleService(this);
			pushService = new PushService(this);
			dossierDetailsService = new DossierDetailsService(this);
			dossiersUpToDateService = new DossiersUpToDateService(this);
			testService = new TestService(this);
			trainIconsService = new TrainIconsService(this);
			stationService = new StationService(this);
			//dossierPromoCodeService = new DossierPromoCodeService(this);
			//set context
			//TrackerService.getTrackerService().setContext(this);
			//record the number of open program
			//TrackerService.getTracker().createPageViewTracker();
			//initializeGa();

		setAlarmCheckOptions();
		registerNotification();
		setAlarmRefreshDossier();
/*			if (!alarmset) {
				setAlarm();
			}*/
			Date updateTime = DossiersUpToDateService.getUpdateTime(getApplicationContext());
			if(updateTime == null){
				DossiersUpToDateService.saveUpdateTime(getApplicationContext(), new Date());
				//Log.e("updateTime", "updateTime is null...");
				String emailR6 = SharedPreferencesUtils.getSharedPreferencesByKey(SettingsActivity.PREFS_EMAIL, getApplicationContext());
				//Log.e("updateTime", "emailR6 email is..." + emailR6);
				SettingsPref.saveSettingsEmail(getApplicationContext(), emailR6);
			}else {

			}
		LogUtils.e("UpToDate", "alarmsetUptudate::::" + alarmsetUptudate);
			if (!alarmsetUptudate) {
				setAlarmUpdate();
			}
			if(TestService.isTestMode){
				//CrashHandler crashHandler = CrashHandler.getInstance();
				//crashHandler.init(getApplicationContext());
			}
		//}
	}



	public Activity getActivity(){
		return activityTop;
	}

	private void registerNotification(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 16);
		cal.set(Calendar.MINUTE, 01);
		cal.set(Calendar.SECOND, 00);
		cal.set(Calendar.MILLISECOND, 00);
		AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(this, CheckOptionNotificationReceiver.class);
		//Intent intent = new Intent();
		//对应BroadcastReceiver中intentFilter的action
		//intent.setAction("BROADCAST_ACTION");
		PendingIntent pendingIntent =
				PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			am.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
		} else {
			// 使用AlarmManger的setRepeating方法设置定期执行的时间间隔（seconds秒）和需要执行的Service
			am.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
		}
	}

	public void setAlarmCheckOptions() {

		Calendar cal = Calendar.getInstance();

		cal.set(Calendar.HOUR_OF_DAY, 14);
		cal.set(Calendar.MINUTE, 30);
		cal.set(Calendar.SECOND, 00);
		cal.set(Calendar.MILLISECOND, 00);
		/*Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.get(Calendar.SECOND));*/
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		LogUtils.d("setAlarmCheckOptions", sdf.format(cal.getTime()));

		Intent intent = new Intent(NMBSApplication.getInstance(), CheckOptionReceiver.class);
		int RequestCode = NMBSApplication.REQUESTCODE_CHECKOPTIONS;
		intent.putExtra("RequestCode", RequestCode);
		PendingIntent sender = PendingIntent.getBroadcast(NMBSApplication.getInstance(), RequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager am = (AlarmManager) NMBSApplication.getInstance().getSystemService(Context.ALARM_SERVICE);
		//am.cancel(sender);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, sender);
		} else {
			// 使用AlarmManger的setRepeating方法设置定期执行的时间间隔（seconds秒）和需要执行的Service
			am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, sender);
		}
	}

	public void setAlarmRefreshDossier() {
		Calendar cal = Calendar.getInstance();

		cal.set(Calendar.HOUR_OF_DAY, 17);
		cal.set(Calendar.MINUTE, 10);
		cal.set(Calendar.SECOND, 00);
		cal.set(Calendar.MILLISECOND, 00);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Intent intent = new Intent(NMBSApplication.getInstance(), AlarmsRefreshDossierBroadcastReceiver.class);
		int RequestCode = NMBSApplication.REQUESTCODE_REFRESH;
		intent.putExtra("RequestCode", RequestCode);
		PendingIntent sender = PendingIntent.getBroadcast(NMBSApplication.getInstance(), RequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		LogUtils.d("setAlarmRefreshDossier", sdf.format(cal.getTime()));
		AlarmManager am = (AlarmManager) NMBSApplication.getInstance().getSystemService(Context.ALARM_SERVICE);
		//am.cancel(sender);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),4 * 60 * 60 * 1000, sender);
		} else {
			// 使用AlarmManger的setRepeating方法设置定期执行的时间间隔（seconds秒）和需要执行的Service
			am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),4 * 60 * 60 * 1000, sender);
		}
	}

	public void setAlarmUpdate() {


		Date updateTime = dossiersUpToDateService.getUpdateTime(getApplicationContext());
		if(updateTime != null){
			Calendar cal = Calendar.getInstance();
			cal.setTime(updateTime);

			Calendar calUpDte = Calendar.getInstance();
			calUpDte.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
			calUpDte.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
			calUpDte.set(Calendar.SECOND, cal.get(Calendar.SECOND));
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			LogUtils.e("UpToDate", "Start time is::::" + sdf.format(calUpDte.getTime()));

			Intent intent = new Intent(NMBSApplication.getInstance(), UpdateAlarmsBroadcastReceiver.class);
			int RequestCode = REQUESTCODE_UPDATE;
			intent.putExtra("RequestCode", RequestCode);
			PendingIntent sender = PendingIntent.getBroadcast(NMBSApplication.getInstance(), RequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

			AlarmManager am = (AlarmManager) NMBSApplication.getInstance().getSystemService(Context.ALARM_SERVICE);
			//am.cancel(sender);

			//am.setRepeating(AlarmManager.RTC, calUpDte.getTimeInMillis(), 1 * 30 * 1000, sender);
			// 这里要注意，如果API>=19，就不能再使用setRepeating，应该改为setWindow
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				am.setWindow(AlarmManager.RTC_WAKEUP, calUpDte.getTimeInMillis(), AlarmManager.INTERVAL_DAY, sender);
			} else {
				// 使用AlarmManger的setRepeating方法设置定期执行的时间间隔（seconds秒）和需要执行的Service
				am.setRepeating(AlarmManager.RTC_WAKEUP, calUpDte.getTimeInMillis(), AlarmManager.INTERVAL_DAY, sender);
			}

			//android.util.Log.e("UpToDate", "getNextAlarmClock::::" + am.getNextAlarmClock());
			alarmsetUptudate = true;

			//acquireWakeLock();

		}
		// cal.add(Calendar.SECOND, 5);
	}

	public void timeOut(){

		t = new Timer();
		t.schedule(new TimerTask() {
			public void run() {
				LogUtils.e("timeOut", "timeOut::::run");
				GetSubScriptionListAsyncTask.isAlertSubscriptionFinished = true;
				Intent alertCountIntent = new Intent(ServiceConstant.PUSH_GET_SUBSCRIPTION_LIST_ACTION);
				getApplicationContext().sendBroadcast(alertCountIntent);
				MobileMessageAsyncTask.isMessageFinished = true;
				Intent broadcastIntent = new Intent(ServiceConstant.MESSAGE_SERVICE_ACTION);
				getApplicationContext().sendBroadcast(broadcastIntent);
			}
		}, 12000, 12000);
		if(t != null){
			t.cancel();
			t = null;
		}
	}

	public static NMBSApplication getInstance() {
		return instance;
	}
/*	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		//settingService.initLanguageSettings();
	}*/

	/**
	 * The OfferService to be used by the planner Activities.
	 * Get OfferService from NMBSService
	 * @return An implementation of IOfferService to be used by the planner Activities
	 */
	public IPushService getPushService(){
		return this.pushService;
	}

	public IScheduleService getScheduleService(){
		return scheduleService;
	}

	public IStationInfoService getStationInfoService(){
		return stationInfoService;
	}

	public IOfferService getOfferService() {
		return offerService;
	}

	public IMessageService getMessageService(){
		return this.messageService;
	}

	public INotificationService getNotificationService(){
		return notificationService;
	}

	public IRegisterService getRegisterService() {
		return registerService;
	}

	public IDossierService getDossierService() {
		return dossierService;
	}

	public ICheckUpdateService getCheckUpdateService(){
		return checkUpdateService;
	}

	public DossiersUpToDateService getDossiersUpToDateService() {
		return dossiersUpToDateService;
	}

	/*	public IDossierPromoCodeService getDossierPromoCodeService() {
		return dossierPromoCodeService;
	}*/

	public RatingService getRatingService() {
		return ratingService;
	}

	public LoginService getLoginService() {
		return loginService;
	}

	public SettingService getSettingService() {
		return settingService;
	}

	public IAssistantService getAssistantService() {
		return assistantService;
	}
	public IMasterService getMasterService() {
		return masterService;
	}

	public TestService getTestService() {
		return testService;
	}


	public TrainIconsService getTrainIconsService() {
		return trainIconsService;
	}

	public IClickToCallService getClickToCallService() {
		return clickToCallService;
	}

	public StationService getStationService() {
		return stationService;
	}

	/** Instance of the current application. */
	private static NMBSApplication instance;

	/**
	 * Constructor.
	 */
	public NMBSApplication() {
		//LogUtils.e("NMBSApplication", "this::::" + this);

		instance = this;
		//LogUtils.e("NMBSApplication", "this::::" + NMBSApplication.getInstance().getApplicationContext());
	}

	/**
	 * Gets the application context.
	 *
	 * @return the application context
	 */
	public static Context getContext() {
		return instance;
	}


	//* Method to handle basic Google Analytics initialization. This call will
	// * not block as all Google Analytics work occurs off the main thread.

	/*		private void initializeGa() {
                if (mTracker == null) {
                    GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
                    mTracker = analytics.newTracker(R.xml.global_tracker);
                }
            }



            // * Returns the Google Analytics tracker.

            public Tracker getGaTracker() {
                if (mTracker == null) {
                    GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
                    mTracker = analytics.newTracker(R.xml.global_tracker);
                }
                return mTracker;
            }*/
	public FirebaseAnalytics getGaTracker() {
		if (mFirebaseAnalytics == null) {
			mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
		}
		return mFirebaseAnalytics;
	}
	public DossierDetailsService getDossierDetailsService() {
		return dossierDetailsService;
	}
/*	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);

	}*/

	public String getServerUrl(){
		return getContext().getResources().getString(R.string.server_url);
	}

	public String getHafasUrl(){
		if(TestService.isTestMode && testService.getHafasUr().equalsIgnoreCase("Demo")){
			//return "http://demo.hafas.de/bene/hcssproxy/subscription";
			//return "http://demo.hafas.de/ns/hcssproxy/subscription";
			return "https://accept-realtime-push.bene-system.com/hcssproxy/subscription";
		}else {
			return "https://realtime-push.bene-system.com/hcssproxy/subscription";
		}
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(AppLanguageUtils.attachBaseContext(base, getAppLanguage(base)));
		MultiDex.install(this);
	}

	/**
	 * Handling Configuration Changes
	 * @param newConfig newConfig
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		onLanguageChange();
	}

	private void onLanguageChange() {
		AppLanguageUtils.changeAppLanguage(this, AppLanguageUtils.getSupportLanguage(getAppLanguage(this)));
	}

	private String getAppLanguage(Context context) {
		/*return PreferenceManager.getDefaultSharedPreferences(context)
				.getString(context.getString(R.string.app_language_pref_key), ConstantLanguages.ENGLISH);*/
		return SettingsPref.getCurrentLanguagesKey(context);
	}
}
