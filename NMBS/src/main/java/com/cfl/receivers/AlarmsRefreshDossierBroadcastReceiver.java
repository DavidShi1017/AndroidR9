package com.cfl.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cfl.application.NMBSApplication;
import com.cfl.async.AutoRetrievalDossiersTask;
import com.cfl.async.MigrateDossierAsyncTask;
import com.cfl.async.RefreshMultipleDossierAsyncTask;
import com.cfl.log.LogUtils;
import com.cfl.model.GeneralSetting;
import com.cfl.model.Order;

import java.util.List;

public class AlarmsRefreshDossierBroadcastReceiver extends BroadcastReceiver {


	private static final String TAG = AlarmsRefreshDossierBroadcastReceiver.class.getSimpleName();


	public AlarmsRefreshDossierBroadcastReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			Bundle bundle = intent.getExtras();
			int e_requestCode = bundle.getInt("RequestCode");
			if (e_requestCode == NMBSApplication.REQUESTCODE_REFRESH) {
				if(NMBSApplication.getInstance().getLoginService().isLogon() && !AutoRetrievalDossiersTask.isWorking){
					NMBSApplication.getInstance().getDossierDetailsService().autoRetrievalDossiers();
				}
				LogUtils.i(TAG, "AlarmsRefreshDossierBroadcastReceiver Receivered...");
			}
		} catch (Exception e) {
			//Log.i(TAG, e.getMessage());
		}
	}
}
