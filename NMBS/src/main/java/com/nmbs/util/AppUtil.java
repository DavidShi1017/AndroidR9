package com.nmbs.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.nmbs.log.LogUtils;

import java.util.List;

public class AppUtil {

	private static final String PROPERTY_APP_VERSION_NAME = "appVersionName";
	private static final String PROPERTY_APP_VERSION_CODE = "appVersionCode";
	private static final String PREFERENCES = "com.nmbs";
	
	public static void saveAppVersionName(Context context){
		String currentVersion = Utils.getAppVersion(context);
		SharedPreferences.Editor editor = getVersionPreferences(context).edit();
		
		editor.putString(PROPERTY_APP_VERSION_NAME, currentVersion);
		editor.commit();
	}
	
	public static String getAppVersionName(Context context){
		String oldVersionString = getVersionPreferences(context).getString(PROPERTY_APP_VERSION_NAME, "");
		return oldVersionString;
	}
	
	private static SharedPreferences getVersionPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }
	
	public static int getAppVersionCodeInSharedPreferences(Context context){
		int oldVersion = getVersionPreferences(context).getInt(PROPERTY_APP_VERSION_CODE, 0);
		return oldVersion;
	}
	
	
	public static void saveAppVersionCode(Context context){
		int currentVersion = getAppVersionCode(context);
		SharedPreferences.Editor editor = getVersionPreferences(context).edit();		
		editor.putInt(PROPERTY_APP_VERSION_CODE, currentVersion);
		editor.commit();
	}
	
	
    public static int getAppVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Coult not get package name: " + e);
        }
    }

	public static boolean isAppAlive(Context context, String packageName){
		ActivityManager activityManager =
				(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> processInfos
				= activityManager.getRunningAppProcesses();
		for(int i = 0; i < processInfos.size(); i++){
			if(processInfos.get(i).processName.equals(packageName)){
				LogUtils.i("NotificationLaunch",
						String.format("the %s is running, isAppAlive return true", packageName));
				return true;
			}
		}
		LogUtils.i("NotificationLaunch",
				String.format("the %s is not running, isAppAlive return false", packageName));
		return false;
	}

	public static String getRunningActivity(Context context){
		ActivityManager activityManager=(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		try{
			if(activityManager.getRunningTasks(1).size() > 0){
				String runningActivity=activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
				return runningActivity;
			}
		}catch (Exception e){
			return "";
		}
		return "";
	}
}
