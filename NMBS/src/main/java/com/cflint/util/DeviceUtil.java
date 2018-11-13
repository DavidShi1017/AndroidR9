package com.cflint.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.telephony.TelephonyManager;

public class DeviceUtil {

	public static String getDeviceId(Context context){
		try{
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String deviceId = tm.getDeviceId();
			return deviceId;
		}catch (Exception e){
			return null;
		}
	}
	
	public static String getDeviceOsVersion(){
		String deviceVersion = android.os.Build.VERSION.RELEASE;
		return deviceVersion;
	}
	
    /**
     * Gets the application version.
     */
	public static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Coult not get package name: " + e);
		}
	}

	public static String getAppVersionName(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Coult not get package name: " + e);
		}
	}
	public static boolean isTabletDevice(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >=
				Configuration.SCREENLAYOUT_SIZE_LARGE;
	}
}
