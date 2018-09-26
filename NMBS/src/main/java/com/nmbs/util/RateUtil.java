package com.nmbs.util;

import com.nmbs.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;


public class RateUtil {

	private static final String PROPERTY_CURRENT_TIMES = "currentTimes";
	private static final String PROPERTY_TOTAL_TIMES = "totalTimes";
	private static final String PROPERTY_IS_RATED = "isRated";
	private static final String PREFERENCES_RATE = "com.nmbs.rate.app";

	public static void saveTotalTimes(Context context, int totalTimes) {

		SharedPreferences.Editor editor = getRatePreferences(context).edit();
		editor.putInt(PROPERTY_TOTAL_TIMES, totalTimes);
		editor.commit();
	}

	public static void saveCurrentTimes(Context context) {
		int currentTimes = getCurrentTimes(context);
		SharedPreferences.Editor editor = getRatePreferences(context).edit();
		currentTimes = currentTimes + 1;
		editor.putInt(PROPERTY_CURRENT_TIMES, currentTimes);
		editor.commit();
	}

	public static void setRate(Context context) {

		SharedPreferences.Editor editor = getRatePreferences(context).edit();
		editor.putBoolean(PROPERTY_IS_RATED, true);
		editor.commit();
	}

	public static boolean isRated(Context context) {

		boolean isRated = getRatePreferences(context).getBoolean(PROPERTY_IS_RATED, false);

		return isRated;
	}

	private static SharedPreferences getRatePreferences(Context context) {
		return context.getSharedPreferences(PREFERENCES_RATE, Context.MODE_PRIVATE);
	}

	public static int getTotalTimes(Context context) {

		int totalTimes = getRatePreferences(context).getInt(PROPERTY_TOTAL_TIMES, 3);

		return totalTimes;
	}

	public static int getCurrentTimes(Context context) {

		int currentTimes = getRatePreferences(context).getInt(PROPERTY_CURRENT_TIMES, 0);

		return currentTimes;
	}


	public static void resetCurrentTimes(Context context) {
		
		SharedPreferences.Editor editor = getRatePreferences(context).edit();
		editor.putInt(PROPERTY_CURRENT_TIMES, 0);
		//editor.putInt(PROPERTY_TOTAL_TIMES, 3);
		editor.commit();
	}
	
	public static void showRateDialog(final Context context){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);         
        builder.setIcon(null);  
        builder.setTitle(context.getString(R.string.review_option_title));  
        builder.setMessage(context.getString(R.string.review_option_info));  
        
        builder.setPositiveButton(context.getString(R.string.review_option_rate), new DialogInterface.OnClickListener() {  
              
            @Override  
            public void onClick(DialogInterface arg0, int arg1) {  
            	Intent intent = new Intent();
            	intent.setAction(Intent.ACTION_VIEW);
            	//intent.setData(Uri.parse("market://details?id=" + context.getPackageName()));
            	intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName()));
            	context.startActivity(intent);
            	
            	setRate(context);
            }  
        });  
        
        builder.setNeutralButton(context.getString(R.string.review_option_remind_later), new DialogInterface.OnClickListener() {  
              
            @Override  
            public void onClick(DialogInterface arg0, int arg1) {  
            	saveTotalTimes(context, 3);
            	resetCurrentTimes(context);
            }  
        });  
        
        builder.setNegativeButton(context.getString(R.string.review_option_no), new DialogInterface.OnClickListener() {  
              
            @Override  
            public void onClick(DialogInterface arg0, int arg1) {  
            	resetCurrentTimes(context);
            	saveTotalTimes(context, 100);
            }  
        });  
                
        builder.create().show();     
	}
}
