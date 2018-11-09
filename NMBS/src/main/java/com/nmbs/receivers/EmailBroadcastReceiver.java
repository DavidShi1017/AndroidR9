package com.nmbs.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.nmbs.application.NMBSApplication;
import com.nmbs.async.MigrateDossierAsyncTask;
import com.nmbs.log.LogUtils;
import com.nmbs.model.GeneralSetting;
import com.nmbs.model.Order;


import java.util.List;

public class EmailBroadcastReceiver extends BroadcastReceiver {


	private static final String TAG = EmailBroadcastReceiver.class.getSimpleName();


	public EmailBroadcastReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			Bundle bundle = intent.getExtras();
			int e_requestCode = bundle.getInt("RequestCode");
			String text = bundle.getString("Text");
			if (e_requestCode == NMBSApplication.REQUESTCODE_EMAIL_SYNC) {
				LogUtils.i(TAG, "EmailBroadcast Receivered...");

				//Email email = new Email();
				//email.sendEmail(text);
				LogUtils.i(TAG, "EmailBroadcast Receivered..." + text);
			}
		} catch (Exception e) {
			//Log.i(TAG, e.getMessage());
		}
	}

}
