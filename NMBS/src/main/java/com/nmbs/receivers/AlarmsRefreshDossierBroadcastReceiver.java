package com.nmbs.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;

import com.nmbs.application.NMBSApplication;
import com.nmbs.async.AutoRetrievalDossiersTask;
import com.nmbs.async.MigrateDossierAsyncTask;
import com.nmbs.async.RefreshMultipleDossierAsyncTask;
import com.nmbs.log.LogUtils;
import com.nmbs.model.GeneralSetting;
import com.nmbs.model.Order;

import java.lang.ref.SoftReference;
import java.util.List;

public class AlarmsRefreshDossierBroadcastReceiver extends BroadcastReceiver {


	private static final String TAG = AlarmsRefreshDossierBroadcastReceiver.class.getSimpleName();


	public AlarmsRefreshDossierBroadcastReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtils.i(TAG, "AlarmsRefreshDossierBroadcastReceiver Receivered...");
		try {
			Bundle bundle = intent.getExtras();
			final int e_requestCode = bundle.getInt("RequestCode");
			new Thread() {
				public void run() {

					if (e_requestCode == NMBSApplication.REQUESTCODE_REFRESH) {
						if(NMBSApplication.getInstance().getLoginService().isLogon() && !AutoRetrievalDossiersTask.isWorking){
							NMBSApplication.getInstance().getDossierDetailsService().autoRetrievalDossiers();
						}
						LogUtils.i(TAG, "AlarmsRefreshDossierBroadcastReceiver Receivered...");
					}
				}
			}.start();

		} catch (Exception e) {
			//Log.i(TAG, e.getMessage());
		}
	}
}
