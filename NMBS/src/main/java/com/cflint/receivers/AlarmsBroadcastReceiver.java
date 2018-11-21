package com.cflint.receivers;

import com.cflint.application.NMBSApplication;
import com.cflint.async.MigrateDossierAsyncTask;
import com.cflint.model.GeneralSetting;
import com.cflint.model.Order;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

public class AlarmsBroadcastReceiver extends BroadcastReceiver {
	
	
	//private static final String TAG = AlarmsBroadcastReceiver.class.getSimpleName();


	public AlarmsBroadcastReceiver() {
	}

	public void onReceive(final Context context, Intent intent) {
		try {
			Bundle bundle = intent.getExtras();
			int e_requestCode = bundle.getInt("RequestCode");
			if (e_requestCode == NMBSApplication.REQUESTCODE_MATERIAL_SYNC) {
				new Thread() {
					public void run() {
						GeneralSetting generalSetting = NMBSApplication.getInstance().getMasterService().loadGeneralSetting();
						String dossierAftersalesLifetime = String.valueOf(generalSetting.getDossierAftersalesLifetime());
						List<Order> listOrders = NMBSApplication.getInstance().getAssistantService().searchOrders(0, dossierAftersalesLifetime);
						if(listOrders != null && listOrders.size() > 0){
							MigrateDossierAsyncTask asyncTask = new MigrateDossierAsyncTask( null, NMBSApplication.getInstance().getSettingService().getCurrentLanguagesKey(),
									context, listOrders);
							asyncTask.execute((Void) null);
							//Log.i(TAG, "MigrateDossierAsyncTask execute...");
						}
					}
				}.start();


				//Log.i(TAG, "AlarmsBroadcast Receivered...");
			}
		} catch (Exception e) {
			//Log.i(TAG, e.getMessage());
		}
	}

}
