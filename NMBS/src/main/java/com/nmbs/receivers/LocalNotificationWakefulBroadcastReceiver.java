package com.nmbs.receivers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.nmbs.application.NMBSApplication;
import com.nmbs.services.impl.LocalNotificationService;

public class LocalNotificationWakefulBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("LocalNotification", "AlarmReceiver...");
        if(NMBSApplication.getInstance().getSettingService().isTravelReminders()){
            Intent myIntent = new Intent(context, LocalNotificationService.class);
            context.startService(myIntent);
        }else {
            Log.e("LocalNotification", "AlarmReceiver...Canceled....");
        }
    }
}
