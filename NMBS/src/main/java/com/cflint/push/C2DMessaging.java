package com.cflint.push;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;
/** 
 * Utilities for device registration. 
 * 
 * Will keep track of the registration token in a private preference. 
 */  
public class C2DMessaging {
	static final String TAG = "C2DMessaging";
	public static final String EXTRA_SENDER = "sender";
	public static final String EXTRA_APPLICATION_PENDING_INTENT = "app";
	public static final String REQUEST_REGISTRATION_INTENT = "com.google.android.c2dm.intent.REGISTER";
	public static final String GSF_PACKAGE = "com.google.android.gsf";
	public static final String REQUEST_UNREGISTRATION_INTENT = "com.google.android.c2dm.intent.UNREGISTER";
	public static final String LAST_REGISTRATION_CHANGE = "last_registration_change";	
	static final String PREFERENCE = "com.google.android.c2dm";
	private static final long DEFAULT_BACKOFF = 30000;
	public static final String BACKOFF = "backoff";
	private final String REGISTRATION_ID_KEY = "regId";
	
	private static C2DMessaging instance;
	SharedPreferences prefs;
	
	private C2DMessaging(Context context){		
		this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	public static C2DMessaging getInstance(Context context){
		if(instance == null){
			instance = new C2DMessaging(context);			
		}
		return instance;
	}

	public long getBackoff(Context context) {
		return this.prefs.getLong(BACKOFF, DEFAULT_BACKOFF);
	}

	public void setBackoff(Context context, long backoff) {		
		Editor editor = this.prefs.edit();
		editor.putLong(BACKOFF, backoff);
		editor.commit();
	}
	
	public void clearRegistrationId(Context context) {
		Log.d(TAG, "Clear RegistrationId.");
		Editor editor = this.prefs.edit();
		editor.putString(REGISTRATION_ID_KEY, "");
		editor.putLong(LAST_REGISTRATION_CHANGE, System.currentTimeMillis());
		editor.commit();
	}
	
	public void setRegistrationId(Context context, String registrationId) {
		Log.d(TAG, "Store RegistrationId: "+registrationId);
		Editor editor = this.prefs.edit();
		editor.putString(REGISTRATION_ID_KEY, registrationId);
		editor.commit();

	}

	/**
	 * Return the current registration id.
	 * 
	 * If result is empty, the registration has failed.
	 * 
	 * @return registration id, or empty string if the registration is not
	 *         complete.
	 */
	public String getRegistrationId(Context context) {		
		String registrationId = this.prefs.getString(REGISTRATION_ID_KEY, "");
		Log.d(TAG, "Get the Store RegistrationId: "+registrationId);
		return registrationId;
	}
	
}
