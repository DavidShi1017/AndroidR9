package com.cflint.util;

import com.cflint.activity.SettingsActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;


/**
 * Used for storing or getting information from SharedPreferences.
 * 
 * @author Tony
 */
public class SharedPreferencesUtils {

	public static SharedPreferences prefs;

	public static SharedPreferences initSharedPreferences(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs;
	}

	public static String getSharedPreferencesByKey(String key, Context context) {
		String value = "";
		if (prefs == null) {
			initSharedPreferences(context);
		}
		if (key.equals(SettingsActivity.PREFS_FIRST_NAME)
				|| key.equals(SettingsActivity.PREFS_LAST_NAME)
				|| key.equals(SettingsActivity.PREFS_EMAIL)
				|| key.equals(SettingsActivity.PREFS_PHONE_NUMBER)
				|| key.equals(SettingsActivity.PREFS_GENDER)) {
			//Log.i("TAG", key+"-----key-----"+prefs.getString(key, ""));
			if (!prefs.getString(key, "").equals("")){
				try {
					value = decrypt(prefs.getString(key, ""));
				} catch (Exception e) {
					e.printStackTrace();
					storeSharedPreferences(key,prefs.getString(key, ""),context);
					value = getSharedPreferencesByKey(key,context);
					return value;
				}
				
			}
		}else {
			value = prefs.getString(key, "");
		}
		return value;
	}
	
	public static String getUnencryptSharedPreferencesByKey(String key, Context context){
		if (prefs == null) {
			initSharedPreferences(context);
		}
		return prefs.getString(key, "");
	}
	
	public static boolean getSharedPreferencesByKey(String key,boolean defaultValue, Context context){
		if (prefs == null) {
			initSharedPreferences(context);
		}
		return prefs.getBoolean(key, defaultValue);
	}
	


	public static void storeSharedPreferences(String key, String value,
			Context context) {

		if (prefs == null) {
			initSharedPreferences(context);
		}
		if (key.equals(SettingsActivity.PREFS_FIRST_NAME)
				|| key.equals(SettingsActivity.PREFS_LAST_NAME)
				|| key.equals(SettingsActivity.PREFS_EMAIL)
				|| key.equals(SettingsActivity.PREFS_PHONE_NUMBER)
				|| key.equals(SettingsActivity.PREFS_GENDER)) {
			value = encrypt(value);
		}
		//Log.i("SharedPreferencesUtils", value+"-----value-----" + value);
		Editor editor = prefs.edit();
		
		editor.putString(key, value);
		editor.commit();
	}

	public static void storeAppInfoSharedPreferences(String key, String value,
			Context context) {
		if (prefs == null) {
			initSharedPreferences(context);
		}
		Editor editor = prefs.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public static void deleteAppInfoSharePreferences(String key,Context context){
		if (prefs == null) {
			initSharedPreferences(context);
		}
		Editor editor = prefs.edit();
		editor.remove(key);
		editor.commit();
	}
	
	private static String decrypt(String encryptString) throws Exception {
		try {
//			Log.i("TAG", "decrypt----"+encryptString);
			return AESUtils.decrypt(encryptString);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}
	private static String encrypt(String decryptString) {
		try {
			return AESUtils.encrypt(decryptString);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public static void storeSharedPreferences(String key1, boolean value1, Context context){
		if (prefs == null) {
			initSharedPreferences(context);
		}
		Editor editor = prefs.edit();		
		editor.putBoolean(key1, value1);
		editor.commit();
	}
	
}
