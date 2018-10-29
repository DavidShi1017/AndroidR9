package com.nmbs.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.nmbs.application.NMBSApplication;
import com.nmbs.dataaccess.database.TravelSegmentDatabaseService;
import com.nmbs.model.LocalNotification;
import com.nmbs.services.IPushService;
import com.nmbs.services.impl.PushService;
import com.nmbs.util.NotificationHelper;

import java.util.List;

/**
 * Created by ptyagi on 4/18/17.
 */

public class TestBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null && intent.getAction() != null){
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                //only enabling one type of notifications for demo purposes
                Log.e("Notification", "Notification android.intent.action.BOOT_COMPLETED-------");
               // NotificationHelper.scheduleRepeatingElapsedNotification(context);
                NotificationHelper.enableBootReceiver(context);

            }
        }
    }
}
