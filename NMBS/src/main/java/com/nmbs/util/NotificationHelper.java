package com.nmbs.util;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.nmbs.application.NMBSApplication;
import com.nmbs.dataaccess.database.TravelSegmentDatabaseService;
import com.nmbs.log.LogUtils;
import com.nmbs.model.LocalNotification;
import com.nmbs.receivers.LocalNotificationWakefulBroadcastReceiver;
import com.nmbs.receivers.TestBootReceiver;
import com.nmbs.services.IPushService;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by ptyagi on 4/17/17.
 */

public class NotificationHelper {
    public static int ALARM_TYPE_RTC = 100;
    private static AlarmManager alarmManagerRTC;
    private static PendingIntent alarmIntentRTC;

    public static int ALARM_TYPE_ELAPSED = 100;
    private static AlarmManager alarmManagerElapsed;
    private static PendingIntent alarmIntentElapsed;

    /**
     * This is the real time /wall clock time
     * @param context
     */
    public static void scheduleRepeatingRTCNotification(Context context, String hour, String min) {
        //get calendar instance to be able to select what time notification should be scheduled
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        //Setting time of the day (8am here) when notification will be sent every day (default)
        /*calendar.set(Calendar.HOUR_OF_DAY,
                Integer.getInteger(hour, 8) + 1,
                Integer.getInteger(min, 0));*/
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hour));
        calendar.set(Calendar.MINUTE, Integer.valueOf(min));
        calendar.set(Calendar.SECOND, 00);
        //Setting intent to class where Alarm broadcast message will be handled
        Random random = new Random();

        Intent intent = new Intent(context, LocalNotificationWakefulBroadcastReceiver.class);
        //Setting alarm pending intent
        alarmIntentRTC = PendingIntent.getBroadcast(context, random.nextInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //getting instance of AlarmManager service
        alarmManagerRTC = (AlarmManager)context.getSystemService(ALARM_SERVICE);

        //Setting alarm to wake up device every day for clock time.
        //AlarmManager.RTC_WAKEUP is responsible to wake up device for sure, which may not be good practice all the time.
        // Use this when you know what you're doing.
        //Use RTC when you don't need to wake up device, but want to deliver the notification whenever device is woke-up
        //We'll be using RTC.WAKEUP for demo purpose only
        if (Build.VERSION.SDK_INT >= 23) {
            alarmManagerRTC.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntentRTC);
            /*AlarmManager.AlarmClockInfo info = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), alarmIntentRTC);
            alarmManagerRTC.setAlarmClock(info, alarmIntentRTC);*/

        } else if (Build.VERSION.SDK_INT >= 19) {
            alarmManagerRTC.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntentRTC);
        } else {
            alarmManagerRTC.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntentRTC);
        }
    }

    /***
     * This is another way to schedule notifications using the elapsed time.
     * Its based on the relative time since device was booted up.
     * @param context
     */
    public static void scheduleRepeatingElapsedNotification(Context context) {

        LogUtils.e("Notification", "Notification schedule-------");
        //Setting intent to class where notification will be handled
        Intent intent = new Intent(context, LocalNotificationWakefulBroadcastReceiver.class);

        //Setting pending intent to respond to broadcast sent by AlarmManager everyday at 8am
        alarmIntentElapsed = PendingIntent.getBroadcast(context, ALARM_TYPE_ELAPSED, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //getting instance of AlarmManager service
        alarmManagerElapsed = (AlarmManager)context.getSystemService(ALARM_SERVICE);

        //Inexact alarm everyday since device is booted up. This is a better choice and
        //scales well when device time settings/locale is changed
        //We're setting alarm to fire notification after 15 minutes, and every 15 minutes there on

        alarmManagerElapsed.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + 1000 * 10,
                1000 * 10, alarmIntentElapsed);
        /*PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);

        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"My Tag");
        wl.acquire();*/
    }

    public static void cancelAlarmRTC() {
        if (alarmManagerRTC!= null) {
            alarmManagerRTC.cancel(alarmIntentRTC);
        }
    }

    public static void cancelAlarmElapsed() {
        if (alarmManagerElapsed!= null) {
            alarmManagerElapsed.cancel(alarmIntentElapsed);
        }
    }

    public static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * Enable boot receiver to persist alarms set for notifications across device reboots
     */
    public static void enableBootReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, TestBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
        TravelSegmentDatabaseService travelSegmentDatabaseService = new TravelSegmentDatabaseService(context);
        List<LocalNotification> notifications =  travelSegmentDatabaseService.getAllTravelSegment();
        IPushService pushService = NMBSApplication.getInstance().getPushService();

        if(notifications != null){
            for(LocalNotification localNotification : notifications){
                if(localNotification != null){
                    int id = pushService.getPushId(localNotification.getDepartureDate());
                    LogUtils.e("enableBootReceiver", "enableBootReceiver id----->" + id);
                    pushService.createLocalNotification(pushService.getPushTime(localNotification.getDepartureDate()), id);
                }
            }
        }
    }

    /**
     * Disable boot receiver when user cancels/opt-out from notifications
     */
    public static void disableBootReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, TestBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}
