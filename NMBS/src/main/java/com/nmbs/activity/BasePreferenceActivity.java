package com.nmbs.activity;

import java.util.List;

import com.nmbs.application.NMBSApplication;
import com.nmbs.async.MobileMessageAsyncTask;
import com.nmbs.services.IMessageService;
import com.nmbs.services.ISettingService;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class BasePreferenceActivity extends PreferenceActivity {
	public static final String ISGARBAGECOLLECTION= "collect";
	private boolean isActive = true;
	private boolean isForeground = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			isForeground = true;
				getMessageData();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		if (!isAppOnForeground()) {
			isActive = false;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		isForeground = false;
		if (!isActive) {
			isForeground = true;
			isActive = true;
			getMessageData();
		}
	}

	public boolean getForeground(){
		return this.isForeground;
	}
	
	public void getMessageData() {
		/*ISettingService settingService = ((NMBSApplication) getApplication())
				.getSettingService();
		IMessageService messageService = ((NMBSApplication) getApplication())
				.getMessageService();
		MobileMessageAsyncTask mobileMessageAsyncTask = new MobileMessageAsyncTask(
				messageService, "", getApplicationContext());
		mobileMessageAsyncTask.execute((Void) null);*/
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(ISGARBAGECOLLECTION, isActive);
	}

	public boolean isAppOnForeground() {

		ActivityManager activityManager = (ActivityManager) getApplicationContext()
				.getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = getApplicationContext().getPackageName();

		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		if (appProcesses == null)
			return false;

		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(packageName)
					&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}

		return false;
	}
}
