package com.cfl.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.cfl.application.NMBSApplication;
import com.cfl.async.DossierUpToDateAsyncTask;
import com.cfl.model.GeneralSetting;
import com.cfl.model.Order;

import java.util.List;

public class UpdateAlarmsBroadcastReceiver extends BroadcastReceiver {


	//private static final String TAG = UpdateAlarmsBroadcastReceiver.class.getSimpleName();


	public UpdateAlarmsBroadcastReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			Bundle bundle = intent.getExtras();
			int e_requestCode = bundle.getInt("RequestCode");
			//android.util.Log.e("UpToDate", "e_requestCode...");
			if (e_requestCode == NMBSApplication.REQUESTCODE_UPDATE) {
				DossierUpToDateAsyncTask asyncTask = new DossierUpToDateAsyncTask(context);
				asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				//android.util.Log.e("UpToDate", "UpdateAlarmsBroadcastReceiver Receivered...");
			}
		} catch (Exception e) {
			//Log.i(TAG, e.getMessage());
		}
	}

}
