package com.nmbs.receivers;

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

import com.nmbs.R;
import com.nmbs.activities.MainActivity;
import com.nmbs.activities.MyTicketsActivity;
import com.nmbs.application.NMBSApplication;
import com.nmbs.dataaccess.database.TravelSegmentDatabaseService;
import com.nmbs.log.LogUtils;
import com.nmbs.model.LocalNotification;
import com.nmbs.services.IPushService;
import com.nmbs.services.impl.LocalNotificationService;
import com.nmbs.util.AppUtil;
import com.nmbs.util.DateUtils;

import java.util.Date;
import java.util.List;

public class LocalNotificationWakefulBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        LogUtils.e("LocalNotification", "AlarmReceiver...");
        new Thread() {
            public void run() {
                if(NMBSApplication.getInstance().getSettingService().isTravelReminders()){
                    buildLocalNotification(context);
                }else {
                    LogUtils.e("LocalNotification", "AlarmReceiver...Canceled....");
                }
            }
        }.start();

    }


    public void buildLocalNotification(Context context) {
        // TODO Auto-generated method stub
        //Intent broadcastIntent = new Intent(this.getApplicationContext(), LocalNotificationReceiver.class);

        Date now = new Date();
        String nowStr = DateUtils.dateToString(DateUtils.getFewLaterDay(now, 1));
        TravelSegmentDatabaseService travelSegmentDatabaseService = new TravelSegmentDatabaseService(context);
        List<LocalNotification> notifications =  travelSegmentDatabaseService.getAllTravelSegment();
        boolean isBuild = false;
        if(notifications != null){
            for(LocalNotification localNotification : notifications){
                if(localNotification != null){
                    Date date = localNotification.getDepartureDate();
                    String dateStr =  DateUtils.dateToString(date);
                    LogUtils.e("LocalNotification", "nowStr------->" + nowStr);
                    LogUtils.e("LocalNotification", "dateStr------->" + dateStr);
                    if(nowStr != null && nowStr.equalsIgnoreCase(dateStr)){
                        isBuild = true;
                    }
                }
            }
        }

        if(!isBuild){
            return;
        }
        String name = AppUtil.getRunningActivity(context);
        PendingIntent pendingIntent = PendingIntent.
                getActivities(context, 0, makeIntentStack(context), PendingIntent.FLAG_UPDATE_CURRENT);
        if(name.contains("com.nmbs")){
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
