package com.cflint.dataaccess.restservice.impl;

import com.cflint.application.NMBSApplication;
import com.cflint.dataaccess.restservice.INotificationDataService;
import com.cflint.exceptions.RequestFail;
import com.cflint.push.C2DMBaseReceiver;
import com.cflint.services.INotificationService;
import com.cflint.services.impl.ServiceConstant;
import com.cflint.services.impl.NotificationService;
import com.cflint.util.HTTPRestServiceCaller;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;



/**
 * Call web service and Support Notification Data for NotificationService.
 */
public class NotificationDataService extends C2DMBaseReceiver implements INotificationDataService {

	public enum ErrorNotification {
		 FAILREGISTER,
		 FAILUNREGISTER,
		 SERVICE_NOT_AVAILABLE,
		 ACCOUNT_MISSING,
		 AUTHENTICATION_FAILED,
		 TOO_MANY_REGISTRATIONS,
		 INVALID_SENDER,
		 PHONE_REGISTRATION_ERROR
	}

	static final String TAG = "NotificationDataService";
	INotificationService iNotificationService;
	public static final int NOTIFICATION_ID = 4745;
	public static final String NOTIFICATION_SETTINGS = "";		
	public static final String EXTRA_SENDER = "sender";
	public static final String EXTRA_APPLICATION_PENDING_INTENT = "app";
	public static final String REQUEST_REGISTRATION_INTENT = "com.google.android.c2dm.intent.REGISTER";
	public static final String GSF_PACKAGE = "com.google.android.gsf";
	public static final String REQUEST_UNREGISTRATION_INTENT = "com.google.android.c2dm.intent.UNREGISTER";
	public static final String LAST_REGISTRATION_CHANGE = "last_registration_change";
	private static String senderdmId = "adniosdwqs@gmail.com";	
	static final String PREFERENCE = "com.google.android.c2dm";
	public static final String BACKOFF = "backoff";
	private static NotificationDataService instance;
	public static final String PARAM_OUT_ERROR = "error";
	SharedPreferences prefs;
	
	public NotificationDataService(){	
		super(senderdmId);
	}
	
	/**
	 * Get NotificationDataService Singleton.
	 * @return NotificationDataService
	 */
	public static NotificationDataService getInstance(){
		if(instance == null){
			instance = new NotificationDataService();			
		}
		return instance;
	}
	
	/**
	 *  Execute to subscribe Favorite push notification service
	 *  @param settings
	 *  @param context 
	 *  @param senderId	 
	 */
	public void executeFavoriteNotification(boolean settings, Context context, String senderId){
		if (settings) {
			register(context, senderId);
		}else {
			unregister(context);
		}
	}
	
	/**
	 * Initiate c2d messaging registration for the current application
	 */
	public void register(Context context, String senderId) {
		//Log.d(TAG, "register... ");
		Intent registrationIntent = new Intent(REQUEST_REGISTRATION_INTENT);
		registrationIntent.putExtra(EXTRA_APPLICATION_PENDING_INTENT,
				PendingIntent.getBroadcast(context, 0, new Intent(), 0));
		registrationIntent.putExtra(EXTRA_SENDER, senderdmId);
		context.startService(registrationIntent);		
	}

	/**
	 * Unregister the application. New messages will be blocked by server.
	 */
	public void unregister(Context context) {
		//Log.d(TAG, "unregister... ");
		Intent regIntent = new Intent(REQUEST_UNREGISTRATION_INTENT);
		regIntent.putExtra(EXTRA_APPLICATION_PENDING_INTENT, PendingIntent
				.getBroadcast(context, 0, new Intent(), 0));		
		context.startService(regIntent);
	}


    @Override
    public void onRegistered(Context context, String registrationId) {
    	//Log.d(TAG, "Starting ... onRegistered()");
    	// The registrationId should be send to your application server.
    	String isRegistered = null;
		try {
			isRegistered = new HTTPRestServiceCaller().executeHTTPRequest(context, 0, 1);
			if (Boolean.parseBoolean(isRegistered)) {
				
				iNotificationService =((NMBSApplication)getApplication()).getNotificationService();
				boolean isSettingsOn = iNotificationService.getFavoriteNotificationPreferences(getApplicationContext());
				iNotificationService.setFavoriteNotificationPreferences(getApplicationContext(),!isSettingsOn);
				broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_NOTIFICATION_RESPONSE);
				broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
				sendBroadcast(broadcastIntent);
			}
		} catch (RequestFail e) {
			catchError(ErrorNotification.FAILREGISTER);
		}
    }
    Intent broadcastIntent = new Intent();
    @Override
    public void onUnregistered(Context context) { 
    	//Log.d(TAG, "Starting ... onUnregistered()");
    	// The registrationId should be send to your application server.
    	String isRegistered = null;
		try {
			isRegistered = new HTTPRestServiceCaller().executeHTTPRequest(context, 0, 0);
			if (Boolean.parseBoolean(isRegistered)) {
				
				iNotificationService =((NMBSApplication)getApplication()).getNotificationService();
				boolean isSettingsOn = iNotificationService.getFavoriteNotificationPreferences(getApplicationContext());
				iNotificationService.setFavoriteNotificationPreferences(getApplicationContext(),!isSettingsOn);
				broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_NOTIFICATION_RESPONSE);
				broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
				sendBroadcast(broadcastIntent);
			}
		} catch (RequestFail e) {
			catchError(ErrorNotification.FAILUNREGISTER);
		}
		
    }
	 
	@Override
	public void onError(Context context, String errorId) {
		//context.sendBroadcast(new Intent("com.google.ctp.UPDATE_UI"));
		if ("SERVICE_NOT_AVAILABLE".equals(errorId)) {
			catchError(ErrorNotification.SERVICE_NOT_AVAILABLE);
		} else if ("ACCOUNT_MISSING".equals(errorId)) {
			catchError(ErrorNotification.ACCOUNT_MISSING);
		} else if ("AUTHENTICATION_FAILED".equals(errorId)) {
			catchError(ErrorNotification.AUTHENTICATION_FAILED);
		} else if ("TOO_MANY_REGISTRATIONS".equals(errorId)) {
			catchError(ErrorNotification.TOO_MANY_REGISTRATIONS);
		} else if ("INVALID_SENDER".equals(errorId)) {
			catchError(ErrorNotification.INVALID_SENDER);
		} else if ("PHONE_REGISTRATION_ERROR".equals(errorId)) {
			catchError(ErrorNotification.PHONE_REGISTRATION_ERROR);
		}
	}


   @Override
    protected void onMessage(Context context, Intent intent) {
        String message = intent.getExtras().getString(C2DM_MESSAGE_EXTRA);
        if(message != null){
        	 //Log.d(TAG, "message " + message);
        }    
    	
        iNotificationService =((NMBSApplication)getApplication()).getNotificationService();
		boolean isSettingsOn = iNotificationService.getFavoriteNotificationPreferences(getApplicationContext());
    	if(intent != null && isSettingsOn){
    		NotificationService.getInstance(context).registerNotificationOnStatusBar(message);
    	}
    }

	
	public void catchError(ErrorNotification error){
		broadcastIntent.setAction(ServiceConstant.INTENT_ACTION_NOTIFICATION_ERROR);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra(PARAM_OUT_ERROR, error);
		sendBroadcast(broadcastIntent);
	}
	
}
