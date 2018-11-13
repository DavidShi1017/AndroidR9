package com.cflint.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cflint.application.NMBSApplication;
import com.cflint.async.AutoRetrievalDossiersTask;
import com.cflint.async.MigrateDossierAsyncTask;
import com.cflint.async.RefreshMultipleDossierAsyncTask;
import com.cflint.log.LogUtils;
import com.cflint.model.GeneralSetting;
import com.cflint.model.Order;

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
