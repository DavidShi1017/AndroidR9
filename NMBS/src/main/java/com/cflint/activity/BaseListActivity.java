package com.cflint.activity;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import com.cflint.R;
import com.cflint.application.NMBSApplication;
import com.cflint.async.AutoRetrievalDossiersTask;
import com.cflint.async.CheckOptionAsyncTask;
import com.cflint.async.MobileMessageAsyncTask;
import com.cflint.services.IMessageService;
import com.cflint.services.ISettingService;
import com.cflint.util.AppLanguageUtils;

public class BaseListActivity extends ListActivity {
	public static final String ISGARBAGECOLLECTION = "collect";
	private boolean isActive = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			getMessageData();
		}
	}
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(AppLanguageUtils.attachBaseContext(newBase, newBase.getString(R.string.app_language_pref_key)));
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
		if (!isActive) {
			isActive = true;
			getMessageData();
			refreshDossier();

			if(NMBSApplication.getInstance().getLoginService().isLogon() && !CheckOptionAsyncTask.isChecking){
				CheckOptionAsyncTask asyncTask = new CheckOptionAsyncTask(getApplicationContext());
				asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		}
	}
	public void refreshDossier(){
		if(NMBSApplication.getInstance().getLoginService().isLogon() && !AutoRetrievalDossiersTask.isWorking){
			NMBSApplication.getInstance().getDossierDetailsService().autoRetrievalDossiers();
		}
	}
	public void getMessageData() {
		ISettingService settingService = ((NMBSApplication) getApplication())
				.getSettingService();
		IMessageService messageService = ((NMBSApplication) getApplication())
				.getMessageService();
		MobileMessageAsyncTask mobileMessageAsyncTask = new MobileMessageAsyncTask(
				messageService, settingService.getCurrentLanguagesKey(), getApplicationContext());
		mobileMessageAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
