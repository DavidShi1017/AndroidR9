package com.cfl.services.impl;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.cfl.application.NMBSApplication;
import com.cfl.log.LogUtils;
import com.cfl.receivers.AlarmsRefreshDossierBroadcastReceiver;
import com.cfl.receivers.CheckOptionReceiver;
import com.cfl.util.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class CheckOptionNotificationService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e("NotificationService", "NotificationService  create---------->");
        setAlarmCheckOptions();
        registerNotification();
        setAlarmRefreshDossier();
    }

    private void registerNotification(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 16);
        cal.set(Calendar.MINUTE, 01);
        cal.set(Calendar.SECOND, 00);
        cal.set(Calendar.MILLISECOND, 00);
        AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //Intent intent = new Intent(this, CheckOptionNotificationReceiver.class);
        Intent intent = new Intent();
        //对应BroadcastReceiver中intentFilter的action
        intent.setAction("BROADCAST_ACTION");
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public void setAlarmCheckOptions() {
        // setup of Alarm for sync Material
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, 4);
        cal.set(Calendar.MINUTE, 30);
        cal.set(Calendar.SECOND, 00);
        cal.set(Calendar.MILLISECOND, 00);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        LogUtils.d("setAlarmCheckOptions", sdf.format(cal.getTime()));

        Intent intent = new Intent(NMBSApplication.getInstance(), CheckOptionReceiver.class);
        int RequestCode = NMBSApplication.REQUESTCODE_CHECKOPTIONS;
        intent.putExtra("RequestCode", RequestCode);
        PendingIntent sender = PendingIntent.getBroadcast(NMBSApplication.getInstance(), RequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) NMBSApplication.getInstance().getSystemService(Context.ALARM_SERVICE);
        am.cancel(sender);
        am.setRepeating(AlarmManager.RTC, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, sender);
    }

    public void setAlarmRefreshDossier() {

        Date date = DateUtils.getAfterManyHour(new Date(), 1);
        long firstTime = date.getTime();
        Intent intent = new Intent(NMBSApplication.getInstance(), AlarmsRefreshDossierBroadcastReceiver.class);
        int RequestCode = NMBSApplication.REQUESTCODE_REFRESH;
        intent.putExtra("RequestCode", RequestCode);
        PendingIntent sender = PendingIntent.getBroadcast(NMBSApplication.getInstance(), RequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) NMBSApplication.getInstance().getSystemService(Context.ALARM_SERVICE);
        //am.cancel(sender);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime,4 * 60 * 60 * 1000, sender);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
