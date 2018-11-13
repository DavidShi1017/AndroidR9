package com.cflint.services.impl;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.cflint.R;
import com.cflint.activities.MainActivity;
import com.cflint.activities.MyTicketsActivity;
import com.cflint.log.LogUtils;
import com.cflint.util.AppUtil;
import com.cflint.util.LocalNotificationReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LocalNotificationService extends Service {

    private NotificationManager mManager;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        //Intent broadcastIntent = new Intent(this.getApplicationContext(), LocalNotificationReceiver.class);
        String name = AppUtil.getRunningActivity(getApplicationContext());
        PendingIntent pendingIntent = PendingIntent.
                getActivities(this.getApplicationContext(), 0, makeIntentStack(getApplicationContext()), PendingIntent.FLAG_UPDATE_CURRENT);
        LogUtils.e("RunningActivity", "RunningActivity name------->" + name);
        if(name.contains("com.cflint")){
            LogUtils.e("RunningActivity", "com.cf------->");
            Intent i = MyTicketsActivity.createIntent(getApplicationContext());
            pendingIntent = PendingIntent.
                    getActivity(getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        /*String nowTime = new SimpleDateFormat("HH:MM").format(new Date());
        int i = DateCompare(nowTime,"16:00","HH:MM");*/
        Date now = new Date();
        LogUtils.e("RunningActivity", "i------->" + new Date().getHours());
        LogUtils.e("RunningActivity", "i------->" + new Date().getMinutes());
        if(now.getHours() == 16 && now.getMinutes() < 1){
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this.getApplicationContext())
                    .setSmallIcon(R.drawable.ic_notification)
                    /*.setLargeIcon(bitmap)*/
                    .setContentTitle(this.getApplicationContext().getResources().getString(R.string.push_notification_new_message))
                    .setContentText(this.getApplicationContext().getResources().getString(R.string.local_notification_text))
                    .setSound(defaultSoundUri)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager =
                    (NotificationManager) this.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notificationBuilder.build());
        }
        //Bitmap bitmap = BitmapFactory.decodeResource(this.getApplicationContext().getResources(), R.drawable.ic_notification);

        return START_NOT_STICKY;
    }
    public static int DateCompare(String source, String traget, String type){
        int ret = 2;
        SimpleDateFormat format = new SimpleDateFormat(type);
        Date sourcedate = null;
        Date tragetdate = null;
        try {
            sourcedate = format.parse(source);
            tragetdate = format.parse(traget);
            LogUtils.e("RunningActivity", "sourcedate------->" + sourcedate);
            LogUtils.e("RunningActivity", "tragetdate------->" + tragetdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ret = sourcedate.compareTo(tragetdate);
        return ret;
    }
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
    Intent[] makeIntentStack(Context context) {
        Intent[] intents = new Intent[2];
        intents[0] = Intent.makeRestartActivityTask(new ComponentName(context, MainActivity.class));
        intents[1] = MyTicketsActivity.createIntent(context);
        return intents;
    }
}