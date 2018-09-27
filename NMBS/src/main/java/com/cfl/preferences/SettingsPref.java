package com.cfl.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cfl.R;
import com.cfl.util.ConstantLanguages;
import com.cfl.util.DeviceUtil;

public class SettingsPref {

	private static final String PROPERTY_APP_EMAIL = "SettingsEmail";
	private static final String PROPERTY_APP_PASSWORD = "SettingsPasswrod";
	private static final String PROPERTY_APP_AUTOUPDATE = "SettingsAutoUpdate";
	private static final String PROPERTY_APP_TRAVELREMINDERS = "SettingsTravelReminders";
	private static final String PROPERTY_APP_DNR = "SettingsDnr";
	private static final String PROPERTY_APP_OPTIONS = "SettingsOptions";
	private static final String PROPERTY_APP_FAVEBOOKTRACK = "SettingsFacebookTrack";
	private static final String PROPERTY_APP_3RDTRACK = "Settings3rdTrack";
	private static final String PROPERTY_APP_STARTNOTIFI = "SettingsStartNotifi";
	private static final String PROPERTY_APP_DELAYNOTIFI = "SettingsDelayNotifi";
	private static final String PROPERTY_APP_LANGUAGES = "SettingsLanguages";
	private static final String PROPERTY_APP_Version = "SettingsVersion";
	private static final String PREFERENCES_SETTINGS = "com.nmbs.settings";
	private static final String PREFERENCES_LOCAL_NOTIFICATION = "com.nmbs.localNotification";
	private static final String PROPERTY_APP_LOGINVIEW = "LoginView";

	public static void saveSettingsVersion(Context context, String version){

		SharedPreferences.Editor editor = getSettingsPreferences(context).edit();

		editor.putString(PROPERTY_APP_Version, version);
		editor.commit();
	}

	public static String getSettingsVersion(Context context){
		//String version = getSettingsPreferences(context).getString(PROPERTY_APP_Version, "");
		return DeviceUtil.getAppVersionName(context);
		//String version = "7.0";
		//return version;
	}

	public static void saveSettingsEmail(Context context, String email){

		SharedPreferences.Editor editor = getSettingsPreferences(context).edit();

		editor.putString(PROPERTY_APP_EMAIL, email);
		editor.commit();
	}

	public static String getSettingsEmail(Context context){
		String email = getSettingsPreferences(context).getString(PROPERTY_APP_EMAIL, "");
		return email;
	}

	private static SharedPreferences getSettingsPreferences(Context context) {
		return context.getSharedPreferences(PREFERENCES_SETTINGS, Context.MODE_PRIVATE);
	}

	public static void saveSettingsPassword(Context context, String password){
		SharedPreferences.Editor editor = getSettingsPreferences(context).edit();
		editor.putString(PROPERTY_APP_PASSWORD, password);
		editor.commit();
	}


	public static String getSettingsPassword(Context context) {
		String password = getSettingsPreferences(context).getString(PROPERTY_APP_PASSWORD, "");
		return password;
	}
	public static void deletePersonalData(Context context){
		SharedPreferences.Editor editor = getSettingsPreferences(context).edit();
		editor.remove(PROPERTY_APP_EMAIL);
		editor.remove(PROPERTY_APP_PASSWORD);
		editor.commit();
	}

	public static void saveAutoUpdate(Context context, boolean isChecked){
		SharedPreferences.Editor editor = getSettingsPreferences(context).edit();
		editor.putBoolean(PROPERTY_APP_AUTOUPDATE, isChecked);
		editor.commit();
	}
	public static boolean isAutoUpdate(Context context){
		boolean isChecked = getSettingsPreferences(context).getBoolean(PROPERTY_APP_AUTOUPDATE, true);
		return isChecked;
	}

	public static void saveDnr(Context context, boolean isChecked){
		SharedPreferences.Editor editor = getSettingsPreferences(context).edit();
		editor.putBoolean(PROPERTY_APP_DNR, isChecked);
		editor.commit();
	}
	public static boolean isDnr(Context context){
		boolean isChecked = getSettingsPreferences(context).getBoolean(PROPERTY_APP_DNR, true);
		return isChecked;
	}

	public static void saveOption(Context context, boolean isChecked){
		SharedPreferences.Editor editor = getSettingsPreferences(context).edit();
		editor.putBoolean(PROPERTY_APP_OPTIONS, isChecked);
		editor.commit();
	}
	public static boolean isOption(Context context){
		boolean isChecked = getSettingsPreferences(context).getBoolean(PROPERTY_APP_OPTIONS, true);
		return isChecked;
	}

	public static void saveTravelReminders(Context context, boolean isChecked){
		SharedPreferences.Editor editor = getSettingsPreferences(context).edit();
		editor.putBoolean(PROPERTY_APP_TRAVELREMINDERS, isChecked);
		editor.commit();
	}
	public static boolean isTravelReminders(Context context){
		boolean isChecked = getSettingsPreferences(context).getBoolean(PROPERTY_APP_TRAVELREMINDERS, true);
		return isChecked;
	}


	public static void saveFacebookTrack(Context context, boolean isChecked){
		SharedPreferences.Editor editor = getSettingsPreferences(context).edit();
		editor.putBoolean(PROPERTY_APP_FAVEBOOKTRACK, isChecked);
		editor.commit();
	}
	public static boolean isFacebookTrack(Context context){
		boolean isChecked = getSettingsPreferences(context).getBoolean(PROPERTY_APP_FAVEBOOKTRACK, true);
		return isChecked;
	}

	public static void save3rdTrack(Context context, boolean isChecked){
		SharedPreferences.Editor editor = getSettingsPreferences(context).edit();
		editor.putBoolean(PROPERTY_APP_3RDTRACK, isChecked);
		editor.commit();
	}
	public static boolean is3rdTrack(Context context){
		boolean isChecked = getSettingsPreferences(context).getBoolean(PROPERTY_APP_3RDTRACK, true);
		return isChecked;
	}

	public static void saveStartNotifiTime(Context context, String time){
		SharedPreferences.Editor editor = getSettingsPreferences(context).edit();
		editor.putString(PROPERTY_APP_STARTNOTIFI, time);
		editor.commit();
	}


	public static String getStartNotifiTime(Context context) {
		String password = getSettingsPreferences(context).getString(PROPERTY_APP_STARTNOTIFI, "");
		return password;
	}

	public static void saveLoginViewTime(Context context, String time){
		SharedPreferences.Editor editor = getSettingsPreferences(context).edit();
		editor.putString(PROPERTY_APP_LOGINVIEW, time);
		editor.commit();
	}


	public static String getLoginViewTime(Context context) {
		String password = getSettingsPreferences(context).getString(PROPERTY_APP_LOGINVIEW, "");
		return password;
	}

	public static void saveDelayNotifiTime(Context context, String time){
		SharedPreferences.Editor editor = getSettingsPreferences(context).edit();
		editor.putString(PROPERTY_APP_DELAYNOTIFI, time);
		editor.commit();
	}


	public static String getDelayNotifiTime(Context context) {
		String password = getSettingsPreferences(context).getString(PROPERTY_APP_DELAYNOTIFI, "");
		return password;
	}

	public static void saveCurrentLanguagesKey(Context context, String languagesKey){
		/*SharedPreferences.Editor editor = getSettingsPreferences(context).edit();
		editor.putString(PROPERTY_APP_LANGUAGES, languagesKey);
		editor.commit();*/
		Log.e("languagesKey", "saveCurrentLanguagesKey----->" + languagesKey);
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putString(context.getString(R.string.app_language_pref_key), languagesKey);
		editor.commit();
	}

	public static String getCurrentLanguagesKey(Context context) {
		String password = PreferenceManager.getDefaultSharedPreferences(context)
				.getString(context.getString(R.string.app_language_pref_key), "");//getSettingsPreferences(context).getString(PROPERTY_APP_LANGUAGES, "");
		//Log.e("languagesKey", "getCurrentLanguagesKey----->" + password);
		return password;
	}
}
