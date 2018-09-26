package com.nmbs.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.nmbs.R;
import com.nmbs.services.impl.RatingService;


public class RatingUtil {

	private static final String PROPERTY_CURRENT_TIMES = "currentTimes";
	private static final String PROPERTY_neverShowAgain = "NeverShowAgain";
	private static final String PREFERENCES_RATE = "com.nmbs.rating.app";

	private static final String PREFERENCES_stationboard_search = "StationboardSearch";
	private static final String PREFERENCES_schedule_search = "ScheduleSearch";
	private static final String PREFERENCES_DNR_upload = "DNRUpload";
	private static final String PREFERENCES_Dossier_Details = "Dossier_Details";


	public static void saveStationboardSearch(Context context) {
		SharedPreferences.Editor editor = getRatePreferences(context).edit();
		editor.putBoolean(PREFERENCES_stationboard_search, true);
		editor.commit();
	}

	public static boolean getStationboardSearch(Context context) {
		boolean is = getRatePreferences(context).getBoolean(PREFERENCES_stationboard_search, false);
		return is;
	}

	public static void saveScheduleSearch(Context context) {
		SharedPreferences.Editor editor = getRatePreferences(context).edit();
		editor.putBoolean(PREFERENCES_schedule_search, true);
		editor.commit();
	}
	public static boolean getScheduleSearch(Context context) {
		boolean is = getRatePreferences(context).getBoolean(PREFERENCES_schedule_search, false);
		return is;
	}
	public static void saveDNRUpload(Context context) {
		SharedPreferences.Editor editor = getRatePreferences(context).edit();
		editor.putBoolean(PREFERENCES_DNR_upload, true);
		editor.commit();
	}
	public static boolean getDNRUpload(Context context) {
		boolean is = getRatePreferences(context).getBoolean(PREFERENCES_DNR_upload, false);
		return is;
	}
	public static void saveDossierDetails(Context context) {
		SharedPreferences.Editor editor = getRatePreferences(context).edit();
		editor.putBoolean(PREFERENCES_Dossier_Details, true);
		editor.commit();
	}
	public static boolean getDossierDetails(Context context) {
		boolean is = getRatePreferences(context).getBoolean(PREFERENCES_Dossier_Details, false);
		return is;
	}
	public static void saveCurrentTimes(Context context) {
		int currentTimes = getCurrentTimes(context);
		SharedPreferences.Editor editor = getRatePreferences(context).edit();
		currentTimes = currentTimes + 1;
		editor.putInt(PROPERTY_CURRENT_TIMES, currentTimes);
		editor.commit();
	}


	public static void saveNeverShowAgain(Context context, boolean neverShowAgain) {
		SharedPreferences.Editor editor = getRatePreferences(context).edit();
		editor.putBoolean(PROPERTY_neverShowAgain, neverShowAgain);
		editor.commit();
	}

	public static boolean getNeverShowAgain(Context context) {

		boolean neverShowAgain = getRatePreferences(context).getBoolean(PROPERTY_neverShowAgain, false);

		return neverShowAgain;
	}

	private static SharedPreferences getRatePreferences(Context context) {
		return context.getSharedPreferences(PREFERENCES_RATE, Context.MODE_PRIVATE);
	}

	public static int getCurrentTimes(Context context) {

		int currentTimes = getRatePreferences(context).getInt(PROPERTY_CURRENT_TIMES, 0);

		return currentTimes;
	}

	public static int isPerformed(Context context){
		int i = 0;
		if(getStationboardSearch(context)){
			i ++;
		}
		if(getScheduleSearch(context)){
			i ++;
		}
		if(getDNRUpload(context)){
			i ++;
		}
		if(getDossierDetails(context)){
			i ++;
		}
		return i;
	}


	public static void resetCurrentTimes(Context context) {
		
		SharedPreferences.Editor editor = getRatePreferences(context).edit();
		editor.putInt(PROPERTY_CURRENT_TIMES, 0);
		//editor.putInt(PROPERTY_TOTAL_TIMES, 3);
		editor.commit();
	}
}
