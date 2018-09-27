package com.cfl.application;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;


import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.cfl.R;
import com.cfl.activity.SettingsActivity;
import com.cfl.async.GetSubScriptionListAsyncTask;
import com.cfl.async.MobileMessageAsyncTask;
import com.cfl.exceptions.NetworkError;
import com.cfl.log.LogUtils;
import com.cfl.preferences.SettingsPref;
import com.cfl.receivers.AlarmsBroadcastReceiver;
import com.cfl.receivers.AlarmsRefreshDossierBroadcastReceiver;
import com.cfl.receivers.CheckOptionNotificationReceiver;
import com.cfl.receivers.CheckOptionReceiver;
import com.cfl.receivers.UpdateAlarmsBroadcastReceiver;
import com.cfl.services.IAssistantService;
import com.cfl.services.ICheckUpdateService;
import com.cfl.services.IClickToCallService;
import com.cfl.services.IDossierService;
import com.cfl.services.ILoginService;
import com.cfl.services.IMasterService;
import com.cfl.services.IMessageService;
import com.cfl.services.INotificationService;
import com.cfl.services.IOfferService;
import com.cfl.services.IPushService;
import com.cfl.services.IRegisterService;
import com.cfl.services.IScheduleService;
import com.cfl.services.ISettingService;
import com.cfl.services.IStationInfoService;
import com.cfl.services.impl.AssistantService;
import com.cfl.services.impl.CheckOptionNotificationService;
import com.cfl.services.impl.CheckUpdateService;
import com.cfl.services.impl.ClickToCallService;
import com.cfl.services.impl.DossierDetailsService;
import com.cfl.services.impl.DossierService;
import com.cfl.services.impl.DossiersUpToDateService;
import com.cfl.services.impl.LoginService;
import com.cfl.services.impl.MasterService;
import com.cfl.services.impl.MessageService;
import com.cfl.services.impl.NotificationService;
import com.cfl.services.impl.OfferService;
import com.cfl.services.impl.PushService;
import com.cfl.services.impl.RatingService;
import com.cfl.services.impl.RegisterService;
import com.cfl.services.impl.ScheduleService;
import com.cfl.services.impl.ServiceConstant;
import com.cfl.services.impl.SettingService;
import com.cfl.services.impl.StationInfoService;
import com.cfl.services.impl.StationService;
import com.cfl.services.impl.TestService;
import com.cfl.services.impl.TrackerService;
import com.cfl.services.impl.TrainIconsService;
import com.cfl.util.AppLanguageUtils;
import com.cfl.util.ConstantLanguages;
import com.cfl.util.CrashHandler;
import com.cfl.util.DateUtils;
import com.cfl.util.SharedPreferencesUtils;

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
	private static Tracker mTracker;
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
		Intent intentOne = new Intent(this, CheckOptionNotificationService.class);
		//intentOne.setAction(ACTION_START);
		intentOne.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startService(intentOne);
		if(!alarmsetCheckOptionNotification){
			//setAlarmCheckOptionsNotification();
		}

		if(!alarmsetCheckOption){
			//setAlarmCheckOptions();
		}
		if (!alarmsetRefresh) {
			//setAlarmRefreshDossier();
		}
		if (!alarmset) {
			setAlarm();
		}
		Date updateTime = DossiersUpToDateService.getUpdateTime(getApplicationContext());
		if(updateTime == null){
			DossiersUpToDateService.saveUpdateTime(getApplicationContext(), new Date());
			//Log.e("updateTime", "updateTime is null...");
			String emailR6 = SharedPreferencesUtils.getSharedPreferencesByKey(SettingsActivity.PREFS_EMAIL, getApplicationContext());
			//Log.e("updateTime", "emailR6 email is..." + emailR6);
			SettingsPref.saveSettingsEmail(getApplicationContext(), emailR6);
		}else {

		}
		android.util.Log.e("UpToDate", "alarmsetUptudate::::" + alarmsetUptudate);
		if (!alarmsetUptudate) {
			setAlarmUpdate();
		}
		if(TestService.isTestMode){
			//CrashHandler crashHandler = CrashHandler.getInstance();
			//crashHandler.init(getApplicationContext());
		}
	}



	public Activity getActivity(){
		return activityTop;
	}

	public void setAlarmRefreshDossier() {

		Date date = DateUtils.getAfterManyHour(new Date(), 1);
		long firstTime = date.getTime();
		Intent intent = new Intent(NMBSApplication.getInstance(), AlarmsRefreshDossierBroadcastReceiver.class);
		int RequestCode = NMBSApplication.REQUESTCODE_REFRESH;
		intent.putExtra("RequestCode", RequestCode);
		PendingIntent sender = PendingIntent.getBroadcast(NMBSApplication.getInstance(), RequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager am = (AlarmManager) NMBSApplication.getInstance().getSystemService(Context.ALARM_SERVICE);
		//am.cancel(sender);
		am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime,4 * 60 * 60 * 1000, sender);

		alarmsetRefresh = true;
	}

	public void setAlarmCheckOptions() {
		// setup of Alarm for sync Material
		Calendar cal = Calendar.getInstance();

		cal.set(Calendar.HOUR_OF_DAY, 4);
		cal.set(Calendar.MINUTE, 30);
		cal.set(Calendar.SECOND, 00);
		cal.set(Calendar.MILLISECOND, 00);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		LogUtils.d("setAlarmCheckOptions", sdf.format(cal.getTime()));

		Intent intent = new Intent(NMBSApplication.getInstance(), CheckOptionReceiver.class);
		int RequestCode = NMBSApplication.REQUESTCODE_CHECKOPTIONS;
		intent.putExtra("RequestCode", RequestCode);
		PendingIntent sender = PendingIntent.getBroadcast(NMBSApplication.getInstance(), RequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager am = (AlarmManager) NMBSApplication.getInstance().getSystemService(Context.ALARM_SERVICE);
		am.cancel(sender);

		am.setRepeating(AlarmManager.RTC, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, sender);

		alarmsetCheckOption = true;
	}

	public void setAlarmCheckOptionsNotification() {
		// setup of Alarm for sync Material
		Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 13);
        cal.set(Calendar.MINUTE, 41);
        cal.set(Calendar.SECOND, 00);
        cal.set(Calendar.MILLISECOND, 00);
        AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //Intent intent = new Intent(this, CheckOptionNotificationReceiver.class);
		Intent intent = new Intent();
		//对应BroadcastReceiver中intentFilter的action
		intent.setAction("BROADCAST_ACTION");
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
		alarmsetCheckOptionNotification = true;
	}

	public void setAlarm() {
		// setup of Alarm for sync Material
		Calendar cal = Calendar.getInstance();

		cal.set(Calendar.HOUR_OF_DAY, 1);
		cal.set(Calendar.MINUTE, 00);
		cal.set(Calendar.SECOND, 00);
		cal.set(Calendar.MILLISECOND, 00);

		// cal.add(Calendar.SECOND, 5);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		android.util.Log.d("ScantecApplication", sdf.format(cal.getTime()));

		Intent intent = new Intent(NMBSApplication.getInstance(), AlarmsBroadcastReceiver.class);
		int RequestCode = NMBSApplication.REQUESTCODE_MATERIAL_SYNC;
		intent.putExtra("RequestCode", RequestCode);
		PendingIntent sender = PendingIntent.getBroadcast(NMBSApplication.getInstance(), RequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager am = (AlarmManager) NMBSApplication.getInstance().getSystemService(Context.ALARM_SERVICE);
		am.cancel(sender);

		am.setRepeating(AlarmManager.RTC, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, sender);

		alarmset = true;
	}

	public void setAlarmUpdate() {
		// setup of Alarm for sync Material

		Date updateTime = dossiersUpToDateService.getUpdateTime(getApplicationContext());
		if(updateTime != null){
			Calendar cal = Calendar.getInstance();
			cal.setTime(updateTime);

			Calendar calUpDte = Calendar.getInstance();
			calUpDte.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
			calUpDte.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
			calUpDte.set(Calendar.SECOND, cal.get(Calendar.SECOND));
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			android.util.Log.e("UpToDate", "Start time is::::" + sdf.format(calUpDte.getTime()));

			Intent intent = new Intent(NMBSApplication.getInstance(), UpdateAlarmsBroadcastReceiver.class);
			int RequestCode = REQUESTCODE_UPDATE;
			intent.putExtra("RequestCode", RequestCode);
			PendingIntent sender = PendingIntent.getBroadcast(NMBSApplication.getInstance(), RequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

			AlarmManager am = (AlarmManager) NMBSApplication.getInstance().getSystemService(Context.ALARM_SERVICE);
			am.cancel(sender);

			am.setRepeating(AlarmManager.RTC, calUpDte.getTimeInMillis(), AlarmManager.INTERVAL_DAY, sender);
			//android.util.Log.e("UpToDate", "getNextAlarmClock::::" + am.getNextAlarmClock());
			alarmsetUptudate = true;
		}
		// cal.add(Calendar.SECOND, 5);
	}

	public void timeOut(){

		t = new Timer();
		t.schedule(new TimerTask() {
			public void run() {
				android.util.Log.e("timeOut", "timeOut::::run");
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
		instance = this;
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
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getString(context.getString(R.string.app_language_pref_key), ConstantLanguages.ENGLISH);
	}
}
