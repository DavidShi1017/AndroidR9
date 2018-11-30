package com.nmbs.activity;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import com.nmbs.R;
import com.nmbs.application.NMBSApplication;
import com.nmbs.async.AutoRetrievalDossiersTask;
import com.nmbs.async.CheckOptionAsyncTask;
import com.nmbs.async.DossierUpToDateAsyncTask;
import com.nmbs.async.MobileMessageAsyncTask;
import com.nmbs.async.RefreshMultipleDossierAsyncTask;
import com.nmbs.services.IMessageService;
import com.nmbs.services.ISettingService;
import com.nmbs.util.AppLanguageUtils;


public class BaseActivity extends Activity {
	public static final String ISGARBAGECOLLECTION = "collect";
	private boolean isActive = true;
	private boolean isForeground = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			boolean flag = savedInstanceState.getBoolean("ISGARBAGECOLLECTION", false);
			if(!flag){
				isForeground = true;
				getMessageData();
			}
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
		try {
			isForeground = false;
			if (!isActive) {
				isForeground = true;
				isActive = true;
				getMessageData();
				refreshDossier();

				if(NMBSApplication.getInstance().getLoginService().isLogon() && !CheckOptionAsyncTask.isChecking){
					CheckOptionAsyncTask asyncTask = new CheckOptionAsyncTask(getApplicationContext());
					asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
			}
		}catch (Exception e){

		}

	}
	
	
	
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
	}

	public boolean getForeground(){
		return this.isForeground;
	}

	public void getMessageData() {
		ISettingService settingService = ((NMBSApplication) getApplication()).getSettingService();
		IMessageService messageService = ((NMBSApplication) getApplication()).getMessageService();
		MobileMessageAsyncTask mobileMessageAsyncTask = new MobileMessageAsyncTask(messageService, settingService.getCurrentLanguagesKey(), getApplicationContext());
		mobileMessageAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	public void refreshDossier(){
		if(NMBSApplication.getInstance().getLoginService().isLogon() && !AutoRetrievalDossiersTask.isWorking){
			NMBSApplication.getInstance().getDossierDetailsService().autoRetrievalDossiers();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		//outState.putSerializable(ISGARBAGECOLLECTION, isActive);
		outState.putBoolean(ISGARBAGECOLLECTION, isActive);
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
