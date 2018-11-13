package com.cflint.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.cflint.application.NMBSApplication;
import com.cflint.async.MigrateDossierAsyncTask;
import com.cflint.model.GeneralSetting;
import com.cflint.model.Order;


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
				Log.i(TAG, "EmailBroadcast Receivered...");

				//Email email = new Email();
				//email.sendEmail(text);
				Log.i(TAG, "EmailBroadcast Receivered..." + text);
			}
		} catch (Exception e) {
			//Log.i(TAG, e.getMessage());
		}
	}

}
