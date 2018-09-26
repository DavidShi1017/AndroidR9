package com.nmbs.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.nmbs.application.NMBSApplication;
import com.nmbs.async.DossierUpToDateAsyncTask;
import com.nmbs.model.GeneralSetting;
import com.nmbs.model.Order;

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
