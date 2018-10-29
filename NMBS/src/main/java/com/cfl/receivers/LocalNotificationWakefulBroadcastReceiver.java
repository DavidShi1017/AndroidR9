package com.cfl.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.cfl.R;
import com.cfl.activities.MainActivity;
import com.cfl.activities.MyTicketsActivity;
import com.cfl.application.NMBSApplication;
import com.cfl.log.LogUtils;
import com.cfl.services.impl.LocalNotificationService;
import com.cfl.util.AppUtil;

import java.util.Date;


public class LocalNotificationWakefulBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("LocalNotification", "AlarmReceiver...");
        if(NMBSApplication.getInstance().getSettingService().isTravelReminders()){
            buildLocalNotification(context);
        }else {
            Log.e("LocalNotification", "AlarmReceiver...Canceled....");
        }
    }


    public void buildLocalNotification(Context context) {
        // TODO Auto-generated method stub
        //Intent broadcastIntent = new Intent(this.getApplicationContext(), LocalNotificationReceiver.class);
        String name = AppUtil.getRunningActivity(context);
        PendingIntent pendingIntent = PendingIntent.
                getActivities(context, 0, makeIntentStack(context), PendingIntent.FLAG_UPDATE_CURRENT);
        if(name.contains("com.cfl")){
            Intent i = MyTicketsActivity.createIntent(context);
            pendingIntent = PendingIntent.
                    getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //Bitmap bitmap = BitmapFactory.decodeResource(this.getApplicationContext().getResources(), R.drawable.ic_notification);
        LogUtils.e("RunningActivity", "i------->" + new Date().getHours());
        LogUtils.e("RunningActivity", "i------->" + new Date().getMinutes());

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notification)
                /*.setLargeIcon(bitmap)*/
                .setContentTitle(context.getResources().getString(R.string.push_notification_new_message))
                .setContentText(context.getResources().getString(R.string.local_notification_text))
                .setSound(defaultSoundUri)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());

    }

    Intent[] makeIntentStack(Context context) {
        Intent[] intents = new Intent[2];
        intents[0] = Intent.makeRestartActivityTask(new ComponentName(context, MainActivity.class));
        intents[1] = MyTicketsActivity.createIntent(context);
        return intents;
    }
}